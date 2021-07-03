package me.fallenbreath.tcuhc.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Set;

public class LastWinnerList {
	private final Set<String> winnerList = Sets.newHashSet();
	private final File winnerFile;

	public LastWinnerList(File listFile) {
		winnerFile = listFile;
		readFile();
	}

	public boolean isWinner(String name) {
		return winnerList.contains(name);
	}

	public void setWinner(Iterable<UhcGamePlayer> playerList) {
		winnerList.clear();
		if (playerList == null)
			return;
		playerList.forEach(player -> winnerList.add(player.getName()));
		saveFile();
	}

	private void saveFile() {
		BufferedWriter bufferedwriter = null;
		try {
			bufferedwriter = Files.newWriter(this.winnerFile, Charsets.UTF_8);
			for (String name : winnerList)
				bufferedwriter.write(name + System.getProperty("line.separator", "\n"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly((Writer) bufferedwriter);
		}
	}

	private void readFile() {
		BufferedReader bufferedreader = null;
		winnerList.clear();
		String name;
		try {
			bufferedreader = Files.newReader(this.winnerFile, Charsets.UTF_8);
			while (bufferedreader.ready())
				if ((name = bufferedreader.readLine()) != null)
					if (!name.isEmpty())
						winnerList.add(name);
		} catch (FileNotFoundException ignored) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly((Reader) bufferedreader);
		}
	}
}
