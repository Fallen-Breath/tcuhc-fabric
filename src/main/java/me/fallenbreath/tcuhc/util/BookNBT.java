/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGameColor;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.UhcGameTeam;
import me.fallenbreath.tcuhc.options.Option;
import me.fallenbreath.tcuhc.options.Options;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.BaseText;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.Optional;

public class BookNBT {
	
	public static NbtList appendPageText(NbtList nbt, BaseText text) {
		nbt.add(NbtString.of(BaseText.Serializer.toJson(text)));
		return nbt;
	}
	
	public static BaseText createTextEvent(String text, String cmd, String hover, Formatting color) {
		BaseText res = new LiteralText(text);
		if (color != null) res.setStyle(res.getStyle().withColor(color));
		if (cmd != null) res.setStyle(res.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd)));
		if (hover != null) res.setStyle(res.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(hover))));
		return res;
	}
	
	public static BaseText createOptionText(Optional<Option> opt) {
		return (BaseText)opt.map(option -> createTextEvent(option.getName(), null, option.getDescription(), Formatting.BLUE)
				.append(createTextEvent(" < ", "/uhc option " + option.getId() + " sub", option.getDecString(), Formatting.RED))
				.append(createTextEvent(option.getStringValue(), "/uhc option " + option.getId() + " set", "Click to input value", Formatting.GOLD))
				.append(createTextEvent(" >", "/uhc option " + option.getId() + " add", option.getIncString(), Formatting.GREEN))
				.append("\n")).orElse(new LiteralText("Unknown Option"));
	}
	
	public static ItemStack createWrittenBook(String author, String title, NbtElement pages) {
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
		book.getOrCreateNbt().put("author", NbtString.of(author));
		book.getOrCreateNbt().put("title", NbtString.of(title));
		book.getOrCreateNbt().put("pages", pages);
		return book;
	}
	
	public static BaseText createReturn() {
		return new LiteralText("\n");
	}
	
	public static ItemStack getConfigBook(UhcGameManager gameManager) {
		Options options = gameManager.getOptions();
		NbtList pages = new NbtList();
		
		appendPageText(pages, (BaseText) new LiteralText("General Settings\n\n")
				.append(createOptionText(options.getOption("gameMode")))
				.append(createOptionText(options.getOption("randomTeams")))
				.append(createOptionText(options.getOption("teamCount")))
				.append(createReturn())
				.append(createOptionText(options.getOption("difficulty")))
				.append(createOptionText(options.getOption("daylightCycle")))
				.append(createOptionText(options.getOption("friendlyFire")))
				.append(createOptionText(options.getOption("teamCollision")))
				.append(createOptionText(options.getOption("greenhandProtect")))
				.append(createOptionText(options.getOption("forceViewport")))
				.append(createOptionText(options.getOption("deathBonus")))
		);
		
		appendPageText(pages, (BaseText) new LiteralText("Time Settings\n\n")
				.append(createOptionText(options.getOption("borderStart")))
				.append(createOptionText(options.getOption("borderEnd")))
				.append(createOptionText(options.getOption("borderFinal")))
				.append(createReturn())
				.append(createOptionText(options.getOption("gameTime")))
				.append(createOptionText(options.getOption("borderStartTime")))
				.append(createOptionText(options.getOption("borderEndTime")))
				.append(createOptionText(options.getOption("netherCloseTime")))
				.append(createOptionText(options.getOption("caveCloseTime")))
				.append(createOptionText(options.getOption("greenhandTime")))
		);
		
		appendPageText(pages, (BaseText) new LiteralText("World Settings\n\n")
				.append(createOptionText(options.getOption("merchantFrequency")))
				.append(createOptionText(options.getOption("oreFrequency")))
				.append(createOptionText(options.getOption("chestFrequency")))
				.append(createOptionText(options.getOption("trappedChestFrequency")))
				.append(createOptionText(options.getOption("chestItemFrequency")))
				.append(createOptionText(options.getOption("mobCount")))
				.append(createReturn())
				.append(createTextEvent("     Reset Gameplay\n", "/uhc reset 0", "Reset Gameplay Settings", Formatting.GOLD))
				.append(createTextEvent("    Reset Generation\n", "/uhc reset 1", "Reset Generation Settings", Formatting.GOLD))
				.append(createTextEvent("       Regenerate\n", "/uhc regen", "Regenerate Terrain", Formatting.LIGHT_PURPLE))
				.append(createTextEvent("          Start !\n", "/uhc start", "Start the UHC game !", Formatting.LIGHT_PURPLE))
		);
		appendPageText(pages, (BaseText) new LiteralText("MatchMaking Settings\n\n")
				.append(createOptionText(options.getOption("matchMakingLevel")))
				.append(createOptionText(options.getOption("k_point_factor")))
				.append(createOptionText(options.getOption("k_singleGame")))
				.append(createOptionText(options.getOption("k_wStreak")))
				.append(createOptionText(options.getOption("k_player_kill")))


		);
		return createWrittenBook("sbGP", "UHC Game Configuration", pages);
	}
	
	public static ItemStack getPlayerBook(UhcGameManager gameManager) {
		Options options = gameManager.getOptions();
		int teamCount = options.getIntegerOptionValue("teamCount");
		boolean randomTeams = options.getBooleanOptionValue("randomTeams");
		BaseText text = new LiteralText("Select Teams\n\n");
		String line = "***********************\n";
		text.append(createTextEvent(line, "/uhc select 8", "Select to observe", Formatting.GRAY));
		if (randomTeams)
			text.append(createTextEvent(line, "/uhc select 9", "Select to fight", Formatting.BLACK));
		else {
			switch ((UhcGameManager.EnumMode) options.getOptionValue("gameMode")) {
				case NORMAL: {
					text.append(createTextEvent(line, "/uhc select 9", "Select to join random team", Formatting.BLACK));
					for (int i = 0; i < teamCount; i++) {
						UhcGameColor color = UhcGameColor.getColor(i);
						text.append(createTextEvent(line, "/uhc select " + color.getId(), "Select to join " + color.dyeColor, color.chatColor));
					}
					break;
				}
				case SOLO: 
				case GHOST:
					text.append(createTextEvent(line, "/uhc select 9", "Select to fight", Formatting.BLACK));
					break;
				case BOSS: {
					text.append(createTextEvent(line, "/uhc select 0", "Select to become a bully", Formatting.RED));
					text.append(createTextEvent(line, "/uhc select 1", "Select to become a vegetable chicken", Formatting.BLUE));
				}
			}
		}
		NbtList pages = new NbtList();
		appendPageText(pages, text);
		
		return createWrittenBook("sbGP", "UHC Team Selection", pages);
	}
	
	public static BaseText createPlayerText(UhcGamePlayer player) {
		BaseText text = createTextEvent(player.getName(), null, player.getName(), player.getTeam().getTeamColor().chatColor);
		if (player.isAlive())
			text.append(createTextEvent(" alive\n", "/uhc adjust kill " + player.getName(), "Click to kill " + player.getName(), Formatting.DARK_GREEN));
		else text.append(createTextEvent(" dead\n", "/uhc adjust resu " + player.getName(), "Click to resurrent " + player.getName(), Formatting.DARK_RED));
		return text;
	}
	
	public static ItemStack getAdjustBook(UhcGameManager gameManager) {
		Options options = gameManager.getOptions();
		NbtList pages = new NbtList();
		
		switch ((UhcGameManager.EnumMode) options.getOptionValue("gameMode")) {
			case BOSS:
			case NORMAL:
			case KING: {
				for (UhcGameTeam team : gameManager.getUhcPlayerManager().getTeams()) {
					BaseText text = new LiteralText(team.getColorfulTeamName() + "\n\n");
					for (UhcGamePlayer player : team.getPlayers()) {
						text.append(createPlayerText(player));
					}
					appendPageText(pages, text);
				}
				break;
			}
			case SOLO:
			case GHOST: {
				BaseText text = new LiteralText(Formatting.LIGHT_PURPLE + "All Players\n\n");
				for (UhcGamePlayer player : gameManager.getUhcPlayerManager().getCombatPlayers()) {
					text.append(createPlayerText(player));
				}
				appendPageText(pages, text);
			}
		}
		
		BaseText text = new LiteralText("End\n\n");
		text.append(createTextEvent("Stop Adjusting", "/uhc adjust end", "Click to remove this book", Formatting.LIGHT_PURPLE));
		appendPageText(pages, text);
		return createWrittenBook("sbGP", "UHC Game Adjustion", pages);
	}

}
