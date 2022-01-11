package me.fallenbreath.tcuhc.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class SinglePieceLandStructure<C extends FeatureConfig> extends StructureFeature<C>
{
	protected SinglePieceLandStructure(Codec<C> configCodec, StructureGeneratorFactory<C> piecesGenerator, PostPlacementProcessor postPlacementProcessor)
	{
		super(configCodec, piecesGenerator, postPlacementProcessor);
	}

	public static boolean canGenerateIn(Biome biome)
	{
		return UhcStructures.CONTINENT_BIOMES.contains(biome.getCategory());
	}

	protected static <FC extends FeatureConfig> BlockPos shiftStartPosRandomly(StructurePiecesGenerator.Context<FC> context)
	{
		return context.chunkPos().getStartPos().add(context.random().nextInt(16), 0, context.random().nextInt(16));
	}

	protected static <FC extends FeatureConfig> boolean isBiomeValidInChunk(StructureGeneratorFactory.Context<FC> context)
	{
		for (int x = context.chunkPos().getStartX(); x <= context.chunkPos().getEndX(); x++)
		{
			for (int z = context.chunkPos().getStartZ(); z <= context.chunkPos().getEndZ(); z++)
			{
				if (!isBiomeValid(context, new BlockPos(x, 0, z)))
				{
					return false;
				}
			}
		}
		return true;
	}

	protected static <FC extends FeatureConfig> boolean isBiomeValid(StructureGeneratorFactory.Context<FC> context, BlockPos pos)
	{
		int y = context.chunkGenerator().getHeightInGround(pos.getX(), pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world());
		Biome biome = context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(pos.getX()), BiomeCoords.fromBlock(y), BiomeCoords.fromBlock(pos.getY()));
		return context.validBiome().test(biome);
	}

	protected static void fillBottomAirGap(StructureWorldAccess world, Random random, BlockBox chunkBox, StructurePiecesList children, BiPredicate<BlockPos, BlockState> blockTester, Function<Random, BlockState> blockGetter, int yOffset)
	{
		int worldBottomY = world.getBottomY();
		BlockBox blockBox = children.getBoundingBox();
		int minY = blockBox.getMinY() + yOffset;
		BlockPos.Mutable blockPos = new BlockPos.Mutable();

		for (int x = chunkBox.getMinX(); x <= chunkBox.getMaxX(); x++)
		{
			for (int z = chunkBox.getMinZ(); z <= chunkBox.getMaxZ(); z++)
			{
				blockPos.set(x, minY, z);
				if (blockTester.test(blockPos, world.getBlockState(blockPos)) && blockBox.contains(blockPos) && children.contains(blockPos))
				{
					for (int y = minY - 1; y > worldBottomY; y--)
					{
						blockPos.setY(y);
						if (world.isAir(blockPos) || world.getBlockState(blockPos).getMaterial().isLiquid())
						{
							world.setBlockState(blockPos, blockGetter.apply(random), Block.NOTIFY_ALL);
						}
						else if (y < blockBox.getMinY())  // outside the bounding box, stop filling now
						{
							break;
						}
					}
				}
			}
		}
	}
	protected static void fillBottomAirGap(StructureWorldAccess world, Random random, BlockBox chunkBox, StructurePiecesList children, BiPredicate<BlockPos, BlockState> blockTester, Function<Random, BlockState> blockGetter)
	{
		fillBottomAirGap(world, random, chunkBox, children, blockTester, blockGetter, 0);
	}

	protected abstract static class Piece extends SimpleStructurePiece
	{
		private final Object posLock = new Object();
		private boolean positionAdjusted = false;

		public Piece(StructurePieceType type, StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation)
		{
			super(type, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), pos);
			if (this.structure.getSize() == Vec3i.ZERO)
			{
				throw new IllegalArgumentException("Unknown structure: " + identifier);
			}
		}

		public Piece(StructurePieceType type, StructureManager manager, NbtCompound nbt)
		{
			super(type, nbt, manager, identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rotation"))));
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation)
		{
			return (new StructurePlacementData()).setRotation(rotation).setPlaceFluids(false);
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
			synchronized (this.posLock)
			{
				if (!this.positionAdjusted)
				{
					this.positionAdjusted = true;
					this.adjustPosByTerrain(world);
				}
			}
			super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pos);
		}

		protected void adjustPosByTerrain(StructureWorldAccess world)
		{
			int sumY = 0;
			Vec3i size = this.structure.getSize();
			Heightmap.Type type = Heightmap.Type.WORLD_SURFACE_WG;
			for (BlockPos blockPos : BlockPos.iterate(this.boundingBox.getMinX(), 0, this.boundingBox.getMinZ(), this.boundingBox.getMaxX(), 0, this.boundingBox.getMaxZ()))
			{
				sumY += world.getTopY(type, blockPos.getX(), blockPos.getZ());
			}
			int avgY = sumY / (size.getX() * size.getZ());
			this.pos = new BlockPos(this.pos.getX(), avgY, this.pos.getZ());
		}

		protected void setChestLoot(ServerWorldAccess world, BlockPos chestPos, Random random, Identifier lootTableId)
		{
			BlockEntity blockEntity = world.getBlockEntity(chestPos);
			if (blockEntity instanceof ChestBlockEntity)
			{
				((ChestBlockEntity)blockEntity).setLootTable(lootTableId, random.nextLong());
			}
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox)
		{
		}
	}
}
