package me.pixelgames.pixelcrack3r.pswrapper.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;

import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.channel.ChannelMessageTarget;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.network.buffer.DataBuf;

import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Command;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Dependency;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;

import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;

import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;
import me.pixelgames.pixelcrack3r.pswrapper.cloudlistener.ChannelMessageReceiveListener;
import me.pixelgames.pixelcrack3r.pswrapper.listeners.OnPlayerConnectionListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandBan;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandGamemode;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandKick;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandMod;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandPHelp;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandPL;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandPrivate;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandPublic;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandTime;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandUnban;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandWeather;
import me.pixelgames.pixelcrack3r.pswrapper.commands.CommandWhitelist;

@Singleton
@PlatformPlugin(
		api = "1.13",
		pluginFileNames = "plugin.yml",
		platform = "bukkit",
		authors = "PixelCrack3r",
		version = "2.0",
		name = "PrivateServerWrapper",
		description = "Connects the private server to the lobby.",
		dependencies = {
				@Dependency(
						name = "CloudNet-Bridge"
				)
		},
		commands = {
				@Command(
						name = "phelp",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "public",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "private",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "pwhitelist",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "ppl",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "pmod",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "pgamemode",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "pkick",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "pban",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "punban",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "ptime",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				),
				@Command(
						name = "pweather",
						description = "A pixelgames provided command.",
						usage = "/<command>"
				)
		}

)
public class PrivateServer implements PlatformEntrypoint {

	private final static String PREFIX = "[PrivateServer]";

	private static PrivateServer instance;

	private final JavaPlugin plugin;
	private final EventManager eventManager;
	private final ServiceInfoHolder serviceInfoHolder;
	private final BridgeServiceHelper bridgeServiceHelper;
	
	private JsonObject properties;

	@Inject
	public PrivateServer(JavaPlugin javaPlugin, EventManager eventManager, ServiceInfoHolder serviceInfoHolder, BridgeServiceHelper bridgeServiceHelper) {
		this.plugin = javaPlugin;
		this.eventManager = eventManager;
		this.serviceInfoHolder = serviceInfoHolder;
		this.bridgeServiceHelper = bridgeServiceHelper;
	}

	@Override
	public void onLoad() {
		instance = this;
		
		this.eventManager.registerListener(new ChannelMessageReceiveListener());
		
		JsonObject properties = this.requestProperties();
		
		if(!properties.has("privateserver.owner")) {
			Bukkit.getConsoleSender().sendMessage(PREFIX + " Invalid service properties received. Could not identify the owner.");
			return;
		}
		
		this.setProperties(properties);
		this.updateProperties();
		this.loadLanguage();
		this.loadCommands();
		
		this.loadListeners();
		
	}
	
	@Override
	public void onDisable() {
		this.eventManager.unregisterListener(this.getClass().getClassLoader());
	}
	
	public JsonObject requestProperties() {
		List<ChannelMessage> msgs = new ArrayList<>(this.sendQuery(Document.newJsonDocument().append("request", "startup_properties")));
		
		if(msgs.size() > 0) return new JsonParser().parse(DocumentFactory.json().parse(msgs.get(0).content().readString()).getString("properties")).getAsJsonObject();
		return null;
	}
	
	private void loadListeners() {
		Bukkit.getPluginManager().registerEvents(new OnPlayerConnectionListener(), this.plugin);
	}
	
	private void loadCommands() {
		this.plugin.getCommand("phelp").setExecutor(new CommandPHelp());
		this.plugin.getCommand("private").setExecutor(new CommandPrivate());
		this.plugin.getCommand("pmod").setExecutor(new CommandMod());
		this.plugin.getCommand("pkick").setExecutor(new CommandKick());
		this.plugin.getCommand("pban").setExecutor(new CommandBan());
		this.plugin.getCommand("punban").setExecutor(new CommandUnban());
		this.plugin.getCommand("pweather").setExecutor(new CommandWeather());
		this.plugin.getCommand("ptime").setExecutor(new CommandTime());
		this.plugin.getCommand("pgamemode").setExecutor(new CommandGamemode());
		this.plugin.getCommand("ppl").setExecutor(new CommandPL());
		this.plugin.getCommand("public").setExecutor(new CommandPublic());
		this.plugin.getCommand("pwhitelist").setExecutor(new CommandWhitelist());
	}
	
	private void loadLanguage() {
		MultiLanguageMessanger de = new MultiLanguageMessanger("PSWrapper", "de");
		
		de.getConfig().addDefault("player-join", "&7[&a»&7] Der Spieler &6" + "%player%" + " &7hat die Welt betreten.");
		de.getConfig().addDefault("join-info-owner", "&7Willkommen auf deinem privaten Server. Hier kannst du deiner Fantasie ganz freien lauf lassen. Solltest du eine Servervorlage ausgewählt haben, kann es sein, dass deine Rechte aus Sicherheitsgründen eingeschränkt sind. Dennoch: Viel Spass! :)");
		de.getConfig().addDefault("join-info-player", "&7Willkommen! Du bist einem privaten Server beigetreten. Auf privaten Servern sind die Chatfilter und andere Sicherheitsvorkehrungen eingeschränkt. Du kannst, sollte jemand gegen das Regelwerkt verstoßen, die jeweilige Person in unserem Forum melden.");
		de.getConfig().addDefault("join-command-info", "&8» &6PrivateServer &7» Du kannst dir mit &9/phelp &7alle Befehle anzeigen lassen.");
		de.getConfig().addDefault("player-quit", "&7[&c»&7] Der Spieler &6" + "%player%" + " &7hat die Welt verlassen.");
		
		de.getConfig().addDefault("command-help", "&7Zeigt die möglichen Befehle an.");
		de.getConfig().addDefault("command-public", "&7Setzt die Zugänglichkeit des Servers auf öffentlich.");
		de.getConfig().addDefault("command-private", "&7Setzt die Zugänglichkeit des Servers auf Private.");
		de.getConfig().addDefault("command-pwhitelist", "&7Füge Spieler der Whitelist hinzu oder entferne sie.");
		de.getConfig().addDefault("command-ppl", "&7Zeigt die installierten plugins an.");
		de.getConfig().addDefault("command-pmod", "&7Befördere einen Spieler zum Moderator des Servers.");
		de.getConfig().addDefault("command-pgamemode", "&7Ändere deinen aktuellen Spielmodus.");
		de.getConfig().addDefault("command-pkick", "&7Werfe einen anderen Spieler von deinem Server.");
		de.getConfig().addDefault("command-pban", "&7Verbanne einen Spieler von deinem Server.");
		de.getConfig().addDefault("command-punban", "&7Lasse einen Spieler wieder auf deinem Server spielen.");
		de.getConfig().addDefault("command-ptime", "&7Ändere die Tageszeit auf deinem Server.");
		de.getConfig().addDefault("command-pweather", "&7Ändere das Wetter auf deinem Server.");
		
		de.getConfig().addDefault("access-set-private", "&7Die Zugänglichkeit des Servers wurde auf &cPrivat &7gestellt.");
		de.getConfig().addDefault("access-set-public", "&7Die Zugänglichkeit des Servers wurde auf &aÖffentlich &7gestellt.");
		
		de.getConfig().addDefault("gamemode-not-exists", "&7Dieser Spielmodus existiert nicht!");
		de.getConfig().addDefault("gamemode-no-target", "&7Es wurde kein Ziel gefunden, auf das der Spielmodus angewandt werden konnte.");
		de.getConfig().addDefault("gamemode-set-survival", "&7Der Spielmodus wurde auf &6Survival &7geändert.");
		de.getConfig().addDefault("gamemode-set-creative", "&7Der Spielmodus wurde auf &6Creative &7geändert.");
		de.getConfig().addDefault("gamemode-set-adventure", "&7Der Spielmodus wurde auf &6Adventure &7geändert.");
		de.getConfig().addDefault("gamemode-set-spectator", "&7Der Spielmodus wurde auf &6Spectator &7geändert.");
		
		de.getConfig().addDefault("plugins-installed", "&8» &6PrivateServer &7» Folgende plugins sind installiert:");
		de.getConfig().addDefault("plugins-entry", "&8» &6PrivateServer &7» %name%");
		de.getConfig().addDefault("plugins-empty", "&8» &6PrivateServer &7» &cEs sind aktuell keine Plugins auf dem Server installiert.");
		
		de.getConfig().addDefault("kick-usage", "&8» &6PrivateServer &7» &c/kick <player>");
		de.getConfig().addDefault("kick-offline-target", "&8» &6PrivateServer &7» &cDer Spieler &e%target% &cwurde nicht gefunden.");
		de.getConfig().addDefault("kick-success", "&8» &6PrivateServer &7» &7Der Spieler &e%target% &7wurde vom Server geworfen.");
		
		de.getConfig().addDefault("ban-usage", "&8» &6PrivateServer &7» &c/ban <player>");
		de.getConfig().addDefault("ban-no-target", "&8» &6PrivateServer &7» &cDer Spieler &e%target% &cdarf nicht als Ziel angegeben werden!");
		de.getConfig().addDefault("ban-success", "&8» &6PrivateServer &7» &7Der Spieler &e%target% &7wurde vom Spiel ausgeschlossen.");
		
		de.getConfig().addDefault("unban-usage", "&8» &6PrivateServer &7» &c/unban <player>");
		de.getConfig().addDefault("unban-no-target", "&8» &6PrivateServer &7» &cDer Spieler &e%target% &cist nicht gebannt!");
		de.getConfig().addDefault("unban-success", "&8» &6PrivateServer &7» &7Der Spieler &e%target% &7darf wieder am Spiel teilnehmen.");
		
		de.save();
	}
	
	public void setProperties(JsonObject properties) {
		this.properties = properties;
	}
	
	@Deprecated
	public void setProperties(String properties) {
		JsonObject obj = JsonParser.parseString(properties).getAsJsonObject();
		this.properties = obj;
		this.bridgeServiceHelper.extra().set(obj.toString());
		this.serviceInfoHolder.publishServiceInfoUpdate();
	}
	
	public void updateProperties() {
		this.bridgeServiceHelper.extra().set(this.properties.toString());
		this.serviceInfoHolder.publishServiceInfoUpdate();
	}
	
	public void setProperty(String key, String value) {
		if(value == null) {
			this.properties.remove(key);
			return;
		}
		this.properties.addProperty(key, value);
		this.updateProperties();
	}
	
	public void setProperty(String key, Boolean value) {
		if(value == null) {
			this.properties.remove(key);
			return;
		}
		this.properties.addProperty(key, value);
		this.updateProperties();
	}
	
	public void setProperty(String key, Number value) {
		if(value == null) {
			this.properties.remove(key);
			return;
		}
		this.properties.addProperty(key, value);
		this.updateProperties();
	}
	
	public void setProperty(String key, Character value) {
		if(value == null) {
			this.properties.remove(key);
			return;
		}
		this.properties.addProperty(key, value);
		this.updateProperties();
	}
	
	public void setProperty(String key, JsonElement value) {
		if(value == null) {
			this.properties.remove(key);
			return;
		}
		this.properties.add(key, value);
		this.updateProperties();
	}
	
	@Deprecated
	public void removeProperty(String key) {
		this.properties.remove(key);
		this.updateProperties();
	}
	
	public JsonElement getProperty(String key) {
		return this.getProperty(key, null);
	}
	
	public JsonElement getProperty(String key, JsonElement def) {
		return this.properties.has(key) ? this.properties.get(key) : def;
	}
	
	public UUID getOwner() {
		return UUID.fromString(this.getProperty("privateserver.owner").getAsString());
	}
	
	public boolean isTemplate() {
		return this.getProperty("privateserver.isTemplate", new JsonPrimitive("false")).getAsString().equalsIgnoreCase("true");
	}
	
	private Collection<ChannelMessage> sendQuery(Document data) {
		return ChannelMessage.builder().targetAll(ChannelMessageTarget.Type.SERVICE).channel("private_server").message("send_query").buffer(DataBuf.empty().writeString(data.toString())).build().sendQuery();
	}

	public PluginDescriptionFile getDescription() {
		return this.plugin.getDescription();
	}

	public static PrivateServer getInstance() {
		return instance;
	}
	
}