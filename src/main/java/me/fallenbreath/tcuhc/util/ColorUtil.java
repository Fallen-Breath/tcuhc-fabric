/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.DyeColor;

public class ColorUtil
{
	private static final ImmutableMap<DyeColor, ColorfulBlocks> MAPPING = new ImmutableMap.Builder<DyeColor, ColorfulBlocks>().
			put(DyeColor.WHITE, new ColorfulBlocks(Blocks.WHITE_WOOL, Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_CARPET)).
			put(DyeColor.ORANGE, new ColorfulBlocks(Blocks.ORANGE_WOOL, Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_CARPET)).
			put(DyeColor.MAGENTA, new ColorfulBlocks(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_CARPET)).
			put(DyeColor.LIGHT_BLUE, new ColorfulBlocks(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_CARPET)).
			put(DyeColor.YELLOW, new ColorfulBlocks(Blocks.YELLOW_WOOL, Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_CARPET)).
			put(DyeColor.LIME, new ColorfulBlocks(Blocks.LIME_WOOL, Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_CARPET)).
			put(DyeColor.PINK, new ColorfulBlocks(Blocks.PINK_WOOL, Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_CARPET)).
			put(DyeColor.GRAY, new ColorfulBlocks(Blocks.GRAY_WOOL, Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_CARPET)).
			put(DyeColor.LIGHT_GRAY, new ColorfulBlocks(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_CARPET)).
			put(DyeColor.CYAN, new ColorfulBlocks(Blocks.CYAN_WOOL, Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_CARPET)).
			put(DyeColor.PURPLE, new ColorfulBlocks(Blocks.PURPLE_WOOL, Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_CARPET)).
			put(DyeColor.BLUE, new ColorfulBlocks(Blocks.BLUE_WOOL, Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_CARPET)).
			put(DyeColor.BROWN, new ColorfulBlocks(Blocks.BROWN_WOOL, Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_CARPET)).
			put(DyeColor.GREEN, new ColorfulBlocks(Blocks.GREEN_WOOL, Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_CARPET)).
			put(DyeColor.RED, new ColorfulBlocks(Blocks.RED_WOOL, Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_CARPET)).
			put(DyeColor.BLACK, new ColorfulBlocks(Blocks.BLACK_WOOL, Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_CARPET)).
			build();

	public static ColorfulBlocks fromColor(DyeColor color)
	{
		return MAPPING.get(color);
	}

	public static class ColorfulBlocks
	{
		public final Block wool;
		public final Block glass;
		public final Block glassPane;
		public final Block carpet;

		private ColorfulBlocks(Block wool, Block glass, Block glassPane, Block carpet)
		{
			this.wool = wool;
			this.glass = glass;
			this.glassPane = glassPane;
			this.carpet = carpet;
		}
	}
}
