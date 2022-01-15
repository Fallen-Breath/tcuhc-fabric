package me.fallenbreath.tcuhc.gen.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.util.collection.ExpiringMap;
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

import java.util.Collection;
import java.util.List;
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
						if (world.isAir(blockPos) || world.getBlockState(blockPos).getMaterial().isReplaceable())
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

	@SuppressWarnings("deprecation")
	protected static void fillBottomAirGapInAutoBox(StructureWorldAccess world, Random random, BlockBox chunkBox, StructurePiecesList children, Collection<Block> baseBlocks, List<Block> fillerBlocks, int yOffset)
	{
		BlockBox[] bottomBox = new BlockBox[]{null};
		fillBottomAirGap(
				world, random, chunkBox, children,
				(pos, blockState) -> {
					if (bottomBox[0] != null && bottomBox[0].contains(pos))
					{
						return true;
					}
					if (baseBlocks.contains(blockState.getBlock()))
					{
						bottomBox[0] = bottomBox[0] == null ? new BlockBox(pos) : bottomBox[0].encompass(pos);
					}
					return bottomBox[0] != null && bottomBox[0].contains(pos);
				},
				rnd -> fillerBlocks.get(random.nextInt(fillerBlocks.size())).getDefaultState(),
				yOffset
		);
	}

	protected abstract static class Piece extends SimpleStructurePiece
	{
		private static final Long2ObjectMap<Integer> Y_ADJUST_CACHE = Long2ObjectMaps.synchronize(new ExpiringMap<>(5 * 1000));  // 5s cache

		public Piece(StructurePieceType type, StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation)
		{
			super(type, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), pos);
			if (this.structure.getSize() == Vec3i.ZERO)
			{
				throw new IllegalArgumentException("Unknown structure: " + identifier);
			}
			this.ensureStructureDataExists();
		}

		public Piece(StructurePieceType type, StructureManager manager, NbtCompound nbt)
		{
			super(type, nbt, manager, identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rotation"))));
			this.ensureStructureDataExists();
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation)
		{
			return (new StructurePlacementData()).setRotation(rotation).setPlaceFluids(false);
		}

		private void ensureStructureDataExists()
		{
			Vec3i size = this.structure.getSize();
			if (size.getX() * size.getY() * size.getZ() == 0)
			{
				UhcGameManager.LOG.error("Empty structure with template {}", this.template);
			}
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

		protected void adjustPosByTerrain(StructureWorldAccess world)
		{
			int newY = Y_ADJUST_CACHE.computeIfAbsent(this.pos.asLong(), key -> {
				int sumY = 0;
				Vec3i size = this.structure.getSize();
				Heightmap.Type type = Heightmap.Type.WORLD_SURFACE_WG;
				for (BlockPos blockPos : BlockPos.iterate(this.boundingBox.getMinX(), 0, this.boundingBox.getMinZ(), this.boundingBox.getMaxX(), 0, this.boundingBox.getMaxZ()))
				{
					sumY += world.getTopY(type, blockPos.getX(), blockPos.getZ());
				}
				return sumY / (size.getX() * size.getZ());
			});
			this.pos = new BlockPos(this.pos.getX(), newY, this.pos.getZ());
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
