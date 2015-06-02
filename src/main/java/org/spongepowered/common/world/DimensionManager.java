/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.world;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import org.apache.logging.log4j.Level;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.interfaces.IMixinEntityPlayerMP;
import org.spongepowered.common.interfaces.IMixinMinecraftServer;
import org.spongepowered.common.interfaces.IMixinWorldProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class DimensionManager {

    private static final Hashtable<Integer, Class<? extends WorldProvider>> providers = new Hashtable<Integer, Class<? extends WorldProvider>>();
    private static final Hashtable<Integer, Boolean> spawnSettings = new Hashtable<Integer, Boolean>();
    private static final Hashtable<Integer, Integer> dimensions = new Hashtable<Integer, Integer>();
    private static final Hashtable<Integer, WorldServer> worlds = new Hashtable<Integer, WorldServer>();
    private static final ConcurrentMap<World, World> weakWorldMap = new MapMaker().weakKeys().weakValues().makeMap();
    private static final ArrayList<Integer> unloadQueue = Lists.newArrayList();
    private static final BitSet dimensionMap = new BitSet(Long.SIZE << 4);
    private static final Multiset<Integer> leakedWorlds = HashMultiset.create();

    static {
        registerProviderType(0, WorldProviderSurface.class, true);
        registerProviderType(-1, WorldProviderHell.class, true);
        registerProviderType(1, WorldProviderEnd.class, true);
        registerDimension(0, 0);
        registerDimension(-1, -1);
        registerDimension(1, 1);
    }

    public static boolean registerProviderType(int id, Class<? extends WorldProvider> provider, boolean keepLoaded) {
        if (providers.containsKey(id)) {
            return false;
        }
        // register dimension type
        String worldType;
        switch (id) {
            case -1:
                worldType = "NETHER";
                break;
            case 0:
                worldType = "OVERWORLD";
                break;
            case 1:
                worldType = "END";
                break;
            default: // modded
                worldType = provider.getSimpleName().toLowerCase();
                worldType = worldType.replace("worldprovider", "");
                worldType = worldType.replace("provider", "");
        }

        Sponge.getSpongeRegistry().registerDimensionType(new SpongeDimensionType(worldType, keepLoaded, provider, id));
        providers.put(id, provider);
        spawnSettings.put(id, keepLoaded);
        return true;
    }

    public static int getProviderType(int dim) {
        if (!dimensions.containsKey(dim)) {
            throw new IllegalArgumentException(String.format("Could not get provider type for dimension %d, does not exist", dim));
        }
        return dimensions.get(dim);
    }

    public static WorldProvider createProviderFor(int dim) {
        try {
            if (dimensions.containsKey(dim)) {
                WorldProvider provider = providers.get(getProviderType(dim)).newInstance();
                ((IMixinWorldProvider) provider).setDimension(dim);
                return provider;
            } else {
                throw new RuntimeException(String.format("No WorldProvider bound for dimension %d", dim));
            }
        } catch (Exception e) {
            Sponge.getLogger().log(Level.ERROR, String.format("An error occurred trying to create an instance of WorldProvider %d (%s)",
                    dim, providers.get(getProviderType(dim)).getSimpleName()), e);
            throw new RuntimeException(e);
        }
    }

    public static boolean shouldLoadSpawn(int dim) {
        int id = getProviderType(dim);
        return spawnSettings.containsKey(id) && spawnSettings.get(id);
    }

    public static void loadDimensionDataMap(NBTTagCompound compound) {
        dimensionMap.clear();
        if (compound == null) {
            for (Integer id : dimensions.keySet()) {
                if (id >= 0) {
                    dimensionMap.set(id);
                }
            }
        } else {
            int[] intArray = compound.getIntArray("DimensionArray");
            for (int i = 0; i < intArray.length; i++) {
                for (int j = 0; j < Integer.SIZE; j++) {
                    dimensionMap.set(i * Integer.SIZE + j, (intArray[i] & (1 << j)) != 0);
                }
            }
        }
    }

    public static NBTTagCompound saveDimensionDataMap() {
        int[] data = new int[(dimensionMap.length() + Integer.SIZE - 1) / Integer.SIZE];
        NBTTagCompound dimMap = new NBTTagCompound();
        for (int i = 0; i < data.length; i++) {
            int val = 0;
            for (int j = 0; j < Integer.SIZE; j++) {
                val |= dimensionMap.get(i * Integer.SIZE + j) ? (1 << j) : 0;
            }
            data[i] = val;
        }
        dimMap.setIntArray("DimensionArray", data);
        return dimMap;
    }

    public static Integer[] getIDs(boolean check) {
        if (check) {
            List<World> allWorlds = Lists.newArrayList(weakWorldMap.keySet());
            allWorlds.removeAll(worlds.values());
            for (World w : allWorlds) {
                leakedWorlds.add(System.identityHashCode(w));
            }
            for (World w : allWorlds) {
                int leakCount = leakedWorlds.count(System.identityHashCode(w));
                if (leakCount == 5) {
                    Sponge.getLogger().log(Level.WARN, "The world %x (%s) may have leaked: first encounter (5 occurences).\n", System
                            .identityHashCode(w), w.getWorldInfo().getWorldName());
                } else if (leakCount % 5 == 0) {
                    Sponge.getLogger().log(Level.WARN, "The world %x (%s) may have leaked: seen %d times.\n", System.identityHashCode(w),
                            w.getWorldInfo().getWorldName
                                    (), leakCount);
                }
            }
        }
        return getIDs();
    }

    public static Integer[] getIDs() {
        return worlds.keySet().toArray(new Integer[worlds.size()]); //Only loaded dims, since usually used to cycle through loaded worlds
    }

    public static Integer[] getStaticDimensionIDs() {
        return dimensions.keySet().toArray(new Integer[dimensions.keySet().size()]);
    }

    public static WorldServer getWorldFromDimId(int id) {
        return worlds.get(id);
    }

    public static void unloadWorldFromDimId(int id) {
        unloadQueue.add(id);
    }

    public static void setWorld(int id, WorldServer world) {
        if (world != null) {
            worlds.put(id, world);
            weakWorldMap.put(world, world);
            ((IMixinMinecraftServer) MinecraftServer.getServer()).getWorldTickTimes().put(id, new long[100]);
            Sponge.getLogger().info("Loading dimension %d (%s) (%s)", id, world.getWorldInfo().getWorldName(), world.getMinecraftServer());
        } else {
            worlds.remove(id);
            ((IMixinMinecraftServer) MinecraftServer.getServer()).getWorldTickTimes().remove(id);
            Sponge.getLogger().info("Unloading dimension %d", id);
        }

        ArrayList<WorldServer> tmp = new ArrayList<WorldServer>();
        if (worlds.get(0) != null) {
            tmp.add(worlds.get(0));
        }
        if (worlds.get(-1) != null) {
            tmp.add(worlds.get(-1));
        }
        if (worlds.get(1) != null) {
            tmp.add(worlds.get(1));
        }

        for (Map.Entry<Integer, WorldServer> entry : worlds.entrySet()) {
            int dim = entry.getKey();
            if (dim >= -1 && dim <= 1) {
                continue;
            }
            tmp.add(entry.getValue());
        }

        MinecraftServer.getServer().worldServers = tmp.toArray(new WorldServer[tmp.size()]);
    }

    public static WorldServer[] getWorlds() {
        return worlds.values().toArray(new WorldServer[worlds.size()]);
    }

    public static boolean isDimensionRegistered(int dim) {
        return dimensions.containsKey(dim);
    }

    public static void registerDimension(int id, int providerType) {
        if (!providers.containsKey(providerType)) {
            throw new IllegalArgumentException(
                    String.format("Failed to register dimension for id %d, provider type %d does not exist", id, providerType));
        }
        if (dimensions.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, One is already registered", id));
        }
        dimensions.put(id, providerType);
        if (id >= 0) {
            dimensionMap.set(id);
        }
    }

    public static int getNextFreeDimId() {
        int next = 0;
        while (true) {
            next = dimensionMap.nextClearBit(next);
            if (dimensions.containsKey(next)) {
                dimensionMap.set(next);
            } else {
                return next;
            }
        }
    }

    public static File getCurrentSaveRootDirectory() {
        if (DimensionManager.getWorldFromDimId(0) != null) {
            return DimensionManager.getWorldFromDimId(0).getSaveHandler().getWorldDirectory();
        } else if (MinecraftServer.getServer() != null) {
            MinecraftServer srv = MinecraftServer.getServer();
            SaveHandler saveHandler = (SaveHandler) srv.getActiveAnvilConverter().getSaveLoader(srv.getFolderName(), false);
            return saveHandler.getWorldDirectory();
        } else {
            return null;
        }
    }

    public static void initDimension(int dim) {
        WorldServer overworld = getWorldFromDimId(0);
        if (overworld == null) {
            throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
        }
        try {
            DimensionManager.getProviderType(dim);
        } catch (Exception e) {
            Sponge.getLogger().log(Level.ERROR, "Cannot Hotload Dim: " + e.getMessage());
            return; // If a provider hasn't been registered then we can't hotload the dim
        }
        MinecraftServer mcServer = overworld.getMinecraftServer();
        ISaveHandler savehandler = overworld.getSaveHandler();
        WorldSettings worldSettings = new WorldSettings(overworld.getWorldInfo());

        WorldServer world =
                (dim == 0 ? overworld : (WorldServer) (new WorldServerMulti(mcServer, savehandler, dim, overworld, mcServer.theProfiler).init()));
        world.addWorldAccess(new WorldManager(mcServer, world));
        Sponge.getGame().getEventManager().post(SpongeEventFactory.createWorldLoad(Sponge.getGame(), (org.spongepowered.api.world.World) world));
        if (!mcServer.isSinglePlayer()) {
            world.getWorldInfo().setGameType(mcServer.getGameType());
        }

        mcServer.setDifficultyForAllWorlds(mcServer.getDifficulty());
    }

    public static int getClientDimensionToSend(int dim, WorldServer worldserver, EntityPlayerMP playerIn) {
        if (!((IMixinEntityPlayerMP) playerIn).isCustomPlayer()) {
            if (((Dimension) worldserver.provider).getType().equals(DimensionTypes.NETHER)) {
                dim = -1;
            } else if (((Dimension) worldserver.provider).getType().equals(DimensionTypes.END)) {
                dim = 1;
            } else {
                dim = 0;
            }
        }

        return dim;
    }

    public static void sendDimensionRegistration(WorldServer worldserver, EntityPlayerMP playerIn, int dim) {
//        // register dimension on client-side
//        FMLEmbeddedChannel serverChannel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
//        serverChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
//        serverChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(playerIn);
//        serverChannel.writeOutbound(new ForgeMessage.DimensionRegisterMessage(dimension,
//                ((SpongeDimensionType) ((Dimension) worldserver.provider).getType()).getDimensionTypeId()));
    }
}
