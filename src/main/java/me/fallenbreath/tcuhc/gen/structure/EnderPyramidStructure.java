package me.fallenbreath.tcuhc.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
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
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;
import java.util.Random;

public class EnderPyramidStructure extends SinglePieceLandStructure
{
	public static final StructureConfig CONFIG = new StructureConfig(40, 24, 591497057);
	private static final StructurePieceType PIECE_TYPE = UhcRegistry.registerStructurePieceType(Piece::new, "ender_pyramid_piece");

	private static final Identifier MAIN_TEMPLATE = TcUhcMod.id("ender_pyramid/main");
	private static final Identifier CHEST_LOOT_TABLE = TcUhcMod.id("ender_pyramid/chest");
	private static final List<Block> BASE_BLOCKS = ImmutableList.of(
			Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS,
			Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE, Blocks.MOSSY_COBBLESTONE
	);

	public EnderPyramidStructure(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec, StructureGeneratorFactory.simple(StructureGeneratorFactory.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG), EnderPyramidStructure::addPieces), EnderPyramidStructure::fillBottomAirGap);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context)
	{
		BlockRotation rotation = BlockRotation.random(context.random());
		BlockPos pos = context.chunkPos().getStartPos().add(context.random().nextInt(16), context.random().nextInt(16), context.random().nextInt(16));
		collector.addPiece(new Piece(context.structureManager(), MAIN_TEMPLATE, pos, rotation));
	}

	private static void fillBottomAirGap(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList children)
	{
		// fill the bottom with base building blocks

		int worldBottomY = world.getBottomY();
		BlockBox blockBox = children.getBoundingBox();
		int minY = blockBox.getMinY();
		BlockPos.Mutable blockPos = new BlockPos.Mutable();

		for (int x = chunkBox.getMinX(); x <= chunkBox.getMaxX(); x++)
		{
			for (int z = chunkBox.getMinZ(); z <= chunkBox.getMaxZ(); z++)
			{
				blockPos.set(x, minY, z);
				if (!world.isAir(blockPos) && blockBox.contains(blockPos) && children.contains(blockPos))
				{
					for (int y = minY - 1; y > worldBottomY; y--)
					{
						blockPos.setY(y);
						if (!world.isAir(blockPos) && !world.getBlockState(blockPos).getMaterial().isLiquid())
						{
							break;
						}
						world.setBlockState(blockPos, BASE_BLOCKS.get(random.nextInt(BASE_BLOCKS.size())).getDefaultState(), Block.NOTIFY_LISTENERS);
					}
				}
			}
		}
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
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox)
		{
			if ("chest".equals(metadata))
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
				BlockEntity blockEntity = world.getBlockEntity(pos.up());
				if (blockEntity instanceof ChestBlockEntity)
				{
					((ChestBlockEntity)blockEntity).setLootTable(CHEST_LOOT_TABLE, random.nextLong());
				}
			}
		}

		@Override
		public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos)
		{
			System.err.println("ENDER PYRAMID @ " + this.pos);
			super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pos);
		}
	}
}
