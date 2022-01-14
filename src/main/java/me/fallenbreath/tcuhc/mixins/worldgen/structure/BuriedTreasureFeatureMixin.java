package me.fallenbreath.tcuhc.mixins.worldgen.structure;

import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BuriedTreasureFeature.class)
public abstract class BuriedTreasureFeatureMixin
{
	@ModifyConstant(method = "addPieces", constant = @Constant(intValue = 9, ordinal = 0))
	private static int tweaksXOffset(int x, StructurePiecesCollector collector, StructurePiecesGenerator.Context<ProbabilityConfig> context)
	{
		return calcXOffset(context.chunkPos());
	}

	@ModifyConstant(method = "addPieces", constant = @Constant(intValue = 9, ordinal = 1))
	private static int tweaksZOffset(int z, StructurePiecesCollector collector, StructurePiecesGenerator.Context<ProbabilityConfig> context)
	{
		return calcZOffset(context.chunkPos());
	}

	@ModifyConstant(method = "getLocatedPos", constant = @Constant(intValue = 9, ordinal = 0))
	private int tweaksXOffset(int x, ChunkPos chunkPos)
	{
		return calcXOffset(chunkPos);
	}

	@ModifyConstant(method = "getLocatedPos", constant = @Constant(intValue = 9, ordinal = 1))
	private int tweaksZOffset(int z, ChunkPos chunkPos)
	{
		return calcZOffset(chunkPos);
	}

	@Unique
	private static int calcXOffset(ChunkPos chunkPos)
	{
		return magic(chunkPos) & 0xF;
	}

	@Unique
	private static int calcZOffset(ChunkPos chunkPos)
	{
		return (magic(chunkPos) >> 4) & 0xF;
	}

	@Unique
	private static int magic(ChunkPos chunkPos)
	{
		int hash = chunkPos.hashCode();
		return hash ^ (hash >> 8) ^ (hash >> 16) ^ (hash >> 24);
	}
}
