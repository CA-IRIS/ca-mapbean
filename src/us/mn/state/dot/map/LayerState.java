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
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import us.mn.state.dot.map.event.LayerChangedEvent;
import us.mn.state.dot.map.event.LayerChangedListener;

/**
 * Layer state is the rendering state for one layer on a map. Multiple layer
 * states can share the same underlying layer for separate map components.
 * 
 * The layer state can be made invisible and can listen for mouse actions.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class LayerState implements LayerChangedListener {

	/** Empty selection special case (for equality comparisons) */
	static protected final MapObject[] NO_SELECTION = new MapObject[0];

	/** Layer this state is for */
	public final Layer layer;

	/** Listeners that listen to this layer state */
	protected final Set<LayerChangedListener> listeners =
		new HashSet<LayerChangedListener>();

	/** List of available layer renderers */
	protected final List<LayerRenderer> renderers =
		new LinkedList<LayerRenderer>();

	/** Current LayerRenderer */
	protected LayerRenderer renderer;

	/** Selection renderer which paints object selections */
	protected SelectionRenderer selectionRenderer;

	/** Currently selected map objects */
	protected MapObject[] selections = NO_SELECTION;

	/** Visibility flag */
	protected boolean visible = true;

	/**
	 * Create a new layer state.
	 *
	 * @param layer Layer this state is based upon.
	 */
	public LayerState(Layer layer) {
		this(layer, null, true);
	}

	/**
	 * Create a new layer state.
	 *
	 * @param layer Layer this state is based upon.
	 * @param renderer LayerRenderer used to paint the layer.
	 */
	public LayerState(Layer layer, LayerRenderer renderer) {
		this(layer, renderer, true);
	}

	/**
	 * Create a new layer state.
	 *
	 * @param layer Layer this state is based upon.
	 * @param renderer LayerRenderer used to paint the layer.
	 * @param visible The visible flag.
	 */
	public LayerState(Layer layer, LayerRenderer renderer, boolean visible) {
		this.layer = layer;
		this.renderer = renderer;
		this.visible = visible;
		selectionRenderer = null;
		layer.addLayerChangedListener(this);
	}

	/** Dispose of the layer state */
	public void dispose() {
		layer.removeLayerChangedListener(this);
		renderers.clear();
		listeners.clear();
		selectionRenderer = null;
	}

	/** Add a LayerRenderer to this states list of available renderers */
	public void addLayerRenderer(LayerRenderer renderer) {
		renderers.add(renderer);
	}

	/** Get a List containing all of the renderers that have been
	 * added to the layer state */
	public List<LayerRenderer> getLayerRenderers() {
		return renderers;
	}

	/** Set the current LayerRenderer */
	public void setCurrentLayerRenderer(LayerRenderer r) {
		if(r != renderer) {
			renderer = r;
			notifyLayerChangedListeners(LayerChangedEvent.SHADE);
		}
	}

	/** Get the current layer renderer */
	public LayerRenderer getCurrentLayerRenderer() {
		return renderer;
	}

	/** Set the selection renderer */
	public void setSelectionRenderer(SelectionRenderer r) {
		selectionRenderer = r;
	}

	/** Set the selected map objects */
	public void setSelections(MapObject[] s) {
		if(s != selections) {
			selections = s;
			notifyLayerChangedListeners(
				LayerChangedEvent.SELECTION);
		}
	}

	/** Clear the selected map objects */
	public void clearSelections() {
		setSelections(NO_SELECTION);
	}

	/** Get the selected map objects */
	public MapObject[] getSelections() {
		return selections;
	}

	/** Get the layer extent */
	public Rectangle2D getExtent() {
		return layer.getExtent();
	}

	/** Paint the layer */
	public void paint(Graphics2D g) {
		if(visible)
			layer.paint(g, renderer);
	}

	/** Paint the selections for the layer */
	public void paintSelections(Graphics2D g) {
		MapObject[] sel = selections;
		if(visible && selectionRenderer != null) {
			for(MapObject s: sel)
				selectionRenderer.render(g, s);
		}
	}

	/** Get the appropriate tool tip text for the specified point */
	public String getTip(Point2D p) {
		if(isSearchable()) {
			MapObject o = layer.search(p);
			if(o != null)
				return renderer.getTip(o);
		}
		return null;
	}

	/** Get the visibility flag */
	public boolean isVisible() {
		return visible;
	}

	/** Check if the layer is currently searchable */
	protected boolean isSearchable() {
		return visible && layer instanceof DynamicLayer;
	}

	/** Set the visibility of the layer */
	public void setVisible(boolean v) {
		if(v != visible) {
			visible = v;
			notifyLayerChangedListeners(LayerChangedEvent.DATA);
		}
	}

	/** Add a layer changed listener to this layer state */
	public void addLayerChangedListener(LayerChangedListener l) {
		listeners.add(l);
	}

	/** Remove a layer changed listener from this layer state */
	public void removeLayerChangedListener(LayerChangedListener l) {
		listeners.remove(l);
	}

	/** Notify all listeners that this layer state has changed */
	protected void notifyLayerChangedListeners(int reason) {
		LayerChangedEvent e = new LayerChangedEvent(this, reason);
		for(LayerChangedListener l: listeners)
			l.layerChanged(e);
	}

	/** Deal with a layer changed event */
	public void layerChanged(LayerChangedEvent e) {
		notifyLayerChangedListeners(e.getReason());
	}

	/** Get the layer */
	public Layer getLayer() {
		return layer;
	}

	/** Process a left click on a map object */
	protected void doLeftClick(MouseEvent e, MapObject o) {}

	/** Process a right click on a map object */
	protected void doRightClick(MouseEvent e, MapObject o) {}

	/** Process a mouse click for the layer */
	public boolean doMouseClicked(MouseEvent e, Point2D p) {
		if(isSearchable()) {
			MapObject o = layer.search(p);
			if(o != null) {
				setSelections(new MapObject[] { o });
				if(SwingUtilities.isLeftMouseButton(e))
					doLeftClick(e, o);
				if(SwingUtilities.isRightMouseButton(e))
					doRightClick(e, o);
				return true;
			} else
				clearSelections();
		}
		return false;
	}
}
