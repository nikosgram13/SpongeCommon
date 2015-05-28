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
package org.spongepowered.common.world.type;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.gen.ChunkProviderDebug;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

import java.util.Arrays;

public abstract class SpongeWorldType extends WorldType {

    private static int getNextID()
    {
        for (int x = 0; x < worldTypes.length; x++)
        {
            if (worldTypes[x] == null)
            {
                return x;
            }
        }

        int oldLen = worldTypes.length;
        worldTypes = Arrays.copyOf(worldTypes, oldLen + 16);
        return oldLen;
    }

    protected SpongeWorldType(String name) {
        super(getNextID(), name);
    }

    public net.minecraft.world.biome.WorldChunkManager getChunkManager(World world) {
        if (this == FLAT) {
            final FlatGeneratorInfo flatgeneratorinfo = FlatGeneratorInfo.createFlatGeneratorFromString(world.getWorldInfo().getGeneratorOptions());
            return new WorldChunkManagerHell(
                    BiomeGenBase.getBiomeFromBiomeList(flatgeneratorinfo.getBiome(), net.minecraft.world.biome.BiomeGenBase.field_180279_ad), 0.5F);
        }
        else if (this == DEBUG_WORLD) {
            return new WorldChunkManagerHell(net.minecraft.world.biome.BiomeGenBase.plains, 0.0F);
        }
        else {
            return new WorldChunkManager(world);
        }
    }

    public net.minecraft.world.chunk.IChunkProvider getChunkGenerator(World world, String generatorOptions) {
        if (this == FLAT) {
            return new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(),
                    generatorOptions);
        }
        if (this == DEBUG_WORLD) {
            return new ChunkProviderDebug(world);
        }
        return new ChunkProviderGenerate(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
    }
}
