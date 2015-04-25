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
package org.spongepowered.common.util.weighted;

import org.spongepowered.api.item.ItemTypes;

import com.google.common.collect.Lists;
import org.spongepowered.api.util.VariableAmount;
import net.minecraft.init.Items;
import net.minecraft.util.WeightedRandomChestContent;
import org.spongepowered.api.data.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.util.weighted.WeightedItem;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class WeightedEnchantmentBook extends WeightedItem {

    public WeightedEnchantmentBook(VariableAmount quantity, int weight) {
        super(ItemTypes.ENCHANTED_BOOK, quantity, weight);
    }

    @Override
    public Collection<ItemStack> getRandomItem(ItemStackBuilder builder, Random rand, int maxStacks) {
        WeightedRandomChestContent book = Items.enchanted_book.getRandom(rand);
        ItemStack stack = (ItemStack) book.theItemId;

        int total = getQuantity().getFlooredAmount(rand);
        if (total <= 0) {
            return Lists.newArrayList();
        }
        ItemType type = get();
        int max = type.getMaxStackQuantity();
        if (total / max > maxStacks) {
            total = maxStacks * max;
        }
        List<ItemStack> result = Lists.newArrayList();
        for (int i = 0; i < total;) {
            int n = (i + type.getMaxStackQuantity() > total) ? total - i : type.getMaxStackQuantity();
            builder.reset().fromItemStack(stack).quantity(n);
            result.add(builder.build());
            i += n;
        }
        return result;
    }

}
