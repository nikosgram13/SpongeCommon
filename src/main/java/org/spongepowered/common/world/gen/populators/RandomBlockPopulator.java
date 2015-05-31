/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
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
package org.spongepowered.common.world.gen.populators;

import net.minecraft.world.World;

import org.spongepowered.api.block.BlockTypes;
import com.google.common.base.Predicate;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;

import java.util.Random;

import org.spongepowered.api.world.gen.populator.RandomBlock;

public class RandomBlockPopulator implements RandomBlock {

    public static final Predicate<Location> NETHER_FIRE = new Predicate<Location>() {

        @Override
        public boolean apply(Location input) {
            if (input.getState().getType() == BlockTypes.AIR && input.add(0, -1, 0).getState().getType() == BlockTypes.NETHERRACK) {
                return true;
            }
            return false;
        }

    };

    public static final Predicate<Location> CAVE_LIQUIDS = new Predicate<Location>() {

        @Override
        public boolean apply(Location input) {
            if (input.add(0, 1, 0).getState().getType() != BlockTypes.STONE || input.add(0, -1, 0).getState().getType() != BlockTypes.STONE
                    || (input.getState().getType() != BlockTypes.STONE && input.getState().getType() != BlockTypes.AIR)) {
                return false;
            }
            int air = 0;
            int stone = 0;
            if (input.add(1, 0, 0).getState().getType() == BlockTypes.STONE || input.add(-1, 0, 0).getState().getType() == BlockTypes.STONE
                    || input.add(0, 0, 1).getState().getType() == BlockTypes.STONE || input.add(0, 0, -1).getState().getType() == BlockTypes.STONE) {
                stone++;
            }
            if (input.add(1, 0, 0).getState().getType() == BlockTypes.AIR || input.add(-1, 0, 0).getState().getType() == BlockTypes.AIR
                    || input.add(0, 0, 1).getState().getType() == BlockTypes.AIR || input.add(0, 0, -1).getState().getType() == BlockTypes.AIR) {
                air++;
            }
            if(air == 1 && stone == 3) {
                return true;
            }
            return false;
        }

    };
    
    private VariableAmount count;
    private VariableAmount height;
    private Predicate<Location> check;
    private BlockState state;
    
    public RandomBlockPopulator() {
        this.count = VariableAmount.fixed(64);
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        int n = this.count.getFlooredAmount(random);
        Location chunkMin = new Location(chunk.getWorld(), chunk.getBlockMin().getX(), chunk.getBlockMin().getY(), chunk.getBlockMin().getZ());
        for(int i = 0; i < n; i++) {
            Location pos = chunkMin.add(random.nextInt(16) + 8, this.height.getFlooredAmount(random), random.nextInt(16) + 8);
            if(this.check.apply(pos)) {
                chunk.getWorld().setBlock(pos.getBlockPosition(), this.state);
            }
        }
    }

    @Override
    public BlockState getBlock() {
        return this.state;
    }

    @Override
    public void setBlock(BlockState block) {
        this.state = block;
    }

    @Override
    public VariableAmount getAttemptsPerChunk() {
        return this.count;
    }

    @Override
    public void setAttemptsPerChunk(VariableAmount count) {
        this.count = count;
    }

    @Override
    public Predicate<Location> getPlacementTarget() {
        return this.check;
    }

    @Override
    public void getPlacementTarget(Predicate<Location> target) {
        this.check = target;
    }

    @Override
    public VariableAmount getHeightRange() {
        return this.height;
    }

    @Override
    public void setHeightRange(VariableAmount height) {
        this.height = height;
    }

}
