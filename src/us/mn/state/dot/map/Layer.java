/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2007  Minnesota Department of Transportation
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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import us.mn.state.dot.map.event.LayerChangedEvent;
import us.mn.state.dot.map.event.LayerChangedListener;

/**
 * A layer is a grouping of similar elements which are painted on the map.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public interface Layer {

	/** Get the name */
	String getName();

	/** Get the extent */
	Rectangle2D getExtent();

	/** Paint the layer */
	void paint(Graphics2D g, LayerRenderer r);

	/** Register a LayerChangedListener with the layer */
	void addLayerChangedListener(LayerChangedListener listener);

	/** Remove a LayerChangedListener from the layer */
	void removeLayerChangedListener(LayerChangedListener listener);

	/** Search the layer for a MapObject which is located at or near the
	 * specified point */
	MapObject search(Point2D p);

	/** Create a new layer state */
	LayerState createState();
}
