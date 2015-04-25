package org.spongepowered.common.mixin.core.world.gen.populators;

import net.minecraft.world.gen.feature.WorldGenForest;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.util.weighted.WeightedCollection;
import org.spongepowered.api.util.weighted.WeightedObject;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.gen.populator.Forest;
import org.spongepowered.api.world.gen.type.BiomeTreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WorldGenForest.class)
public class MixinWorldGenForest implements Forest {
	

    @Inject(method = "<init>(ZZ)V", at = @At("RETURN"))
    public void onConstructed(boolean a, boolean b, CallbackInfo ci) {
    	
    }

	@Override
	public void populate(Chunk chunk, Random random) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public VariableAmount getTreesPerChunk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTreesPerChunk(VariableAmount count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WeightedCollection<WeightedObject<BiomeTreeType>> getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
