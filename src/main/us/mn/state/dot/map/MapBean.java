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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import us.mn.state.dot.shape.event.*;

/**
 * The Map class is a container for a MapPane which allows the pane to be
 * scrolled and zoomed.  It has several convenience methods giving access to
 * the internal MapPane.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.17 $ $Date: 2001/04/20 15:52:03 $
 * @see us.mn.state.dot.shap.MapPane
 */
public final class MapBean extends JComponent implements MapChangedListener{
	
	/** buffer used to pan the map. */
	private transient Image panBuffer = null;

	/** home location */
	private Rectangle2D extentHome = new Rectangle2D.Double();

	/** current mouse mode */
	private MapMouseMode activeMouseMode = null;
	
	/** MapPane that will create the map */
	private final MapPane mapPane;
		
	/**
	 * Constructor
	 */
	public MapBean() {
		this( new ArrayList() );
	}

	/**
	 * Constructs a Map containing a MapPane using the themes contained in the
	 * themes parameter.
	 * @param themes a list of themes to be used in the map
	 */
	public MapBean( java.util.List themes ) {
		mapPane = new MapPane( themes );
		mapPane.addMapChangedListener( this );
		mapPane.setBackground( this.getBackground() );
		//this.setDoubleBuffered( false );
		this.setOpaque( true );
		this.setToolTipText( " " );
		addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e ) {
				rescale();
			}
		});
		setMouseMode( new SelectMouseMode() );
	}
	
   /**
    * Set the background color of the MapBean.
    * @param c, the color to use for the background.
    */
	public void setBackground( Color c ) {
		super.setBackground( c );
		mapPane.setBackground( c );
	}
	
	/**
	 * Sets the action occuring on mouse events
	 * @param m an integer describing mouse action either Map.NONE, Map.SELECT,
	 * Map.PAN, or Map.ZOOM
	 */
	public void setMouseMode( MapMouseMode mode ) {
		if ( activeMouseMode != null ) {
			removeMouseListener( activeMouseMode );
			removeMouseMotionListener( activeMouseMode );
			activeMouseMode.removeAllMapMouseListeners();
		}
		activeMouseMode = mode;
		setCursor( activeMouseMode.getCursor() );
		addMouseListener( mode );
		addMouseMotionListener( mode );
		String modeId = activeMouseMode.getID();
		java.util.List themes = mapPane.getThemes();
		Iterator it = themes.iterator();
		while ( it.hasNext() ) {
			Theme theme = ( Theme ) it.next();
			MapMouseListener listener = theme.getMapMouseListener();
			if ( listener != null && listener.listensToMouseMode( modeId ) ) {
				activeMouseMode.addMapMouseListener( listener );
			}
		}
	}
	
	/**
	 * Gets the action occuring on mouse events
	 * @return an int describing the current mouse action
	 */
	public MapMouseMode getMouseMode() {
		return activeMouseMode;
	}
	
	/**
	 * Add a new Theme to the Map.
	 * @param theme Theme to be added to the Map
	 */
	public void addTheme( Theme theme ) {
		mapPane.addTheme( theme );
		extentHome = theme.getExtent();
		registerWithMouseListener( theme );
	}

	/** 
	 * Remove a Theme from the Map.
	 * @param name Name of theme to be removed.
 	 */
	/*public void removeTheme( String name ) {
		Theme theme = mapPane.getTheme( name );
		theme.removeThemeChangedListener( this );
		mapPane.removeTheme( name );
	}*/
		
	
	/**
	 * Add a new theme to the Map at the specified index.
	 * @param theme Theme to be added to the Map
	 * @param index int specifying the index at which the theme should be added
	 */
	public void addTheme( Theme theme, int index ) {
		mapPane.addTheme( theme, index );
		//theme.addThemeChangedListener( this );
		registerWithMouseListener( theme );
	}
	
	private void registerWithMouseListener( Theme theme ){
		if ( ! theme.isStatic() ) {
			MapMouseListener listener = theme.getMapMouseListener();
			if ( listener != null && activeMouseMode != null && 
					listener.listensToMouseMode( activeMouseMode.getID() ) ){
				activeMouseMode.addMapMouseListener( listener );
			}
		}
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
		for ( ListIterator li = layers.listIterator(); li.hasNext(); ){
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
	}
	
	/**
	 * Gets the theme with the name name from the Map.
	 * @param name the string containing the Name of layer to return.
	 * @return Theme or null if not found.
	 */
	public Theme getTheme( String name ) {
		return mapPane.getTheme( name );
	}
	
	/**
	 * Returns a List of the themes contained by this Map.
	 * @return List of current themes contained by this Map
	 */
	public java.util.List getThemes() {
		return mapPane.getThemes();
	}

	/**
	 * Sets extent to given coordinates
	 */
	public void home() {
		setExtent( extentHome );
		repaint();
	}
		
	public String getToolTipText( MouseEvent e ) {
		String result = null;
		AffineTransform world = null;
		try {
			world = mapPane.getTransform().createInverse();
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		Point p1 = new Point();
		Point2D p = world.transform( e.getPoint(), p1 );
		for ( ListIterator it = mapPane.getThemes().listIterator();
				it.hasNext(); ) {
			Theme l = ( Theme ) it.next();
			if ( l.isVisible() ) {
				result = l.getTip( p );
				if ( result != null ) {
					break;
				}
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
		mapPane.setExtentFrame( x, y, width, height );
		extentHome.setFrame( x, y, width, height );
		repaint();
	}
	
	/**
	 * Pan the map.
	 * @param distanceX, number of pixels to move in the X coordinate.
	 * @param distanceY, number of pixels to move in the Y coordinate.
	 */
	public void pan( int distanceX, int distanceY ) {
		if ( panBuffer == null ) {
			panBuffer = this.createImage( getBounds().width,
				getBounds().height );
		}
		Graphics pb = panBuffer.getGraphics();
		pb.setColor( this.getBackground() );
		pb.fillRect( 0, 0, getBounds().width, getBounds().height );
		pb.drawImage( mapPane.getImage(), distanceX, distanceY, this );
		Graphics g = this.getGraphics();
		g.drawImage( panBuffer, 0, 0, this );
		pb.dispose();
		g.dispose();
	}
	
	public void finishPan( Point2D start, Point2D end ) {
		AffineTransform transform = mapPane.getTransform();
		try {
			transform.inverseTransform( start, start );
			transform.inverseTransform( end, end );
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		double newX = start.getX() - end.getX();
		double newY = start.getY() - end.getY();
		Rectangle2D extent = mapPane.getExtent();
		mapPane.setExtentFrame( extent.getX() + newX, extent.getY() + newY, 
			extent.getWidth(), extent.getHeight() );
		repaint();
	}
	
	public void zoomOut( Point center ) { // FIXME CHANGE SO THAT IT CENTERS THE VIEW AT THE POINT OF CLICK
		Rectangle2D extent = mapPane.getExtent();
		mapPane.setExtentFrame( extent.getX() - extent.getWidth() / 2, 
			extent.getY() - extent.getHeight() / 2, extent.getWidth() * 2,
			extent.getHeight() * 2 );
		repaint();
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
		AffineTransform transform = mapPane.getTransform();
		try {
			transform.inverseTransform( upperLeft, upperLeft );
			transform.inverseTransform( lowerRight, lowerRight );
		} catch ( NoninvertibleTransformException e ) {
			e.printStackTrace();
		}
		double x = Math.min( upperLeft.getX(), lowerRight.getX() );
		double y = Math.min( upperLeft.getY(), lowerRight.getY() );
		double width = Math.abs( upperLeft.getX() - lowerRight.getX() );
		double height = Math.abs( upperLeft.getY() - lowerRight.getY() );
		mapPane.setExtentFrame( x, y, width, height );
		repaint();
	}

	/**
	 * Called when the MapBean is resized or the extent is changed.
	 */
	private synchronized void rescale() {
		mapPane.setSize( this.getSize() );
		panBuffer = null;
		if ( this.isShowing() ) {
			repaint();
		}
	}
	
	/**
	 * Get the transform that the map uses to convert from map coordinates
	 * to screen coordinates.
	 */
	public AffineTransform getTransform(){
		return mapPane.getTransform();
	}

	public void paintComponent( Graphics g ) {
		//super.paintComponent( g );
		Image image = mapPane.getImage();
		Graphics2D g2d = ( Graphics2D ) g;
		g2d.drawImage( image, 0, 0, this );
		g2d.transform( mapPane.getTransform() );
		java.util.List themes = mapPane.getThemes();
		ListIterator li = themes.listIterator( themes.size() );
		while ( li.hasPrevious() ) {
			( ( Theme ) li.previous() ).paintSelections( g2d );
		}
	}
	
	public void mapChanged() {
		repaint();
	}
	
	public BufferedImage getImage() {
		return mapPane.getImage();
	}
}
