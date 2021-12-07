/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.gen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
import java.util.Random;

public class MerchantsFeature extends Feature<DefaultFeatureConfig>
{
	private static final List<UHCRecipe> randRecipeList;
	private static final List<UHCRecipe> staticRecipeList;

	public MerchantsFeature(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec);
	}

	@Override
	public boolean generate(StructureWorldAccess worldIn, ChunkGenerator chunkGenerator, Random rand, BlockPos position, DefaultFeatureConfig config)
	{
		int chunkX = position.getX() >> 4;
		int chunkZ = position.getZ() >> 4;
		if (Math.abs(chunkX) < 2 || Math.abs(chunkZ) < 2)
			return false;
		float merchantChance = UhcGameManager.instance.getOptions().getFloatOptionValue("merchantFrequency");
		if (chunkX % 4 == 0 && chunkZ % 4 == 0 && rand.nextFloat() < 0.3 * merchantChance) {
			BlockPos pos = worldIn.getTopPosition(Heightmap.Type.OCEAN_FLOOR, position.add(rand.nextInt(16) - 8, 0, rand.nextInt(16) - 8)).down();
			if (worldIn.getBlockState(pos).getFluidState().isIn(FluidTags.WATER))
				return false;
			VillagerEntity villager = new VillagerEntity(EntityType.VILLAGER, worldIn.toServerWorld());
			villager.setAiDisabled(true);
			villager.setInvulnerable(true);
			villager.updatePosition(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5);
			for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++)
				for (int z = pos.getZ() - 1; z <= pos.getZ() + 1; z++) {
					worldIn.setBlockState(new BlockPos(x, pos.getY(), z), Blocks.STONE_BRICKS.getDefaultState(), 2);
					if (x != pos.getX() || z != pos.getZ())
						worldIn.setBlockState(new BlockPos(x, pos.getY() + 1, z), Blocks.IRON_BARS.getDefaultState(), 2);
					worldIn.setBlockState(new BlockPos(x, pos.getY() + 3, z), Blocks.STONE_BRICKS.getDefaultState(), 2);
				}
			worldIn.setBlockState(pos.up(4), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 2);
			int recipeCnt = rand.nextInt(3) + 2;
			TradeOfferList recipes = new TradeOfferList();
			for (int i = 0; i < recipeCnt; i++)
				recipes.add(getRandomRecipe(rand));
			addStaticRecipes(recipes, rand);
			villager.setOffers(recipes);
			worldIn.spawnEntity(villager);
			return true;
		}
		return false;
	}

	private TradeOffer getRandomRecipe(Random rand) {
		return randRecipeList.get(rand.nextInt(randRecipeList.size())).getRecipe(rand);
	}

	private void addStaticRecipes(TradeOfferList list, Random rand) {
		for (UHCRecipe recipe : staticRecipeList) {
			list.add(recipe.getRecipe(rand));
		}
	}

	static {
		staticRecipeList = new ImmutableList.Builder<UHCRecipe>().
				add(new UHCRecipe(Items.GOLDEN_APPLE, Items.APPLE, 1, 1, 18, 30, true)).
				add(new UHCRecipe(Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE, 1, 1, 36, 48, true)).
				add(new UHCRecipe(Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS, 1, 1, 30, 42, true)).
				add(new UHCRecipe(Items.DIAMOND_HELMET, Items.IRON_HELMET, 1, 1, 22, 30, true)).
				add(new UHCRecipe(Items.DIAMOND_BOOTS, Items.IRON_BOOTS, 1, 1, 18, 24, true)).
				build();
		randRecipeList = new ImmutableList.Builder<UHCRecipe>().
				add(new UHCRecipe(Items.EXPERIENCE_BOTTLE, 2, 4, 1, 1, true)).
				add(new UHCRecipe(Items.NETHER_WART, Items.BLAZE_POWDER, 1, 2, 2, 4, true)).
				add(new UHCRecipe(Items.COAL, 3, 6, 1, 1, false)).
				add(new UHCRecipe(Items.REDSTONE, 3, 6, 1, 1, false)).
				add(new UHCRecipe(Items.IRON_INGOT, 1, 2, 1, 1, false)).
				add(new UHCRecipe(Items.GOLD_INGOT, 1, 1, 1, 2, false)).
				add(new UHCRecipe(Items.ENDER_PEARL, 1, 1, 10, 20, false)).
				add(new UHCRecipe(Items.EMERALD, 1, 1, 3, 6, false)).
				add(new UHCRecipe(Items.DIAMOND, 1, 1, 2, 4, false)).
				add(new UHCRecipe(Items.ANCIENT_DEBRIS, 1, 1, 12, 24, false)).
				build();
	}

	public static class UHCRecipe {
		Item item1, item2;
		int itemMin, itemMax;
		int moneyMin, moneyMax;
		boolean sell;

		public UHCRecipe(Item item1, Item item2, int imin, int imax, int mmin, int mmax, boolean sell) {
			this.item1 = item1;
			this.item2 = item2;
			itemMin = imin;
			itemMax = imax;
			moneyMin = mmin;
			moneyMax = mmax;
			this.sell = sell;
		}

		public UHCRecipe(Item item1, int imin, int imax, int mmin, int mmax, boolean sell) {
			this(item1, null, imin, imax, mmin, mmax, sell);
		}

		public TradeOffer getRecipe(Random rand) {
			ItemStack stack = itemMax == itemMin ? new ItemStack(item1, itemMax) : new ItemStack(item1, rand.nextInt(itemMax - itemMin + 1) + itemMin);
			ItemStack money = moneyMax == moneyMin ? new ItemStack(Items.QUARTZ, moneyMax) : new ItemStack(Items.QUARTZ, rand.nextInt(moneyMax - moneyMin + 1) + moneyMin);
			if (sell) return new TradeOffer(money, item2 == null ? ItemStack.EMPTY : new ItemStack(item2), stack, 10000, 0, 1);
			else return new TradeOffer(stack, ItemStack.EMPTY, money, 10000, 0, 1);
		}
	}
}
