package me.fallenbreath.tcuhc.util;

import com.google.common.collect.Sets;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.Set;

public class UhcRegistry
{
	private static final Set<Feature<?>> FEATURES = Sets.newLinkedHashSet();
	private static final Set<RecipeSerializer<?>> RECIPE_SERIALIZERS = Sets.newLinkedHashSet();

	private static Identifier getIdentifier(String id)
	{
		return new Identifier("tc-uhc", id);
	}

	public static Set<Feature<?>> getFeatures()
	{
		return FEATURES;
	}

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
	 * Like net.minecraft.recipe.RecipeSerializer#register(java.lang.String, net.minecraft.recipe.RecipeSerializer)
	 */
	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipeSerializer(String id, S serializer)
	{
		RECIPE_SERIALIZERS.add(serializer);
		return Registry.register(Registry.RECIPE_SERIALIZER, getIdentifier(id), serializer);
	}
}
