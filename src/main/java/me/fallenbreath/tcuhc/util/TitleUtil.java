/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class TitleUtil
{
	public static void sendTitleToPlayer(String title, String subtitle, ServerPlayerEntity player)
	{
		TitleS2CPacket titlePacket = new TitleS2CPacket(new LiteralText(title));
		player.networkHandler.sendPacket(titlePacket);
		if (subtitle != null)
		{
			SubtitleS2CPacket subtitlePacket = new SubtitleS2CPacket(new LiteralText(subtitle));
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
