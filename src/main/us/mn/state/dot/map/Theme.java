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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import us.mn.state.dot.shape.event.LayerChangedEvent;
import us.mn.state.dot.shape.event.LayerChangedListener;
import us.mn.state.dot.shape.event.MapMouseListener;
import us.mn.state.dot.shape.event.ThemeChangedEvent;
import us.mn.state.dot.shape.event.ThemeChangedListener;

/**
 * Base class for all themes.
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class Theme implements LayerChangedListener {
	
	/** The Layer that this theme controls */
	protected Layer layer;
	
	/** The LayerRenderer used to paint this theme's layer */
	protected LayerRenderer renderer;
	
	/** The LayerRenderer used to paint selected objects in the layer */
	protected LayerRenderer selectionRenderer;
	
	/** List of available renderers for theme */
	protected java.util.List layerRenderers = new ArrayList();
		
	/** The name of this theme */
	private String name;
	
	/** Currently selected shapes */
	private MapObject[] selections = new MapObject[ 0 ];
	
	/** Visibility flag for this theme */
	private boolean visible = true;
	
	protected MapPane map = null;
	
	/** ThemeChangedListeners that listen to this theme */
	private List listeners = new ArrayList();

	/**
	 * Constructs a new theme based on the layer parameter.  The theme will have
	 * no name and all shapes will be painted grey.  It is visible by default.
	 * @param layer Layer this theme is base upon.
	 */
	public Theme( Layer layer ) {
		this( layer, null, true );
	}
		
	/**
	 * Constructs a new theme based on the layer parameter.  The LayerRenderer
	 * referenced in the renderer parameter will be used to paint the theme.
	 *
	 * @param layer Layer this theme is base upon.
	 * @param renderer LayerRenderer used to paint the layer.
	 */
	public Theme( Layer layer, LayerRenderer renderer ) {
		this( layer, renderer, true );
	}
	
	/**
	 * Constructs a new theme based on the layer parameter.  The LayerRenderer
	 * referenced in the renderer parameter will be used to paint the theme.
	 *
	 * @param layer Layer this theme is base upon.
	 * @param renderer LayerRenderer used to paint the layer.
	 * @param visible Sets the visible flag of this theme.
	 */
	public Theme( Layer layer, LayerRenderer renderer, boolean visible ) {
		this.layer = layer;
		this.renderer = renderer;
		this.name = layer.getName();
		this.visible = visible;
		selectionRenderer = renderer;
	}
	
	public void setMap( MapPane map ) {
		if ( map != null ) {
			layer.addLayerChangedListener( this );
		} else {
			layer.removeLayerChangedListener( this );
		}
		this.map = map;
	}
	
	/** Add a LayerRenderer to this themes list of available renderers */
	public void addLayerRenderer( LayerRenderer renderer ) {
		layerRenderers.add( renderer );
	}
	
	/** Get a List containing all of the renderers that have been
	 * added to the theme */
	public java.util.List getLayerRenderers() {
		return layerRenderers;
	}
	
	/** Set the current LayerRenderer */
	public void setCurrentLayerRenderer( LayerRenderer renderer ) {
		this.renderer = renderer;
		notifyThemeChangedListeners( new ThemeChangedEvent( this,
			ThemeChangedEvent.SHADE ) ); 
	}
	
	public LayerRenderer getCurrentLayerRenderer() {
		return renderer;
	}
	
	public void setSelectionRenderer( LayerRenderer renderer ) {
		selectionRenderer = renderer;
	}
	
	public void clearSelections() {
		setSelections( new MapObject[ 0 ] );
	}
			
	public void setSelections( MapObject[] newSelections ) {
		selections = newSelections;
	}
	
	public MapObject[] getSelections() {
		return selections;
	}
	
	/** Get the extent of this theme */
	public Rectangle2D getExtent() {
		return layer.getExtent();
	}

	/** Paint the theme */
	public void paint(Graphics2D g) {
		if(visible) {
			layer.paint(g, renderer);
		}
	}
	
	/** Called by the map to paint this theme */
	public void paintSelections( Graphics2D g ){
		if ( visible ) {
			layer.paintSelections( g, selectionRenderer, selections );
		}
	}
	
	/** Get the appropriate tool tip text for the specified point */
	public String getTip( Point2D point ) {
		String result = null;
		if ( mapTip == null ) {
			return result;
		}
		java.util.List found = layer.getPaths( point, renderer );
		if ( ! found.isEmpty() ) {
			result = mapTip.getTip( ( MapObject ) found.get( 0 ) );
		}
		return result;
	}
	
	private MapTip mapTip;
	
	public void setTip( MapTip tip ) {
		mapTip = tip;
	}
	
	/** Get the visibility flag */
	public boolean isVisible() {
		return visible;
	}
	
	/** Set the visibility of the theme */
	public void setVisible(boolean visible) {
		this.visible = visible;
		notifyThemeChangedListeners( new ThemeChangedEvent( this,
			ThemeChangedEvent.DATA ) );
	}
	
	/** Get the name of the theme */
	public String getName() {
		return name;
	}
	
	/** Add a ThemeChangedListener to the listeners of this theme */
	public void addThemeChangedListener(ThemeChangedListener listener) {
		if ( ! listeners.contains( listener ) ) {
			listeners.add( listener );
		}
	}
	
	/** Remove a ThemeChangedListener from the listeners of this theme */
	public void removeThemeChangedListener(ThemeChangedListener listener) {
		if ( listeners.contains( listener ) ) {
			listeners.remove( listener );
		}
	}
	
	/** Notify all listeners that this theme has changed */
	protected void notifyThemeChangedListeners(ThemeChangedEvent event) {
		ThemeChangedListener listener;
		for ( Iterator it = listeners.iterator(); it.hasNext(); ) {
			listener = ( ThemeChangedListener ) it.next();
			listener.themeChanged( event );
		}
	}
	
	/** Checks the static flag for this theme */
	public boolean isStatic() {
		return layer.isStatic();
	}
	
	/** Sets the static flag for this theme */
	public void setStatic(boolean b) {
		layer.setStatic( b );
	}
	
	public void layerChanged( LayerChangedEvent e ) {
		notifyThemeChangedListeners( new ThemeChangedEvent( this,
			e.getReason() ) );
	}
	
	public Layer getLayer() {
		return layer;
	}
	
	/**
	 * Themes that wish to respond to mouse events should over ride this
	 * method.
	 */
	public MapMouseListener getMapMouseListener() {
		return null;
	}
}
