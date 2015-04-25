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
package org.spongepowered.common.world.gen.builders;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.world.gen.feature.WorldGenBigMushroom;

import com.google.common.collect.Lists;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.util.weighted.WeightedObject;
import org.spongepowered.api.world.gen.populator.BigMushroom;
import org.spongepowered.api.world.gen.populator.BigMushroom.Builder;
import org.spongepowered.api.world.gen.type.MushroomType;

import java.util.Collection;
import java.util.List;

public class BigMushroomBuilder implements BigMushroom.Builder {

    private List<WeightedObject<MushroomType>> types;
    private VariableAmount count;

    public BigMushroomBuilder() {
        reset();
    }

    @Override
    public Builder types(WeightedObject<MushroomType>... types) {
        checkNotNull(types, "types");
        this.types.clear();
        for (WeightedObject<MushroomType> type : types) {
            if (type != null) {
                this.types.add(type);
            }
        }
        return this;
    }

    @Override
    public Builder types(Collection<WeightedObject<MushroomType>> types) {
        checkNotNull(types, "types");
        this.types.clear();
        for (WeightedObject<MushroomType> type : types) {
            if (type != null) {
                this.types.add(type);
            }
        }
        return this;
    }

    @Override
    public Builder mushroomsPerChunk(VariableAmount count) {
        checkNotNull(count, "count");
        this.count = count;
        return this;
    }

    @Override
    public Builder reset() {
        this.types = Lists.newArrayList();
        this.count = VariableAmount.fixed(1);
        return this;
    }

    @Override
    public BigMushroom build() throws IllegalStateException {
        BigMushroom populator = (BigMushroom) new WorldGenBigMushroom();
        populator.getType().addAll(this.types);
        populator.setMushroomsPerChunk(this.count);
        return populator;
    }

}
