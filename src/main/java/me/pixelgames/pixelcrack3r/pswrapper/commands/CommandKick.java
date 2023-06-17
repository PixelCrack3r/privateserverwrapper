package me.pixelgames.pixelcrack3r.pswrapper.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class CommandKick implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MultiLanguageMessanger messenger = new MultiLanguageMessanger("PSWrapper", "de");
		
		if(sender instanceof Player) {
			MultiLanguageManager man = new MultiLanguageManager(((Player)sender).getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
			messenger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		}
		
		if(args.length <= 0) {
			sender.sendMessage(messenger.getMessage("kick-usage"));
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(messenger.getMessage("kick-offline-target").replaceAll("%target%", args[0]));
			return true;
		}
		
		OfflinePlayer owner = Bukkit.getOfflinePlayer(PrivateServer.getInstance().getOwner());
		
		target.kickPlayer("You were kicked from the server of " + owner.getName());
		sender.sendMessage(messenger.getMessage("kick-success").replaceAll("%target%", target.getName()));
		
		return true;
	}
	
}
