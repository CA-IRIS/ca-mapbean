/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2010  Minnesota Department of Transportation
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

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * The map model is a collection of LayerStates and the current extent of the
 * map.
 *
 * @author Douglas Lau
 */
public class MapModel implements LayerChangedListener {

	/** Listeners for map changed events */
	protected Set<LayerChangedListener> listeners =
		new HashSet<LayerChangedListener>();

	/** Add a LayerChangedListener to the map model */
	public void addLayerChangedListener(LayerChangedListener l) {
		listeners.add(l);
	}

	/** Remove a LayerChangedListener from the map model */
	public void removeLayerChangedListener(LayerChangedListener l) {
		listeners.remove(l);
	}

	/** Change a layer in the map model */
	public void layerChanged(LayerChangedEvent ev) {
		if(ev.getSource() == home_layer) {
			if(ev.getReason() == LayerChange.extent)
				setExtent(home_layer.getExtent());
		}
		notifyLayerChangedListeners(ev);
	}

	/** Notify registered LayerChangedListeners that a layer has changed */
	protected void notifyLayerChangedListeners(LayerChange reason) {
		notifyLayerChangedListeners(new LayerChangedEvent(this,
			reason));
	}

	/** Notify registered LayerChangedListeners that a layer has changed */
	protected void notifyLayerChangedListeners(LayerChangedEvent event) {
		for(LayerChangedListener l: listeners)
			l.layerChanged(event);
	}

	/** List of all layers */
	protected final LinkedList<LayerState> lstates =
		new LinkedList<LayerState>();

	/** Add a new layer to the map model */
	public void addLayer(LayerState lstate) {
		lstates.add(lstate);
		lstate.addLayerChangedListener(this);
	}

	/** Remove a layer from the map model */
	public void removeLayer(LayerState lstate) {
		lstates.remove(lstate);
		lstate.removeLayerChangedListener(this);
	}

	/** Get the list of layer states in the map model */
	public List<LayerState> getLayers() {
		return lstates;
	}

	/** Get a list iterator for layers */
	public ListIterator<LayerState> getLayerIterator() {
		return lstates.listIterator(lstates.size());
	}

	/** Home layer */
	protected LayerState home_layer;

	/** Set the home layer */
	public void setHomeLayer(LayerState ls) {
		home_layer = ls;
	}

	/** Set the extents to the home layer extends */
	public void home() {
		if(home_layer != null) 
			setExtent(home_layer.getExtent());
	}

	/** Current extent (bounding box) */
	protected final Rectangle2D extent = new Rectangle2D.Double();

	/** Get the extent of the map model */
	public Rectangle2D getExtent() {
		return extent;
	}

	/**
	 * Set the bounding box for display
	 * @param x the new x-coordinate for the map.
	 * @param y the new y-coordinate for the map.
	 * @param width the new width for the map.
	 * @param height the new height for the map.
	 */
	public void setExtent(double x, double y, double width, double height) {
		setExtent(new Rectangle2D.Double(x, y, width, height));
	}

	/** Set the map extent */
	protected void setExtent(Rectangle2D e) {
		Rectangle2D le = getLayerExtent();
		if(le != null)
			Rectangle2D.intersect(e, le, e);
		if(!e.equals(extent)) {
			extent.setRect(e);
			notifyLayerChangedListeners(LayerChange.extent);
		}
	}

	/** Get the full extent of all layers */
	public Rectangle2D getLayerExtent() {
		Rectangle2D le = null;
		for(LayerState ls: lstates) {
			Rectangle2D e = ls.getExtent();
			if(le == null) {
				le = new Rectangle2D.Double();
				le.setRect(e);
			} else
				Rectangle2D.union(le, e, le);
		}
		return le;
	}

	/** Dispose of the map model */
	public void dispose() {
		for(LayerState s: lstates)
			s.dispose();
		lstates.clear();
		listeners.clear();
	}
}
