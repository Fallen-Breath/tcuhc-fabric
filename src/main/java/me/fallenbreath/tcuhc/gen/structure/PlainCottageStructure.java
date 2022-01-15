package me.fallenbreath.tcuhc.gen.structure;

import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

import java.util.Random;

public class PlainCottageStructure extends SinglePieceLandStructure<DefaultFeatureConfig>
{
	private static final StructurePieceType PIECE_TYPE = UhcRegistry.registerStructurePieceType(Piece::new, "plain_cottage_piece");

	private static final Identifier MAIN_TEMPLATE = TcUhcMod.id("plain_cottage/main");
	private static final Identifier CHEST_LOOT_TABLE = TcUhcMod.id("plain_cottage/chest");

	private static final int FLOOR_HEIGHT = 1;

	public PlainCottageStructure(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec, StructureGeneratorFactory.simple(PlainCottageStructure::canGenerate, PlainCottageStructure::addPieces), PlainCottageStructure::postGenerated);
	}

	private static boolean canGenerate(StructureGeneratorFactory.Context<DefaultFeatureConfig> context)
	{
		return context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG) && isBiomeValidInChunk(context) && isSurroundingFlat(context, Heightmap.Type.WORLD_SURFACE_WG, 7, 3);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context)
	{
		BlockRotation rotation = BlockRotation.random(context.random());
		collector.addPiece(new Piece(context, MAIN_TEMPLATE, shiftStartPosRandomly(context), rotation));
	}

	private static void postGenerated(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList children)
	{
		fillBottomAirGap(world, random, chunkBox, children, (pos, state) -> !state.isAir(), pos -> Blocks.DIRT.getDefaultState(), FLOOR_HEIGHT);
	}

	private static class Piece extends SinglePieceLandStructure.YOffsetPiece
	{
		private static final int CHEST_AMOUNT = 3;
		private final int bonusChestIndex;
		private int chestCounter = 0;

		public Piece(StructurePiecesGenerator.Context<DefaultFeatureConfig> context, Identifier identifier, BlockPos pos, BlockRotation rotation)
		{
			super(PIECE_TYPE, context.structureManager(), identifier, pos, rotation, FLOOR_HEIGHT);
			this.bonusChestIndex = context.random().nextInt(CHEST_AMOUNT) + 1;
		}

		public Piece(StructureManager manager, NbtCompound nbt)
		{
			super(PIECE_TYPE, manager, nbt);
			this.bonusChestIndex = nbt.getInt("BonusChestIndex");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt)
		{
			super.writeNbt(context, nbt);
			nbt.putInt("BonusChestIndex", this.bonusChestIndex);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox)
		{
			EntityType<?> entityType = null;
			int amount = 1;
			switch (metadata)
			{
				case "horse":
					entityType = random.nextInt(2) == 0 ? EntityType.HORSE : EntityType.DONKEY;
					break;
				case "chicken":
					entityType = EntityType.CHICKEN;
					amount = random.nextInt(2) + 3;
					break;
				case "chest":
					this.chestCounter++;
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					if (this.chestCounter == this.bonusChestIndex)
					{
						setChestLoot(world, pos.down(), random, CHEST_LOOT_TABLE);
					}
					break;
			}
			if (entityType != null)
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
				for (int i = 0; i < amount; i++)
				{
					this.placeEntity(entityType, pos.down(), world, random);
				}
			}
		}
	}
}
