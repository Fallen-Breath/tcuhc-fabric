/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc;

import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.UhcGamePlayer.EnumStat;
import net.minecraft.util.Formatting;

import java.util.List;

public class UhcGameTeam {

    private final List<UhcGamePlayer> players = Lists.newArrayList();
    private final TeamType type = new TeamType();

    public UhcGameTeam setColorTeam(UhcGameColor color) {
        type.setColor(color);
        players.clear();
        return this;
    }

    public UhcGameTeam setPlayerTeam(UhcGamePlayer player) {
        type.setPlayer(player);
        players.clear();
        addPlayer(player);
        return this;
    }

    public String getTeamName() {
        return type.teamName;
    }

    public String getColorfulTeamName() {
        return type.getColorfulName();
    }

    public UhcGameColor getTeamColor() {
        return type.color;
    }

    public UhcGameTeam addPlayer(UhcGamePlayer player) {
        players.add(player);
        player.setTeam(this);
        return this;
    }

    public UhcGameTeam removePlayer(UhcGamePlayer player) {
        players.remove(player);
        return this;
    }

    public Iterable<UhcGamePlayer> getPlayers() {
        return players;
    }

    // The first player of the team is always the king
    public UhcGamePlayer getKing() {
        return UhcGameManager.getGameMode() == UhcGameManager.EnumMode.KING ? players.get(0) : null;
    }

    public void clearTeam() {
        players.clear();
    }

    public int getAliveCount() {
        return (int) players.stream().filter(UhcGamePlayer::isAlive).count();
    }

    public int getKillCount() {
        return (int) (players.stream().mapToDouble(player -> player.getStat().getFloatStat(EnumStat.PLAYER_KILLED)).sum() + 0.5);
    }

    public int getPlayerCount() {
        return players.size();
    }

    static class TeamType {
        private UhcGameColor color = UhcGameColor.WHITE;
        private UhcGamePlayer player;
        private String name = "Team Empty", teamName = "Team Empty";

        public void setColor(UhcGameColor color) {
            this.color = color;
            player = null;
            teamName = name = "Team " + color.name;
        }

        public void setPlayer(UhcGamePlayer player) {
            this.player = player;
            color = UhcGameColor.randomColor();
            teamName = player.getName();
            name = "Team " + teamName;
        }

        public String getColorfulName() {
            return color.chatColor + name + Formatting.RESET;
        }
    }

}
