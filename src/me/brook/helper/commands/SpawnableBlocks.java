package me.brook.helper.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.brook.helper.Main;
import me.brook.helper.chunk.SubChunk;

public class SpawnableBlocks implements CommandExecutor {

	private static final String format = "&c%s) X:%s Y:%s Z:%s";

	private Map<UUID, List<SubChunk>> storage;

	public SpawnableBlocks() {
		storage = new HashMap<>();
	}

	private List<SubChunk> getSpawnableBlocks(Location center) {
		List<SubChunk> list = new ArrayList<>();

		World world = center.getWorld();
		for(int dx = -8; dx <= 8; dx++) {
			for(int dz = -8; dz <= 8; dz++) {
				Chunk chunk = center.clone().add(dx * 16, 0, dz * 16).getChunk();
				for(int dy = 0; dy < 256; dy += 16) {
					SubChunk sc = new SubChunk(chunk, dy / 16);

					// check subchunk for spawnable
					x: for(int x = 0; x < 16; x++) {
						for(int y = 0; y < 16; y++) {
							for(int z = 0; z < 16; z++) {
								Location loc = sc.getBlockAt(x, y, z).getLocation();
								Block focus = world.getBlockAt(loc);
								Biome b = focus.getBiome();

								// check for solid block underneath (not a slab/liquid/redstone component), two
								// spawnable blocks, and the biome type
								boolean liquid = focus.isLiquid();
								boolean empty = focus.isEmpty() && focus.getRelative(BlockFace.UP).isEmpty();
								boolean hasSolidBlock = isSolid(focus.getRelative(BlockFace.DOWN));
								boolean biome = b != Biome.MUSHROOM_FIELD_SHORE && b != Biome.MUSHROOM_FIELDS;
								boolean distance = loc.distance(center) <= 128;

								if(!liquid && empty && hasSolidBlock && biome && distance) {
									list.add(sc);
									break x; // we can stop checking this chunk after the first spot is found.
								}
							}
						}
					}

				}
			}

		}

		return list;
	}

	private boolean isSolid(Block relative) {
		Material mat = relative.getType();
		String string = mat.toString().toLowerCase();

		// Is liquid
		if(mat == Material.WATER || mat == Material.LAVA) {
			return false;
		}

		// is redstone material
		if(mat == Material.REDSTONE || mat == Material.REDSTONE_TORCH || mat == Material.REDSTONE_WIRE ||
				string.contains("button")) {
			return false;
		}

		// slabs and misc
		if(string.contains("slab") || mat == Material.ICE || string.contains("fence")) {
			return false;
		}

		return true;
	}

	// private List<SubChunk> compressSpawnable(List<Location> spawnable) {
	// List<SubChunk> list = new ArrayList<>();
	//
	// for(Location l1 : spawnable) {
	//
	// int y = ((int) (l1.getBlockY() / 16)) * 16;
	// SubChunk sc = new SubChunk(l1.getChunk(), y);
	//
	// if(!list.contains(sc)) {
	// list.add(sc);
	// }
	//
	// }
	//
	// return list;
	// }

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only players may use this.");
			return false;
		}

		if(sender.hasPermission("SurvivalHelper.find.spawnableblocks")) {
			Player player = (Player) sender;

			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("search")) {
					searchLocation(player, player.getLocation());
				}
				else if(args[0].equalsIgnoreCase("read")) {
					int page = 0;
					try {
						page = (args.length > 1) ? Integer.parseInt(args[1]) - 1 : 0;
					}
					catch(Exception e) {
						sender.sendMessage("Please only use numeric pages.");
					}

					readPage(player, Math.max(page, 0));
				}
			}
			else {
				sender.sendMessage("/spawnablef [search/read]");
			}

			return true;
		}
		else {
			sender.sendMessage("You do not have the proper permissions.");
		}

		return false;
	}

	private void readPage(Player player, int page) {
		List<SubChunk> spawnable = storage.get(player.getUniqueId());
		
		if(spawnable != null) {
			for(int i = page * 5; i < page * 5 + 5 && i < spawnable.size(); i++) {
				SubChunk sc = spawnable.get(i);
				 player.sendMessage(Main.color(String.format(format, i, sc.getChunkX(), sc.ylevel, sc.getChunkZ())));
			}
		}
		else {
			player.sendMessage(Main.color("&cYou must use '/spawnablef search' first."));
		}
	}

	private void searchLocation(Player player, Location location) {
		new BukkitRunnable() {

			@Override
			public void run() {
				player.sendMessage("Please wait for the data to load.");

				// get all spawnable blocks
				List<SubChunk> spawnable = getSpawnableBlocks(location);

				player.sendMessage(Main.color("&2" + spawnable.size() + " Chunks with spawnable blocks..."));
				storage.put(player.getUniqueId(), spawnable);
				readPage(player, 0);

			}
		}.run();
	}

}
