
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;

public final class MapPane extends JPanel {

	private Image screenBuffer;
	private Image staticBuffer;

	/** List of dynamic layers */
	private final ArrayList layers = new ArrayList();

	public ArrayList getLayers(){
		return layers;
	}

	/** List of static layers */
	private final ArrayList staticLayers = new ArrayList();

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform at = new AffineTransform();

	AffineTransform getTransform(){
		return at;
	}

	/** Optional viewport */
	private Map viewport;

	/** Bounding box */
	private Rectangle2D extent = new Rectangle2D.Double();

	/** Scale factor */
	private double scale;
	private double shiftX;
	private double shiftY;

	public Layer getLayer( String name ) {
		Layer result = null;
		for ( ListIterator li = layers.listIterator(); li.hasNext(); ){
			Layer temp = ( Layer ) li.next();
			if ( name.equals( temp.getName() ) ) {
				result = temp;
				break;
			}
		}
		return result;
	}

	public Point2D convertPoint( Point2D point ) {
		Point2D result = null;
		result = at.transform( point, result );
		return result;
	}

	/** Increases size of this mapPane so that the mapSpace will fill the
		viewerSpace */
	public void zoom( Rectangle2D mapSpace, Rectangle2D viewerSpace ) {
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

	/** Set the bounding box for display */
	public void setExtent( Rectangle2D r ){
		extent = r;
		resized();
		repaint();
	}

	/** Constructor */
	public MapPane(){
		this( null );
	}

	public MapPane( Map vp ) {
		setDoubleBuffered( false );
		viewport = vp;
		addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e ) {
				resized();
				repaint();
			}
		});
	}

	/** Called when the ShapePane is resized */
	public void resized() {
		if ( ! layers.isEmpty() ) {
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
		}
	}

	/** Add a new layer to the ShapePane */
	public void addLayer( Layer layer ) {
		layers.add( layer );
		setExtent( layer.getExtent() );
	}

	/** Overridden to prevent flashing */
	public void update( Graphics g ) {
		paint( g );
	}

	public void refresh(){
		bufferDirty = true;
		repaint();
	}

	private BufferedImage createBuffer(){
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

	public void updateScreenBuffer(){
		Graphics2D g2D = ( Graphics2D ) screenBuffer.getGraphics();
		int w = screenBuffer.getWidth( null );
		int h = screenBuffer.getHeight( null );
		g2D.setColor( Color.lightGray );
		g2D.fillRect( 0, 0, w, h );
		g2D.transform( at );
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

	private boolean bufferDirty = true;

	public void selectionChanged() {
		repaint();
	}

	/** Paint the shapes */
	public void paint ( Graphics g ) {
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
