package me.fallenbreath.tcuhc.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;
import java.util.Random;

public class VillainHouseStructure extends SinglePieceLandStructure<DefaultFeatureConfig>
{
	private static final StructurePieceType PIECE_TYPE = UhcRegistry.registerStructurePieceType(Piece::new, "villain_house_piece");

	private static final Identifier MAIN_TEMPLATE = TcUhcMod.id("villain_house/main");
	private static final Identifier CHEST_LOOT_TABLE = TcUhcMod.id("villain_house/chest");

	private static final List<Block> BASE_BLOCKS = ImmutableList.of(Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE, Blocks.ANDESITE);
	private static final int FLOOR_HEIGHT = 1;

	public VillainHouseStructure(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec, StructureGeneratorFactory.simple(VillainHouseStructure::canGenerate, VillainHouseStructure::addPieces), VillainHouseStructure::postGenerated);
	}

	private static boolean canGenerate(StructureGeneratorFactory.Context<DefaultFeatureConfig> context)
	{
		return context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG) && isBiomeValidInChunk(context) && isSurroundingFlat(context, Heightmap.Type.WORLD_SURFACE_WG, 5, 3);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context)
	{
		BlockRotation rotation = BlockRotation.random(context.random());
		collector.addPiece(new Piece(context.structureManager(), MAIN_TEMPLATE, shiftStartPosRandomly(context), rotation));
	}

	private static void postGenerated(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList children)
	{
		fillBottomAirGapInAutoBox(world, random, chunkBox, children, BASE_BLOCKS, BASE_BLOCKS, FLOOR_HEIGHT);
	}

	private static class Piece extends SinglePieceLandStructure.YOffsetPiece
	{
		private static final List<EntityType<?>> VILLAINS = ImmutableList.of(EntityType.WITCH, EntityType.VINDICATOR, EntityType.PILLAGER);

		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation)
		{
			super(PIECE_TYPE, manager, identifier, pos, rotation, FLOOR_HEIGHT);
		}

		public Piece(StructureManager manager, NbtCompound nbt)
		{
			super(PIECE_TYPE, manager, nbt);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox)
		{
			switch (metadata)
			{
				case "villain":
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					for (int i = 0; i < 2; i++)
					{
						this.placeEntity(VILLAINS.get(random.nextInt(VILLAINS.size())), pos, world, random);
					}
					break;
				case "chest":
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					BlockPos chestPos = pos.down();
					BlockState chestBlock = world.getBlockState(chestPos);
					if (random.nextInt(2) == 0)
					{
						if (chestBlock.getBlock() == Blocks.CHEST)
						{
							world.setBlockState(chestPos, Blocks.ENDER_CHEST.getDefaultState().with(EnderChestBlock.FACING, chestBlock.get(ChestBlock.FACING)), Block.NOTIFY_ALL);
						}
					}
					else
					{
						setChestLoot(world, chestPos, random, CHEST_LOOT_TABLE);
					}
					break;
			}
		}
	}
}
