
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

	/** Main Layer (drawn on top, bounds set) */
	private Layer mainLayer;

	/** Array to hold background layer information */
	private final ArrayList layers = new ArrayList();

	public ArrayList getLayers(){
		return layers;
	}

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

	double getShiftX() {
		return shiftX;
	}

	double getShiftY() {
		return shiftY;
	}

	/** Returns width of main layer */
	public double getMapWidth() {
		return extent.getWidth() * scale;
	}

	/** Retruns height of main layer */
	public double getMapHeight() {
		return extent.getHeight() * scale;
	}

	/** Returns layer at index l */
	public Layer getLayer( int l ) {
		return ( Layer ) layers.get( l );
	}

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

	/** Increases size of this mapPane so that the mapSpace will fit in the
		viewerSpace */
	public void zoom( Rectangle2D mapSpace, Rectangle2D viewerSpace ) {
		double w = mapSpace.getWidth();
		double h = mapSpace.getHeight();
		//Calculate the new size of the panel
		double oldWidth = getMapWidth();
		double oldHeight = getMapHeight();
		double ratioMap = oldWidth / oldHeight;
		double viewWidth = viewerSpace.getWidth();
		double viewHeight = viewerSpace.getHeight();
		double width = 0;
		double height = 0;
		if ( w >= h ) {
			width = oldWidth * ( viewWidth / w );
			height = width / ratioMap;
		} else if ( h > w ) {
			height = oldHeight * ( viewHeight / h );
			width = height * ratioMap;
		}
		//Cheap way to limit zoom FIX LATER!!!!!!!
		if ( ( width > 5000 ) || ( height > 5000 ) ){
			return;
		}
		//Set the new size of the panel
		Point viewLocation = new Point(viewerSpace.getX(), viewerSpace.getY());
		setMinimumSize( new Dimension( ( int ) width,
			( int ) height) );
		setPreferredSize( new Dimension( ( int ) width,
			( int ) height ) );
		setSize( ( int ) width, ( int ) height );
		if ( viewport != null ) {
			//Scroll to the center of the zoom rectangle
			double X1 = ( ( mapSpace.getMinX() + viewLocation.getX()
				- shiftX ) * ( getWidth() / oldWidth ) );
			double Y1 = ( ( mapSpace.getMinY() + viewLocation.getY()
				- shiftY ) * ( getHeight() / oldHeight ));
			double newBoxWidth = mapSpace.width * ( getWidth() / oldWidth );
			double newBoxHeight = mapSpace.height * ( getHeight() / oldHeight );
			Point pan = new Point( ( int ) ( X1 - ( ( viewWidth -
				newBoxWidth) / 2 ) ), ( int ) ( Y1 - ( ( viewHeight -
				newBoxHeight ) / 2 ) ) );
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

	public void refreshLayer( int index ){
		ListIterator it = layers.listIterator( index++ );
		repaintLayers( it );
	}

	public void refreshLayer( Layer l ){
		ListIterator it = layers.listIterator( layers.lastIndexOf( l ) + 1 );
		repaintLayers( it );
	}

	private void repaintLayers(ListIterator it){
		bufferDirty = true;
		repaint();
	}

	public void refresh(){
		bufferDirty = true;
		repaint();
	}

	private void createScreenBuffer(){
		int h = 0;
		int w = 0;
		if ( this.isDisplayable() ) {
			w = this.getHeight();
			h = this.getWidth();
		} else {
			h = 600;
			w = 600;
		}
		screenBuffer = new BufferedImage( h, w, BufferedImage.TYPE_INT_RGB );
		bufferDirty = true;
	}

	public void updateScreenBuffer(){
		Graphics2D g2D = ( Graphics2D ) screenBuffer.getGraphics();
		int w = screenBuffer.getWidth( null );
		int h = screenBuffer.getHeight( null );
		/*if( ! this.isDisplayable() ){
			w = 600;
			h = 600;
		} */
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

	public void setBufferDirty( boolean b ) {
		bufferDirty = b;
	}

	public boolean isBufferDirty() {
		return bufferDirty;
	}

	/** Paint the shapes */
	public void paint ( Graphics g ) {

		if ( screenBuffer == null ) {
			createScreenBuffer();
			updateScreenBuffer();
		} else if ( bufferDirty ) {
			updateScreenBuffer();
		}
		g.drawImage( screenBuffer, 0, 0, this );
		//g.drawImage( screenBuffer, 0, 0, this.getWidth(), this.getHeight(),
		//	this );
	}
}
