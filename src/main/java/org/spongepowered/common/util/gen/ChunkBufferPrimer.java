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
package org.spongepowered.common.util.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.gen.MutableBlockBuffer;

/**
 * Wraps a {@link MutableBlockBuffer} within a ChunkPrimer in order to be able
 * to pass the block buffer to vanilla populators.
 */
public class ChunkBufferPrimer extends ChunkPrimer {

    private final MutableBlockBuffer buffer;

    public ChunkBufferPrimer(MutableBlockBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        return (IBlockState) this.buffer.getBlock(x + this.buffer.getBlockMin().getX(), y + this.buffer.getBlockMin().getY(), z
                + this.buffer.getBlockMin().getZ());
    }

    @Override
    public IBlockState getBlockState(int index) {
        return getBlockState((index >> 12) & 0xf, (index) & 0xff, (index >> 8) & 0xf);
    }

    @Override
    public void setBlockState(int x, int y, int z, IBlockState state) {
        this.buffer.setBlock(x + this.buffer.getBlockMin().getX(), y + this.buffer.getBlockMin().getY(), z + this.buffer.getBlockMin().getZ(),
                (BlockState) state);
    }

    @Override
    public void setBlockState(int index, IBlockState state) {
        setBlockState((index >> 12) & 0xf, (index) & 0xff, (index >> 8) & 0xf, state);
    }

}
