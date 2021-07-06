/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.util;

import net.minecraft.util.math.BlockPos;

public class SpawnPosition {
	
	int next = 0;
	BlockPos[] poses;
	
	public SpawnPosition(int cnt, int border) {
		poses = new BlockPos[cnt];
		border *= 0.45;
		for (int i = 0; i < cnt; i++) {
			double angle = i * (360.0 / cnt) * Math.PI / 180;
			int x = (int) (border * Math.sin(angle));
			int z = (int) (border * Math.cos(angle));
			poses[i] = new BlockPos(x, 255, z);
		}
	}
	
	public BlockPos nextPos() {
		return poses[next++];
	}

}
