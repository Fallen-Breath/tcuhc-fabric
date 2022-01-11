package me.fallenbreath.tcuhc.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;
import java.util.Random;

public class VillainHouseStructure extends SinglePieceLandStructure<DefaultFeatureConfig>
{
	public static final StructureConfig STRUCTURE_CONFIG = new StructureConfig(28, 12, 1323770494);
	private static final StructurePieceType PIECE_TYPE = UhcRegistry.registerStructurePieceType(Piece::new, "villain_house_piece");

	private static final Identifier MAIN_TEMPLATE = TcUhcMod.id("villain_house/main");
	private static final Identifier CHEST_LOOT_TABLE = TcUhcMod.id("villain_house/chest");

	private static final List<Block> BASE_BLOCKS = ImmutableList.of(Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE, Blocks.ANDESITE);
	private static final int FLOOR_OFFSET = 1;

	public VillainHouseStructure(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec, StructureGeneratorFactory.simple(VillainHouseStructure::canGenerate, VillainHouseStructure::addPieces), VillainHouseStructure::postGenerated);
	}

	private static boolean canGenerate(StructureGeneratorFactory.Context<DefaultFeatureConfig> context)
	{
		if (!context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG) || !isBiomeValidInChunk(context))
		{
			return false;
		}
		BlockPos startPos = context.chunkPos().getStartPos();
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (int x = -5; x <= 5; x++)
		{
			for (int z = -5; z <= 5; z++)
			{
				BlockPos pos = startPos.add(x, 0, z);
				int y = context.chunkGenerator().getHeightInGround(pos.getX(), pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world());
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
			}
		}
		return maxY - minY <= 3;
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context)
	{
		BlockRotation rotation = BlockRotation.random(context.random());
		collector.addPiece(new Piece(context.structureManager(), MAIN_TEMPLATE, shiftStartPosRandomly(context), rotation));
	}

	@SuppressWarnings("deprecation")
	private static void postGenerated(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList children)
	{
		BlockBox[] bottomBox = new BlockBox[]{null};
		fillBottomAirGap(
				world, random, chunkBox, children,
				(pos, blockState) -> {
					if (bottomBox[0] != null && bottomBox[0].contains(pos))
					{
						return true;
					}
					if (BASE_BLOCKS.contains(blockState.getBlock()))
					{
						bottomBox[0] = bottomBox[0] == null ? new BlockBox(pos) : bottomBox[0].encompass(pos);
					}
					return bottomBox[0] != null && bottomBox[0].contains(pos);
				},
				rnd -> BASE_BLOCKS.get(random.nextInt(BASE_BLOCKS.size())).getDefaultState(),
				FLOOR_OFFSET
		);
	}

	private static class Piece extends SinglePieceLandStructure.Piece
	{
		private static final List<EntityType<?>> VILLAINS = ImmutableList.of(EntityType.WITCH, EntityType.VINDICATOR, EntityType.PILLAGER);

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
			switch (metadata)
			{
				case "villain":
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					for (int i = random.nextInt(2); i >= 0; i--)
					{
						EntityType<?> villainType = VILLAINS.get(random.nextInt(VILLAINS.size()));
						Entity entity = villainType.create(world.toServerWorld());
						if (entity instanceof MobEntity)
						{
							MobEntity villain = (MobEntity)entity;
							Vec3d vec3d = Vec3d.ofBottomCenter(pos);
							villain.updatePosition(vec3d.x + random.nextFloat() / 10, vec3d.y, vec3d.z + random.nextFloat() / 10);
							villain.setPersistent();
							villain.initialize(world, world.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null, null);
							world.spawnEntity(villain);
						}
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
