package me.pixelgames.pixelcrack3r.pswrapper.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class CommandBan implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MultiLanguageMessanger messenger = new MultiLanguageMessanger("PSWrapper", "de");
		
		if(sender instanceof Player) {
			MultiLanguageManager man = new MultiLanguageManager(((Player)sender).getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
			messenger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		}
		
		if(args.length <= 0) {
			sender.sendMessage(messenger.getMessage("ban-usage"));
			return true;
		}
		
		String target = args[0];
		Player targetPlayer = Bukkit.getPlayer(target);
		
		if(targetPlayer == (Player) sender) {
			sender.sendMessage(messenger.getMessage("ban-no-target").replaceAll("%target%", args[0]));
			return true;
		}
		
		String name = targetPlayer != null ? targetPlayer.getName() : target;
		
		JsonObject playerData = new JsonObject();
		playerData.addProperty("name", name);
		if(targetPlayer != null) playerData.addProperty("uuid", targetPlayer.getUniqueId().toString());
		
		JsonArray array = PrivateServer.getInstance().getProperty("bannedPlayers", new JsonArray()).getAsJsonArray();
		array.add(playerData);
		
		PrivateServer.getInstance().setProperty("bannedPlayers", array);
		sender.sendMessage(messenger.getMessage("ban-success").replaceAll("%target%", name));
		if(targetPlayer != null) targetPlayer.kickPlayer("You were kicked from the server of " + Bukkit.getOfflinePlayer(PrivateServer.getInstance().getOwner()).getName());
		
		return true;
	}
	
}