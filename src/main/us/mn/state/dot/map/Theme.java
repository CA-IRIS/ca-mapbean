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

/*
 * Theme.java
 *
 * Created on June 16, 2000, 3:12 PM
 */

package us.mn.state.dot.shape;

import java.util.*;
import java.awt.geom.*;
import java.awt.*;

/**
 * Base class for all themes.
 * @author  engs1eri
 * @version 
 */
public class Theme implements LayerChangedListener {
	/** The Layer that this theme controls.*/
	protected Layer layer;
	
	/** The Renderer used to paint this theme's layer */
	protected Renderer renderer;
	
	/** The name of this theme.*/
	private String name;
	
	/** Currently selected shapes.*/
	private ArrayList selections = null;
	
	/** Visibility flag for this theme.*/
	private boolean visible = true;
	
	/** ThemeChangedListeners that listen to this theme.*/
	private ArrayList listeners = new ArrayList();

	/**
	 * Constructs a new theme based on the layer parameter.  The theme will have
	 * no name and all shapes will be painted grey.  It is visible by default.
	 * @param layer Layer this theme is base upon.
	 */
	public Theme( Layer layer ) {
		this( layer, null, true );
	}
		
	/**
	 * Constructs a new theme based on the layer parameter.  The Renderer
	 * referenced in the renderer parameter will be used to paint the theme.
	 *
	 * @param layer Layer this theme is base upon.
	 * @param renderer Renderer used to paint the layer.
	 */
	public Theme( Layer layer, Renderer renderer ) {
		this( layer, renderer, true );
	}
	
		/**
	 * Constructs a new theme based on the layer parameter.  The Renderer
	 * referenced in the renderer parameter will be used to paint the theme.
	 *
	 * @param layer Layer this theme is base upon.
	 * @param renderer Renderer used to paint the layer.
	 * @param visible Sets the visible flag of this theme.
	 */
	public Theme( Layer layer, Renderer renderer, boolean visible ) {
		this.layer = layer;
		this.layer.addLayerChangedListener( this );
		this.renderer = renderer;
		this.name = layer.getName();
		this.visible = visible;
	}
	
	public void setRenderer( Renderer renderer ) {
		this.renderer = renderer;
	}
	
	public Renderer getRenderer() {
		return renderer;
	}
	
	/** 
	 * Returns the extent of this theme.
	 */
	public Rectangle2D getExtent(){
		return layer.getExtent();
	}
	
	/** 
	 * Paint this layer.
	 *
	 * @param g Graphics2D object to paint on.
	 */
	public void paint( Graphics2D g ){
		layer.paint( g, renderer );
	}
	
	/**
	 * Called by the map to paint this theme.
	 * @param g Graphics2D object to paint to
	 */
	public void paintSelections( Graphics2D g ){
		layer.paintSelections( g, renderer, selections );
	}
	
	/**
	 * Called by the map on a mouseClick event.
	 * @param clickCount number of clicks
	 * @param point location of click
	 * @param g Graphics2D object that click occured on
	 */
	public boolean mouseClick( int clickCount, Point2D point,
			Graphics2D g ) {
		return false;
	}
	
	/**
	 * Called by the map to get appropriate tool tip text
	 */
	public String getTip( Point2D point ) {
		return "";
	}
	
	/**
	 * Gets the visibility flag
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Sets the visibility of the theme.
	 * 
	 * @param visible new value of the visible flag
	 */
	public void setVisible( boolean visible ){
		this.visible = visible;
	}
	
	/**
	 * Gets the name of the theme
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Add a ThemeChangedListener to the listeners of this theme.
	 * 
	 * @param listener to add.
	 */
	public void addThemeChangedListener( ThemeChangedListener listener ) {
		if ( ! listeners.contains( listener ) ) {
			listeners.add( listener );
		}
	}
	
	/**
	 * Remove a ThemeChangedListener from the listeners of this theme.
	 *
	 * @param listener to remove
	 */
	public void removeThemeChangedListener( ThemeChangedListener listener ) {
		if ( listeners.contains( listener ) ) {
			listeners.remove( listener );
		}
	}
	
	/**
	 * Notify all listeners that this theme has changed.
	 *
	 * @param event ThemeChangedEvent that occured.
	 */
	protected void notifyThemeChangedListeners( ThemeChangedEvent event ){
		ThemeChangedListener listener;
		for ( Iterator it = listeners.iterator(); it.hasNext(); ) {
			listener = ( ThemeChangedListener ) it.next();
			listener.themeChanged( event );
		}
	}
	
	/**
	 * Checks the static flag for this theme.
	 */
	public boolean isStatic() {
		return layer.isStatic();
	}
	
	/**
	 * Sets the static flag for this theme.
	 */
	public void setStatic( boolean b ){
		layer.setStatic( b );
	}
	
	public void layerChanged( LayerChangedEvent e ) {
	}
	
	public Field [] getFields(){
		return layer.getFields();
	}
}