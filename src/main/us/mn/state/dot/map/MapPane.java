package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;

/**
 * A sublcass of JPanel used to display geographical information.  It must be
 * contained by a <CODE>us.mn.state.dot.shape.Map</CODE> in order to implement panning and
 * zooming.  Use the </CODE>MapPane(us.mn.state.dot.Map)</CODE> constructor to do this.
 * @author Erik Engstrom
 * @version 1.0
 */
public final class MapPane extends JPanel implements LayerListener {
	/** buffer for map */
	private BufferedImage screenBuffer;

	/** buffer for static layers in map */
	private BufferedImage staticBuffer;

	/** List of dynamic layers */
	private final ArrayList layers = new ArrayList();

	/** List of static layers */
	private final ArrayList staticLayers = new ArrayList();

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform screenTransform = new AffineTransform();

	/** Optional viewport */
	private Map viewport;

	/** Bounding box */
	private Rectangle2D extent = new Rectangle2D.Double();
	public Rectangle2D extentHome = new Rectangle2D.Double();

	/** Scale factor */
	private double scale;

	/** X-Axis */
	private double shiftX;

	/** Y-Axis */
	private double shiftY;

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
	public MapPane(Map map) {
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
	 * Returns a List of the layers contained by this MapPane.
	 * @return List of current layers contained by this MapPane
	 */
	public java.util.List getLayers() {
		java.util.List result = new ArrayList( layers );
		result.addAll( staticLayers );
		return result;
	}

	AffineTransform getTransform() {
		return screenTransform;
	}

	/**
	 * Returns the layers contained in the MapPane.
	 * @param name Name of layer to return.
	 * @return Layer or null if not found.
	 */
	public Layer getLayer( String name ) {
		Layer result = findLayer( name, layers );
		if ( result == null ) {
			result = findLayer( name, staticLayers );
		}
		return result;
	}

	private Layer findLayer( String name, java.util.List layerList ) {
		Layer result = null;
		for ( ListIterator li = layerList.listIterator(); li.hasNext(); ){
			Layer temp = ( Layer ) li.next();
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
			extent.setFrame( newX, newY, extent.getWidth(), extent.getHeight() );
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
		   //	bufferDirty = true;
		   //	repaint();
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
		double newX = extent.getX() + ( how.getX() * shiftX );
		if ( newX < extentHome.getX() ) {
			newX = extentHome.getX();
		}
		if ( newX > extentHome.getMaxX() - extent.getWidth() ) {
			newX = extentHome.getMaxX() - extent.getWidth();
		}
		double newY = extent.getY() + ( how.getY() * shiftY );
		if ( newY < extentHome.getMinY() ) {
			newY = extentHome.getMinY();
		}
		double yMax = extentHome.getMaxY() - extent.getHeight();
		if ( newY > yMax ) {
			newY = yMax;
		}
		extent.setFrame( newX, newY, extent.getWidth(), extent.getHeight() );
		resized();
		screenTransform.transform( point, point );
		return new Point( ( int ) point.getX(), ( int ) point.getY() );
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
			if ( newX < extentHome.getX() ) {
				newX = extentHome.getX();
			}
			if ( newY < extentHome.getY() ) {
				newY = extentHome.getY();
			}
			extent.setFrame( newX, newY, newWidth, newHeight );
		} else if ( ( getWidth() > viewport.getWidth() ) ||
				( getHeight() > viewport.getHeight() ) ) {
			int newWidth = ( int ) ( getWidth() / factor );
			int newHeight = ( int ) ( getHeight() / factor );
			if ( newWidth < viewport.getWidth() ) {
				newWidth = viewport.getWidth();
			}
			if ( newHeight < viewport.getHeight() ) {
				newHeight = viewport.getHeight();
			}
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
		if ( xCoor < 0 ) {
			xCoor = 0;
		}
		if ( yCoor < 0 ) {
			yCoor = 0;
		}
		int xMax = this.getWidth() - viewport.getWidth();
		int yMax = this.getHeight() - viewport.getHeight();
		if ( xCoor > xMax ) {
			xCoor = xMax;
		}
		if ( yCoor > yMax ) {
			yCoor = yMax;
		}
		viewport.setViewPosition( new Point( xCoor, yCoor ) );
		revalidate();
	}

	/**
	 * Increase the size of this MapPane so that the mapSpace will fill the
	 * viewerSpace
	 * @param mapSpace seleted region to zoom to
	 * @param viewerSpace current viewport size.
	 */
	public void zoom(Rectangle2D mapSpace,Rectangle2D viewerSpace) {
		double w = mapSpace.getWidth();
		double h = mapSpace.getHeight();
		//Calculate the new size of the panel
		double oldWidth = extent.getWidth() * scale;
		double oldHeight = extent.getHeight() * scale;
		double ratioMap = oldWidth / oldHeight;
		double viewWidth = viewerSpace.getWidth();
		double viewHeight = viewerSpace.getHeight();
		double maxWidth = 1776;//viewWidth * 3;
		double maxHeight = 2268;//viewHeight * 3;
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
	}

	/**
	 * Set the bounding box for display
	 * @param r The rectangle which describes the new bounding box for the display.
	 */
	public void setExtent( Rectangle2D r ) {
		extent.setFrame( r.getMinX(), r.getMinY(), r.getWidth(),
			r.getHeight() );
		extentHome.setFrame( r.getMinX(), r.getMinY(), r.getWidth(),
			r.getHeight() );
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
		if ( h == 0 ) {
			return;
		}
		if ( w == 0 ) {
			return;
		}
		if ( extent == null ) {
			return;
		}
		double scaleX = ( double ) w / extent.getWidth();
		double scaleY = ( double ) h / extent.getHeight();
		double scaleTemp = scaleX;
		shiftX = 0;
		shiftY = ( h - ( extent.getHeight() * scaleTemp ) ) / 2;
		if ( scaleTemp > scaleY ) {
			scaleTemp = scaleY;
			shiftY = 0;
			shiftX = ( w - ( extent.getWidth() * scaleTemp ) ) / 2;
		}
		scale = scaleTemp;
		screenTransform.setToTranslation( - ( extent.getMinX() * scale )
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
	public void addLayer( Layer layer ) {
		if ( layer.isStatic() ) {
			staticLayers.add( layer );
		} else {
			layers.add( layer );
		}
		layer.addLayerListener( this );
		setExtent( layer.getExtent() );
	}

	/**
	 * Overridden to prevent flashing
	 * @param g Graphics object to paint on
	 */
	public void update( Graphics g ) {
		paint( g );
	}

	public void updateLayer( Layer l ) {
		refresh( l );
	}

	/**
	 * Refreshes map data
	 */
	public void refresh( Layer l ) {
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
		g2D.setColor( Color.lightGray );
		g2D.fillRect( 0, 0, w, h );
		g2D.transform( screenTransform );
		for ( int i = ( staticLayers.size() - 1 ); i >= 0; i-- ) {
			Layer layer = ( Layer ) staticLayers.get( i );
			if ( layer.isVisible() ){
				layer.paint( g2D );
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
		for ( int i = ( layers.size() - 1 ); i >= 0; i-- ) {
			Layer layer = ( Layer ) layers.get( i );
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
		for ( int i = ( layers.size() - 1 ); i >= 0; i-- ) {
			Layer layer = ( Layer ) layers.get( i );
			if ( layer.isVisible() ){
				layer.paintSelections( g2D );
			}
		}
	}

	/**
	 * Paint the shapes
	 * @param g Graphics object to paint on
	 */
	/*public void paint( Graphics g ) {
		if ( screenBuffer == null ) {
			screenBuffer = createBuffer();
			updateScreenBuffer();
		} else if ( bufferDirty ) {
			updateScreenBuffer();
		}
		g.drawImage( screenBuffer, 0, 0, this );
		Graphics2D g2D = ( Graphics2D ) g;
		g2D.transform( screenTransform );
		for ( int i = ( layers.size() - 1 ); i >= 0; i-- ) {
			Layer layer = ( Layer ) layers.get( i );
			if ( layer.isVisible() ){
				layer.paintSelections( g2D );
			}
		}

	} */
}
