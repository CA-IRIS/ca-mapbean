/*
 * MouseDelegator.java
 *
 * Created on June 20, 2000, 2:47 PM
 */

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author  engs1eri
 * @version 
 */
public class MouseDelegator extends MouseAdapter implements
			MouseMotionListener {

	/** holds current mouse behavior */
	private int mouseAction = SELECT;
	
	/**
	 * Mouse behavior constant, no action.
	 */
	public static final int NONE = 0;

	/**
	 * Mouse action constant, select map object.
	 */
	public static final int SELECT = 1;

	/**
	 * Mouse action constant, zoom to selected map region.
	 */
	public static final int ZOOM = 2;

	/**
	 * Mouse action constant, pan map.
	 */
	public static final int PAN = 3;
	
	private boolean box = false;

	private int x1;

	private int y1;

	private int x2;

	private int y2;

	private Rectangle rect = new Rectangle();

	private Point last = new Point();

	private Point scrollTo = new Point();
	
	private us.mn.state.dot.shape.Map map;
	
    /** Creates new MouseDelegator */
    public MouseDelegator( us.mn.state.dot.shape.Map map ) {
		this.map = map;
    }
	
	/**
	 * Sets the action occuring on mouse events
	 * @param m an integer describing mouse action either Map.NONE, Map.SELECT,
	 * Map.PAN, or Map.ZOOM
	 */
	public void setMouseAction( int m ) {
		switch ( m ) {
			case NONE: case ZOOM: case PAN:
			mouseAction = m;
			map.setToolTipText( null );
			break;
			case SELECT:
			mouseAction = m;
			map.setToolTipText( "" );
			break;
			default:
			return;
		}
	}

	/**
	 * Gets the action occuring on mouse events
	 * @return an integer describint the current mouse action
	 */
	public int getMouseAction() {
		return mouseAction;
	}
	
	public void mousePressed( MouseEvent e ) {
		last = e.getPoint();
		x1 = e.getX();
		y1 = e.getY();
		x2 = x1;
		y2 = y1;
		box = false;
	}

	public void mouseReleased( MouseEvent e ) {
		if ( SwingUtilities.isLeftMouseButton( e ) ){
			switch ( mouseAction ){
				//Mouse Select
				case SELECT:
				break;
				//Mouse Zoom
				case ZOOM:
				if ( box ) {
					map.drawBox( rect );
					box = false;
					map.zoom( rect );
				}
				break;
				//Mouse Pan
				case PAN:
				map.finishPan( new Point2D.Double( x1, y1 ), 
					new Point2D.Double( x2, y2 ) );
				break;
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if ( SwingUtilities.isRightMouseButton( e ) ){
			switch ( mouseAction ){
				case SELECT:
					Graphics2D g = ( Graphics2D ) map.getGraphics();
				AffineTransform t = map.getTransform();
				AffineTransform world;
				try {
					world = t.createInverse();
				} catch ( NoninvertibleTransformException ex ) {
					ex.printStackTrace();
					return;
				}
				Point2D p = world.transform( e.getPoint(), null );
				java.util.List themes = map.getThemes();
				g.setTransform( t );
				boolean found = false;
				for ( ListIterator it = themes.listIterator();
						it.hasNext();){
					Theme l = ( Theme ) it.next();
					found = l.mouseClick( e.getClickCount(), p, g );
					if ( found ) {
						break;
					}
				}
				break;
				case ZOOM:
				map.zoomOut( e.getPoint() );
				break;
				case PAN:
				break;
			}
		} else if ( SwingUtilities.isLeftMouseButton( e ) ) {
			switch ( mouseAction ){
				case SELECT:
				Graphics2D g = ( Graphics2D ) map.getGraphics();
				AffineTransform t = map.getTransform();
				AffineTransform world;
				try {
					world = t.createInverse();
				} catch ( NoninvertibleTransformException ex ) {
					ex.printStackTrace();
					return;
				}
				Point2D p = world.transform( e.getPoint(), new Point( 0, 0 ) );
				java.util.List themes = map.getThemes();
				g.setTransform( t );
				boolean found = false;
				for ( ListIterator it = themes.listIterator();
						it.hasNext();){
					Theme l = ( Theme ) it.next();
					found = l.mouseClick( e.getClickCount(), p, g );
					if ( found ) {
						break;
					}
				}
				case ZOOM:
				break;
				case PAN:
				break;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton( e ) ){
			x2 = e.getX();
			y2 = e.getY();
			switch ( mouseAction ){
				case SELECT:
				break;
				case ZOOM:
				if ( box ){
					map.drawBox( rect );
				}
				rect.x = Math.min( x1, x2 );
				rect.width = Math.abs( x2 - x1 );
				rect.y = Math.min( y1, y2 );
				rect.height = Math.abs( y2 - y1 );
				map.drawBox( rect );
				box = true;
				break;
				case PAN:
				map.pan( x2 - last.x, y2 - last.y );
				break;
			}
		}
	}

	public void mouseMoved( MouseEvent e ) {
		if ( box ) {
			box = false;
		}
	}
}