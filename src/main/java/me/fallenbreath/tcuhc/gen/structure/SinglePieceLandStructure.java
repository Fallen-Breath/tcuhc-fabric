package me.fallenbreath.tcuhc.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Random;

public abstract class SinglePieceLandStructure extends StructureFeature<DefaultFeatureConfig>
{
	protected SinglePieceLandStructure(Codec<DefaultFeatureConfig> configCodec, StructureGeneratorFactory<DefaultFeatureConfig> piecesGenerator, PostPlacementProcessor postPlacementProcessor)
	{
		super(configCodec, piecesGenerator, postPlacementProcessor);
	}

	public static boolean canGenerateIn(Biome biome)
	{
		return UhcStructures.CONTINENT_BIOMES.contains(biome.getCategory());
	}

	protected abstract static class Piece extends SimpleStructurePiece
	{
		private boolean positionAdjusted = false;

		public Piece(StructurePieceType type, StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation)
		{
			super(type, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), pos);
		}

		public Piece(StructurePieceType type, StructureManager manager, NbtCompound nbt)
		{
			super(type, nbt, manager, identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rotation"))));
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation)
		{
			return (new StructurePlacementData()).setRotation(rotation);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt)
		{
			super.writeNbt(context, nbt);
			nbt.putString("Rotation", this.placementData.getRotation().name());
		}

		@Override
		public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos)
		{
			this.adjustPosByTerrain(world);
			super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pos);
		}

		private synchronized void adjustPosByTerrain(StructureWorldAccess world)
		{
			if (this.positionAdjusted)
			{
				return;
			}
			int sumY = 0;
			Vec3i size = this.structure.getSize();
			Heightmap.Type type = Heightmap.Type.WORLD_SURFACE_WG;
			for (BlockPos blockPos : BlockPos.iterate(this.boundingBox.getMinX(), 0, this.boundingBox.getMinZ(), this.boundingBox.getMaxX(), 0, this.boundingBox.getMaxZ()))
			{
				sumY += world.getTopY(type, blockPos.getX(), blockPos.getZ());
			}
			int avgY = sumY / (size.getX() * size.getZ());
			this.pos = new BlockPos(this.pos.getX(), avgY, this.pos.getZ());
			this.positionAdjusted = true;
		}
	}
}
