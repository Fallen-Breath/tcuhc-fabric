package me.fallenbreath.tcuhc.util;

import java.util.UUID;

public class PlayerMatchMakingData {
    private UUID UUID;
    private int WStreak;
    private double hisPP;
    public PlayerMatchMakingData(UUID UUID,int WStreak, double hisPP){
        this.UUID = UUID;
        this.WStreak = WStreak;
        this.hisPP = hisPP;
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

    public void setUUID(UUID UUID) {
        this.UUID = UUID;
    }

    public void setWStreak(int WStreak) {
        this.WStreak = WStreak;
    }
}
