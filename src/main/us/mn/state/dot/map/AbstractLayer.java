/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2004  Minnesota Department of Transportation
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
package us.mn.state.dot.shape;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ListIterator;
import us.mn.state.dot.shape.symbol.CircleMarker;

/** 
 * The AbstractLayer implements much of the functionality of the Layer
 * interface.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public abstract class AbstractLayer implements Layer {

	/** extent of layer */
	protected Rectangle2D extent = new Rectangle2D.Double();

	private String name;

	private java.util.List layerChangedListeners = new ArrayList();

	private boolean dynamic = false;

	/**
	 * Paints the selected Map objects.
	 * @param g the Graphics object to draw on.
	 */
	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			MapObject[] selections) {
		if (selections != null) {
			for (int i = 0; i < selections.length; i++) {
				MapObject object = selections[i];
				if (object != null) {
					renderer.render(g, object);
				}
			}
		}
	}

	/** Gets the extent of the layer */
	public Rectangle2D getExtent() {
		return extent;
	}

	/**
	 * Returns a string contining the name of the layer.
	 *
	 * @return String containing the name of the layer
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the layer.
	 *
	 * @param s String containing the name of the layer
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * Adds a LayerListener to the layer that is notified when the layer's data
	 * is changed.
	 *
	 * @param l LayerListener to be added to the layer
	 */
	public void addLayerChangedListener(LayerChangedListener l) {
		if (!layerChangedListeners.contains(l)) {
			layerChangedListeners.add(l);
		}
	}

	/**
	 * Remove a LayerListener from the layer.
	 */
	public void removeLayerChangedListener(LayerChangedListener l) {
		layerChangedListeners.remove(l);
	}

	public void notifyLayerChangedListeners(LayerChangedEvent event) {
		ListIterator it = layerChangedListeners.listIterator();
		while (it.hasNext()) {
			((LayerChangedListener) it.next()).layerChanged(event);
		}
	}

	/** 
	 * Is this layer static?
	 * @return true if data is not dynamic
	 */
	public boolean isStatic() {
		return !dynamic;
	}

	/** 
	 * If set to false layer is static and the data will not change.  Static
	 * layers are painted behind non-static (dynamic) layers on a map.  If it is
	 * desired for a static layer to be higher in the map it setStatic() must be
	 * set to true.
	 * @param b true - layer is static and data will not be update; layer is
	 * painted behind any non-static layers.
	 * false - layer is dynamic and layer will change if the layer's data is
	 * changed; will be painted in front of any static layers
	 */
	public void setStatic(boolean b) {
		dynamic = !b;
	}

	public Theme getTheme() {
		return new Theme(this, new DefaultRenderer(new CircleMarker()));
	}
}
