/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000  Minnesota Department of Transportation
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

import java.util.*;
import java.awt.geom.*;
import java.awt.*;

/** 
 * The AbstractLayer implements much of the functionality of the Layer
 * interface.
 *
 * @author Erik Engstrom
 * @since 1.0
 */
public abstract class AbstractLayer implements Layer {
	/** extent of layer */
	protected Rectangle2D.Double extent = new Rectangle2D.Double();

	private boolean visible = true;

	private String name;

	private java.util.List layerChangedListeners = new ArrayList();

	private boolean dynamic = false;

	/** 
	 * Gets the extent of the layer.
	 *
	 * @return the extent of the layer.
	 */
	public Rectangle2D getExtent() {
		return extent;
	}

	/**
	 * Returns true if the layer is visible.
	 *
	 * @return returns true if the layer is visible
	 */
	/*public boolean isVisible() {
		return visible;
	}*/

	/**
	 * Shows or hides this layer depending on the value of parameter b.
	 *
	 * @param b boolean determining if the layer is visible or not
	 */
	/*public void setVisible( boolean b ) {
		visible = b;
		repaintLayer();
	}*/

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
	 * Adds a LayerListener to the layer that is notified when the layer's data is
	 * changed.
	 *
	 * @param l LayerListener to be added to the layer
	 */
	public void addLayerChangedListener( LayerChangedListener l) {
		if ( ! layerChangedListeners.contains( l ) ) {
			layerChangedListeners.add( l );
		}
	}

	public void updateLayer() {
		ListIterator it = layerChangedListeners.listIterator();
		LayerChangedEvent event = new LayerChangedEvent( this, 2 );
		while ( it.hasNext() ){
			(( LayerChangedListener ) it.next() ).layerChanged( event );
		}
	}

	/** Is this layer static?
	 * @return true if data is not dynamic
	 */
	public boolean isStatic() {
		return ! dynamic;
	}

	/** if set to false layer is static and the data will not change.  Static layers
	 * are painted behind non-static (dynamic) layers on a map.  If it is desired for
	 * a static layer to be higher in the map it setStatic() must be set to true.
	 * @param b true - layer is static and data will not be update; layer is painted behind any
	 * non-static layers.
	 * false - layer is dynamic and layer will change if the layer's data is changed;
	 * will be painted in front of any static layers
	 */
	public void setStatic( boolean b ) {
		dynamic = ! b;
	}

	/*public void paint( Graphics2D g, Renderer r ) {
	}
	
	public void paintSelections( Graphics2D g, Renderer renderer,
		ArrayList selections ) {
	}*/
	public Field[] getFields() {
		return null;
	}
}