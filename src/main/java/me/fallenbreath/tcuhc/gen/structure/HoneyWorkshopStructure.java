package me.fallenbreath.tcuhc.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

public class HoneyWorkshopStructure extends SinglePieceLandStructure<DefaultFeatureConfig>
{
	private static final StructurePieceType PIECE_TYPE = UhcRegistry.registerStructurePieceType(Piece::new, "honey_workshop_piece");

	private static final Identifier MAIN_TEMPLATE = TcUhcMod.id("honey_workshop/main");
	private static final Identifier CHEST_LOOT_TABLE = TcUhcMod.id("honey_workshop/chest");

	private static final List<Block> BASE_BLOCKS = ImmutableList.of(Blocks.STONE_BRICKS);
	private static final int FLOOR_OFFSET = 0;

	public HoneyWorkshopStructure(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec, StructureGeneratorFactory.simple(StructureGeneratorFactory.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG), HoneyWorkshopStructure::addPieces), HoneyWorkshopStructure::postGenerated);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context)
	{
		BlockRotation rotation = BlockRotation.random(context.random());
		collector.addPiece(new Piece(context.structureManager(), MAIN_TEMPLATE, shiftStartPosRandomly(context), rotation));
	}

	private static void postGenerated(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList children)
	{
		fillBottomAirGapInAutoBox(world, random, chunkBox, children, BASE_BLOCKS, BASE_BLOCKS, FLOOR_OFFSET);
	}

	private static class Piece extends SinglePieceLandStructure.Piece
	{
		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation)
		{
			super(PIECE_TYPE, manager, identifier, pos, rotation);
		}

		public Piece(StructureManager manager, NbtCompound nbt)
		{
			super(PIECE_TYPE, manager, nbt);
		}

		@Override
		protected void adjustPosByTerrain(StructureWorldAccess world)
		{
			super.adjustPosByTerrain(world);
			this.pos = this.pos.down(FLOOR_OFFSET);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox)
		{
			if ("chest".equals(metadata))
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
				setChestLoot(world, pos.down(), random, CHEST_LOOT_TABLE);
			}
		}
	}
}
