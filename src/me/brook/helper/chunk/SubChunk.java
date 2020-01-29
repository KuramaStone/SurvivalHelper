package me.brook.helper.chunk;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class SubChunk {
	
	public Chunk chunk;
	public int ylevel;
	
	public SubChunk(Chunk chunk, int ylevel) {
		this.chunk = chunk;
		this.ylevel = ylevel;
	}
	
	public Block getBlockAt(int x, int y, int z) {
		return chunk.getBlock(x, ylevel * 16 + y, z);
	}
	
	public int getChunkX() {
		return chunk.getX();
	}
	
	public int getChunkZ() {
		return chunk.getZ();
	}

	@Override
	public boolean equals(Object arg0) {
		SubChunk sc1 = this;
		SubChunk sc2 = (SubChunk) arg0;
		
		if(sc1.chunk == sc2.chunk &&
				sc1.ylevel == sc2.ylevel) {
			return true;
		}
		
		return false;
	}
	
}
