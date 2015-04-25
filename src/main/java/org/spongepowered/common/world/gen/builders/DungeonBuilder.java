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

import net.minecraft.world.gen.feature.WorldGenDungeons;
import org.spongepowered.api.data.manipulator.MobSpawnerData;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.util.weighted.WeightedCollection;
import org.spongepowered.api.util.weighted.WeightedEntity;
import org.spongepowered.api.util.weighted.WeightedItem;
import org.spongepowered.api.world.gen.populator.Dungeon;
import org.spongepowered.api.world.gen.populator.Dungeon.Builder;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.interfaces.IWorldGenDungeons;

import java.util.Collection;

public class DungeonBuilder implements Dungeon.Builder {

    private VariableAmount attempts;
    private VariableAmount count;
    private MobSpawnerData data;
    private WeightedCollection<WeightedItem> items;

    public DungeonBuilder() {
        reset();
    }

    @Override
    public Builder attempts(VariableAmount attempts) {
        this.attempts = attempts;
        return this;
    }

    @Override
    public Builder mobSpawnerData(MobSpawnerData data) {
        this.data = data;
        return this;
    }

    @Override
    public Builder minimumSpawnDelay(short delay) {
        this.data.setMinimumSpawnDelay(delay);
        return this;
    }

    @Override
    public Builder maximumSpawnDelay(short delay) {
        this.data.setMaximumSpawnDelay(delay);
        return this;
    }

    @Override
    public Builder spawnCount(short count) {
        this.data.setSpawnCount(count);
        return this;
    }

    @Override
    public Builder maximumNearbyEntities(short count) {
        this.data.setMaximumNearbyEntities(count);
        return this;
    }

    @Override
    public Builder requiredPlayerRange(short range) {
        this.data.setRequiredPlayerRange(range);
        return this;
    }

    @Override
    public Builder spawnRange(short range) {
        this.data.setSpawnRange(range);
        return this;
    }

    @Override
    public Builder possibleEntities(WeightedEntity... entities) {
        this.data.getPossibleEntitiesToSpawn().clear();
        for (WeightedEntity entity : entities) {
            if (entity != null && entity.get() != null) {
                this.data.getPossibleEntitiesToSpawn().add(entity);
            }
        }
        return this;
    }

    @Override
    public Builder possibleEntities(Collection<WeightedEntity> entities) {
        this.data.getPossibleEntitiesToSpawn().clear();
        for (WeightedEntity entity : entities) {
            if (entity != null && entity.get() != null) {
                this.data.getPossibleEntitiesToSpawn().add(entity);
            }
        }
        return this;
    }

    @Override
    public Builder possibleItems(WeightedItem... items) {
        this.items.clear();
        for (WeightedItem item : items) {
            if (item != null && item.get() != null) {
                this.items.add(item);
            }
        }
        return this;
    }

    @Override
    public Builder possibleItems(Collection<WeightedItem> items) {
        this.items.clear();
        for (WeightedItem item : items) {
            if (item != null && item.get() != null) {
                this.items.add(item);
            }
        }
        return this;
    }

    @Override
    public Builder itemsQuantity(VariableAmount count) {
        this.count = count;
        return this;
    }

    @Override
    public Builder reset() {
        this.attempts = VariableAmount.fixed(8);
        this.count = VariableAmount.fixed(8);
        this.data = Sponge.getSpongeRegistry().getManipulatorRegistry().getBuilder(MobSpawnerData.class).get().create();
        this.items = new WeightedCollection<WeightedItem>();
        return this;
    }

    @Override
    public Dungeon build() throws IllegalStateException {
        Dungeon populator = (Dungeon) new WorldGenDungeons();
        populator.setAttemptsPerChunk(this.attempts);
        ((IWorldGenDungeons) populator).setSpawnerData(this.data);
        populator.setNumberOfItems(this.count);
        populator.getPossibleContents().addAll(this.items);
        return populator;
    }

}
