package me.fallenbreath.tcuhc.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;

import java.util.Random;

public class AverageIntProvider extends IntProvider
{
	// plz dont crash
	private static final Codec<AverageIntProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.
					group(Codec.FLOAT.fieldOf("value").forGetter(AverageIntProvider::getValue)).
					apply(instance, AverageIntProvider::new)
	);
	private static final IntProviderType<AverageIntProvider> TYPE = UhcRegistry.registerIntProviderType("average",  CODEC);

	private final float value;
	private final int base;
	private final float decimal;

	public AverageIntProvider(float value)
	{
		this.value = value;
		this.base = (int)value;
		this.decimal = value - this.base;
	}

	public float getValue()
	{
		return value;
	}

	@Override
	public int get(Random random)
	{
		return this.base + (random.nextFloat() < this.decimal ? 1 : 0);
	}

	@Override
	public int getMin()
	{
		return this.base;
	}

	@Override
	public int getMax()
	{
		return this.base + 1;
	}

	@Override
	public IntProviderType<?> getType()
	{
		return TYPE;
	}
}