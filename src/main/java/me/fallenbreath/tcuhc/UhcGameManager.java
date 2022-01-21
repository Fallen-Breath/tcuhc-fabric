/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc;

import me.fallenbreath.tcuhc.UhcGamePlayer.EnumStat;
import me.fallenbreath.tcuhc.mixins.core.MinecraftServerAccessor;
import me.fallenbreath.tcuhc.mixins.core.SessionAccessor;
import me.fallenbreath.tcuhc.options.Options;
import me.fallenbreath.tcuhc.task.*;
import me.fallenbreath.tcuhc.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;

public class UhcGameManager extends Taskable {

	public static final Logger LOG = LogManager.getLogger("TC UHC");
	public static UhcGameManager instance;
	public static final Random rand = new Random();
	PlayerMatchMakingDataHandler dataHandler = PlayerMatchMakingDataHandler.getDataBase();

	private final MinecraftServer mcServer;
	private PlayerMatchMakingDataHandler Handler = PlayerMatchMakingDataHandler.getDataBase();

	private final UhcPlayerManager playerManager;
	private final UhcConfigManager configManager = new UhcConfigManager();
	private final Options uhcOptions;

	private boolean isGamePlaying;
	private boolean isGameEnded;
	
	private boolean isPregenerating;
	private static boolean preloaded;
	
	public LastWinnerList winnerList;
	private Optional<ServerBossBar> bossInfo = Optional.empty();

	public final MsptRecorder msptRecorder = new MsptRecorder();
	private final UhcWorldData worldData;
	
	public UhcGameManager(MinecraftServer server)
	{
		instance = this;
		mcServer = server;
		uhcOptions = Options.instance;
		playerManager = new UhcPlayerManager(this);
		winnerList = new LastWinnerList(new File("lastwinners.txt"));
		worldData = UhcWorldData.load();
	}

	public MinecraftServer getMinecraftServer() { return mcServer; }
	public PlayerManager getServerPlayerManager() { return mcServer.getPlayerManager(); }
	public UhcPlayerManager getUhcPlayerManager() { return playerManager; }
	public UhcConfigManager getConfigManager() { return configManager; }
	public Options getOptions() { return uhcOptions; }
	public boolean isGamePlaying() { return isGamePlaying; }
	public boolean isConfiguring() { return configManager.isConfiguring(); }
	public boolean hasGameEnded() { return isGameEnded; }
	public static EnumMode getGameMode() { return (EnumMode)instance.getOptions().getOptionValue("gameMode"); }

	public ServerWorld getOverWorld()
	{
		return mcServer.getWorld(World.OVERWORLD);
	}

	public UhcWorldData getWorldData()
	{
		return worldData;
	}

	public void onPlayerJoin(ServerPlayerEntity player) {
		try {
			playerManager.onPlayerJoin(player);
			bossInfo.ifPresent(info -> info.addPlayer(player));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean onPlayerChat(ServerPlayerEntity player, String msg) {
		try {
			if (configManager.onPlayerChat(player, msg))
				playerManager.onPlayerChat(player, msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void onPlayerDeath(ServerPlayerEntity player, DamageSource cause) {
		try {
			playerManager.onPlayerDeath(player, cause);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onPlayerRespawn(ServerPlayerEntity player) {
		try {
			playerManager.onPlayerRespawn(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onPlayerDamaged(ServerPlayerEntity player, DamageSource cause, float amount) {
		try {
			playerManager.onPlayerDamaged(player, cause, amount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Entity onPlayerSpectate(ServerPlayerEntity player, Entity target, Entity origin) {
		try {
			return playerManager.onPlayerSpectate(player, target, origin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}
	
	public void onServerInited()
	{
		this.displayHealth();
		TaskScoreboard.hideScoreboard();
		if (!preloaded) {
			int borderStart = uhcOptions.getIntegerOptionValue("borderStart");
			int radius = borderStart / 32;
			this.addTask(new TaskPregenerate(mcServer, radius + 5, getOverWorld()));
			this.addTask(new TaskPregenerate(mcServer, radius / 8 + 10, mcServer.getWorld(World.NETHER)));
			isPregenerating = true;
		}
		SpawnPlatform.generatePlatform(this, getOverWorld());
		this.addTask(new TaskHUDInfo(mcServer));
	}
	
	public void setPregenerateComplete() {
		isPregenerating = false;
	}
	
	public boolean isPregenerating() {
		return isPregenerating;
	}
	
	public float modifyPlayerDamage(float amount) {
		if (isGamePlaying) {
			boolean greenHand = uhcOptions.getBooleanOptionValue("greenhandProtect");
			int time = uhcOptions.getIntegerOptionValue("greenhandTime");
			int gameTime = uhcOptions.getIntegerOptionValue("gameTime") - this.getGameTimeRemaining();
			if (greenHand && gameTime < time) return amount / 2;
		}
		return amount;
	}
	
	public static void tryUpdateSaveFolder(Path saveFolder) {
		if (!saveFolder.resolve("preload").toFile().exists()) {
			LOG.warn("Deleting {} for UHC world regenerate", saveFolder);
			deleteFolder(saveFolder.toFile());
		} else {
			preloaded = true;
		}
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isDirectory()) deleteFolder(file);
			else if (!file.getName().equals("carpet.conf")) file.delete();
		}
	}

	public static File getPreloadFile() {
		return ((SessionAccessor)((MinecraftServerAccessor)instance.mcServer).getSession()).getDirectory().resolve("preload").toFile();
	}

	public static File getDataFile() {
		return ((SessionAccessor)((MinecraftServerAccessor)instance.mcServer).getSession()).getDirectory().resolve("uhc.json").toFile();
	}
	
	public static void regenerateTerrain() {
		File preload = getPreloadFile();
		if (preload.exists()) preload.delete();
		instance.mcServer.stop(false);  // TODO: Check param
	}
	
	public void startGame(ServerPlayerEntity operator) {
		if (isGamePlaying || !configManager.isConfiguring()) {
			operator.sendMessage(new LiteralText("It's not time to start."), false);
			return;
		}
		boolean autoTeams = uhcOptions.getBooleanOptionValue("randomTeams");
		if (!playerManager.formTeams(autoTeams)) return;
		switch (getGameMode()) {
			case BOSS:
				bossInfo = Optional.of(new ServerBossBar(new LiteralText(playerManager.getBossPlayer().getName()), BossBar.Color.PURPLE, BossBar.Style.PROGRESS));
				getServerPlayerManager().getPlayerList().forEach(player -> bossInfo.ifPresent(info -> info.addPlayer(player)));
				bossInfo.ifPresent(info -> info.setVisible(true));
				break;
		}
		isGamePlaying = true;
		this.initWorlds();
		configManager.stopConfiguring();
		playerManager.setupIngameTeams();
		playerManager.spreadPlayers();
		this.destroySpawnPlatform();
		this.addTask(new TaskTitleCountDown(10, 80, 20));
	}
	
	public void endGame() {
		if (isGameEnded) return;
		isGameEnded = true;
		removeWorldBorder();
		TaskScoreboard.hideScoreboard();
		//暂停接受UHC regen的指令,计算和更新pp
		playerManager.endPlayerCal();
		bossInfo.ifPresent(info -> info.setVisible(false));
		bossInfo = Optional.empty();
	}
	
	public void checkWinner() {
		if (isGameEnded || !isGamePlaying) return;
		int remainTeamCnt = 0;
		UhcGameTeam winner = null;

		for (UhcGameTeam team : playerManager.getTeams()) {
			if (team.getAliveCount() > 0) {
				remainTeamCnt++;
				winner = team;

			}
		}
		if (remainTeamCnt == 1)
			this.onTeamWin(winner);
	}
	
	private void onTeamWin(UhcGameTeam team) {
		TitleUtil.sendTitleToAllPlayers(team.getColorfulTeamName() + " Wins !", "Congratulations !");
		this.broadcastMessage(team.getColorfulTeamName() + " is the winner !");
		for (UhcGamePlayer player : playerManager.getCombatPlayers()) {
			if (player.getStat().getFloatStat(EnumStat.ALIVE_TIME) < 1)
				player.getStat().setStat(EnumStat.ALIVE_TIME, uhcOptions.getIntegerOptionValue("gameTime") - this.getGameTimeRemaining());
		}
		winnerList.setWinner(team.getPlayers());
		for (UhcGameTeam t:playerManager.getTeams()){
			if (t == team){
				for (UhcGamePlayer player:t.getPlayers()){ //更新每个玩家的连胜数据
					dataHandler.processWinStreak(player.getPlayerUUID(),true);
					System.out.println("Win: "+player.getPlayerUUID()+"\n");
				}
			} else {
				for (UhcGamePlayer player:t.getPlayers()){//更新每个玩家的连胜数据
					dataHandler.processWinStreak(player.getPlayerUUID(),false);
					System.out.println("Lost: "+player.getPlayerUUID()+"\n");
				}
			}
		}
		this.endGame();
		this.addTask(new TaskBroadcastData(160));
	}
	
	private void initWorlds() {
		boolean daylightCycle = uhcOptions.getBooleanOptionValue("daylightCycle");
		Difficulty difficulty = (Difficulty) uhcOptions.getOptionValue("difficulty");
		int borderStart = uhcOptions.getIntegerOptionValue("borderStart");
		for (ServerWorld world : mcServer.getWorlds()) {
			world.getGameRules().get(GameRules.NATURAL_REGENERATION).set(false, mcServer);
			world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(daylightCycle, mcServer);
			world.setTimeOfDay(0);
			world.getWorldBorder().setSize(borderStart);
		}
		mcServer.setDifficulty(difficulty, true);
	}
	
	private void removeWorldBorder() {
		for (ServerWorld world : mcServer.getWorlds()) {
			world.getWorldBorder().setSize(world.getWorldBorder().getMaxRadius());
		}
	}
	
	public void displayHealth() {
		Scoreboard scoreboard = getMainScoreboard();
		String name = "Health";
		ScoreboardObjective objective;
		if ((objective = scoreboard.getObjective(name)) == null) {
			objective = scoreboard.addObjective(name, ScoreboardCriterion.HEALTH, new LiteralText(name), ScoreboardCriterion.RenderType.HEARTS);
		}
		scoreboard.setObjectiveSlot(0, objective);
		scoreboard.setObjectiveSlot(2, objective);
	}
	
	public Scoreboard getMainScoreboard() {
		return getOverWorld().getScoreboard();
	}
	
	public void tick() {
		try {
			this.updateTasks();
			if (!this.isGamePlaying)
				this.winnerParticles();
			for (UhcGamePlayer player : playerManager.getAllPlayers()) {
				player.tick();
			}
			bossInfo.ifPresent(info -> playerManager.getBossPlayer().getRealPlayer().ifPresent(player -> info.setPercent(player.getHealth() / player.getMaxHealth())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void winnerParticles() {
		for (ServerPlayerEntity player : getServerPlayerManager().getPlayerList()) {
			if (player.age % 2 == 0 && winnerList.isWinner(player.getEntityName())) {
				double angle = (player.age % 360) * 9 * Math.PI / 180;
				double dx = Math.cos(angle) * 0.6;
				double dz = Math.sin(angle) * 0.6;
				double dy = Math.cos(angle) * 0.4;
				((ServerWorld) player.world).spawnParticles(ParticleTypes.FLAME, player.getPos().getX() + dx, player.getPos().getY() + dy + player.getStandingEyeHeight() / 2, player.getPos().getZ() + dz, 1, 0, 0, 0, 0);
				((ServerWorld) player.world).spawnParticles(ParticleTypes.FLAME, player.getPos().getX() - dx, player.getPos().getY() + dy + player.getStandingEyeHeight() / 2, player.getPos().getZ() - dz, 1, 0, 0, 0, 0);
			}
		}
	}
	
	public void generateSpawnPlatform() { SpawnPlatform.generatePlatform(this, getOverWorld()); }
	public void destroySpawnPlatform() { SpawnPlatform.destroyPlatform(getOverWorld()); }
	
	public void startConfiguration(ServerPlayerEntity operator) {
		configManager.startConfiguring(playerManager.getGamePlayer(operator));
		operator.getInventory().insertStack(BookNBT.getConfigBook(this));
		if (!UhcGameManager.instance.isGamePlaying()) SpawnPlatform.generateSafePlatform(getOverWorld());
	}
	
	public void broadcastMessage(String msg) {
		BaseText text = new LiteralText(msg);
		getServerPlayerManager().getPlayerList().forEach(player -> player.sendMessage(text, false));
		LOG.info(msg);
	}
	
	public BlockPos buildSmallHouse(BlockPos pos, DyeColor color) {
		World world = getOverWorld();
		world.getBlockState(pos);
		pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).down();
		ColorUtil.ColorfulBlocks colorfulBlocks = ColorUtil.fromColor(color);
		BlockState floor = colorfulBlocks.wool.getDefaultState();
		BlockState wall = colorfulBlocks.glassPane.getDefaultState();
		BlockState ceiling = colorfulBlocks.glass.getDefaultState();
		for (int x = -3; x <= 3; x++) {
			for (int z = -3; z <= 3; z++) {
				world.setBlockState(pos.add(x, 0, z), floor);
				world.setBlockState(pos.add(x, 4, z), ceiling);
				if (x == -3 || x == 3 || z == -3 || z == 3) {
					for (int y = 1; y <= 3; y++) {
						world.setBlockState(pos.add(x, y, z), wall);
					}
				} else {
					for (int y = 1; y <= 3; y++) {
						world.setBlockState(pos.add(x, y, z), Blocks.AIR.getDefaultState());
					}
				}
			}
		}
		world.setBlockState(pos.up(), Blocks.CHEST.getDefaultState());
		return pos.up();
	}
	
	public int getGameTimeRemaining() {
		Scoreboard scoreboard = getMainScoreboard();
		ScoreboardObjective objective = scoreboard.getObjective(TaskScoreboard.scoreName);
		return scoreboard.getPlayerScore(TaskScoreboard.lines[0], objective).getScore();
	}
	
	public static enum EnumMode {
		NORMAL(true),
		SOLO(false),
		BOSS(false),
		GHOST(false),
		KING(true);

		private final boolean deathRegen;

		EnumMode(boolean deathRegen)
		{
			this.deathRegen = deathRegen;
		}

		public boolean doDeathRegen()
		{
			return deathRegen;
		}
	}

}
