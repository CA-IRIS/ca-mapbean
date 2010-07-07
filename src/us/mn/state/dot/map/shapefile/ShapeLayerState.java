/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2010  Minnesota Department of Transportation
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
package us.mn.state.dot.map.shapefile;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;

/**
 * A shape layer state is the rendering state for a shape layer on a map.
 * Multiple layer states can share the same underlying layer for separate map
 * components.
 *
 * @author Douglas Lau
 */
public class ShapeLayerState extends LayerState {

	/** List of shapes from the shapefile */
	protected final List<ShapeObject> shapes;

	/**
	 * Create a new shape layer state.
	 * @param layer Layer this state is based upon.
	 * @param mb Map bean for rendering.
	 * @param theme Theme used to paint the layer.
	 */
	public ShapeLayerState(ShapeLayer layer, MapBean mb, ShapeTheme theme) {
		super(layer, mb, theme);
		shapes = layer.getShapes();
	}

	/** Iterate through the shapes in the layer */
	public MapObject forEach(MapSearcher s) {
		for(ShapeObject so: shapes) {
			if(s.next(so))
				return so;
		}
		return null;
	}

	/** Search the layer for a map object containing the given point */
	public MapObject search(final Point2D p) {
		return forEach(new MapSearcher() {
			public boolean next(MapObject mo) {
				return mo.getShape().contains(p);
			}
		});
	}
}
