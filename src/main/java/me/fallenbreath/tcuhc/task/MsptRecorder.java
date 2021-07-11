package me.fallenbreath.tcuhc.task;

import net.minecraft.util.math.MathHelper;

public class MsptRecorder
{
	public long[] lastTickLengths = new long[100];
	private long lastMiliTime = System.currentTimeMillis();
	private long ticks;

	public double getMspt()
	{
		return MathHelper.average(this.lastTickLengths);
	}

	public double getThisTickMspt()
	{
		return System.currentTimeMillis() - this.lastMiliTime;
	}

	public void startTick()
	{
		this.lastMiliTime = System.currentTimeMillis();
	}

	public void endTick()
	{
		this.lastTickLengths[(int)(this.ticks % 100)] = System.currentTimeMillis() - this.lastMiliTime;
		this.ticks++;
	}
}
