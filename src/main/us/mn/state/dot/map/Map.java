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
public final class Map extends JViewport {
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

	/**
	 * Map panel
	 */
	private MapPane map = new MapPane( this );

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform at = new AffineTransform();

	/** holds current mouse behavior */
	private int mouseAction = SELECT;

	/** Mouse helper class */
	private final MouseHelper mouse = new MouseHelper( ( JViewport ) this );

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
	public Map(java.util.List themes) {
		addMouseListener( mouse );
		addMouseMotionListener( mouse );
		this.setView( map );
		this.setToolTipText( "" );
		for ( ListIterator li = themes.listIterator(); li.hasNext(); ){
			addTheme( ( Theme ) li.next() );
		}
		setMouseAction( SELECT );
	}

	/**
	 * Sets the action occuring on mouse events
	 * @param m an integer describing mouse action either Map.NONE, Map.SELECT,
	 * Map.PAN, or Map.ZOOM
	 */
	public void setMouseAction(int m) {
		switch ( m ) {
			case NONE: case ZOOM: case PAN:
			mouseAction = m;
			this.setToolTipText( null );
			break;
			case SELECT:
			mouseAction = m;
			this.setToolTipText( "" );
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

	/**
	 * notifies the Map that the selected map objects have changed and the map
	 * should be updated
	 */
	public void selectionChanged() {
		map.selectionChanged();
		repaint();
	}

	/**
	 * gets the theme with of the name
	 * @param name string containing name of theme to be retrieved
	 * @return reurns the theme with the corresponding name; returns null if not
	 * found
	 */
	public Theme getTheme( String name ) {
		return map.getTheme( name );
	}
	
	public java.util.List getThemes() {
		return map.getThemes();
	}

	/**
	 * Sets extent to given coordinates
	 */
	public void home() {
		this.setViewSize( this.getSize() );
		map.setPreferredSize( this.getSize() );
		map.setExtent( map.extentHome );
		map.resized();
		map.setLocation( new Point( 0, 0 ) );
		revalidate();
	}

	/**
	 * Add a new theme to the ShapePane
	 * @param theme theme to be added to the map
	 */
	public void addTheme( Theme theme ) {
		map.addTheme( theme );
	}
	
	/**
	 * Add a new theme to the ShapePane at the specified index
	 * @param theme theme to be added to the map
	 * @param index index theme is to be added to
	 */
	public void addTheme( Theme theme, int index ) {
		map.addTheme( theme, index );
	}
	
	/**
	 * Add a List of themes to the ShapePane
	 * @param themes List of themes to be added to the map
	 */
	public void addThemes( java.util.List themes ) {
		ListIterator li = themes.listIterator();
		while ( li.hasNext() ) {
			map.addTheme( ( Theme ) li.next() );
		}
	}

	/** Draw an XOR box (rubberbanding box) */
	void drawBox(Rectangle r) {
		Graphics g = getGraphics();
		if ( g == null ) {
			return;
		}
		g.setXORMode( Color.white );
		g.drawRect( r.x, r.y, r.width, r.height );
	}

	/** Pan to point on map */
	void panTo(Point p) {
	   map.panTo( p );
	}

	/**
	 * move the viewport so that the point is visible
	 * @param center a Point2D in world coordinates
	 */
	public void scrollToMapPoint(Point2D center) {
		map.scrollToMapPoint( center );
	}

	public String getToolTipText(MouseEvent e) {
		AffineTransform t = map.getTransform();
		AffineTransform world = null;
		try {
			world = t.createInverse();
		} catch ( NoninvertibleTransformException ex ) {
			ex.printStackTrace();
		}
		double xCoord = e.getPoint().getX();
		double yCoord = e.getPoint().getY();
		Point2D viewPosition = this.getViewPosition();
		Point2D p1 = new Point2D.Double( xCoord + viewPosition.getX(),
		yCoord + viewPosition.getY() );
		Point2D p = world.transform( p1, new Point( 0, 0 ) );
		java.util.List themes = map.getThemes();
		String result = null;
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

	public void setExtent(Rectangle2D r) {
		map.setExtent( r );
	}

	/** Inner class to take care of mouse events */
	private final class MouseHelper extends MouseAdapter implements
			MouseMotionListener {
		boolean box = false;

		int x1;

		int y1;

		int x2;

		int y2;

		Rectangle rect = new Rectangle();

		private Point last = new Point();

		private Point scrollTo = new Point();

		private JViewport viewport;

		public MouseHelper( JViewport viewport ) {
			this.viewport = viewport;
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
						drawBox( rect );
						box = false;
						map.zoom( rect, viewport.getViewRect() );
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
					double pointX = e.getPoint().getX();
					double pointY = e.getPoint().getY();
					Point2D viewPosition = viewport.getViewPosition();
					Point2D p1 = new Point2D.Double( pointX +
					viewPosition.getX(), pointY + viewPosition.getY() );
					Point2D p = world.transform( p1, new Point( 0, 0 ) );
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
					/*Point2D center = new Point2D.Double( ( e.getX() +
						getViewPosition().getX() ), ( e.getY() +
						getViewPosition().getY() ) );
					map.zoomOut( center, 1.5 );*/
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
					double pointX = e.getPoint().getX();
					double pointY = e.getPoint().getY();
					Point2D viewPosition = viewport.getViewPosition();
					Point2D p1 = new Point2D.Double( pointX +
					viewPosition.getX(), pointY + viewPosition.getY() );
					Point2D p = world.transform( p1, new Point( 0, 0 ) );
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
						drawBox( rect );
					}
					rect.x = Math.min( x1, x2 );
					rect.width = Math.abs( x2 - x1 );
					rect.y = Math.min( y1, y2 );
					rect.height = Math.abs( y2 - y1 );
					drawBox( rect );
					box = true;
					break;
					case PAN:
					/*Point viewPos = viewport.getViewPosition();
					Point offset = new Point( ( x2 - last.x ),
					( y2 - last.y ) );
					last.x = x2;
					last.y = y2;
					scrollTo.x = viewPos.x - offset.x;
					scrollTo.y = viewPos.y - offset.y;
					panTo( scrollTo );*/
					map.pan( x2 - last.x, y2 - last.y );
					break;
				}
			}
		}

		public void mouseMoved(MouseEvent e) {
			if ( box ) {
				box = false;
			}
		}
	}
}