/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package us.mn.state.dot.map;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import us.mn.state.dot.map.event.LayerChangedEvent;
import us.mn.state.dot.map.event.LayerChangedListener;

/**
 * The AbstractLayer implements much of the functionality of the Layer
 * interface.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
abstract public class AbstractLayer implements Layer {

	/** Layer name */
	protected final String name;

	/** Extent of layer */
	protected Rectangle2D extent = new Rectangle2D.Double();

	/** Layer change listeners */
	protected final List layerChangedListeners = new LinkedList();

	/** Create a new abstract layer */
	public AbstractLayer(String n) {
		name = n;
	}

	/** Get the extent of the layer */
	public Rectangle2D getExtent() {
		return extent;
	}

	/** Get the name of the layer */
	public String getName() {
		return name;
	}

	/** Add a LayerListener that is notified when the layer data changes */
	public void addLayerChangedListener(LayerChangedListener l) {
		if(!layerChangedListeners.contains(l))
			layerChangedListeners.add(l);
	}

	/** Remove a LayerListener from the layer */
	public void removeLayerChangedListener(LayerChangedListener l) {
		layerChangedListeners.remove(l);
	}

	/** Notify listeners that the layer has changed */
	public void notifyLayerChangedListeners(LayerChangedEvent event) {
		Iterator it = layerChangedListeners.iterator();
		while(it.hasNext())
			((LayerChangedListener)it.next()).layerChanged(event);
	}

	/** Create the default theme */
	abstract public Theme createTheme();
}
