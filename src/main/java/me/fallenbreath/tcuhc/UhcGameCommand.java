package me.fallenbreath.tcuhc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.fallenbreath.tcuhc.options.Option;
import me.fallenbreath.tcuhc.options.Options;
import me.fallenbreath.tcuhc.task.TaskOnce;
import me.fallenbreath.tcuhc.util.PlayerItems;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandSource.suggestMatching;

public class UhcGameCommand
{
	private static final String PREFIX = "uhc";

	private static boolean isOp(ServerCommandSource source)
	{
		return source.hasPermissionLevel(2);
	}

	private static Stream<String> getGamePlayerNameSuggestion()
	{
		return UhcGameManager.instance.getUhcPlayerManager().getAllPlayers().stream().map(UhcGamePlayer::getName);
	}

	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		LiteralArgumentBuilder<ServerCommandSource> generalCommands = literal(PREFIX).
				executes(c -> sendVersionInfo(c.getSource())).
				then(literal("version").executes(c -> sendVersionInfo(c.getSource()))).
				then(literal("select").
						then(argument("team_id", integer(0, 9)).
								executes(c -> selectTeam(c.getSource(), getInteger(c, "team_id")))
						)
				).
				then(literal("deathpos").executes(c -> sendDeathPos(c.getSource())));

		LiteralArgumentBuilder<ServerCommandSource> opCommands = literal(PREFIX).
				requires(UhcGameCommand::isOp).
				then(literal("config").executes(c -> giveConfig(c.getSource()))).
				then(literal("reset").
						then(argument("value", integer(0, 1)).
								executes(c -> executeReset(c.getSource(), getInteger(c, "value")))
						)
				).
				then(literal("regen").executes(c -> executeRegen(c.getSource()))).
				then(literal("start").executes(c -> executeStart(c.getSource()))).
				then(literal("stop").executes(c -> executeStop(c.getSource()))).
				then(literal("option").
						then(argument("name", string()).
								suggests((c, b) -> suggestMatching(UhcGameManager.instance.getOptions().getOptionIdStream(), b)).
								then(argument("operation", string()).
										suggests((c, b) -> suggestMatching(new String[]{"add", "sub", "set"}, b)).
										executes(c -> manipulateOption(c.getSource(), getString(c, "name"), getString(c, "operation")))
								)
						)
				).
				then(literal("adjust").
						executes(c -> regiveAdjustBook(c.getSource(), true)).
						then(literal("end").executes(c -> removeAdjustBook(c.getSource()))).
						then(literal("kill").
								then(argument("player", string()).
										suggests((c, b) -> suggestMatching(getGamePlayerNameSuggestion(), b)).
										executes(c -> killPlayer(c.getSource(), getString(c, "player")))
								)
						).
						then(literal("resu").
								then(argument("player", string()).
										suggests((c, b) -> suggestMatching(getGamePlayerNameSuggestion(), b)).
										executes(c -> resurrentPlayer(c.getSource(), getString(c, "player")))
								)
						)
				).
				then(literal("givemorals").
						executes(c -> giveMorals(c.getSource(), null)).
						then(argument("player", string()).
								suggests((c, b) -> suggestMatching(PlayerItems.getAvailableNames(), b)).
								executes(c -> giveMorals(c.getSource(), getString(c, "player")))
						)
				);
		dispatcher.register(generalCommands);
		dispatcher.register(opCommands);
	}

	private static int sendVersionInfo(ServerCommandSource sender) {
		sender.sendFeedback(new LiteralText(Formatting.GOLD + "== UHC Plugin for " + Formatting.RED + "T" + Formatting.BLUE + "opology" + Formatting.RED + "C" + Formatting.BLUE + "raft" + Formatting.GOLD + " =="), false);
		sender.sendFeedback(new LiteralText("          " + Formatting.GREEN + "Plugin Version " + Formatting.GOLD + "1.4.6"), false);
		sender.sendFeedback(new LiteralText("     " + Formatting.GREEN + "Minecraft Version " + Formatting.GOLD + "1.12-pre7"), false);
		return 1;
	}

	private static int selectTeam(ServerCommandSource sender, int teamId) throws CommandSyntaxException
	{
		UhcGameColor color = UhcGameColor.getColor(teamId);
		ServerPlayerEntity player = sender.getPlayer();
		UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(player).setColorSelected(color);
		UhcGameManager.instance.getUhcPlayerManager().regiveConfigItems(player);
		return 1;
	}

	private static int sendDeathPos(ServerCommandSource sender) throws CommandSyntaxException
	{
		UhcGamePlayer gamePlayer = UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(sender.getPlayer());
		BlockPos pos = gamePlayer.getDeathPos();
		if (pos.equals(BlockPos.ORIGIN))
		{
			sender.sendFeedback(new LiteralText("You are still alive."), false);
		}
		else
		{
			sender.sendFeedback(new LiteralText(String.format("[%d, %d, %d]", pos.getX(), pos.getY(), pos.getZ())), false);
		}
		return 1;
	}

	private static int giveConfig(ServerCommandSource sender)
	{
		try
		{
			UhcGameManager.instance.startConfiguration(sender.getPlayer());
			UhcGameManager.instance.getOptions().savePropertiesFile();
			if (!UhcGameManager.instance.isGamePlaying())
			{
				UhcGameManager.instance.addTask(new TaskOnce(Options.instance.taskReselectTeam));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1;
	}

	private static int executeReset(ServerCommandSource sender, int value)
	{
		Options.instance.resetOptions(value == 1);
		UhcGameManager.instance.getUhcPlayerManager().refreshConfigBook();
		return 1;
	}

	private static int executeRegen(ServerCommandSource sender)
	{
		UhcGameManager.regenerateTerrain();
		return 1;
	}

	private static int executeStart(ServerCommandSource sender) throws CommandSyntaxException
	{
		UhcGameManager.instance.startGame(sender.getPlayer());
		return 1;
	}

	private static int executeStop(ServerCommandSource sender)
	{
		UhcGameManager.instance.endGame();
		return 1;
	}

	private static int manipulateOption(ServerCommandSource sender, String optionName, String operation)
	{
		Optional<Option> optional = UhcGameManager.instance.getOptions().getOption(optionName);
		if (optional.isPresent())
		{
			Option option = optional.get();
			switch (operation)
			{
				case "add":
					option.incValue();
					break;
				case "sub":
					option.decValue();
					break;
				case "set":
					UhcGameManager.instance.getConfigManager().inputOptionValue(option);
					sender.sendFeedback(new LiteralText(String.format("Input the value for %s:", option.getName())), false);
					break;
				default:
					sender.sendFeedback(new LiteralText(String.format("Unknown operation %s", operation)), false);
			}
		}
		else
		{
			sender.sendFeedback(new LiteralText(String.format("Unknown option %s", optionName)), false);
		}
		return 1;
	}

	private static int regiveAdjustBook(ServerCommandSource source, boolean force) throws CommandSyntaxException
	{
		UhcGameManager.instance.getUhcPlayerManager().regiveAdjustBook(source.getPlayer(), force);
		return 1;
	}

	private static int removeAdjustBook(ServerCommandSource source) throws CommandSyntaxException
	{
		UhcGameManager.instance.getUhcPlayerManager().removeAdjustBook(source.getPlayer());
		return 1;
	}

	private static boolean ensureGameIsPlaying(ServerCommandSource source)
	{
		if (UhcGameManager.instance.isGamePlaying())
		{
			return true;
		}
		else
		{
			source.sendFeedback(new LiteralText(Formatting.RED + "Game has not started yet"), false);
			return false;
		}
	}

	private static int resurrentPlayer(ServerCommandSource source, String player) throws CommandSyntaxException
	{
		if (ensureGameIsPlaying(source))
		{
			UhcGameManager.instance.getUhcPlayerManager().resurrentPlayer(player);
			regiveAdjustBook(source, false);
		}
		return 1;
	}

	private static int killPlayer(ServerCommandSource source, String player) throws CommandSyntaxException
	{
		if (ensureGameIsPlaying(source))
		{
			UhcGameManager.instance.getUhcPlayerManager().killPlayer(player);
			regiveAdjustBook(source, false);
		}
		return 1;
	}

	private static int giveMorals(ServerCommandSource sender, String targetPlayerName) throws CommandSyntaxException
	{
		PlayerItems.dumpMoralsToPlayer(sender.getPlayer(), targetPlayerName);
		return 1;
	}
}
