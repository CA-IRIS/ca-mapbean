/*
 * ZoomMode.java
 *
 * Created on June 29, 2000, 3:46 PM
 */

package us.mn.state.dot.shape.event;

import us.mn.state.dot.shape.MapBean;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author  engs1eri
 * @version 
 */
public class ZoomMouseMode extends MouseModeAdapter {
	
	/**
     * Mouse Mode identifier, which is "Zoom".
     * This is returned on getID()
     */
    public final static transient String modeID = "Zoom".intern();

	/** Are we drawing a selectionbox? */
	private boolean selecting = false;
	
	/** The start of a mouse drag*/
	private Point startPoint = null;
	
	/** The end of a mouse drag*/
	//private Point endPoint = null;
	
	/** The last selection rectangle to be drawn */
	private Rectangle selectionArea = new Rectangle();
	
    /**
	 * Creates new ZoomMouseMode 
	 * Default constructor.  Sets the ID to the modeID, and the
     * consume mode to true. 
     */
    public ZoomMouseMode() {
		this( true );
    }

	/**
     * Construct a ZoomMouseMode.
     * The constructor that lets you set the consume mode. 
     * @param consumeEvents the consume mode setting.
     */
    public ZoomMouseMode( boolean consumeEvents ){
		super( modeID, consumeEvents );
		URL url = getClass().getResource( "/images/zoomMod.gif" );
		ImageIcon img = new ImageIcon( url );
		cursor = Toolkit.getDefaultToolkit().createCustomCursor( img.getImage(),
			new Point( 0, 0 ), "Zoom" );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseClicked( MouseEvent e ) {
		if ( SwingUtilities.isRightMouseButton( e ) ){
			MapBean map = ( MapBean ) e.getSource();
			map.zoomOut( e.getPoint() );
		} 
		mouseSupport.fireMapMouseClicked( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mousePressed( MouseEvent e ) {
		startPoint = e.getPoint();
		//endPoint = e.getPoint();
		selecting = false;
		mouseSupport.fireMapMousePressed( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseReleased( MouseEvent e ) {
		if ( selecting ) {
			MapBean map = ( MapBean ) e.getSource();
			drawBox( map, selectionArea );
			selecting = false;
			map.zoom( selectionArea );
		}
		mouseSupport.fireMapMouseReleased( e );
    }
	
	/** Draw an XOR box (rubberbanding box) */
	protected void drawBox( MapBean map, Rectangle r ) {
		Graphics g = map.getGraphics();
		if ( g == null ) {
			return;
		}
		g.setXORMode( Color.white );
		g.drawRect( r.x, r.y, r.width, r.height );
	}
		

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseEntered( MouseEvent e ) {
		mouseSupport.fireMapMouseEntered( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseExited( MouseEvent e ) {
		mouseSupport.fireMapMouseExited( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseDragged( MouseEvent e ) {
		if ( SwingUtilities.isLeftMouseButton( e ) ){
			MapBean map = ( MapBean ) e.getSource();
			Point endPoint = e.getPoint();
			if ( selecting ){
				drawBox( map, selectionArea );
			}
			selectionArea.x = ( int ) Math.min( startPoint.getX(),
				endPoint.getX() );
			selectionArea.width = ( int ) Math.abs( endPoint.getX() -
				startPoint.getX() );
			selectionArea.y = ( int ) Math.min( startPoint.getY(),
				endPoint.getY() );
			selectionArea.height = ( int ) Math.abs( endPoint.getY() -
				startPoint.getY() );
			drawBox( map, selectionArea );
			selecting = true;
		}
		mouseSupport.fireMapMouseDragged( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseMoved( MouseEvent e ) {
		if ( selecting ) {
			selecting = false;
		}
		mouseSupport.fireMapMouseMoved( e );
    }
}