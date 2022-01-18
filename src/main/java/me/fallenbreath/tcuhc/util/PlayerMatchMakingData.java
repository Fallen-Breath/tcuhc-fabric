package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGamePlayer;

import java.util.UUID;

public class PlayerMatchMakingData {
	private final UUID UUID;
	private final String name;
	private int WStreak;
	private double hisPP;

	public PlayerMatchMakingData(UUID UUID, int WStreak, double hisPP,String name) {
		this.UUID = UUID;
		this.WStreak = WStreak;
		this.hisPP = hisPP;
		this.name = name;
	}

	public double getHisPP() {
		return hisPP;
	}

	public int getWStreak() {
		return WStreak;
	}

	public UUID getUUID() {
		return UUID;
	}

	public void setHisPP(double hisPP) {
		this.hisPP = hisPP;
	}

	public void setWStreak(int WStreak) {
		this.WStreak = WStreak;
	}

	public String getName() {
		return name;
	}

}
