/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2004  Minnesota Department of Transportation
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import us.mn.state.dot.shape.event.MapChangedListener;
import us.mn.state.dot.shape.event.MapMouseListener;
import us.mn.state.dot.shape.event.MapMouseMode;
import us.mn.state.dot.shape.event.SelectMouseMode;

/**
 * The Map class is a container for a MapPane which allows the pane to be
 * scrolled and zoomed.  It has several convenience methods giving access to
 * the internal MapPane.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @see us.mn.state.dot.shape.MapPane
 */
public class MapBean extends JComponent implements MapChangedListener{

	/** Buffer used to pan the map */
	private transient Image panBuffer = null;

	/** Home extents */
	protected final Rectangle2D extentHome = new Rectangle2D.Double();

	/** current mouse mode */
	protected MapMouseMode mouseMode = null;

	/** MapPane that will create the map */
	private final MapPane mapPane;

	/** Create a new map */
	public MapBean() {
		this(new ArrayList());
	}

	/**
	 * Constructs a Map containing a MapPane using the themes contained in the
	 * themes parameter.
	 * @param themes a list of themes to be used in the map
	 */
	public MapBean(List themes) {
		mapPane = new MapPane(themes);
		mapPane.addMapChangedListener(this);
		mapPane.setBackground(getBackground());
		setOpaque(true);
		setToolTipText(" ");
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				rescale();
			}
		});
		setMouseMode(new SelectMouseMode());
	}

	/** Set the background color of the Map */
	public void setBackground(Color c) {
		super.setBackground(c);
		mapPane.setBackground(c);
	}

	/** Set the action occuring on mouse events */
	public void setMouseMode(MapMouseMode mode) {
		if(mouseMode != null) {
			removeMouseListener(mouseMode);
			removeMouseMotionListener(mouseMode);
			mouseMode.removeAllMapMouseListeners();
		}
		mouseMode = mode;
		setCursor(mouseMode.getCursor());
		addMouseListener(mode);
		addMouseMotionListener(mode);
		Iterator it = mapPane.getThemes().iterator();
		while(it.hasNext()) {
			Theme t = (Theme)it.next();
			registerWithMouseListener(t);
		}
	}

	/** Gets the action occuring on mouse events */
	public MapMouseMode getMouseMode() {
		return mouseMode;
	}

	/** Add a new theme to the map */
	public void addTheme(Theme theme) {
		mapPane.addTheme(theme);
		registerWithMouseListener(theme);
	}

	/**
	 * Add a List of themes to the Map
	 * @param themes List of themes to be added to the map
	 */
	public void addThemes(List themes) {
		ListIterator li = themes.listIterator();
		while(li.hasNext()) {
			addTheme((Theme)li.next());
		}
	}

	/** Register the theme with mouse listener */
	protected void registerWithMouseListener(Theme theme) {
		if(theme.layer instanceof DynamicLayer) {
			MapMouseMode m = mouseMode;
			MapMouseListener l = theme.getMapMouseListener();
			if(m != null && l != null &&
				l.listensToMouseMode(m.getID()))
			{
				m.addMapMouseListener(l);
			}
		}
	}

	/** Remove a theme from the map */
	public void removeTheme(String name) {
		Theme theme = mapPane.getTheme(name);
		if(theme != null) {
			removeTheme(theme);
		}
	}

	/** Remove a theme from the map */
	public void removeTheme(Theme theme) {
		mapPane.removeTheme(theme);
		unregisterWithMouseListener(theme);
	}

	/** Remove all themes from the map */
	public void removeAllThemes() {
		List list = mapPane.getThemes();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			Theme theme = (Theme)it.next();
			removeTheme(theme);
		}
	}

	/** Unregister the theme with the mouse listener */
	protected void unregisterWithMouseListener(Theme theme) {
		if(theme.layer instanceof DynamicLayer) {
			MapMouseMode m = mouseMode;
			MapMouseListener l = theme.getMapMouseListener();
			if(m != null && l != null &&
				l.listensToMouseMode(m.getID()))
			{
				m.removeMapMouseListener(l);
			}
		}
	}

	public void addLayers(List layers) {
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
	public Theme getTheme(String name) {
		return mapPane.getTheme(name);
	}

	/**
	 * Returns a List of the themes contained by this Map.
	 * @return List of current themes contained by this Map
	 */
	public List getThemes() {
		return mapPane.getThemes();
	}

	/** Sets extent to home coordinates */
	public void home() {
		setHomeExtent(extentHome);
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
		ListIterator it = mapPane.getThemes().listIterator(
			mapPane.getThemes().size());
		while(it.hasPrevious()) {
			Theme t = (Theme)it.previous();
			if(t.isVisible()) {
				result = t.getTip(p);
				if(result != null) break;
			}
		}
		return result;
	}

	public JToolTip createToolTip() {
		return new MapToolTip();
	}

	/** Set the home extents */
	public void setHomeExtent(Rectangle2D r) {
		extentHome.setFrame(r.getMinX(), r.getMinY(), r.getWidth(),
			r.getHeight());
		setExtent(r.getMinX(), r.getMinY(), r.getWidth(),
			r.getHeight());
	}

	public void setExtent(double x, double y, double width, double height) {
		mapPane.setExtent(x, y, width, height);
		repaint();
	}

	public Rectangle2D getExtent() {
		return mapPane.getExtent();
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
		setExtent(extent.getX() + newX, extent.getY() + newY,
			extent.getWidth(), extent.getHeight());
	}

	public void zoomOut( Point center ) {
		// FIXME: SHOULD CENTER THE VIEW AT THE POINT OF CLICK
		Rectangle2D extent = mapPane.getExtent();
		setExtent(extent.getX() - extent.getWidth() / 2,
			extent.getY() - extent.getHeight() / 2,
			extent.getWidth() * 2, extent.getHeight() * 2);
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
		setExtent(x, y, width, height);
	}

	public void zoomTo( Rectangle2D extent ) {
		setExtent(extent.getX(), extent.getY(),
			extent.getWidth(), extent.getHeight());
	}

	/** Called when the map is resized or the extent is changed */
	private synchronized void rescale() {
		mapPane.setSize(this.getSize());
		panBuffer = null;
		if(this.isShowing()) {
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

	public void paintComponent(Graphics g) {
		Image image = mapPane.getImage();
		if(image == null) return;
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(image, 0, 0, this);
		g2d.transform(mapPane.getTransform());
		List themes = mapPane.getThemes();
		ListIterator li = themes.listIterator(themes.size());
		while(li.hasPrevious()) {
			Theme t = (Theme)li.previous();
			t.paintSelections(g2d);
		}
	}

	public void mapChanged() {
		repaint();
	}

	public BufferedImage getImage() {
		return mapPane.getImage();
	}
}
