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
package org.spongepowered.common.mixin.core.world.biome;

import org.spongepowered.api.util.VariableAmount;

import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.GroundCoverLayer;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.PopulatorFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.world.gen.NoiseGroundCoverDepth;

import java.util.List;
import java.util.Random;

@NonnullByDefault
@Mixin(BiomeGenBase.class)
public abstract class MixinBiomeGenBase implements BiomeType {

    @Shadow public String biomeName;
    @Shadow public float temperature;
    @Shadow public float rainfall;
    @Shadow public float minHeight;
    @Shadow public float maxHeight;
    @Shadow public IBlockState topBlock;
    @Shadow public IBlockState fillerBlock;
    @Shadow public BiomeDecorator theBiomeDecorator;

    protected List<Populator> populators;
    protected List<GeneratorPopulator> genpopulators;
    protected List<GroundCoverLayer> groundcover;

    @Inject(method = "<init>(IZ)V", at = @At("RETURN"))
    private void onConstructed(int id, boolean register, CallbackInfo ci) {
        this.populators = Lists.newArrayList();
        this.genpopulators = Lists.newArrayList();
        this.groundcover = Lists.newArrayList();
        this.buildPopulators();
    }

    protected void buildPopulators() {
        this.groundcover.add(new GroundCoverLayer((BlockState) this.topBlock, new NoiseGroundCoverDepth()));
        this.groundcover.add(new GroundCoverLayer((BlockState) this.fillerBlock, new NoiseGroundCoverDepth()));

        PopulatorFactory factory = Sponge.getSpongeRegistry().getPopulatorFactory();

        // TODO move to chunk provider override
//        this.populators.add(new OrePopulator.DirtPopulator());
//        this.populators.add(new OrePopulator.GravelPopulator());
//        this.populators.add(new OrePopulator.DioritePopulator());
//        this.populators.add(new OrePopulator.GranitePopulator());
//        this.populators.add(new OrePopulator.AndesitePopulator());
//        this.populators.add(new OrePopulator.CoalPopulator());
//        this.populators.add(new OrePopulator.IronPopulator());
//        this.populators.add(new OrePopulator.GoldPopulator());
//        this.populators.add(new OrePopulator.RedstonePopulator());
//        this.populators.add(new OrePopulator.DiamondPopulator());
//        this.populators.add(new OrePopulator.LapisPopulator());

        if (this.theBiomeDecorator.sandPerChunk2 > 0) {
            this.populators.add(factory.createSeaFloorPopulator()
                    .block(BlockTypes.SAND.getDefaultState())
                    .perChunk(VariableAmount.fixed(this.theBiomeDecorator.sandPerChunk2))
                    .radius(VariableAmount.fixed(7))
                    .replace(BlockTypes.DIRT, BlockTypes.GRASS)
                    .build());
        }
        if (this.theBiomeDecorator.clayPerChunk > 0) {
            this.populators.add(factory.createSeaFloorPopulator()
                    .block(BlockTypes.CLAY.getDefaultState())
                    .perChunk(VariableAmount.fixed(this.theBiomeDecorator.clayPerChunk))
                    .radius(VariableAmount.fixed(4))
                    .replace(BlockTypes.DIRT, BlockTypes.CLAY)
                    .build());
        }
        if (this.theBiomeDecorator.sandPerChunk > 0) {
            this.populators.add(factory.createSeaFloorPopulator()
                    .block(BlockTypes.SAND.getDefaultState())
                    .perChunk(VariableAmount.fixed(this.theBiomeDecorator.sandPerChunk))
                    .radius(VariableAmount.fixed(6))
                    .replace(BlockTypes.DIRT, BlockTypes.GRASS)
                    .build());
        }
        if (this.theBiomeDecorator.treesPerChunk > 0) {
            // this.populators.add(new
            // TreePopulator(this.theBiomeDecorator.treesPerChunk));
        }
        if (this.theBiomeDecorator.bigMushroomsPerChunk > 0) {
            // this.populators.add(new
            // BigMushroomPopulator(this.theBiomeDecorator.bigMushroomsPerChunk));
        }
        if (this.theBiomeDecorator.flowersPerChunk > 0) {
            // this.populators.add(new
            // FlowerPopulator(this.theBiomeDecorator.flowersPerChunk));
        }
        if (this.theBiomeDecorator.grassPerChunk > 0) {
            // this.populators.add(new
            // TallGrassPopulator(this.theBiomeDecorator.grassPerChunk));
        }
        if (this.theBiomeDecorator.deadBushPerChunk > 0) {
            // this.populators.add(new
            // DeadBushPopulator(this.theBiomeDecorator.deadBushPerChunk));
        }
        if (this.theBiomeDecorator.waterlilyPerChunk > 0) {
            // this.populators.add(new
            // WaterLilyPopulator(this.theBiomeDecorator.waterlilyPerChunk));
        }
        if (this.theBiomeDecorator.mushroomsPerChunk > 0) {
            // this.populators.add(new
            // SmallMushroomPopulator(this.theBiomeDecorator.mushroomsPerChunk));
        }
        if (this.theBiomeDecorator.reedsPerChunk > 0) {
            // this.populators.add(new
            // ReedPopulator(this.theBiomeDecorator.reedsPerChunk));
        }
        // this.populators.add(new PumpkinPopulator());
        if (this.theBiomeDecorator.cactiPerChunk > 0) {
            // this.populators.add(new
            // CactusPopulator(this.theBiomeDecorator.cactiPerChunk));
        }
        if (this.theBiomeDecorator.generateLakes) {
            // this.populators.add(new LiquidsPopulator());
        }

    }

    @Override
    public String getName() {
        return this.biomeName;
    }

    @Override
    public double getTemperature() {
        return this.temperature;
    }

    @Override
    public double getHumidity() {
        return this.rainfall;
    }

    /**
     * 
     * We overwrite the placement of the biome top and filler blocks in order to
     * defer to our ground cover list, which we earlier seeded with the default
     * top and filler blocks for regularly registered biomes.
     * 
     * @author Deamon
     */
    @Overwrite
    public void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunk, int x, int z, double stoneNoise) {
        IBlockState currentPlacement = this.topBlock;
        int k = -1;
        int relativeX = x & 15;
        int relativeZ = z & 15;
        int i = 0;
        for (int currentY = 255; currentY >= 0; --currentY) {
            if (currentY <= rand.nextInt(5)) {
                chunk.setBlockState(relativeZ, currentY, relativeX, Blocks.bedrock.getDefaultState());
            } else {
                IBlockState nextBlock = chunk.getBlockState(relativeZ, currentY, relativeX);

                if (nextBlock.getBlock().getMaterial() == Material.air) {
                    k = -1;
                } else if (nextBlock.getBlock() == Blocks.stone) {
                    if (k == -1) {
                        if (this.groundcover.isEmpty()) {
                            k = 0;
                            continue;
                        }
                        i = 0;
                        GroundCoverLayer layer = this.groundcover.get(i);
                        currentPlacement = (IBlockState) layer.getBlockState();
                        k = layer.getDepth().getFlooredAmount(rand, stoneNoise);
                        if (k <= 0) {
                            continue;
                        }

                        if (currentY >= 62) {
                            chunk.setBlockState(relativeZ, currentY, relativeX, currentPlacement);
                            ++i;
                            if (i < this.groundcover.size()) {
                                layer = this.groundcover.get(i);
                                k = layer.getDepth().getFlooredAmount(rand, stoneNoise);
                                currentPlacement = (IBlockState) layer.getBlockState();
                            }
                        } else if (currentY < 56 - k) {
                            k = 0;
                            chunk.setBlockState(relativeZ, currentY, relativeX, Blocks.gravel.getDefaultState());
                        } else {
                            ++i;
                            if (i < this.groundcover.size()) {
                                layer = this.groundcover.get(i);
                                k = layer.getDepth().getFlooredAmount(rand, stoneNoise);
                                currentPlacement = (IBlockState) layer.getBlockState();
                                chunk.setBlockState(relativeZ, currentY, relativeX, currentPlacement);
                            }
                        }
                    } else if (k > 0) {
                        --k;
                        chunk.setBlockState(relativeZ, currentY, relativeX, currentPlacement);

                        if (k == 0) {
                            ++i;
                            if (i < this.groundcover.size()) {
                                GroundCoverLayer layer = this.groundcover.get(i);
                                k = layer.getDepth().getFlooredAmount(rand, stoneNoise);
                                currentPlacement = (IBlockState) layer.getBlockState();
                            }
                        }
                    }
                }
            }
        }
    }

}
