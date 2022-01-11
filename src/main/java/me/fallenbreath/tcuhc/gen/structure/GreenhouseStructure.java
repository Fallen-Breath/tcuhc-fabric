package me.fallenbreath.tcuhc.gen.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.mixins.feature.structure.WeightedListAccessor;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class GreenhouseStructure extends SinglePieceLandStructure<GreenhouseConfig>
{
	public static final StructureConfig STRUCTURE_CONFIG = new StructureConfig(32, 8, 981666224);

	private static final String SNOW = "snow";
	private static final String DESERT = "desert";

	private static final StructurePieceType SNOW_PIECE_TYPE = UhcRegistry.registerStructurePieceType(Piece::new, "greenhouse_piece_" + SNOW);
	private static final StructurePieceType DESERT_PIECE_TYPE = UhcRegistry.registerStructurePieceType(Piece::new, "greenhouse_piece_" + DESERT);
	private static final Identifier CHEST_SNOW_LOOT_TABLE = TcUhcMod.id("greenhouse/chest_" + SNOW);
	private static final Identifier CHEST_DESERT_LOOT_TABLE = TcUhcMod.id("greenhouse/chest_" + DESERT);

	private static final Map<String, StructurePieceType> POSSIBLE_TYPES = ImmutableMap.of(SNOW, SNOW_PIECE_TYPE, DESERT, DESERT_PIECE_TYPE);
	private static final Map<String, Identifier> CHEST_LOOT_TABLES = ImmutableMap.of(SNOW, CHEST_SNOW_LOOT_TABLE, DESERT, CHEST_DESERT_LOOT_TABLE);
	private static final Map<String, Block> GROUND_FILLER_BLOCK = ImmutableMap.of(SNOW, Blocks.DEEPSLATE_BRICKS, DESERT, Blocks.SANDSTONE);
	private static final Map<String, Integer> FLOOR_OFFSET = ImmutableMap.of(SNOW, 1, DESERT, 3);

	public GreenhouseStructure(Codec<GreenhouseConfig> configCodec)
	{
		super(configCodec, StructureGeneratorFactory.simple(GreenhouseStructure::isBiomeValidInChunk, GreenhouseStructure::addPieces), GreenhouseStructure::postGenerated);
	}

	private static Identifier getStructureId(String type)
	{
		if (POSSIBLE_TYPES.containsKey(type))
		{
			return TcUhcMod.id("greenhouse/" + type);
		}
		else
		{
			throw new IllegalArgumentException(type);
		}
	}

	private static StructurePieceType getStructureType(String type)
	{
		StructurePieceType structurePieceType = POSSIBLE_TYPES.get(type);
		if (structurePieceType != null)
		{
			return structurePieceType;
		}
		else
		{
			throw new IllegalArgumentException(type);
		}
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<GreenhouseConfig> context)
	{
		collector.addPiece(new Piece(context, shiftStartPosRandomly(context), context.config()));
	}

	private static void postGenerated(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList children)
	{
		List<StructurePiece> pieces = children.pieces();
		if (!pieces.isEmpty())
		{
			StructurePiece piece = pieces.get(0);
			if (piece instanceof Piece)
			{
				String type = ((Piece) piece).getGreenhouseType();
				BlockState dummy = GROUND_FILLER_BLOCK.get(type).getDefaultState();
				fillBottomAirGap(world, random, chunkBox, children, (pos, blockState) -> !blockState.isAir(), rnd -> dummy, FLOOR_OFFSET.get(type));
			}
		}
	}

	private static class Piece extends SinglePieceLandStructure.Piece
	{
		private static final WeightedList<Block> MUSHROOM = Util.make(new WeightedList<>(), list -> {
			list.add(Blocks.BROWN_MUSHROOM, 1);
			list.add(Blocks.RED_MUSHROOM, 1);
		});
		private static final WeightedList<Block> FLOWERS = Util.make(new WeightedList<>(), list -> {
			list.add(Blocks.DANDELION, 1);
			list.add(Blocks.POPPY, 1);
			list.add(Blocks.BLUE_ORCHID, 1);
			list.add(Blocks.ALLIUM, 1);
			list.add(Blocks.AZURE_BLUET, 1);
			list.add(Blocks.ORANGE_TULIP, 1);
			list.add(Blocks.WHITE_TULIP, 1);
			list.add(Blocks.PINK_TULIP, 1);
			list.add(Blocks.CORNFLOWER, 1);
			list.add(Blocks.LILY_OF_THE_VALLEY, 1);
			list.add(Blocks.BIG_DRIPLEAF, 1);
			list.add(Blocks.AZALEA, 1);
			list.add(Blocks.GRASS, 1);

			list.add(Blocks.OXEYE_DAISY, 10);
		});

		private final String type;
		private final Block plant1;
		private final Block plant2;
		private final BlockState dirt1;
		private final BlockState dirt2;

		public Piece(StructurePiecesGenerator.Context<GreenhouseConfig> context, BlockPos pos, GreenhouseConfig config)
		{
			super(getStructureType(config.type), context.structureManager(), GreenhouseStructure.getStructureId(config.type), pos, BlockRotation.random(context.random()));
			this.type = config.type;
			this.plant1 = getRandomPlant(context.random());
			this.plant2 = getRandomPlant(context.random());
			this.dirt1 = getDirtFromPlant(this.plant1);
			this.dirt2 = getDirtFromPlant(this.plant2);
		}

		public Piece(StructureManager manager, NbtCompound nbt)
		{
			super(getStructureType(nbt.getString("GreenhouseType")), manager, nbt);
			this.type = nbt.getString("GreenhouseType");
			this.plant1 = Registry.BLOCK.get(new Identifier(nbt.getString("Plant1")));
			this.plant2 = Registry.BLOCK.get(new Identifier(nbt.getString("Plant2")));
			this.dirt1 = getDirtFromPlant(this.plant1);
			this.dirt2 = getDirtFromPlant(this.plant2);
		}

		public String getGreenhouseType()
		{
			return this.type;
		}

		private static BlockState getDirtFromPlant(Block plant)
		{
			return MUSHROOM.stream().anyMatch(block -> block == plant) ? Blocks.MYCELIUM.getDefaultState() : Blocks.GRASS_BLOCK.getDefaultState();
		}

		@SuppressWarnings("unchecked")
		private static Block getRandomPlant(Random random)
		{
			WeightedListAccessor<Block> blocks = (WeightedListAccessor<Block>)(random.nextFloat() < 0.4 ? MUSHROOM : FLOWERS);
			WeightedList<Block> copied = new WeightedList<>();
			blocks.getEntries().forEach(entry -> copied.add(entry.getElement(), entry.getWeight()));
			((WeightedListAccessor<Block>)copied).setRandom(random);
			return copied.shuffle().stream().findFirst().orElseThrow(RuntimeException::new);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt)
		{
			super.writeNbt(context, nbt);
			nbt.putString("GreenhouseType", this.type);
			nbt.putString("Plant1", Registry.BLOCK.getId(this.plant1).toString());
			nbt.putString("Plant2", Registry.BLOCK.getId(this.plant2).toString());
		}

		private static BlockState getPlant(Block plant, Random random)
		{
			if (plant == Blocks.AZALEA && random.nextFloat() < 0.2)
			{
				plant = Blocks.FLOWERING_AZALEA;
			}
			return plant.getDefaultState();
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox)
		{
			BlockState plantState = null;
			BlockState dirtState = null;
			switch (metadata)
			{
				case "plant1":
					plantState = getPlant(this.plant1, random);
					dirtState = this.dirt1;
					break;
				case "plant2":
					plantState = getPlant(this.plant2, random);
					dirtState = this.dirt2;
					break;
				case "chest":
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					setChestLoot(world, pos.down(), random, CHEST_LOOT_TABLES.get(this.type));
					return;
			}
			if (plantState != null && dirtState != null)
			{
				world.setBlockState(pos, dirtState, Block.NOTIFY_ALL);
				if (random.nextFloat() < 0.3F)
				{
					world.setBlockState(pos.up(), plantState, Block.NOTIFY_ALL);
				}
			}
		}

		@Override
		protected void adjustPosByTerrain(StructureWorldAccess world)
		{
			super.adjustPosByTerrain(world);
			this.pos = this.pos.down(FLOOR_OFFSET.getOrDefault(this.type, 0));
		}
	}
}
