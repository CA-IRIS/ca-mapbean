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
public final class MapPane extends JPanel {
	/** buffer for map */
	private Image screenBuffer;
	
	/** buffer for static layers in map */
	private Image staticBuffer;

	/** List of dynamic layers */
	private final ArrayList layers = new ArrayList();

	/** List of static layers */
	private final ArrayList staticLayers = new ArrayList();

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform at = new AffineTransform();

	/** Optional viewport */
	private Map viewport;

	/** Bounding box */
	private Rectangle2D extent = new Rectangle2D.Double();

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
		return at;
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
	 * Converts a Point2D from world coordinates to screen coordinates.
	 * @param point The point in world coordinates to be converted.
	 * @return A new points whose coordinates are in screen coordinates.
	 */
	public Point2D convertPoint(Point2D point) {
		Point2D result = null;
		result = at.transform( point, result );
		return result;
	}

	/** Increase the size of this MapPane so that the mapSpace will fill the
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
		double width = 0;
		double height = 0;
		if ( ( int ) w >= ( int ) h ) {
			width = oldWidth * ( viewWidth / w );
			height = width / ratioMap;
		} else {
			height = oldHeight * ( viewHeight / h );
			width = height * ratioMap;
		}
		//Cheap way to limit zoom FIX LATER!!!!!!!
		if ( ( width > 5000 ) || ( height > 5000 ) ){
			return;
		}
		//Set the new size of the panel
		Point2D viewLocation = new Point2D.Double( viewerSpace.getX(),
		viewerSpace.getY() );
		Dimension d = new Dimension( ( int ) width, ( int ) height );
		Point pan = null;
		if ( viewport != null ) {
		//Scroll to the center of the zoom rectangle
			double X1 = ( ( mapSpace.getMinX() + viewLocation.getX()
				- shiftX ) * ( width / oldWidth ) );
			double Y1 = ( ( mapSpace.getMinY() + viewLocation.getY()
				- shiftY ) * ( height / oldHeight ) );
			double newBoxWidth = mapSpace.getWidth() * ( width /
				oldWidth );
			double newBoxHeight = mapSpace.getHeight() * ( height /
				oldHeight );
			pan = new Point( ( int ) ( X1 - ( ( viewWidth - newBoxWidth ) /
				2 ) ), ( int ) ( Y1 - ( ( viewHeight - newBoxHeight ) / 2 ) ) );
		}
		setSize( d );
		setPreferredSize( d );
		resized();
		if ( pan != null ) {
			viewport.panTo( pan );
		}
	}

	/**
	 * Set the bounding box for display
	 * @param r The rectangle which describes the new bounding box for the display.
	 */
	public void setExtent( Rectangle2D r ) {
		extent = r;
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
		at.setToTranslation( - ( extent.getMinX() * scale )
			+ shiftX, ( extent.getMaxY() * scale ) + shiftY );
		at.scale( scale, -scale );
		screenBuffer = null;
		staticBuffer = null;
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
		setExtent( layer.getExtent() );
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
	public void refresh() {
		bufferDirty = true;
		repaint();
	}

	private BufferedImage createBuffer() {
		int h = 0;
		int w = 0;
		if ( this.isDisplayable() ) {
			w = this.getHeight();
			h = this.getWidth();
		} else {
			h = 600;
			w = 600;
		}
		return new BufferedImage( h, w, BufferedImage.TYPE_INT_RGB );
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
		g2D.transform( at );
		for ( int i = ( staticLayers.size() - 1 ); i >= 0; i-- ) {
			Layer layer = ( Layer ) staticLayers.get( i );
			if ( layer.isVisible() ){
				layer.paint( g2D );
			}
		}
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
		/*g2D.setColor( Color.lightGray );
		g2D.fillRect( 0, 0, w, h );*/
		g2D.drawImage( staticBuffer, 0, 0, this );
		g2D.transform( at );
		for ( int i = ( layers.size() - 1 ); i >= 0; i-- ) {
		//for ( int i = 0; i < layers.size(); i++ ) {
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

	/**
	 * Paint the shapes
	 * @param g Graphics object to paint on
	 */
	public void paint( Graphics g ) {
		if ( screenBuffer == null ) {
			screenBuffer = createBuffer();
			updateScreenBuffer();
		} else if ( bufferDirty ) {
			updateScreenBuffer();
		}
		g.drawImage( screenBuffer, 0, 0, this );
		Graphics2D g2D = ( Graphics2D ) g;
		g2D.transform( at );
		for ( int i = ( layers.size() - 1 ); i >= 0; i-- ) {
			Layer layer = ( Layer ) layers.get( i );
			if ( layer.isVisible() ){
				layer.paintSelections( g2D );
			}
		}
	}
}
