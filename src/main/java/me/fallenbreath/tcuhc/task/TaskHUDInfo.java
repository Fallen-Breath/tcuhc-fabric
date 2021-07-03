package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.mixins.task.PlayerListHeaderS2CPacketAccessor;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;

public class TaskHUDInfo extends Task.TaskTimer
{
	private final MinecraftServer mcServer;

	public TaskHUDInfo(MinecraftServer mcServer)
	{
		super(0, 5);
		this.mcServer = mcServer;
	}

	private BaseText getHUDTexts(ServerPlayerEntity player)
	{
		BaseText text = new LiteralText("");
		double mspt = MathHelper.average(this.mcServer.lastTickLengths) * 1.0E-6D;
		double tps = 1000.0D / Math.max(mspt, 50.0D);
		text.append(new LiteralText(String.format("TPS: %.1f MSPT: %.1f Ping: %dms", tps, mspt, player.pingMilliseconds)));
		return text;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onTimer()
	{
		this.mcServer.getPlayerManager().getPlayerList().forEach(player -> {
			PlayerListHeaderS2CPacket packet = new PlayerListHeaderS2CPacket();
			PlayerListHeaderS2CPacketAccessor accessor = (PlayerListHeaderS2CPacketAccessor)packet;
			accessor.setHeader(new LiteralText(""));
			accessor.setFooter(this.getHUDTexts(player));
			player.networkHandler.sendPacket(packet);
		});
	}
}
