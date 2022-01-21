package me.fallenbreath.tcuhc.util;

public class PlayerMatchMakingData {
    private String UUID;
    private int WStreak;
    private int hisPP;
    public PlayerMatchMakingData(String UUID){
        this.UUID = UUID;
    }

    public int getHisPP() {
        return hisPP;
    }

    public int getWStreak() {
        return WStreak;
    }

    public String getUUID() {
        return UUID;
    }

    public void setHisPP(int hisPP) {
        this.hisPP = hisPP;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setWStreak(int WStreak) {
        this.WStreak = WStreak;
    }
}
