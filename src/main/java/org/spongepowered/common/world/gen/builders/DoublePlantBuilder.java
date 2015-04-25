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

import com.google.common.collect.Lists;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import org.spongepowered.api.data.type.DoubleSizePlantType;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.util.weighted.WeightedObject;
import org.spongepowered.api.world.gen.populator.DoublePlant;
import org.spongepowered.api.world.gen.populator.DoublePlant.Builder;

import java.util.Collection;
import java.util.List;


public class DoublePlantBuilder implements DoublePlant.Builder {
    
    private List<WeightedObject<DoubleSizePlantType>> types;
    private VariableAmount count;
    
    public DoublePlantBuilder() {
        reset();
    }

    @Override
    public Builder types(WeightedObject<DoubleSizePlantType>... types) {
        checkNotNull(types, "types");
        this.types.clear();
        for (WeightedObject<DoubleSizePlantType> type : types) {
            if (type != null) {
                this.types.add(type);
            }
        }
        return this;
    }

    @Override
    public Builder types(Collection<WeightedObject<DoubleSizePlantType>> types) {
        checkNotNull(types, "types");
        this.types.clear();
        for (WeightedObject<DoubleSizePlantType> type : types) {
            if (type != null) {
                this.types.add(type);
            }
        }
        return this;
    }

    @Override
    public Builder perChunk(VariableAmount count) {
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
    public DoublePlant build() throws IllegalStateException {
        if(this.types.isEmpty()) {
            throw new IllegalStateException("Builder is missing required weighted plant types.");
        }
        DoublePlant populator = (DoublePlant) new WorldGenDoublePlant();
        populator.getPossibleTypes().addAll(this.types);
        populator.setPlantsPerChunk(this.count);
        return populator;
    }

}
