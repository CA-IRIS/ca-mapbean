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
import java.util.*;
import javax.swing.*;
import java.awt.image.*;

/**
 * A sublcass of JPanel used to display geographical information.  It must be
 * contained by a <CODE>us.mn.state.dot.shape.Map</CODE> in order to implement
 * panning and zooming.  Use the </CODE>MapPane(us.mn.state.dot.Map)</CODE>
 * constructor to do this.
 * @author Erik Engstrom
 * @version 1.0
 */
public final class MapPane extends JPanel implements ThemeChangedListener {
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

	/** Optional viewport */
	private Map viewport;

	/** Bounding box */
	private Rectangle2D extent = new Rectangle2D.Double();
	public Rectangle2D extentHome = new Rectangle2D.Double();

	/** Scale factor */
	// use screenTransform.getScaleX()
	//private double scale;

	/** X-Axis */
	//use screenTransform.getTranslateX()
	//private double ;

	/** Y-Axis */
	//use screenTransform.getTranslateY()
	//private double shiftY;

	private boolean bufferDirty = true;

	/**
	 * Construct an empty MapPane
	 */
	public MapPane() {
		this( null );
	}

	/**
	 * Construct an empty MapPane that is contained by the map passed as a
	 * parameter.
	 * @param map us.mn.state.dot.shape.Map that contains the newly created
	 * MapPane.
	 */
	public MapPane( Map map ) {
		setDoubleBuffered( false );
		staticBuffer = new BufferedImage( 1405, 1728,
			BufferedImage.TYPE_INT_RGB );
		viewport = map;
		addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e ) {
				resized();
				repaint();
			}
		});
	}

	/**
	 * Returns a List of the themes contained by this MapPane.
	 * @return List of current themes contained by this MapPane
	 */
	public java.util.List getThemes() {
		java.util.List result = new ArrayList( themes );
		result.addAll( staticThemes );
		return result;
	}

	AffineTransform getTransform() {
		return screenTransform;
	}

	/**
	 * Returns the themes contained in the MapPane.
	 * @param name Name of layer to return.
	 * @return Theme or null if not found.
	 */
	public Theme getTheme( String name ) {
		Theme result = findTheme( name, themes );
		if ( result == null ) {
			result = findTheme( name, staticThemes );
		}
		return result;
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
	 * move the viewport so that the point is visible
	 * @param center a Point2D in world coordinates
	 */
	public void scrollToMapPoint(Point2D center) {
		Point2D p = null;
		p = screenTransform.transform( center, p );
		Rectangle2D rec = new Rectangle2D.Double( p.getX(), p.getY(),
			1, 1 );
		Rectangle r = rec.getBounds();
		if ( viewport.getViewRect().contains( r ) ) {
			return;
		} else if ( ! this.getBounds().contains( r ) ) {
			double newX = extent.getX();
			double newY = extent.getY();
			if ( center.getX() < extent.getX() ) {
				newX = center.getX();
			}
			if ( center.getX() > extent.getMaxX() ) {
				newX += center.getX() - extent.getMaxX();
			}
			if ( center.getY() < extent.getMinY() ) {
				newY = center.getY();
			}
			if ( center.getY() > extent.getMaxY() ) {
				newY += center.getY() - extent.getMaxY();
			}
			extent.setFrame( newX, newY, extent.getWidth(),
				extent.getHeight() );
			resized();
			Point loc = viewport.getViewPosition();
			p = screenTransform.transform( center, p );
			rec = new Rectangle2D.Double( p.getX(), p.getY(),
				1, 1 );
			r = rec.getBounds();
		}
		if ( viewport.getViewRect().contains( r ) ) {
			return;
		} else {
			int xPos = ( int ) ( r.getX() - ( viewport.getWidth() / 2 ) );
			if ( xPos < 0 ) {
				xPos = 0;
			} else if ( ( xPos + viewport.getWidth() ) > getWidth() ) {
				xPos = getWidth() - viewport.getWidth();
			}
			int yPos = ( int ) ( r.getY() - ( viewport.getHeight() / 2 ) );
			if ( yPos < 0 ) {
				yPos = 0;
			} else if ( ( yPos + viewport.getHeight() ) > getHeight() ) {
				yPos = getHeight() - viewport.getHeight();
			}

			viewport.setViewPosition( new Point( xPos, yPos ) );
		}
	}

	/**
	 * Converts a Point2D from world coordinates to screen coordinates.
	 * @param point The point in world coordinates to be converted.
	 * @return A new points whose coordinates are in screen coordinates.
	 */
	public Point2D convertPoint(Point2D point) {
		Point2D result = null;
		result = screenTransform.transform( point, result );
		return result;
	}

	private Point shiftExtent( Point scrollTo, Point how ) {
		AffineTransform inverse = null;
		try {
			inverse = screenTransform.createInverse();
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		Point2D point = null;
		point = inverse.transform( scrollTo, point );
		Point2D upperLeft = viewport.getLocation();
		inverse.transform( upperLeft, upperLeft );
		Point2D lowerRight = new Point( ( viewport.getX() +
			viewport.getWidth() ), ( viewport.getY() +
			viewport.getHeight() ) );
		inverse.transform( lowerRight, lowerRight );
		double shiftX = lowerRight.getX() - upperLeft.getX();
		double shiftY = lowerRight.getY() - upperLeft.getY();
		double newX = Math.max( ( extent.getX() + ( how.getX() * shiftX ) ),
			extentHome.getX() );
		newX = Math.min( newX, ( extentHome.getMaxX() - extent.getWidth() ) );
		double newY = Math.max( ( extent.getY() + ( how.getY() * shiftY ) ),
			extentHome.getMinY() );
		newY = Math.min( newY, ( extentHome.getMaxY() - extent.getHeight() ) );
		extent.setFrame( newX, newY, extent.getWidth(), extent.getHeight() );
		resized();
		screenTransform.transform( point, point );
		return new Point( ( int ) point.getX(), ( int ) point.getY() );
	}
	
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
	
	/** Pan to point on map */
	public void panTo(Point p) {
		Point scrollTo = p;
		if ( scrollTo.getX() < 0 ) {
			if ( extent.getX() > extentHome.getX() ) {
				scrollTo = shiftExtent( scrollTo, new Point( -1, 0 ) );
			} else {
				scrollTo.x = 0;
			}
		}
		if ( scrollTo.getY() < 0 ) {
			if ( extent.getY() >= extentHome.getY() ) {
				scrollTo = shiftExtent( scrollTo, new Point( 0, -1 ) );
				if ( scrollTo.getY() < 0 ) {
					scrollTo.y = 0;
				}
			} else {
				scrollTo.y = 0;
			}
		}
		if ( ( scrollTo.getX() + viewport.getWidth() ) > getWidth() ) {
			if ( extent.getMaxX() < extentHome.getMaxX() ) {
				scrollTo = shiftExtent( scrollTo, new Point( 1, 0 ) );
			} else {
				scrollTo.x = getWidth() - viewport.getWidth();
			}
		}
		if ( ( scrollTo.getY() + viewport.getHeight() ) > getHeight() ) {
			if ( extent.getMaxY() <= extentHome.getMaxY() ) {
				scrollTo = shiftExtent( scrollTo, new Point( 0, 1 ) );
				if ( ( scrollTo.getY() + viewport.getHeight() ) >
						getHeight() ) {
					scrollTo.y = getHeight() - viewport.getHeight();
				}

			} else {
				scrollTo.y = getHeight() - viewport.getHeight();
			}
		}
		viewport.setViewPosition( scrollTo );
	}

	/**
	 *
	 */
	public void zoomOut( Point2D center, double factor ) {
		try {
			screenTransform.inverseTransform( center, center );
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		double homeWidth = extentHome.getWidth();
		double homeHeight = extentHome.getHeight();
		double oldWidth = extent.getWidth();
		double oldHeight = extent.getHeight();
		if ( ( oldWidth < homeWidth ) || ( oldHeight < homeHeight ) ) {
			double newWidth = oldWidth * factor;
			double newHeight = oldWidth * factor;
			if ( ( newWidth > homeWidth ) || ( newHeight > homeHeight ) ){
				newWidth = homeHeight;
				newHeight = homeHeight;
			}
			double newX = extent.getX() - ( ( newWidth - oldWidth ) / 2 );
			double newY = extent.getY() - ( ( newHeight - oldHeight ) / 2 );
			newX = Math.max( newX, extentHome.getX() );
			newY = Math.max( newY, extentHome.getY() );
			extent.setFrame( newX, newY, newWidth, newHeight );
		} else if ( ( getWidth() > viewport.getWidth() ) ||
				( getHeight() > viewport.getHeight() ) ) {
			int newWidth = ( int ) ( getWidth() / factor );
			int newHeight = ( int ) ( getHeight() / factor );
			newWidth = Math.max( newWidth, viewport.getWidth() );
			newHeight = Math.max( newHeight, viewport.getHeight() );
			Dimension newSize = new Dimension( newWidth, newHeight );
			setSize( newSize );
			setPreferredSize( newSize );
		} else {
			return;
		}
		resized();
		screenTransform.transform( center, center );
		double xShift = viewport.getWidth() / 2;
		double yShift = viewport.getHeight() / 2;
		int xCoor = ( int ) ( center.getX() - xShift );
		int yCoor = ( int ) ( center.getY() - yShift );
		xCoor = Math.max( xCoor, 0 );
		yCoor = Math.max( yCoor, 0 );
		int xMax = this.getWidth() - viewport.getWidth();
		int yMax = this.getHeight() - viewport.getHeight();
		xCoor = Math.min( xCoor, xMax );
		yCoor = Math.min( yCoor, yMax );
		viewport.setViewPosition( new Point( xCoor, yCoor ) );
		revalidate();
	}

	/**
	 * Increase the size of this MapPane so that the mapSpace will fill the
	 * viewerSpace
	 * @param mapSpace seleted region to zoom to
	 * @param viewerSpace current viewport size.
	 */
	public void zoom( Rectangle2D mapSpace, Rectangle2D viewerSpace ) {
		Point2D upperLeft = new Point2D.Double( mapSpace.getMinX(), mapSpace.getMinY() );
		Point2D lowerRight = new Point2D.Double( mapSpace.getMaxX(), mapSpace.getMaxY() );
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
		/*double w = mapSpace.getWidth();
		double h = mapSpace.getHeight();
		double scale = screenTransform.getScaleX();
		//Calculate the new size of the panel
		double oldWidth = extent.getWidth() * scale;
		double oldHeight = extent.getHeight() * scale;
		double ratioMap = oldWidth / oldHeight;
		double viewWidth = viewerSpace.getWidth();
		double viewHeight = viewerSpace.getHeight();
		double maxWidth = 1776;
		double maxHeight = 2268;
		double width = 0;
		double height = 0;
		if ( ( int ) w >= ( int ) h ) {
			width = oldWidth * ( viewWidth / w );
			height = width / ratioMap;
		} else {
			height = oldHeight * ( viewHeight / h );
			width = height * ratioMap;
		}
		Point2D viewLocation = new Point2D.Double( viewerSpace.getX(),
		viewerSpace.getY() );
		Point2D ptHome = new Point2D.Double( ( mapSpace.getX() +
			viewLocation.getX() ), ( mapSpace.getY() +
			viewLocation.getY() ) );
		AffineTransform inverse = null;
		try {
			inverse = screenTransform.createInverse();
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		inverse.transform( ptHome, ptHome );
		if ( ( width > maxWidth ) || ( height > maxHeight ) ){
			width = maxWidth;
			height = maxHeight;
			Point2D ptMax = new Point2D.Double( ( mapSpace.getMaxX() +
				viewLocation.getX() ), ( mapSpace.getMaxY() +
				viewLocation.getY() ) );
			inverse.transform( ptMax, ptMax );
			double xDist = ptMax.getX() - ptHome.getX();
			double yDist = ptHome.getY() - ptMax.getY();
			extent.setRect( ( ptHome.getX() - xDist ),
				( ( ptHome.getY() - yDist ) ), ( xDist * 3 ), ( yDist * 3 ) );
		}
		Dimension d = new Dimension( ( int ) width, ( int ) height );
		setSize( d );
		setPreferredSize( d );
		resized();
		screenTransform.transform( ptHome, ptHome );
		viewport.setViewPosition( new Point( (int) ptHome.getX(),
			( int ) ptHome.getY() ) );
		repaint();
		*/
	}

	/**
	 * Set the bounding box for display
	 * @param r The rectangle which describes the new bounding box for the display.
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

	/**
	 * Called when the ShapePane is resized
	 */
	public void resized() {
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
	 * Add a new layer to the MapPane
	 * @param layer layer to be added to the MapPane
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
	 * Add a new layer to the MapPane
	 * @param layer layer to be added to the MapPane
	 * @param index 
	 */
	public void addTheme( Theme theme, int index ) {
		themes.add( index, theme );
		theme.addThemeChangedListener( this );
	}

	/**
	 * Overridden to prevent flashing
	 * @param g Graphics object to paint on
	 */
	public void update( Graphics g ) {
		paint( g );
	}

	public void updateLayer( Theme l ) {
		refresh( l );
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
			if ( theme.isVisible() ){
				theme.paint( g2D );
			}
		}
	}
	
	public void setBufferDirty( boolean b ) {
		bufferDirty = b;
	}

	/**
	 * Updates screenBuffer.
	 */
	public void updateScreenBuffer() {
		if ( staticBuffer == null ) {
			staticBuffer = createBuffer();
			updateStaticBuffer();
		}
		Graphics2D g2D = ( Graphics2D ) screenBuffer.getGraphics();
		int w = screenBuffer.getWidth( null );
		int h = screenBuffer.getHeight( null );
		g2D.drawImage( staticBuffer, 0, 0, this );
		g2D.transform( screenTransform );
		for ( int i = ( themes.size() - 1 ); i >= 0; i-- ) {
			Theme layer = ( Theme ) themes.get( i );
			if ( layer.isVisible() ){
				layer.paint( g2D );
			}
		}
		g2D.setColor( Color.black );
		g2D.drawRect( 0, 0, w, h );
		bufferDirty = false;
	}

	/**
	 * Notifies MapPane that the selection data has changed and should be
	 * redrawn.
	 * @since 1.0
	 */
	public void selectionChanged() {
		repaint();
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
			Theme layer = ( Theme ) themes.get( i );
			if ( layer.isVisible() ){
				layer.paintSelections( g2D );
			}
		}
	}
	
	public void themeChanged( ThemeChangedEvent event ) {
	}
}