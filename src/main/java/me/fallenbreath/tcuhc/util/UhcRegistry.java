package me.fallenbreath.tcuhc.util;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.Set;

public class UhcRegistry
{
	private static final Set<Feature<?>> FEATURES = Sets.newLinkedHashSet();
	private static final Set<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = Sets.newLinkedHashSet();
	private static final Set<PlacedFeature> PLACED_FEATURE = Sets.newLinkedHashSet();
	private static final Set<RecipeSerializer<?>> RECIPE_SERIALIZERS = Sets.newLinkedHashSet();

	private static Identifier getIdentifier(String id)
	{
		return new Identifier("tcuhc", id);
	}

	public static Set<Feature<?>> getFeatures()
	{
		return FEATURES;
	}
	public static Set<ConfiguredFeature<?, ?>> getConfiguredFeature() {return CONFIGURED_FEATURE;}
	public static Set<PlacedFeature> getPlacedFeature() {return PLACED_FEATURE;}

	public static Set<RecipeSerializer<?>> getRecipeSerializers()
	{
		return RECIPE_SERIALIZERS;
	}

	/**
	 * Like net.minecraft.world.gen.feature.Feature#register(java.lang.String, net.minecraft.world.gen.feature.Feature)
	 */
	public static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature)
	{
		FEATURES.add(feature);
		return Registry.register(Registry.FEATURE, getIdentifier(name), feature);
	}

	/**
	 * Like {@link net.minecraft.world.gen.feature.ConfiguredFeatures#register}
	 */
	public static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> registerConfiguredFeature(String name, ConfiguredFeature<FC, ?> configuredFeature)
	{
		CONFIGURED_FEATURE.add(configuredFeature);
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, getIdentifier(name), configuredFeature);
	}

	/**
	 * Like {@link net.minecraft.world.gen.feature.PlacedFeatures#register}
	 */
	public static PlacedFeature registerPlacedFeature(String name, PlacedFeature placedFeature)
	{
		PLACED_FEATURE.add(placedFeature);
		return Registry.register(BuiltinRegistries.PLACED_FEATURE, getIdentifier(name), placedFeature);
	}

	/**
	 * Like net.minecraft.recipe.RecipeSerializer#register(java.lang.String, net.minecraft.recipe.RecipeSerializer)
	 */
	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String name, S serializer)
	{
		RECIPE_SERIALIZERS.add(serializer);
		return Registry.register(Registry.RECIPE_SERIALIZER, getIdentifier(name), serializer);
	}

	/**
	 * Like {@link net.minecraft.util.math.intprovider.IntProviderType}
	 */
	public static <P extends IntProvider> IntProviderType<P> registerIntProviderType(String name, Codec<P> codec)
	{
		return Registry.register(Registry.INT_PROVIDER_TYPE, getIdentifier(name), () -> codec);
	}
}
