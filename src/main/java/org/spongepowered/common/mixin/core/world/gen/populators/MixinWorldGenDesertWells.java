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
package org.spongepowered.common.mixin.core.world.gen.populators;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.gen.populator.DesertWell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WorldGenDesertWells.class)
public abstract class MixinWorldGenDesertWells implements DesertWell
{
    
    @Shadow public abstract boolean generate(World worldIn, Random rand, BlockPos position);

    private double spawnProbability;

    @Inject(method = "<init>(I)V", at = @At("RETURN"))
    public void onConstructed(int type, CallbackInfo ci) {
        this.spawnProbability = 0.001;
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        World world = (World) chunk.getWorld();
        Vector3i min = chunk.getBlockMin();
        BlockPos chunkPos = new BlockPos(min.getX(), min.getY(), min.getZ());
        
        if (random.nextDouble() < this.spawnProbability)
        {
            int x = random.nextInt(16) + 8;
            int z = random.nextInt(16) + 8;
            generate(world, random, world.getHeight(chunkPos.add(x, 0, z)).up());
        }
    }

    @Override
    public double getSpawnProbability() {
        return this.spawnProbability;
    }

    @Override
    public void setSpawnProbability(double p) {
        this.spawnProbability = p;
    }

}
