package me.pixelgames.pixelcrack3r.pswrapper.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class CommandGamemode implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MultiLanguageMessanger messenger = new MultiLanguageMessanger("PSWrapper", "de");
		
		if(sender instanceof Player) {
			MultiLanguageManager man = new MultiLanguageManager(((Player)sender).getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
			messenger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		}
		
		GameMode gameMode = null;
		Player target = sender instanceof Player ? (Player) sender : null;
		
		int gmId = 0;
		
		if(args.length > 0) {
			for(GameMode mode : GameMode.values()) {
				if(mode.name().equalsIgnoreCase(args[0])) {
					gameMode = mode;
					break;
				}
			}
			
			try {
				gmId = Integer.parseInt(args[0]);
			} catch(Exception e) {
				sender.sendMessage(messenger.getMessage("gamemode-not-exists").replaceAll("%arg%", args[0]));
				return true;
			}
			
			if(gameMode == null) {
				sender.sendMessage(messenger.getMessage("gamemode-not-exists").replaceAll("%arg%", args[0]));
				return true;
			}
			
			if(args.length > 1) {
				target = Bukkit.getPlayer(args[1]);
			}
		}
		
		if(target == null) {
			sender.sendMessage(messenger.getMessage("gamemode-no-target").replaceAll("%target%", args.length > 1 ? args[1] : "PLAYER"));
			return true;
		}
		
		if(args.length <= 0) {
			switch(target.getGameMode()) {
				case SURVIVAL:
					gmId = 1;
					break;
				case CREATIVE:
					gmId = 2;
					break;
				case ADVENTURE:
					gmId = 3;
					break;
				case SPECTATOR:
					gmId = 0;
					break;
			}	
		}
		
		
		if(gameMode == null) {
			switch(gmId) {
				case 0:
					gameMode = GameMode.SURVIVAL;
					break;
				case 1:
					gameMode = GameMode.CREATIVE;
					break;
				case 2:
					gameMode = GameMode.ADVENTURE;
					break;
				case 3:
					gameMode = GameMode.SPECTATOR;
					break;
				default: break;
			}
		}
		
		if(gameMode == null) {
			sender.sendMessage(messenger.getMessage("gamemode-not-exists").replaceAll("%arg%", args[0]));
			return true;
		}
		
		((Player)sender).setGameMode(gameMode);
		sender.sendMessage(messenger.getMessage("gamemode-set-" + gameMode.name().toLowerCase()));
		
		return true;
	}
	
}
