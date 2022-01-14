package me.fallenbreath.tcuhc.util.collection;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.util.Util;

import java.util.Map;

public class ExpiringMap<T> extends Long2ObjectOpenHashMap<T>
{
	private final int lifespanMs;
	private final Long2LongMap timeMap = new Long2LongLinkedOpenHashMap();

	public ExpiringMap(int lifespanIn)
	{
		this.lifespanMs = lifespanIn;
	}

	private void refreshTimes(long key)
	{
		long currentTime = Util.getMeasuringTimeMs();
		this.timeMap.put(key, currentTime);
		ObjectIterator<Long2LongMap.Entry> iter = this.timeMap.long2LongEntrySet().iterator();

		while (iter.hasNext())
		{
			Long2LongMap.Entry entry = iter.next();
			T element = super.get(entry.getLongKey());

			if (currentTime - entry.getLongValue() <= (long)this.lifespanMs)
			{
				break;
			}

			if (element != null && this.shouldExpire(element))
			{
				super.remove(entry.getLongKey());
				iter.remove();
			}
		}
	}

	protected boolean shouldExpire(T element)
	{
		return true;
	}

	public T put(long k, T t)
	{
		this.refreshTimes(k);
		return super.put(k, t);
	}

	public T put(Long k, T t)
	{
		this.refreshTimes(k);
		return super.put(k, t);
	}

	public T get(long k)
	{
		this.refreshTimes(k);
		return super.get(k);
	}

	public void putAll(Map <? extends Long, ? extends T > map)
	{
		throw new RuntimeException("Not implemented");
	}

	public T remove(long k)
	{
		throw new RuntimeException("Not implemented");
	}

	public T remove(Object k)
	{
		throw new RuntimeException("Not implemented");
	}
}
