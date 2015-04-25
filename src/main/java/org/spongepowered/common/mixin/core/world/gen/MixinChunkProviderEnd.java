/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
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

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.common.Sponge;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Random;

@Mixin(ChunkProviderEnd.class)
public abstract class MixinChunkProviderEnd implements IChunkProvider {

    @Shadow private int chunkX;
    @Shadow private int chunkZ;
    @Shadow private World endWorld;
    @Shadow private Random endRNG;

    /**
     * We overwrite this function as the functionality of replacing the stone
     * blocks with end stone has been moved to a genpop
     * (EndBiomeGeneratorPopulator) attached to BiomeGenEnd.
     * 
     * @author Deamon
     */
    @Overwrite
    public void func_180519_a(ChunkPrimer primer) {
        // BEGIN removed code
//        for (int i = 0; i < 16; ++i)
//        {
//            for (int j = 0; j < 16; ++j)
//            {
//                byte b0 = 1;
//                int k = -1;
//                IBlockState iblockstate = Blocks.end_stone.getDefaultState();
//                IBlockState iblockstate1 = Blocks.end_stone.getDefaultState();
//
//                for (int l = 127; l >= 0; --l)
//                {
//                    IBlockState iblockstate2 = p_180519_1_.getBlockState(i, l, j);
//
//                    if (iblockstate2.getBlock().getMaterial() == Material.air)
//                    {
//                        k = -1;
//                    }
//                    else if (iblockstate2.getBlock() == Blocks.stone)
//                    {
//                        if (k == -1)
//                        {
//                            if (b0 <= 0)
//                            {
//                                iblockstate = Blocks.air.getDefaultState();
//                                iblockstate1 = Blocks.end_stone.getDefaultState();
//                            }
//
//                            k = b0;
//
//                            if (l >= 0)
//                            {
//                                p_180519_1_.setBlockState(i, l, j, iblockstate);
//                            }
//                            else
//                            {
//                                p_180519_1_.setBlockState(i, l, j, iblockstate1);
//                            }
//                        }
//                        else if (k > 0)
//                        {
//                            --k;
//                            p_180519_1_.setBlockState(i, l, j, iblockstate1);
//                        }
//                    }
//                }
//            }
//        }
        // END removed code
    }

    /**
     * 
     * We overwrite this chunk in order to replace the call to the end biome
     * decorator with a call to our biome populators for the correct chunk.
     * 
     * @author Deamon
     */
    @Override
    @Overwrite
    public void populate(IChunkProvider provider, int x, int z) {
        BlockFalling.fallInstantly = true;

        // BEGIN sponge code
        Chunk chunk = (Chunk) this.endWorld.getChunkFromChunkCoords(x, z);
        if (chunk == null) {
            throw new NullPointerException("Failed to fetch chunk at (" + x + "," + z + ") during population.");
        }
        //TODO get pop from gen override
        /*List<Populator> populators = ((org.spongepowered.api.world.World) this.endWorld).getBiome(x * 16 + 15, z * 16 + 15).getPopulators();
        Sponge.getGame().getEventManager().post(SpongeEventFactory.createChunkPrePopulate(Sponge.getGame(), chunk, populators));
        for (Populator populator : populators) {
            populator.populate(chunk, this.endRNG);
        }*/
        Sponge.getGame().getEventManager().post(SpongeEventFactory.createChunkPostPopulate(Sponge.getGame(), chunk));

        // END sponge code

        // BEGIN removed code

//        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(provider, this.endWorld, this.endWorld.rand, x, z, false));
//        BlockPos blockpos = new BlockPos(p_73153_2_ * 16, 0, p_73153_3_ * 16);
//        this.endWorld.getBiomeGenForCoords(blockpos.add(16, 0, 16)).decorate(this.endWorld, this.endWorld.rand, blockpos);
//        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(provider, this.endWorld, this.endWorld.rand, x, z, false));

        // END removed code

        BlockFalling.fallInstantly = false;
    }

}
