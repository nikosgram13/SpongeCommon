/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <http://www.spongepowered.org>
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
package org.spongepowered.common.mixin.core.world.gen;

import org.spongepowered.common.interfaces.gen.IFlaggedPopulator;

import org.spongepowered.common.interfaces.gen.IPopulatorOwner;
import org.spongepowered.common.world.gen.populators.AnimalPopulator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.interfaces.IMixinWorld;

import java.util.List;
import java.util.Random;

@Mixin(ChunkProviderGenerate.class)
public abstract class MixinChunkProviderGenerate implements IChunkProvider, IPopulatorOwner {

    // @formatter:off

    @Shadow private Random rand;
    @Shadow private World worldObj;
    @Shadow private boolean mapFeaturesEnabled;
    @Shadow private BiomeGenBase[] biomesForGeneration;
    @Shadow private ChunkProviderSettings settings;
    @Shadow private MapGenBase caveGenerator;
    @Shadow private MapGenStronghold strongholdGenerator;
    @Shadow private MapGenVillage villageGenerator;
    @Shadow private MapGenMineshaft mineshaftGenerator;
    @Shadow private MapGenScatteredFeature scatteredFeatureGenerator;
    @Shadow private MapGenBase ravineGenerator;
    @Shadow private StructureOceanMonument oceanMonumentGenerator;

    @Shadow public abstract void setBlocksInChunk(int x, int z, ChunkPrimer chunk);
    @Shadow public abstract void replaceBlocksForBiome(int x, int z, ChunkPrimer chunk, BiomeGenBase[] biomes);
    
    // @formatter:on

    private List<Populator> populators;
    private List<GeneratorPopulator> genpopulators;

    @Override
    public ImmutableList<Populator> getPopulators() {
        return ImmutableList.copyOf(this.populators);
    }

    @Override
    public ImmutableList<GeneratorPopulator> getGeneratorPopulators() {
        return ImmutableList.copyOf(this.genpopulators);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;JZLjava/lang/String;)V", at = @At("RETURN"))
    public void onConstructed(World worldIn, long seed, boolean mapFeatures, String generatorOptions, CallbackInfo ci) {
        this.populators = Lists.newArrayList();
        this.genpopulators = Lists.newArrayList();

        if (this.settings.useCaves) {
            this.genpopulators.add((GeneratorPopulator) this.caveGenerator);
        }

        if (this.settings.useRavines) {
            this.genpopulators.add((GeneratorPopulator) this.ravineGenerator);
        }

        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.genpopulators.add((GeneratorPopulator) this.mineshaftGenerator);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.genpopulators.add((GeneratorPopulator) this.villageGenerator);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.genpopulators.add((GeneratorPopulator) this.strongholdGenerator);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.genpopulators.add((GeneratorPopulator) this.scatteredFeatureGenerator);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.genpopulators.add((GeneratorPopulator) this.oceanMonumentGenerator);
        }

        // BEGIN populators

        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.populators.add((Populator) this.mineshaftGenerator);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.populators.add((Populator) this.villageGenerator);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.populators.add((Populator) this.strongholdGenerator);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.populators.add((Populator) this.scatteredFeatureGenerator);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.populators.add((Populator) this.oceanMonumentGenerator);
        }

        if (this.settings.useWaterLakes) {
            this.populators.add((Populator) new WorldGenLakes(Blocks.water));
        }

        if (this.settings.useLavaLakes) {
            this.populators.add((Populator) new WorldGenLakes(Blocks.lava));
        }

        if (this.settings.useDungeons) {
            this.populators.add((Populator) new WorldGenDungeons());
        }

        this.populators.add(new AnimalPopulator());
        //this.populators.add(new SnowPopulator());
    }

    /**
     * 
     * This overwrites the provideChunk method in order to remove the standard
     * calls to GeneratorPopulators as these GeneratorPopulators have instead
     * been added to the genpopulator list from the injection into the
     * constructor {@link #onConstructed}.
     * 
     * @author Deamon
     */
    @Override
    @Overwrite
    public Chunk provideChunk(int x, int z) {
        this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();

        this.setBlocksInChunk(x, z, chunkprimer);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.replaceBlocksForBiome(x, z, chunkprimer, this.biomesForGeneration);

        // BEGIN populator removal
        // These populators are moved to the genpopulator list

//        if (this.settings.useCaves) {
//            this.caveGenerator.populate(this, this.worldObj, x, z, chunkprimer);
//        }
//
//        if (this.settings.useRavines) {
//            this.ravineGenerator.populate(this, this.worldObj, x, z, chunkprimer);
//        }
//
//        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
//            this.mineshaftGenerator.populate(this, this.worldObj, x, z, chunkprimer);
//        }
//
//        if (this.settings.useVillages && this.mapFeaturesEnabled) {
//            this.villageGenerator.populate(this, this.worldObj, x, z, chunkprimer);
//        }
//
//        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
//            this.strongholdGenerator.populate(this, this.worldObj, x, z, chunkprimer);
//        }
//
//        if (this.settings.useTemples && this.mapFeaturesEnabled) {
//            this.scatteredFeatureGenerator.populate(this, this.worldObj, x, z, chunkprimer);
//        }
//
//        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
//            this.oceanMonumentGenerator.populate(this, this.worldObj, x, z, chunkprimer);
//        }

        // END populator removal

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();

        for (int k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) this.biomesForGeneration[k].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    /**
     * @author Deamon
     * 
     *         This overwrites the populate method in order to remove the
     *         standard calls to Populators as these Populators have instead
     *         been added to the populator list from the injection into the
     *         constructor {@link #onConstructed}.
     */
    @Override
    @Overwrite
    public void populate(IChunkProvider provider, int x, int z) {
        BlockFalling.fallInstantly = true;
        BlockPos blockpos = new BlockPos(x * 16, 0, z * 16);
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        long seedRandX = this.rand.nextLong() / 2L * 2L + 1L;
        long seedRandZ = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long) x * seedRandX + (long) z * seedRandZ ^ this.worldObj.getSeed());
        boolean villageFlag = false;
//        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(x, z);
//        int k = x * 16;
//        int l = z * 16;
        // BEGIN sponge
        org.spongepowered.api.world.Chunk chunk = (org.spongepowered.api.world.Chunk) this.worldObj.getChunkFromChunkCoords(x, z);
        List<Populator> populators = ((IMixinWorld) this.worldObj).getPopulators();
        Sponge.getGame().getEventManager().post(SpongeEventFactory.createChunkPrePopulate(Sponge.getGame(), chunk, populators));

        List<String> flags = Lists.newArrayList();
        for (Populator populator : ((IMixinWorld) this.worldObj).getPopulators()) {
            if (populator instanceof IFlaggedPopulator) {
                ((IFlaggedPopulator) populator).populate(provider, chunk, this.rand, flags);
            } else {
                populator.populate(chunk, this.rand);
            }
        }
        //TODO get pop from gen override
        /*for (Populator populator : ((BiomeType) biomegenbase).getPopulators()) {
            populator.populate(chunk, this.rand);
        }*/
        // END sponge
        // BEGIN populator removal
        // These populator calls are instead done by the reference to the
        // populator list

//        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
//            this.mineshaftGenerator.func_175794_a(this.worldObj, this.rand, chunkcoordintpair);
//        }

//        if (this.settings.useVillages && this.mapFeaturesEnabled) {
//            villageFlag = this.villageGenerator.func_175794_a(this.worldObj, this.rand, chunkcoordintpair);
//        }

//        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
//            this.strongholdGenerator.func_175794_a(this.worldObj, this.rand, chunkcoordintpair);
//        }

//        if (this.settings.useTemples && this.mapFeaturesEnabled) {
//            this.scatteredFeatureGenerator.func_175794_a(this.worldObj, this.rand, chunkcoordintpair);
//        }

//        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
//            this.oceanMonumentGenerator.func_175794_a(this.worldObj, this.rand, chunkcoordintpair);
//        }

//        int k1;
//        int l1;
//        int i2;
//
//        if (biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills && this.settings.useWaterLakes && !flag
//                && this.rand.nextInt(this.settings.waterLakeChance) == 0 && TerrainGen.populate(chunk, worldObj, rand, x, z, flag, LAKE)) {
//            k1 = this.rand.nextInt(16) + 8;
//            l1 = this.rand.nextInt(256);
//            i2 = this.rand.nextInt(16) + 8;
//            (new WorldGenLakes(Blocks.water)).generate(this.worldObj, this.rand, blockpos.add(k1, l1, i2));
//        }
//        
//        if (TerrainGen.populate(chunk, worldObj, rand, x, z, flag, LAVA) && !flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0
//                && this.settings.useLavaLakes) {
//            k1 = this.rand.nextInt(16) + 8;
//            l1 = this.rand.nextInt(this.rand.nextInt(248) + 8);
//            i2 = this.rand.nextInt(16) + 8;
//
//            if (l1 < 63 || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
//                (new WorldGenLakes(Blocks.lava)).generate(this.worldObj, this.rand, blockpos.add(k1, l1, i2));
//            }
//        }
//        
//        if (this.settings.useDungeons) {
//            boolean doGen = TerrainGen.populate(chunk, worldObj, rand, x, z, flag, DUNGEON);
//            for (k1 = 0; doGen && k1 < this.settings.dungeonChance; ++k1) {
//                l1 = this.rand.nextInt(16) + 8;
//                i2 = this.rand.nextInt(256);
//                int j2 = this.rand.nextInt(16) + 8;
//                (new WorldGenDungeons()).generate(this.worldObj, this.rand, blockpos.add(l1, i2, j2));
//            }
//        }

//        biomegenbase.decorate(this.worldObj, this.rand, new BlockPos(k, 0, l));
//        if (TerrainGen.populate(chunk, worldObj, rand, x, z, flag, ANIMALS)) {
//            SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, k + 8, l + 8, 16, 16, this.rand);
//        }
//        blockpos = blockpos.add(8, 0, 8);

//        boolean doGen = TerrainGen.populate(chunk, worldObj, rand, x, z, flag, ICE);
//        for (k1 = 0; doGen && k1 < 16; ++k1) {
//            for (l1 = 0; l1 < 16; ++l1) {
//                BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k1, 0, l1));
//                BlockPos blockpos2 = blockpos1.down();
//
//                if (this.worldObj.func_175675_v(blockpos2)) {
//                    this.worldObj.setBlockState(blockpos2, Blocks.ice.getDefaultState(), 2);
//                }
//
//                if (this.worldObj.canSnowAt(blockpos1, true)) {
//                    this.worldObj.setBlockState(blockpos1, Blocks.snow_layer.getDefaultState(), 2);
//                }
//            }
//        }
        // END populator removal
        Sponge.getGame().getEventManager().post(SpongeEventFactory.createChunkPostPopulate(Sponge.getGame(), chunk));

        BlockFalling.fallInstantly = false;
    }

}
