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
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import us.mn.state.dot.shape.symbol.CircleMarker;
import us.mn.state.dot.shape.event.LayerChangedEvent;
import us.mn.state.dot.shape.event.LayerChangedListener;

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

	private final List layerChangedListeners = new LinkedList();

	/** Paint the selected Map objects */
	public void paintSelections(Graphics2D g, LayerRenderer renderer,
		MapObject[] selections)
	{
		if(selections != null) {
			for(int i = 0; i < selections.length; i++) {
				MapObject object = selections[i];
				if(object != null) {
					renderer.render(g, object);
				}
			}
		}
	}

	/** Get the extent of the layer */
	public Rectangle2D getExtent() {
		return extent;
	}

	/** Get the name of the layer */
	public String getName() {
		return name;
	}

	/** Set the name of the layer */
	public void setName(String s) {
		name = s;
	}

	/** Add a LayerListener that is notified when the layer data changes */
	public void addLayerChangedListener(LayerChangedListener l) {
		if(!layerChangedListeners.contains(l)) {
			layerChangedListeners.add(l);
		}
	}

	/** Remove a LayerListener from the layer */
	public void removeLayerChangedListener(LayerChangedListener l) {
		layerChangedListeners.remove(l);
	}

	/** Notify listeners that the layer has changed */
	public void notifyLayerChangedListeners(LayerChangedEvent event) {
		Iterator it = layerChangedListeners.iterator();
		while(it.hasNext()) {
			((LayerChangedListener)it.next()).layerChanged(event);
		}
	}

	public Theme getTheme() {
		return new Theme(this, new DefaultRenderer(new CircleMarker()));
	}
}
