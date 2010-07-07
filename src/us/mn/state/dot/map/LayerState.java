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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import us.mn.state.dot.map.event.LayerChange;
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
abstract public class LayerState implements LayerChangedListener {

	/** Empty selection special case (for equality comparisons) */
	static protected final MapObject[] NO_SELECTION = new MapObject[0];

	/** Layer this state is for */
	protected final Layer layer;

	/** Get the layer */
	public Layer getLayer() {
		return layer;
	}

	/** Map bean for rendering */
	protected final MapBean map;

	/** Listeners that listen to this layer state */
	protected final Set<LayerChangedListener> listeners =
		new HashSet<LayerChangedListener>();

	/** List of available themes */
	protected final List<Theme> themes = new LinkedList<Theme>();

	/** Current theme */
	protected Theme theme;

	/** Currently selected map objects */
	protected MapObject[] selections = NO_SELECTION;

	/** Visibility flag (null means automatic) */
	protected Boolean visible = null;

	/**
	 * Create a new layer state.
	 * @param layer Layer this state is based upon.
	 * @param mb Map bean for rendering.
	 */
	protected LayerState(Layer layer, MapBean mb) {
		this(layer, mb, null, null);
	}

	/**
	 * Create a new layer state.
	 * @param layer Layer this state is based upon.
	 * @param mb Map bean for rendering.
	 * @param theme Theme used to paint the layer.
	 */
	protected LayerState(Layer layer, MapBean mb, Theme theme) {
		this(layer, mb, theme, null);
	}

	/**
	 * Create a new layer state.
	 * @param layer Layer this state is based upon.
	 * @param mb Map bean for rendering.
	 * @param theme Theme used to paint the layer.
	 * @param visible The visible tri-state.
	 */
	protected LayerState(Layer layer, MapBean mb, Theme theme,
		Boolean visible)
	{
		this.layer = layer;
		map = mb;
		this.theme = theme;
		this.visible = visible;
		layer.addLayerChangedListener(this);
	}

	/** Dispose of the layer state */
	public void dispose() {
		layer.removeLayerChangedListener(this);
		themes.clear();
		listeners.clear();
	}

	/** Add a theme to this layer state */
	public void addTheme(Theme t) {
		themes.add(t);
	}

	/** Get a list of all themes for this layer state */
	public List<Theme> getThemes() {
		return themes;
	}

	/** Set the theme */
	public void setTheme(Theme t) {
		if(t != theme) {
			theme = t;
			notifyLayerChangedListeners(LayerChange.status);
		}
	}

	/** Get the theme */
	public Theme getTheme() {
		return theme;
	}

	/** Set the selected map objects */
	public void setSelections(MapObject[] s) {
		if(s != selections) {
			selections = s;
			notifyLayerChangedListeners(LayerChange.selection);
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

	/** Call the specified callback for each map object in the layer */
	abstract public MapObject forEach(MapSearcher s);

	/** Paint the layer */
	public void paint(final Graphics2D g) {
		if(isVisible()) {
			final AffineTransform t = g.getTransform();
			final float scale = getScale();
			forEach(new MapSearcher() {
				public boolean next(MapObject mo) {
					theme.draw(g, mo, scale);
					g.setTransform(t);
					return false;
				}
			});
		}
	}

	/** Paint the selections for the layer */
	public void paintSelections(Graphics2D g) {
		if(isVisible()) {
			AffineTransform t = g.getTransform();
			float scale = getScale();
			MapObject[] sel = selections;
			for(MapObject mo: sel) {
				theme.drawSelected(g, mo, scale);
				g.setTransform(t);
			}
		}
	}

	/** Get the current map scale */
	protected float getScale() {
		return (float)map.getPixelWorld();
	}

	/** Get the visibility flag */
	public boolean isVisible() {
		// Sub-classes can override automatic visibility
		if(visible == null)
			return true;
		else
			return visible;
	}

	/** Get the visibility tri-state */
	public Boolean getVisible() {
		return visible;
	}

	/** Set the visibility of the layer.
	 * @param v New visibility; true == visible, false == invisible, null
	 *           means automatic. */
	public void setVisible(Boolean v) {
		boolean pv = isVisible();
		visible = v;
		if(pv != isVisible())
			notifyLayerChangedListeners(LayerChange.visibility);
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
	protected void notifyLayerChangedListeners(LayerChange reason) {
		LayerChangedEvent e = new LayerChangedEvent(this, reason);
		for(LayerChangedListener l: listeners)
			l.layerChanged(e);
	}

	/** Deal with a layer changed event */
	public void layerChanged(LayerChangedEvent e) {
		notifyLayerChangedListeners(e.getReason());
	}

	/** Get the appropriate tool tip text for the specified point */
	public String getTip(Point2D p) {
		if(isSearchable()) {
			MapObject mo = search(p);
			if(mo != null)
				return theme.getTip(mo);
		}
		return null;
	}

	/** Search the layer for a map object containing the given point */
	public MapObject search(final Point2D p) {
		return forEach(new MapSearcher() {
			public boolean next(MapObject mo) {
				AffineTransform t = mo.getInverseTransform();
				Point2D ip = t.transform(p, null);
				return mo.getShape().contains(ip);
			}
		});
	}

	/** Process a mouse click for the layer */
	public boolean doMouseClicked(MouseEvent e, Point2D p) {
		if(isSearchable()) {
			MapObject mo = search(p);
			if(SwingUtilities.isLeftMouseButton(e))
				doLeftClick(e, mo);
			if(SwingUtilities.isRightMouseButton(e))
				doRightClick(e, mo);
			if(mo != null)
				return true;
		}
		return false;
	}

	/** Process a left click on a map object */
	protected void doLeftClick(MouseEvent e, MapObject mo) {}

	/** Process a right click on a map object */
	protected void doRightClick(MouseEvent e, MapObject mo) {}

	/** Check if the layer is currently searchable */
	protected boolean isSearchable() {
		return isVisible() && layer instanceof DynamicLayer;
	}
}
