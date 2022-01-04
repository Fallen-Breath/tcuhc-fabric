/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.task.TaskSpawnPlatformProtect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;

public class SpawnPlatform {
	private static final int DEFAULT_HEIGHT = 160;
	public static int height = DEFAULT_HEIGHT;
	private static BlockPos[] hexagonPos;

	private static void sampleTerrainHeight(UhcGameManager gameManager, World world) {
		UhcWorldData uhcWorldData = gameManager.getWorldData();
		if (uhcWorldData.isSpawnPlatformHeightValid()) {
			height = uhcWorldData.spawnPlatformHeight;
		}
		else {
			height = DEFAULT_HEIGHT;
			final int sampleWidth = 40;
			for (int x = -sampleWidth; x <= sampleWidth; x++)
				for (int z = -sampleWidth; z <= sampleWidth; z++)
				{
					int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
					height = Math.max(height, Math.min(y + 64, world.getTopY() - 16));
				}
			uhcWorldData.spawnPlatformHeight = height;
			uhcWorldData.save();
		}
		UhcGameManager.LOG.info("Set spawn platform height to y" + height);
	}

	public static void generatePlatform(UhcGameManager gameManager, World world) {
		sampleTerrainHeight(gameManager, world);
		generateHexagonPos();
		generateHexagon(world, hexagonPos[0], DyeColor.BLUE);
		generateHexagon(world, hexagonPos[1], DyeColor.RED);
		generateHexagon(world, hexagonPos[2], DyeColor.BLUE);
		generateHexagon(world, hexagonPos[3], DyeColor.BLUE);
		generateHexagon(world, hexagonPos[4], DyeColor.BLUE);
		generateHexagon(world, hexagonPos[5], DyeColor.RED);
		generateHexagon(world, hexagonPos[6], DyeColor.RED);
		gameManager.addTask(new TaskSpawnPlatformProtect(gameManager));
	}

	private static void generateHexagon(World world, BlockPos pos, DyeColor color) {
		for (int x = pos.getX() - 5; x <= pos.getX() + 6; x++)
			for (int z = pos.getZ() - 6; z <= pos.getZ() + 6; z++) {
				int dx = Math.min(x - pos.getX() + 5, pos.getX() + 6 - x);
				int dz = Math.min(z - pos.getZ() + 6, pos.getZ() + 6 - z);
				if (dx / 2.0f + dz < 2.1f)
					continue;
				if (Math.abs(x - pos.getX()) < 2 && Math.abs(z - pos.getZ()) < 2) {
					world.setBlockState(new BlockPos(x, pos.getY(), z), Blocks.SEA_LANTERN.getDefaultState(), 2);
				} else
					world.setBlockState(new BlockPos(x, pos.getY(), z),
							ColorUtil.fromColor(color).wool.getDefaultState(), 2);
				world.setBlockState(new BlockPos(x, pos.getY() + 1, z),
						ColorUtil.fromColor(color).carpet.getDefaultState(), 2);
			}
	}

	public static void generateSafePlatform(World world) {
		BlockState block = Blocks.BARRIER.getDefaultState();
		for (int x = -25; x <= 25; x++)
			for (int z = -25; z <= 25; z++) {
				if (x == -25 || x == 25 || z == -25 || z == 25)
					for (int y = height + 1; y <= height + 4; y++)
						world.setBlockState(new BlockPos(x, y, z), block, 2);
				if (world.isAir(new BlockPos(x, height, z)))
					world.setBlockState(new BlockPos(x, height, z), block, 2);
			}
	}

	public static void destroyPlatform(World world) {
		for (int x = -30; x <= 30; x++)
			for (int z = -30; z <= 30; z++)
				for (int y = height + 5; y >= height; y--)
					world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), 2);
	}

	public static BlockPos getRandomSpawnPosition(Random rand) {
		BlockPos res = hexagonPos[rand.nextInt(hexagonPos.length)];
		return res.add(new BlockPos(rand.nextInt(7) - 3, 0, rand.nextInt(7) - 3));
	}

	private static void generateHexagonPos() {
		hexagonPos = new BlockPos[7];
		hexagonPos[0] = new BlockPos(0, height, 0);
		hexagonPos[1] = new BlockPos(14, height, 0);
		hexagonPos[2] = new BlockPos(-14, height, 0);
		hexagonPos[3] = new BlockPos(7, height, 12);
		hexagonPos[4] = new BlockPos(7, height, -12);
		hexagonPos[5] = new BlockPos(-7, height, 12);
		hexagonPos[6] = new BlockPos(-7, height, -12);
	}
}
