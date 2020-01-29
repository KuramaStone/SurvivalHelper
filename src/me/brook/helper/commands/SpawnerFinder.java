package me.brook.helper.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.brook.helper.Main;

public class SpawnerFinder implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only players may use this.");
			return false;
		}

		if(sender.hasPermission("SurvivalHelper.find.spawner")) {
			Player player = (Player) sender;
			List<CreatureSpawner> spawners = getSpawnersAround(player);

			sender.sendMessage(color("&2Nearby Spawners: " + spawners.size()));

			boolean color = true;
			for(CreatureSpawner cs : spawners) {
				String loc = cs.getLocation().getBlockX() + "/" + cs.getLocation().getBlockY() + "/"
						+ cs.getLocation().getBlockZ();

				@SuppressWarnings("deprecation")
				String type = cs.getCreatureTypeName();

				player.sendMessage(color((color ? "&b&l" : "&3&l") + type + ": &r" + loc));
				color = !color;
			}

			sender.sendMessage(color("&2---------------"));
			return true;

		}
		else {
			sender.sendMessage("You do not have the proper permissions.");
		}

		return false;
	}

	private List<CreatureSpawner> getSpawnersAround(Player player) {
		List<CreatureSpawner> spawners = new ArrayList<>();
		
		for(Chunk chunk : player.getWorld().getLoadedChunks()) {
			
			if(distanceTo(chunk, player.getLocation().getChunk()) < 10) {
				for(BlockState bs : chunk.getTileEntities()) {
					if(bs.getType() == Material.SPAWNER) {
						spawners.add((CreatureSpawner) bs);
					}
				}
			}
			
		}
		
		return spawners;
	}

	private String color(String string) {
		return Main.color(string);
	}

	private double distanceTo(Chunk chunk, Chunk chunk2) {
		int x = chunk.getX() - chunk2.getX();
		int y = chunk.getZ() - chunk2.getZ();
		return (double) Math.sqrt(x * x - y * y);
	}

}
