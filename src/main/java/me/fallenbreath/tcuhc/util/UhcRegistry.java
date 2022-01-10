package me.fallenbreath.tcuhc.util;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.mixins.feature.structure.StructureFeatureAccessor;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

import java.util.Set;

public class UhcRegistry
{
	private static final Set<RecipeSerializer<?>> RECIPE_SERIALIZERS = Sets.newLinkedHashSet();

	public static Set<RecipeSerializer<?>> getRecipeSerializers()
	{
		return RECIPE_SERIALIZERS;
	}

	/**
	 * Like net.minecraft.world.gen.feature.Feature#register(java.lang.String, net.minecraft.world.gen.feature.Feature)
	 */
	public static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature)
	{
		return Registry.register(Registry.FEATURE, TcUhcMod.id(name), feature);
	}

	/**
	 * Like {@link net.minecraft.world.gen.feature.ConfiguredFeatures#register}
	 */
	public static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> registerConfiguredFeature(String name, ConfiguredFeature<FC, ?> configuredFeature)
	{
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, TcUhcMod.id(name), configuredFeature);
	}

	/**
	 * Like {@link net.minecraft.world.gen.feature.PlacedFeatures#register}
	 */
	public static PlacedFeature registerPlacedFeature(String name, PlacedFeature placedFeature)
	{
		return Registry.register(BuiltinRegistries.PLACED_FEATURE, TcUhcMod.id(name), placedFeature);
	}

	/**
	 * Like net.minecraft.world.gen.feature.StructureFeature#register
	 */
	public static <F extends StructureFeature<?>> F registerStructure(String name, F structureFeature, GenerationStep.Feature step)
	{
		StructureFeatureAccessor.getSTRUCTURES().put(name, structureFeature);
		StructureFeatureAccessor.getSTRUCTURE_TO_GENERATION_STEP().put(structureFeature, step);
		return Registry.register(Registry.STRUCTURE_FEATURE, TcUhcMod.id(name), structureFeature);
	}

	/**
	 * Like net.minecraft.world.gen.feature.ConfiguredStructureFeatures#register
	 */
	public static <FC extends FeatureConfig, F extends StructureFeature<FC>> ConfiguredStructureFeature<FC, F> registerConfiguredStructure(String name, ConfiguredStructureFeature<FC, F> configuredStructureFeature)
	{
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, TcUhcMod.id(name), configuredStructureFeature);
	}

	/**
	 * Like net.minecraft.structure.StructurePieceType#register(net.minecraft.structure.StructurePieceType.ManagerAware, java.lang.String)
	 */
	public static StructurePieceType registerStructurePieceType(StructurePieceType.ManagerAware structurePieceType, String name)
	{
		return Registry.register(Registry.STRUCTURE_PIECE, TcUhcMod.id(name), structurePieceType);
	}

	/**
	 * Like net.minecraft.recipe.RecipeSerializer#register(java.lang.String, net.minecraft.recipe.RecipeSerializer)
	 */
	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String name, S serializer)
	{
		RECIPE_SERIALIZERS.add(serializer);
		return Registry.register(Registry.RECIPE_SERIALIZER, TcUhcMod.id(name), serializer);
	}

	/**
	 * Like {@link net.minecraft.util.math.intprovider.IntProviderType}
	 */
	public static <P extends IntProvider> IntProviderType<P> registerIntProviderType(String name, Codec<P> codec)
	{
		return Registry.register(Registry.INT_PROVIDER_TYPE, TcUhcMod.id(name), () -> codec);
	}
}
