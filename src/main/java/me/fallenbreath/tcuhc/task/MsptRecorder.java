package me.fallenbreath.tcuhc.task;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class MsptRecorder
{
	public long[] lastTickLengths = new long[100];
	private long lastNanoTime = Util.getMeasuringTimeNano();
	private long ticks;

	public double getMspt()
	{
		return MathHelper.average(this.lastTickLengths) * 1.0E-6D;
	}

	public void startTick()
	{
		this.lastNanoTime = Util.getMeasuringTimeNano();
	}

	public void endTick()
	{
		this.lastTickLengths[(int)(this.ticks % 100)] = Util.getMeasuringTimeNano() - this.lastNanoTime;
		this.ticks++;
	}
}
