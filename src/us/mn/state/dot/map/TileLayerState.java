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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Point2D;
import us.mn.state.dot.geokit.ZoomLevel;

/**
 * A tile layer state for drawing a Google-style tile map.
 *
 * @author Douglas Lau
 */
public class TileLayerState extends LayerState {

	/** Cache of tiles */
	protected final TileCache cache;

	/** Create a new tile layer state */
	public TileLayerState(TileLayer layer, MapBean mb, TileCache c) {
		super(layer, mb, new TileTheme());
		cache = c;
	}

	/** Call the specified callback for each map object in the layer */
	public MapObject forEach(MapSearcher s) {
		MapModel model = map.getModel();
		ZoomLevel zoom = model.getZoomLevel();
		Dimension sz = map.getSize();
		Point2D center = model.getCenter();
		int hx = (int)sz.getWidth() / 2;
		int hy = (int)sz.getHeight() / 2;
		int px = (int)zoom.getPixelX(center.getX());
		int py = (int)zoom.getPixelY(center.getY());
		int x0 = ((px - hx) / 256) * 256;
		int x1 = (((px + hx) / 256) + 1) * 256;
		int ox = (px - hx) % 256;
		for(int x = x0; x <= x1; x += 256) {
			int y0 = ((py - hy) / 256) * 256;
			int y1 = (((py + hy) / 256) + 1) * 256;
			int oy = (py + hy) % 256 - 512;
			for(int y = y0; y <= y1; y += 256) {
				String tile = zoom.getTile(x, y);
				try {
					Image img = cache.getTile(tile);
					MapObject mo = new TileMapObject(img,
						x - x0 - ox, y1 - y + oy);
					s.next(mo);
				}
				catch(java.io.IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		return null;
	}
}
