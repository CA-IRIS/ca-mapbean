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
import java.util.HashSet;
import java.util.Set;
import us.mn.state.dot.map.event.LayerChangedEvent;
import us.mn.state.dot.map.event.LayerChangedListener;

/**
 * A layer is a grouping of similar elements which are painted on the map.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
abstract public class Layer {

	/** Layer name */
	protected final String name;

	/** Extent of layer */
	protected final Rectangle2D extent = new Rectangle2D.Double();

	/** Layer change listeners */
	protected final Set<LayerChangedListener> listeners =
		new HashSet<LayerChangedListener>();

	/** Create a new layer */
	public Layer(String n) {
		name = n;
	}

	/** Get the name of the layer */
	public String getName() {
		return name;
	}

	/** Get the extent of the layer */
	public Rectangle2D getExtent() {
		return extent;
	}

	/** Add a listener that is notified when the layer changes */
	public void addLayerChangedListener(LayerChangedListener l) {
		listeners.add(l);
	}

	/** Remove a LayerChangedListener from the layer */
	public void removeLayerChangedListener(LayerChangedListener l) {
		listeners.remove(l);
	}

	/** Notify listeners that the layer has changed */
	protected void notifyLayerChangedListeners(LayerChangedEvent e) {
		for(LayerChangedListener l: listeners)
			l.layerChanged(e);
	}

	/** Create a new layer state */
	abstract public LayerState createState();

	/** Draw the layer using the specified theme */
	abstract public void draw(Graphics2D g, Theme t);

	/** Search the layer for a MapObject which is located at or near the
	 * specified point */
	abstract public MapObject search(Point2D p);
}
