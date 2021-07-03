package me.fallenbreath.tcuhc.gen;

import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.options.Options;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class EnderAltarFeature extends Feature<DefaultFeatureConfig>
{
	static Set<ChunkPos> altarPoses = Sets.newHashSet();

	public EnderAltarFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer)
	{
		super(configDeserializer);
		calcAltarPosition();
	}

	private void calcAltarPosition() {
		if (altarPoses.isEmpty()) {
			int start = Options.instance.getIntegerOptionValue("borderStart") / 2;
			int end = Options.instance.getIntegerOptionValue("borderEnd") / 2;
			start = Math.max(1, (start - end) / 2);

			for (int i = 0; i < 4; i++) {
				int x = i / 2 == 1 ? 1 : -1;
				int z = i % 2 == 1 ? 1 : -1;
				x *= UhcGameManager.rand.nextInt(start) + end;
				z *= UhcGameManager.rand.nextInt(start) + end;
				altarPoses.add(new ChunkPos(new BlockPos(x, 0, z)));
			}
		}
	}

	@Override
	public boolean generate(IWorld worldIn, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random rand, BlockPos position, DefaultFeatureConfig config)
	{
		if (altarPoses.contains(new ChunkPos(position))) {
			BlockPos top = worldIn.getTopPosition(Heightmap.Type.MOTION_BLOCKING, position.add(rand.nextInt(16), 0, rand.nextInt(16))).down();
			BlockState floor = Blocks.OBSIDIAN.getDefaultState();
			BlockState banner = Blocks.BLACK_BANNER.getDefaultState();
			BlockState air = Blocks.AIR.getDefaultState();
			for (int x = -2; x <= 2; x++) {
				for (int z = -2; z <= 2; z++) {
					worldIn.setBlockState(top.add(x, 0, z), floor, 2);
					for (int y = 1; y <= 5; y++)
						worldIn.setBlockState(top.add(x, y, z), air, 2);
				}
			}
			for (int i = -1; i <= 1; i++) {
				worldIn.setBlockState(top.add(3, 0, i), floor, 2);
				worldIn.setBlockState(top.add(-3, 0, i), floor, 2);
				worldIn.setBlockState(top.add(i, 0, 3), floor, 2);
				worldIn.setBlockState(top.add(i, 0, -3), floor, 2);
				for (int y = 1; y <= 5; y++) {
					worldIn.setBlockState(top.add(3, y, i), air, 2);
					worldIn.setBlockState(top.add(-3, y, i), air, 2);
					worldIn.setBlockState(top.add(i, y, 3), air, 2);
					worldIn.setBlockState(top.add(i, y, -3), air, 2);
				}
			}
			worldIn.setBlockState(top.add(-3, 1, 1), floor, 2);
			worldIn.setBlockState(top.add(-3, 1, -1), floor, 2);
			worldIn.setBlockState(top.add(3, 1, 1), floor, 2);
			worldIn.setBlockState(top.add(3, 1, -1), floor, 2);
			worldIn.setBlockState(top.add(1, 1, 3), banner, 2);
			worldIn.setBlockState(top.add(-1, 1, 3), banner, 2);
			worldIn.setBlockState(top.add(1, 1, -3), banner.rotate(BlockRotation.CLOCKWISE_180), 2);
			worldIn.setBlockState(top.add(-1, 1, -3), banner.rotate(BlockRotation.CLOCKWISE_180), 2);
			int radius = 16;
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos pos = top.add(x, 0, z);
					pos = worldIn.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).down();
					if (!worldIn.getBlockState(pos).isSimpleFullBlock(worldIn, pos))
						continue;
					float chance = 64.0f / (x * x + z * z);
					if (rand.nextFloat() < chance)
						worldIn.setBlockState(pos, floor, 2);
				}
			}
			EnderCrystalEntity crystal = new EnderCrystalEntity(worldIn.getWorld(), top.getX() + 0.5, top.getY() + 2, top.getZ() + 0.5);
			crystal.setShowBottom(false);
			worldIn.spawnEntity(crystal);
			for (int x = -1; x <= 1; x++)
				for (int z = -1; z <= 1; z++)
					for (int y = -2; y <= -1; y++)
						worldIn.setBlockState(top.add(x, y, z), floor, 2);
			worldIn.setBlockState(top.down(), Blocks.GOLD_BLOCK.getDefaultState(), 2);
			worldIn.setBlockState(top.add(0, 1, 0), Blocks.END_STONE.getDefaultState(), 2);
			worldIn.setBlockState(top.add(0, 0, 0), Blocks.END_STONE.getDefaultState(), 2);
			return true;
		}
		return false;
	}
}
