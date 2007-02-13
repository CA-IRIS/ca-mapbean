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
import us.mn.state.dot.map.event.ThemeChangedEvent;
import us.mn.state.dot.map.event.ThemeChangedListener;

/**
 * A theme is associated with one layer and one active layer renderer.
 * It can be made invisible and can listen for mouse actions.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class Theme implements LayerChangedListener {

	/** Empty selection special case (for equality comparisons) */
	static protected final MapObject[] NO_SELECTION = new MapObject[0];

	/** Layer controlled by the theme */
	public final Layer layer;

	/** List of available renderers for theme */
	protected final List<LayerRenderer> renderers =
		new LinkedList<LayerRenderer>();

	/** The LayerRenderer used to paint this theme's layer */
	protected LayerRenderer renderer;

	/** Selection renderer which paints object selections for the theme */
	protected SelectionRenderer selectionRenderer;

	/** Currently selected shapes */
	protected MapObject[] selections = new MapObject[0];

	/** Visibility flag for this theme */
	protected boolean visible = true;

	/** ThemeChangedListeners that listen to this theme */
	protected final Set<ThemeChangedListener> listeners =
		new HashSet<ThemeChangedListener>();

	/**
	 * Create a new theme based on the layer parameter. It will have
	 * no name and all shapes will be painted grey.
	 * @param layer Layer this theme is base upon.
	 */
	public Theme(Layer layer) {
		this(layer, null, true);
	}

	/**
	 * Create a new theme based on the layer parameter.  The LayerRenderer
	 * referenced in the renderer parameter will be used to paint the theme.
	 *
	 * @param layer Layer this theme is base upon.
	 * @param renderer LayerRenderer used to paint the layer.
	 */
	public Theme(Layer layer, LayerRenderer renderer) {
		this(layer, renderer, true);
	}

	/**
	 * Create a new theme based on the layer parameter.  The LayerRenderer
	 * referenced in the renderer parameter will be used to paint the theme.
	 *
	 * @param layer Layer this theme is base upon.
	 * @param renderer LayerRenderer used to paint the layer.
	 * @param visible Sets the visible flag of this theme.
	 */
	public Theme(Layer layer, LayerRenderer renderer, boolean visible) {
		this.layer = layer;
		this.renderer = renderer;
		this.visible = visible;
		selectionRenderer = null;
	}

	/** Dispose of the theme */
	public void dispose() {
		renderers.clear();
		listeners.clear();
		selectionRenderer = null;
	}

	/** Add a LayerRenderer to this themes list of available renderers */
	public void addLayerRenderer(LayerRenderer renderer) {
		renderers.add(renderer);
	}

	/** Get a List containing all of the renderers that have been
	 * added to the theme */
	public List<LayerRenderer> getLayerRenderers() {
		return renderers;
	}

	/** Set the current LayerRenderer */
	public void setCurrentLayerRenderer(LayerRenderer r) {
		if(r != renderer) {
			renderer = r;
			notifyThemeChangedListeners(ThemeChangedEvent.SHADE);
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
			notifyThemeChangedListeners(
				ThemeChangedEvent.SELECTION);
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

	/** Get the extent of this theme */
	public Rectangle2D getExtent() {
		return layer.getExtent();
	}

	/** Paint the theme */
	public void paint(Graphics2D g) {
		if(visible)
			layer.paint(g, renderer);
	}

	/** Paint the selections for the theme */
	public void paintSelections(Graphics2D g) {
		MapObject[] s = selections;
		if(visible && s != null && selectionRenderer != null) {
			for(int i = 0; i < s.length; i++) {
				selectionRenderer.render(g, s[i]);
			}
		}
	}

	/** Get the appropriate tool tip text for the specified point */
	public String getTip(Point2D p) {
		if(layer instanceof DynamicLayer) {
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

	/** Set the visibility of the theme */
	public void setVisible(boolean v) {
		if(v != visible) {
			visible = v;
			notifyThemeChangedListeners(ThemeChangedEvent.DATA);
		}
	}

	/** Add a ThemeChangedListener to the listeners of this theme */
	public void addThemeChangedListener(ThemeChangedListener listener) {
		listeners.add(listener);
	}

	/** Remove a ThemeChangedListener from the listeners of this theme */
	public void removeThemeChangedListener(ThemeChangedListener listener) {
		listeners.remove(listener);
	}

	/** Notify all listeners that this theme has changed */
	protected void notifyThemeChangedListeners(int reason) {
		ThemeChangedEvent e = new ThemeChangedEvent(this, reason);
		for(ThemeChangedListener l: listeners)
			l.themeChanged(e);
	}

	public void layerChanged(LayerChangedEvent e) {
		notifyThemeChangedListeners(e.getReason());
	}

	public Layer getLayer() {
		return layer;
	}

	/** Process a left click on a map object */
	protected void doLeftClick(MouseEvent e, MapObject o) {}

	/** Process a right click on a map object */
	protected void doRightClick(MouseEvent e, MapObject o) {}

	/** Process a mouse click for the theme */
	public boolean doMouseClicked(MouseEvent e, Point2D p) {
		if(visible && layer instanceof DynamicLayer) {
			MapObject o = layer.search(p);
			if(o != null) {
				MapObject[] selections = { o };
				setSelections(selections);
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
