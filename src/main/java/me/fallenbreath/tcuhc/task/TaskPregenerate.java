/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;

public class TaskPregenerate extends Task
{
	public static final ChunkTicketType<ChunkPos> PRE_GENERATE = ChunkTicketType.method_20628("pre_generate", Comparator.comparingLong(ChunkPos::toLong), 5);

	private int size;
	private int x;
	private int z;

	private boolean inited;
	private MinecraftServer mcServer;
	private ServerWorld world;
	private ServerChunkManager provider;
	private int lastRatio = 0;

	public TaskPregenerate(MinecraftServer mcServer, int borderSize, ServerWorld worldServer)
	{
		this.mcServer = mcServer;
		this.world = worldServer;
		this.size = borderSize;
		this.inited = false;
		this.x = this.z = -size;
		this.provider = this.world.getChunkManager();
	}

	private String getWorldName()
	{
		return Objects.requireNonNull(DimensionType.getId(world.getDimension().getType())).getPath();
	}

	private void generateChunk(int x, int z)
	{
		ChunkPos chunkPos = new ChunkPos(x, z);
		this.provider.addTicket(PRE_GENERATE, chunkPos, 0, chunkPos);
		this.world.getChunk(x, z);
	}

	private void genOneChunk()
	{
		if (this.hasFinished())
			return;

		if (!inited)
		{
			if (x > size)
			{
				if (z > size)
				{
					inited = true;
					x = z = -size + 1;
					return;
				}
				generateChunk(-size, z);
				z++;
				return;
			}
			generateChunk(x, -size);
			x++;
			return;
		}
		generateChunk(x, z);

		x++;
		if (x > size)
		{
			x = -size + 1;
			z++;
			float ratio = (float) (z + size) * 100 / (2 * size + 1);
			UhcGameManager.LOG.info(String.format("%.2f%% chunks of %s loaded.", (float) (z + size) * 100 / (2 * size + 1), getWorldName()));
			if (ratio + 1e-4 > lastRatio)
			{
				UhcGameManager.instance.broadcastMessage(String.format("Chunk generate of %s progress: %d%%", getWorldName(), lastRatio));
				lastRatio += 10;
			}
		}
	}

	@Override
	public void onUpdate()
	{
		do
		{
			genOneChunk();
		}
		while (((IMinecraftServer)this.mcServer).hasTimeLeft());
	}

	@Override
	public boolean hasFinished()
	{
		return inited && z > size;
	}

	@Override
	public void onFinish()
	{
		UhcGameManager.LOG.info(String.format("Pre-generating of %s finished.", getWorldName()));
		this.provider.getLightingProvider().setTaskBatchSize(5);
		if (this.world == UhcGameManager.instance.getOverWorld())
		{
			try
			{
				File preload = mcServer.getLevelStorage().resolveFile(mcServer.getLevelName(), "preload");
				if (!preload.exists())
					preload.createNewFile();
				UhcGameManager.instance.setPregenerateComplete();
			}
			catch (IOException ignored)
			{
			}
		}
	}

	public interface IMinecraftServer
	{
		boolean hasTimeLeft();
	}
}
