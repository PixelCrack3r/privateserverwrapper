package me.pixelgames.pixelcrack3r.pswrapper.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class CommandUnban implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MultiLanguageMessanger messenger = new MultiLanguageMessanger("PSWrapper", "de");
		
		if(sender instanceof Player) {
			MultiLanguageManager man = new MultiLanguageManager(((Player)sender).getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
			messenger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		}
		
		if(args.length <= 0) {
			sender.sendMessage(messenger.getMessage("unban-usage"));
			return true;
		}
		
		String target = args[0];
		Player targetPlayer = Bukkit.getPlayer(target);
		
		String name = targetPlayer != null ? targetPlayer.getName() : target;
		
		JsonArray array = PrivateServer.getInstance().getProperty("bannedPlayers", new JsonArray()).getAsJsonArray();
		JsonArray bannedPlayers = new JsonArray();
		array.forEach(element -> {
			if(!element.isJsonObject()) return;
			if((element.getAsJsonObject().has("name") || element.getAsJsonObject().has("uuid"))
					&& (element.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(name) || element.getAsJsonObject().get("uuid").getAsString().equalsIgnoreCase(name))) return;
			bannedPlayers.add(element);
		});
		
		if(array.size() - 1 == bannedPlayers.size()) {
			sender.sendMessage(messenger.getMessage("unban-success").replaceAll("%target%", name));
		} else sender.sendMessage(messenger.getMessage("unban-no-target").replaceAll("%target%", args[0]));
		
		PrivateServer.getInstance().setProperty("bannedPlayers", bannedPlayers);
		
		return true;
	}
	
}