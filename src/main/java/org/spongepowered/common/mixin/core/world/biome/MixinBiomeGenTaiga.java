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
package org.spongepowered.common.mixin.core.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenTaiga;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.gen.populator.BlockBlob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.Sponge;

@Mixin(BiomeGenTaiga.class)
public abstract class MixinBiomeGenTaiga extends MixinBiomeGenBase {

    @Shadow private int field_150644_aH; // type

    @Override
    protected void buildPopulators() {
        if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
            BlockBlob blob = Sponge.getSpongeRegistry().getPopulatorFactory().createBlockBlockPopulator()
                    .block((BlockState) Blocks.cobblestone.getDefaultState())
                    .blockCount(VariableAmount.fixed(3))
                    .radius(VariableAmount.baseWithVariance(-1, 1))
                    .build();
            this.populators.add(blob);
        }
        // this.populators.add(new DoublePlantPopulator(7, 1, 1,
        // BlockDoublePlant.EnumPlantType.FERN));
        super.buildPopulators();
    }
}
