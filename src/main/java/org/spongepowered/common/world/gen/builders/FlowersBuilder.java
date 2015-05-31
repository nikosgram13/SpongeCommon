package org.spongepowered.common.world.gen.builders;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenFlowers;

import org.spongepowered.api.data.type.PlantType;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.util.weighted.WeightedObject;
import org.spongepowered.api.world.gen.populator.Flowers;
import org.spongepowered.api.world.gen.populator.Flowers.Builder;

import com.google.common.collect.Lists;

public class FlowersBuilder implements Flowers.Builder {

	private List<WeightedObject<PlantType>> flowers;
	private VariableAmount count;

	public FlowersBuilder() {
		reset();
	}

	@Override
	public Builder perChunk(VariableAmount count) {
		this.count = count;
		return this;
	}

	@Override
	public Builder types(WeightedObject<PlantType>... types) {
		this.flowers.clear();
		for(WeightedObject<PlantType> type: types) {
			if(type != null && type.get() != null) {
				this.flowers.add(type);
			}
		}
		return this;
	}

	@Override
	public Builder types(Collection<WeightedObject<PlantType>> types) {
        this.flowers.clear();
        for(WeightedObject<PlantType> type: types) {
            if(type != null && type.get() != null) {
                this.flowers.add(type);
            }
        }
        return this;
	}

	@Override
	public Builder reset() {
		this.flowers = Lists.newArrayList();
		this.count = VariableAmount.fixed(2);
		return this;
	}

	@Override
	public Flowers build() throws IllegalStateException {
		// these values passed in really don't matter, they are set immediately
		// before the populator is first called from the chunk
		Flowers populator = (Flowers) new WorldGenFlowers(Blocks.yellow_flower, BlockFlower.EnumFlowerType.DANDELION);
		populator.setFlowersPerChunk(this.count);
		populator.getFlowerTypes().addAll(this.flowers);
		return populator;
	}

}
