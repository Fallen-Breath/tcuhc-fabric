/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.gen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class BonusChestFeature extends Feature<DefaultFeatureConfig>
{
	public static final String BONUS_CHEST_NAME = "Bonus Chest";
	public static final String EMPTY_CHEST_NAME = "Empty Chest";

	private static Map<Biome.Category, Double> POSSIBILITY_MAP;
	private static final Enchantment[] POSSIBLE_ENCHANTMENTS = {
			Enchantments.POWER, Enchantments.SHARPNESS, Enchantments.UNBREAKING, Enchantments.EFFICIENCY,
			Enchantments.FIRE_ASPECT, Enchantments.PROTECTION, Enchantments.PROJECTILE_PROTECTION
	};
	private static final Random rand = new Random();

	private static final List<RandomItem> chestItemList = Lists.newArrayList();
	private static final List<RandomItem> valuableItemList = Lists.newArrayList();
	private static final List<RandomItem> emptyItemList = Lists.newArrayList();

	private static double chestChance;
	private static double emptyChestChance;
	private static double itemChance;

	private static boolean dataGenerated = false;

	public BonusChestFeature(Codec<DefaultFeatureConfig> configCodec)
	{
		super(configCodec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context)
	{
		StructureWorldAccess worldIn = context.getWorld();
		BlockPos position = context.getOrigin();
		Random rand = context.getRandom();
		if (!dataGenerated)
		{
			generateData();
			dataGenerated = true;
		}

		int chunkX = position.getX() >> 4;
		int chunkZ = position.getZ() >> 4;
		if (Math.abs(chunkX) <= 1 || Math.abs(chunkZ) <= 1)
			return false;
		int posX = rand.nextInt(16) + position.getX() - 8;
		int posZ = rand.nextInt(16) + position.getZ() - 8;
		int posY = worldIn.getTopY(Heightmap.Type.OCEAN_FLOOR, posX, posZ);
		position = new BlockPos(posX, posY, posZ);
		while (!worldIn.getBlockState(position).isSolidBlock(worldIn, position) && position.getY() > 0)
		{
			position = position.down();
		}
		if (position.getY() == 0)
		{
			return false;
		}
		Biome biome = worldIn.getBiome(position);
		if (!POSSIBILITY_MAP.containsKey(biome.getCategory()))
		{
			return false;
		}
		boolean hasWater = worldIn.getFluidState(position).isIn(FluidTags.WATER);
		if (rand.nextFloat() < POSSIBILITY_MAP.get(biome.getCategory()) * chestChance)
		{
			boolean isEmptyChest = rand.nextDouble() < emptyChestChance;
			Block chestBlock = isEmptyChest ? Blocks.TRAPPED_CHEST : Blocks.CHEST;
			worldIn.setBlockState(
					position,
					chestBlock.getDefaultState().
							rotate(BlockRotation.random(rand)).
							with(ChestBlock.WATERLOGGED, hasWater)
					, 3
			);
			BlockEntity tileentity = worldIn.getBlockEntity(position);
			if (!(tileentity instanceof ChestBlockEntity))
			{
				return false;
			}
			ChestBlockEntity chest = (ChestBlockEntity) tileentity;
			chest.setCustomName(new LiteralText(isEmptyChest ? EMPTY_CHEST_NAME : BONUS_CHEST_NAME));
			if (isEmptyChest)
			{
				this.genChestItem(chest, emptyItemList, false);
			}
			else
			{
				this.genChestItem(chest, chestItemList, false);
				this.genChestItem(chest, valuableItemList, true);
			}
		}
		return true;
	}

	private void genChestItem(ChestBlockEntity chest, List<RandomItem> itemList, boolean valuable)
	{
		for (RandomItem item : itemList)
		{
			Optional<ItemStack> itemstack = item.getItemStack();
			itemstack.ifPresent(stack -> chest.setStack(rand.nextInt(chest.size()), stack));
			if (valuable && itemstack.isPresent()) break;
		}
	}

	static class ItemSupplier implements Supplier<ItemStack>
	{
		Item item;

		public ItemSupplier(Item item)
		{
			this.item = item;
		}

		public ItemStack get()
		{
			return new ItemStack(item);
		}
	}

	static class MinMaxSupplier implements Supplier<ItemStack>
	{
		Item item;
		int min, max;

		public MinMaxSupplier(Item item, int min, int max)
		{
			this.item = item;
			this.min = min;
			this.max = max;
		}

		public ItemStack get()
		{
			return new ItemStack(item, BonusChestFeature.rand.nextInt(max - min + 1) + min);
		}
	}

	static class RandomItem
	{
		int chance;
		Supplier<ItemStack> stack;

		public RandomItem(int chance, Supplier<ItemStack> stack)
		{
			this.chance = chance;
			this.stack = stack;
		}

		public Optional<ItemStack> getItemStack()
		{
			return BonusChestFeature.rand.nextInt(chance) == 0 ? Optional.of(stack.get()) : Optional.empty();
		}
	}

	private static void generateData()
	{
		double forestChance = 0.12;
		double oceanChance = 0.0;
		double desertChance = 0.06;
		double exHillsChance = 0.12;
		double plainChance = 0.06;
		double icePlainChance = 0.2;
		double iceMountainChance = 0.2;
		double jungleChance = 0.12;
		double mesaChance = 0.12;
		double mushroomChance = 0.1;
		double rforestChance = 0.12;
		double savannaChance = 0.12;
		double taigaChance = 0.12;
		double riverChance = 0.0;
		double beachChance = 0.0;
		double swamplandChance = 0.1;
		double miscChance = 0.0;

		POSSIBILITY_MAP = new ImmutableMap.Builder<Biome.Category, Double>().
				put(Biome.Category.NONE, miscChance).
				put(Biome.Category.TAIGA, taigaChance).
				put(Biome.Category.EXTREME_HILLS, exHillsChance).
				put(Biome.Category.JUNGLE, jungleChance).
				put(Biome.Category.MESA, mesaChance).
				put(Biome.Category.PLAINS, plainChance).
				put(Biome.Category.SAVANNA, savannaChance).
				put(Biome.Category.ICY, icePlainChance).
				put(Biome.Category.THEEND, miscChance).
				put(Biome.Category.BEACH, beachChance).
				put(Biome.Category.FOREST, forestChance).
				put(Biome.Category.OCEAN, oceanChance).
				put(Biome.Category.DESERT, desertChance).
				put(Biome.Category.RIVER, riverChance).
				put(Biome.Category.SWAMP, swamplandChance).
				put(Biome.Category.MUSHROOM, mushroomChance).
				put(Biome.Category.NETHER, miscChance).
				build();

		valuableItemList.add(new RandomItem(16, new ItemSupplier(Items.DIAMOND_SWORD)));
		valuableItemList.add(new RandomItem(24, new ItemSupplier(Items.DIAMOND_PICKAXE)));
		valuableItemList.add(new RandomItem(20, new ItemSupplier(Items.GOLDEN_APPLE)));
		valuableItemList.add(new RandomItem(8, new ItemSupplier(Items.DIAMOND)));
		valuableItemList.add(new RandomItem(16, () -> {
			ItemStack item = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(item, new EnchantmentLevelEntry(POSSIBLE_ENCHANTMENTS[rand.nextInt(POSSIBLE_ENCHANTMENTS.length)], rand.nextInt(4) == 0 ? 2 : 1));
			return item;
		}));

		chestItemList.add(new RandomItem(1, new ItemSupplier(Items.STICK)));
		chestItemList.add(new RandomItem(1, new ItemSupplier(Items.BONE)));
		chestItemList.add(new RandomItem(2, new ItemSupplier(Items.STRING)));
		chestItemList.add(new RandomItem(2, new MinMaxSupplier(Items.IRON_INGOT, 1, 2)));
		chestItemList.add(new RandomItem(3, new ItemSupplier(Items.GOLD_INGOT)));
		chestItemList.add(new RandomItem(3, new ItemSupplier(Items.CHORUS_FRUIT)));
		chestItemList.add(new RandomItem(5, new ItemSupplier(Items.LEATHER)));
		chestItemList.add(new RandomItem(5, new MinMaxSupplier(Items.EXPERIENCE_BOTTLE, 2, 4)));

		emptyItemList.add(new RandomItem(1, () -> new ItemStack(Blocks.DEAD_BUSH).setCustomName(new LiteralText("There should be something here, but ..."))));

		chestChance = UhcGameManager.instance.getOptions().getFloatOptionValue("chestFrequency");
		emptyChestChance = UhcGameManager.instance.getOptions().getFloatOptionValue("trappedChestFrequency");
		itemChance = UhcGameManager.instance.getOptions().getFloatOptionValue("chestItemFrequency");
	}
}
