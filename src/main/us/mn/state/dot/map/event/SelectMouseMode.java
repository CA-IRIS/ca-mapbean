/*
 * SelectMode.java
 *
 * Created on June 29, 2000, 3:45 PM
 */

package us.mn.state.dot.shape.event;

import java.awt.event.MouseEvent;

/**
 *
 * @author  engs1eri
 * @version 
 */
public class SelectMouseMode extends MouseModeAdapter {
	
	/**
     * Mouse Mode identifier, which is "Select".
     * This is returned on getID()
     */
    public final static transient String modeID = "Select".intern();

    /**
	 * Creates new SelectMouseMode 
	 * Default constructor.  Sets the ID to the modeID, and the
     * consume mode to true. 
     */
	public SelectMouseMode() {
		this( true );
	}
    
	/**
     * Construct a SelectMouseMode.
     * The constructor that lets you set the consume mode. 
     * @param consumeEvents the consume mode setting.
     */
    public SelectMouseMode( boolean consumeEvents ){
		super( modeID, consumeEvents );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseClicked( MouseEvent e ) {
		mouseSupport.fireMapMouseClicked( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mousePressed( MouseEvent e ) {
		mouseSupport.fireMapMousePressed( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseReleased( MouseEvent e ) {
		mouseSupport.fireMapMouseReleased( e );
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
		mouseSupport.fireMapMouseDragged( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseMoved( MouseEvent e ) {
		mouseSupport.fireMapMouseMoved( e );
    }

}