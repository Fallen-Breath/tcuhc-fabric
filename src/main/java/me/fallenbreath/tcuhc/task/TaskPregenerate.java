/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.mixins.task.ServerChunkManagerAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskPregenerate extends Task
{
	public static final ChunkTicketType<ChunkPos> PRE_GENERATE = ChunkTicketType.create("pre_generate", Comparator.comparingLong(ChunkPos::toLong));

	private static final int PARALLELISM_LIMIT = Runtime.getRuntime().availableProcessors() * 2;
	private static final int ENQUEUE_THRESHOLD = PARALLELISM_LIMIT / 3;

	private long startTimeMili;
	private final List<ChunkPos> chunkToLoad;
	private final Iterator<ChunkPos> iterator;
	private final MinecraftServer mcServer;
	private final ServerWorld world;
	private final AtomicInteger loadedChunkAmount = new AtomicInteger(0);
	private final AtomicInteger queuedCount = new AtomicInteger(0);

	public TaskPregenerate(MinecraftServer mcServer, int borderSize, ServerWorld worldServer)
	{
		this.mcServer = mcServer;
		this.world = worldServer;
		this.chunkToLoad = createChunkToLoadList(borderSize);
		this.iterator = this.chunkToLoad.iterator();
	}

	private String getWorldName()
	{
		return Objects.requireNonNull(DimensionType.getId(world.getDimension().getType())).getPath();
	}

	private void tryGenerateChunks()
	{
		int count = PARALLELISM_LIMIT - this.queuedCount.get();
		List<ChunkPos> chunks = Lists.newArrayList();
		for (int i = 0; i < count && this.iterator.hasNext(); i++)
		{
			chunks.add(this.iterator.next());
		}
		if (!chunks.isEmpty())
		{
			this.mcServer.execute(() -> this.generateChunks(chunks));
		}
	}

	private void generateChunks(List<ChunkPos> chunks)
	{
		chunks.forEach(this::addTicketAt);
		ServerChunkManager chunkManager = this.world.getChunkManager();
		((ServerChunkManagerAccessor) chunkManager).invokeTick();
		chunks.forEach(chunkPos -> {
			this.queuedCount.incrementAndGet();
			ChunkHolder holder = ((ServerChunkManagerAccessor)chunkManager).invokeGetChunkHolder(chunkPos.toLong());
			if (holder == null)
			{
				this.acceptChunkResult(chunkPos, ChunkHolder.UNLOADED_CHUNK);
			}
			else
			{
				holder.createFuture(ChunkStatus.FULL, chunkManager.threadedAnvilChunkStorage).thenAccept(result -> this.acceptChunkResult(chunkPos, result));
			}
		});
	}

	private void acceptChunkResult(ChunkPos chunkPos, Either<Chunk, ChunkHolder.Unloaded> result)
	{
		this.mcServer.execute(() -> this.removeTicketAt(chunkPos));
		result.left().orElseThrow(() -> new RuntimeException("Pregenerate for chunk " + chunkPos + " failed"));
		this.loadedChunkAmount.incrementAndGet();
		if (this.queuedCount.decrementAndGet() <= ENQUEUE_THRESHOLD)
		{
			this.tryGenerateChunks();
		}
	}

	private void addTicketAt(ChunkPos pos)
	{
		this.world.getChunkManager().addTicket(PRE_GENERATE, pos, 0, pos);
	}

	private void removeTicketAt(ChunkPos pos)
	{
		this.world.getChunkManager().removeTicket(PRE_GENERATE, pos, 0, pos);
	}

	@Override
	public boolean hasFinished()
	{
		return !this.iterator.hasNext();
	}

	private static String makeTime(long miliSeconds)
	{
		return String.format("%.2fmin", (double)miliSeconds / (1000 * 60));
	}

	@Override
	public void onUpdate()
	{
		long miliPassed = Util.getMeasuringTimeMs() - this.startTimeMili;
		boolean log = this.mcServer.getTicks() % (20 * 5) == 0;
		boolean say = this.mcServer.getTicks() % (20 * 30) == 0;
		int current = this.loadedChunkAmount.get();
		int total = this.chunkToLoad.size();
		double percentage = 100.0 * current / total;
		long milliEta = current > 0 ? miliPassed * (total - current) / current : -1;
		if (log)
		{
			UhcGameManager.LOG.info(String.format("%d/%d %.2f%% chunks of %s loaded.", current, total, percentage, getWorldName()));
		}
		if (say)
		{
			UhcGameManager.instance.broadcastMessage(String.format("Chunk generate of %s: %.2f%%, ETA %s", getWorldName(), percentage, makeTime(milliEta)));
		}
	}

	@Override
	public void onAdd()
	{
		this.world.getChunkManager().getLightingProvider().setTaskBatchSize(500);
		this.startTimeMili = Util.getMeasuringTimeMs();
		this.tryGenerateChunks();
	}

	@Override
	public void onFinish()
	{
		long miliPassed = Util.getMeasuringTimeMs() - this.startTimeMili;
		UhcGameManager.instance.broadcastMessage(String.format("Pre-generating of %s finished, took %s", getWorldName(), makeTime(miliPassed)));
		this.world.getChunkManager().getLightingProvider().setTaskBatchSize(5);
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

	private static List<ChunkPos> createChunkToLoadList(int targetRadius)
	{
		final byte NORTH = 0;
		final byte SOUTH = 1;
		final byte EAST = 2;
		final byte WEST = 3;
		byte state = NORTH;
		int x = 0, z = 0;
		int currentRadius = 0;
		boolean done = false;
		List<ChunkPos> list = Lists.newArrayList();
		while (!done)
		{
			list.add(new ChunkPos(x, z));
			switch (state)
			{
				case NORTH:
					if (--z <= -currentRadius)  // < for currentRadius == 0
					{
						state = WEST;
						if (currentRadius > targetRadius)
						{
							done = true;
						}
					}
					break;
				case SOUTH:
					if (++z == currentRadius)
					{
						state = EAST;
					}
					break;
				case WEST:
					if (--x == -currentRadius)
					{
						state = SOUTH;
					}
					break;
				case EAST:
					if (++x == currentRadius)
					{
						state = NORTH;
						currentRadius++;
					}
					break;
			}
			if (currentRadius == 0)
			{
				currentRadius++;
			}
		}
		return list;
	}
}
