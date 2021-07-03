package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class TitleUtil
{
	public static void sendTitleToPlayer(String title, String subtitle, ServerPlayerEntity player)
	{
		TitleS2CPacket titlePacket = new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText(title));
		player.networkHandler.sendPacket(titlePacket);
		if (subtitle != null)
		{
			TitleS2CPacket subtitlePacket = new TitleS2CPacket(TitleS2CPacket.Action.SUBTITLE, new LiteralText(subtitle));
			player.networkHandler.sendPacket(subtitlePacket);
		}
	}

	public static void sendTitleToAllPlayers(String title, String subtitle)
	{
		for (ServerPlayerEntity player : UhcGameManager.instance.getServerPlayerManager().getPlayerList())
		{
			sendTitleToPlayer(title, subtitle, player);
		}
	}
}
