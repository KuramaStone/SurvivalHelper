package me.brook.helper.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class GamemodeCycle implements CommandExecutor, Listener {

	private Map<UUID, PlayerInfo> memory;

	public GamemodeCycle() {
		memory = new HashMap<>();
	}

	public void revertEveryone() {
		for(PlayerInfo info : memory.values()) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(info.getUuid());
			if(off.isOnline()) {
				revertPlayer(off.getPlayer());
			}

		}
	}

	private void revertPlayer(Player player) {
		if(memory.containsKey(player.getUniqueId())) {
			PlayerInfo info = memory.remove(player.getUniqueId());
			Location loc = info.getInitialLocation();

			player.teleport(loc);
			player.setGameMode(info.getInitialGamemode());
		}
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		revertPlayer(event.getPlayer());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only players may use this.");
			return false;
		}

		if(sender.hasPermission("SurvivalHelper.cycle")) {
			Player player = (Player) sender;

			if(player.getGameMode() != GameMode.SPECTATOR) {
				memory.put(player.getUniqueId(), new PlayerInfo(player));
				player.setGameMode(GameMode.SPECTATOR);
			}
			else {
				if(memory.containsKey(player.getUniqueId())) {
					revertPlayer(player);
				}
			}

			return true;

		}
		else {
			sender.sendMessage("You do not have the proper permissions.");
		}

		return false;
	}

	public static class PlayerInfo {
		private final UUID uuid;
		private final GameMode initialGamemode;
		private final Location initialLocation;

		public PlayerInfo(Player player) {
			this.uuid = player.getUniqueId();
			this.initialGamemode = player.getGameMode();
			this.initialLocation = player.getLocation();
		}

		public UUID getUuid() {
			return uuid;
		}

		public GameMode getInitialGamemode() {
			return initialGamemode;
		}

		public Location getInitialLocation() {
			return initialLocation;
		}
	}

}
