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

//Title:        Map
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import us.mn.state.dot.dds.client.*;

/**
 * The Map class is a container for a MapPane which allows the pane to be
 * scrolled and zoomed.  It has several convenience methods giving access to
 * the internal MapPane.
 * @author Erik Engstrom
 * @version 1.0
 * @see us.mn.state.dot.shap.MapPane
 */
public final class Map extends JComponent implements ThemeChangedListener {
	
	/** buffer for map */
	private BufferedImage screenBuffer;

	/** buffer for static themes in map */
	private BufferedImage staticBuffer;

	/** List of dynamic themes */
	private final ArrayList themes = new ArrayList();

	/** List of static themes */
	private final ArrayList staticThemes = new ArrayList();

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform screenTransform = new AffineTransform();

	/** Bounding box */
	private Rectangle2D extent = new Rectangle2D.Double();
	
	public Rectangle2D extentHome = new Rectangle2D.Double();

	private boolean bufferDirty = true;

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform at = new AffineTransform();

	/** Mouse helper class */
	private final MouseDelegator mouse = new MouseDelegator( this );

	/**
	 * Constructor
	 */
	public Map() {
		this( new ArrayList() );
	}

	/**
	 * Constructs a Map containing a MapPane using the themes contained in the
	 * themes parameter.
	 * @param themes a list of themes to be used in the map
	 */
	public Map( java.util.List themes ) {
		addMouseListener( mouse );
		addMouseMotionListener( mouse );
		this.setToolTipText( "" );
		for ( ListIterator li = themes.listIterator(); li.hasNext(); ){
			Object ob = li.next();
			Theme theme;
			if ( ob instanceof Layer ) {
				theme = ( ( Layer ) ob ).getTheme();
			} else if ( ob instanceof Theme ) {
				theme = ( Theme ) ob;
			} else {
				throw new IllegalArgumentException( "Must be Layer or Theme" );
			}
			addTheme( theme );
		}
		setMouseAction( MouseDelegator.SELECT );
		staticBuffer = new BufferedImage( 1405, 1728,
			BufferedImage.TYPE_INT_RGB );
		addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e ) {
				resized();
			}
		});
	}
	
	/**
	 * Sets the action occuring on mouse events
	 * @param m an integer describing mouse action either Map.NONE, Map.SELECT,
	 * Map.PAN, or Map.ZOOM
	 */
	public void setMouseAction( int m ) {
		mouse.setMouseAction( m );
	}

	/**
	 * Gets the action occuring on mouse events
	 * @return an int describing the current mouse action
	 */
	public int getMouseAction() {
		return mouse.getMouseAction();
	}

	/**
	 * notifies the Map that the selection data has changed and the map
	 * should be updated
	 */
	/*public void selectionChanged() {
		repaint();
	}*/
	
	
	AffineTransform getTransform() {
		return screenTransform;
	}
	
	/**
	 * Add a new Theme to the Map.
	 * @param theme Theme to be added to the Map
	 */
	public void addTheme( Theme theme ) {
		if ( theme.isStatic() ) {
			staticThemes.add( theme );
		} else {
			themes.add( theme );
		}
		theme.addThemeChangedListener( this );
		setExtent( theme.getExtent() );
	}
	
	/**
	 * Add a new theme to the Map at the specified index.
	 * @param theme Theme to be added to the Map
	 * @param index int specifying the index at which the theme should be added
	 */
	public void addTheme( Theme theme, int index ) {
		themes.add( index, theme );
		theme.addThemeChangedListener( this );
	}
	
	/**
	 * Add a List of themes to the Map
	 * @param themes List of themes to be added to the map
	 */
	public void addThemes( java.util.List themes ) {
		ListIterator li = themes.listIterator();
		while ( li.hasNext() ) {
			addTheme( ( Theme ) li.next() );
		}
	}
	
	public void addLayers( java.util.List layers ) {
		ListIterator li = layers.listIterator();
		while ( li.hasNext() ) {
			Layer layer = ( Layer ) li.next();
			addTheme( layer.getTheme() );
		}
	}
	
	/**
	 * Gets the theme with the name name from the Map.
	 * @param name the string containing the Name of layer to return.
	 * @return Theme or null if not found.
	 */
	public Theme getTheme( String name ) {
		Theme result = findTheme( name, themes );
		if ( result == null ) {
			result = findTheme( name, staticThemes );
		}
		return result;
	}
	
	/**
	 * Returns a List of the themes contained by this Map.
	 * @return List of current themes contained by this Map
	 */
	public java.util.List getThemes() {
		java.util.List result = new ArrayList( themes );
		result.addAll( staticThemes );
		return result;
	}

	/**
	 * Sets extent to given coordinates
	 */
	public void home() {
		setExtent( extentHome );
		resized();
	}

	/** Draw an XOR box (rubberbanding box) */
	void drawBox( Rectangle r ) {
		Graphics g = getGraphics();
		if ( g == null ) {
			return;
		}
		g.setXORMode( Color.white );
		g.drawRect( r.x, r.y, r.width, r.height );
	}
		
	public String getToolTipText( MouseEvent e ) {
		String result = null;
		AffineTransform world = null;
		try {
			world = screenTransform.createInverse();
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		Point p1 = new Point();
		Point2D p = world.transform( e.getPoint(), p1 );
		for ( ListIterator it = themes.listIterator(); it.hasNext(); ) {
			Theme l = ( Theme ) it.next();
			result = l.getTip( p );
			if ( result != null ) {
				break;
			}
		}
		return result;
	}

	public JToolTip createToolTip() {
		return new JMultiLineToolTip();
	}
	
	/**
	 * Set the bounding box for display
	 * @param r The rectangle which describes the new bounding box for the
	 *	display.
	 */
	public void setExtent( Rectangle2D r ) {
		setExtent( r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight() );		
	}
	
	public void setExtent( double x, double y, double width, double height ) {
		extent.setFrame( x, y, width, height );
		extentHome.setFrame( x, y, width, height );
		resized();
		repaint();
	}

	private Theme findTheme( String name, java.util.List layerList ) {
		Theme result = null;
		for ( ListIterator li = layerList.listIterator(); li.hasNext(); ){
			Theme temp = ( Theme ) li.next();
			if ( name.equals( temp.getName() ) ) {
				result = temp;
				break;
			}
		}
		return result;
	}

	/**
	 * Converts a Point2D from world coordinates to screen coordinates.
	 * @param point The point in world coordinates to be converted.
	 * @return A new points whose coordinates are in screen coordinates.
	 */
	/*public Point2D convertPoint( Point2D point) {
		Point2D result = null;
		result = screenTransform.transform( point, result );
		return result;
	}*/

	private transient Image panBuffer = null;
	
	public void pan( int distanceX, int distanceY ) {
		if ( panBuffer == null ) {
			panBuffer = this.createImage( getBounds().width,
				getBounds().height );
		}
		Graphics pb = panBuffer.getGraphics();
		pb.setColor( this.getBackground() );
		pb.fillRect( 0, 0, getBounds().width, getBounds().height );
		pb.drawImage( screenBuffer, distanceX, distanceY, this );
		Graphics g = this.getGraphics();
		g.drawImage( panBuffer, 0, 0, this );
	}
	
	public void zoomOut( Point center ) { //CHANGE SO THAT IT CENTERS THE VIEW AT THE POINT OF CLICK
		extent.setFrame( extent.getX() - extent.getWidth() / 2, 
			extent.getY() - extent.getHeight() / 2, extent.getWidth() * 2,
			extent.getHeight() * 2 );
		resized();
	}
	
	public void finishPan( Point2D start, Point2D end ) {
		try {
			screenTransform.inverseTransform( start, start );
			screenTransform.inverseTransform( end, end );
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		double newX = start.getX() - end.getX();
		double newY = start.getY() - end.getY();
		extent.setFrame( extent.getX() + newX, extent.getY() + newY, 
			extent.getWidth(), extent.getHeight() );
		resized();	
	}
		
	/**
	 * Increase the size of this MapPane so that the mapSpace will fill the
	 * viewerSpace
	 * @param mapSpace seleted region to zoom to
	 * @param viewerSpace current viewport size.
	 */
	public void zoom( Rectangle2D mapSpace ) {
		Point2D upperLeft = new Point2D.Double( mapSpace.getMinX(),
			mapSpace.getMinY() );
		Point2D lowerRight = new Point2D.Double( mapSpace.getMaxX(),
			mapSpace.getMaxY() );
		try {
			screenTransform.inverseTransform( upperLeft, upperLeft );
			screenTransform.inverseTransform( lowerRight, lowerRight );
		} catch ( NoninvertibleTransformException e ) {
			e.printStackTrace();
		}
		double x = Math.min( upperLeft.getX(), lowerRight.getX() );
		double y = Math.min( upperLeft.getY(), lowerRight.getY() );
		double width = Math.abs( upperLeft.getX() - lowerRight.getX() );
		double height = Math.abs( upperLeft.getY() - lowerRight.getY() );
		extent.setFrame( x, y, width, height );
		resized();
	}

	/**
	 * Called when the ShapePane is resized
	 */
	private void resized() {
		int w = getWidth();
		int h = getHeight();
		if ( ! this.isDisplayable() ) {
			h = 600;
			w = 600;
		}
		if ( h == 0 || w == 0 || extent == null ) {
			return;
		}
		double scaleX = ( double ) w / extent.getWidth();
		double scaleY = ( double ) h / extent.getHeight();
		double scale = scaleX;
		double shiftX = 0;
		double shiftY = ( h - ( extent.getHeight() * scale ) ) / 2;
		if ( scale > scaleY ) {
			scale = scaleY;
			shiftY = 0;
			shiftX = ( w - ( extent.getWidth() * scale ) ) / 2;
		}
		screenTransform.setToTranslation(  - ( extent.getMinX() * scale )
			+ shiftX, ( extent.getMaxY() * scale ) + shiftY );
		screenTransform.scale( scale, -scale );
		screenBuffer = null;
		staticBuffer = null;
		repaint();
	}

	/**
	 * Overridden to prevent flashing
	 * @param g Graphics object to paint on
	 */
	public void update( Graphics g ) {
		paint( g );
	}

	/**
	 * Refreshes map data
	 */
	public void refresh( Theme l ) {
		bufferDirty = true;
		if ( l.isStatic() ) {
			staticBuffer = null;
		}
		repaint();
	}

	private BufferedImage createBuffer() {
		int h = 0;
		int w = 0;
		if ( this.isDisplayable() ) {
			h = this.getHeight();
			w = this.getWidth();
		} else {
			h = 600;
			w = 600;
		}
		return new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
	}

	/**
	 * Updates staticBuffer
	 */
	private void updateStaticBuffer() {
		Graphics2D g2D = ( Graphics2D ) staticBuffer.getGraphics();
		int w = staticBuffer.getWidth( null );
		int h = staticBuffer.getHeight( null );
		g2D.setColor( new Color( 204, 204, 204 ) );
		g2D.fillRect( 0, 0, w, h );
		g2D.transform( screenTransform );
		g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
			RenderingHints.VALUE_ANTIALIAS_ON );
		for ( int i = ( staticThemes.size() - 1 ); i >= 0; i-- ) {
			Theme theme = ( Theme ) staticThemes.get( i );
			theme.paint( g2D );
		}
		staticBufferDirty = false;
	}
	
	public void setBufferDirty( boolean b ) {
		bufferDirty = b;
	}
	
	private boolean staticBufferDirty = false;

	/**
	 * Updates screenBuffer.
	 */
	public void updateScreenBuffer() {
		if ( staticBuffer == null ) {
			staticBuffer = createBuffer();
			updateStaticBuffer();
		} else if ( staticBufferDirty ) {
			updateStaticBuffer();
		}
		Graphics2D g2D = ( Graphics2D ) screenBuffer.getGraphics();
		int w = screenBuffer.getWidth( null );
		int h = screenBuffer.getHeight( null );
		g2D.drawImage( staticBuffer, 0, 0, this );
		g2D.transform( screenTransform );
		for ( int i = ( themes.size() - 1 ); i >= 0; i-- ) {
			Theme theme = ( Theme ) themes.get( i );
			if ( theme.isVisible() ){
				theme.paint( g2D );
			}
		}
		g2D.setColor( Color.black );
		g2D.drawRect( 0, 0, w, h );
		bufferDirty = false;
	}

	public void paintComponent( Graphics g ) {
		if ( screenBuffer == null ) {
			screenBuffer = createBuffer();
			updateScreenBuffer();
		} else if ( bufferDirty ) {
			updateScreenBuffer();
		}
		g.drawImage( screenBuffer, 0, 0, this );
		Graphics2D g2D = ( Graphics2D ) g;
		g2D.transform( screenTransform );
		for ( int i = ( themes.size() - 1 ); i >= 0; i-- ) {
			Theme theme = ( Theme ) themes.get( i );
			if ( theme.isVisible() ){
				theme.paintSelections( g2D );
			}
		}
	}
	
	public void themeChanged( ThemeChangedEvent event ) {
		Theme theme = ( Theme ) event.getSource();
		if ( theme.isStatic() ) {
			staticBufferDirty = true;
			bufferDirty = true;
		} else {
			bufferDirty = true;
		}
		repaint();
	}
}