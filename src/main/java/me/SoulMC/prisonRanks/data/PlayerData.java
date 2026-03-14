package me.SoulMC.prisonRanks.data;

public class PlayerData {

    private String rank;
    private int prestige;
    private int rebirth;

    public PlayerData(String rank, int prestige, int rebirth) {
        this.rank = rank;
        this.prestige = prestige;
        this.rebirth = rebirth;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getPrestige() {
        return prestige;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public int getRebirth() {
        return rebirth;
    }

    public void setRebirth(int rebirth) {
        this.rebirth = rebirth;
    }
}
