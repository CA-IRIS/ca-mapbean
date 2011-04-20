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
		super(layer, mb);
		cache = c;
	}

	/** Call the specified callback for each map object in the layer */
	public MapObject forEach(MapSearcher s) {
		// FIXME: find all tiles in the current map extent
		return null;
	}
}
