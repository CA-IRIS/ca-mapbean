/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A cache of image tiles.
 *
 * @author Douglas Lau
 */
public class TileCache {

	/** FIFO of tile names in cache */
	protected final LinkedList<String> tiles = new LinkedList<String>();

	/** Hash of tile names to image files */
	protected final HashMap<String, TempImageFile> tile_hash =
		new HashMap<String, TempImageFile>();

	/** Image fetcher */
	protected final ImageFetcher fetcher;

	/** Size of cache (number of tiles) */
	protected final int size;

	/** Create a new tile cache */
	public TileCache(ImageFetcher f, int sz) {
		fetcher = f;
		size = sz;
	}

	/** Purge the oldest image from the cache */
	protected void purgeOldestImage() throws IOException {
		String name = tiles.removeFirst();
		TempImageFile tif = tile_hash.remove(name);
		tif.destroy();
	}

	/** Get the named tile from the cache */
	public synchronized BufferedImage getTile(String n) throws IOException {
		if(tile_hash.containsKey(n))
			return tile_hash.get(n).getImage();
		while(tile_hash.size() >= size)
			purgeOldestImage();
		TempImageFile tif = new TempImageFile(fetcher.fetchImage(n));
		BufferedImage bi = tif.getImage();
		// Update the FIFO and hash map last so that exceptions cannot
		// leave us in an inconsistent state
		tiles.addLast(n);
		tile_hash.put(n, tif);
		return bi;
	}

	/** Destroy the tile cache */
	public synchronized void destroy() throws IOException {
		for(TempImageFile tif: tile_hash.values())
			tif.destroy();
		tiles.clear();
		tile_hash.clear();
	}
}
