package me.brook.helper;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.brook.helper.commands.GamemodeCycle;
import me.brook.helper.commands.SlimeFinder;
import me.brook.helper.commands.SpawnableBlocks;
import me.brook.helper.commands.SpawnerFinder;

public class Main extends JavaPlugin {
	
	private GamemodeCycle cycle;

	@Override
	public void onEnable() {
		getCommand("spawnablef").setExecutor(new SpawnableBlocks());
		getCommand("spawnerf").setExecutor(new SpawnerFinder());
		getCommand("spectate").setExecutor(cycle = new GamemodeCycle());
		getCommand("slimef").setExecutor(new SlimeFinder());
	}
	
	@Override
	public void onDisable() {
		cycle.revertEveryone();
	}

	public static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
}














