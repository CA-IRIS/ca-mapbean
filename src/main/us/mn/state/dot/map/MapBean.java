/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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
package us.mn.state.dot.map;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import us.mn.state.dot.map.event.MapChangedListener;
import us.mn.state.dot.map.event.MapMouseListener;
import us.mn.state.dot.map.event.MapMouseMode;
import us.mn.state.dot.map.event.SelectMouseMode;

/**
 * The MapBean class is a container for a MapPane which allows the pane to be
 * scrolled and zoomed.  It has several convenience methods giving access to
 * the internal MapPane.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 * @see us.mn.state.dot.map.MapPane
 */
public class MapBean extends JComponent implements MapChangedListener {

	/** Buffer used to pan the map */
	protected transient Image panBuffer = null;

	/** Home extents */
	protected final Rectangle2D extentHome = new Rectangle2D.Double();

	/** current mouse mode */
	protected MapMouseMode mouseMode = null;

	/** MapPane that will create the map */
	protected final MapPane mapPane;

	/** Create a new map */
	public MapBean(boolean a) {
		mapPane = new MapPane(a);
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
		ListIterator it = mapPane.getThemes().listIterator(
			mapPane.getThemes().size());
		while(it.hasPrevious()) {
			Theme t = (Theme)it.previous();
			registerWithMouseListener(t);
		}
	}

	/** Gets the action occuring on mouse events */
	public MapMouseMode getMouseMode() {
		return mouseMode;
	}

	/** Register a theme with the mouse listener */
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

	/** Unregister a theme with the mouse listener */
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

	/** Add a new theme to the map */
	public void addTheme(Theme theme) {
		mapPane.addTheme(theme);
		registerWithMouseListener(theme);
	}

	/**
	 * Add a List of themes to the Map
	 * @param themes List of themes (or layers) to be added to the map
	 */
	public void addThemes(List themes) {
		Iterator it = themes.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			if(o instanceof Layer) {
				Layer l = (Layer)o;
				addTheme(l.getTheme());
			} else if(o instanceof Theme) {
				addTheme((Theme)o);
			} else {
				throw new IllegalArgumentException(
					"Must be Layer or Theme");
			}
		}
	}

	/** Remove a theme from the map */
	public void removeTheme(String name) {
		Theme theme = mapPane.getTheme(name);
		if(theme != null)
			removeTheme(theme);
	}

	/** Remove a theme from the map */
	protected void removeTheme(Theme theme) {
		mapPane.removeTheme(theme);
		unregisterWithMouseListener(theme);
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

	/** Transform a point from screen to world coordinates */
	public Point2D transformPoint(Point p) {
		try {
			AffineTransform t =
				mapPane.getTransform().createInverse();
			return t.transform(p, null);
		}
		catch(NoninvertibleTransformException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Get the tooltip text for the given mouse event */
	public String getToolTipText(MouseEvent e) {
		Point2D p = transformPoint(e.getPoint());
		ListIterator it = mapPane.getThemes().listIterator(
			mapPane.getThemes().size());
		while(it.hasPrevious()) {
			Theme t = (Theme)it.previous();
			if(t.isVisible()) {
				String tip = t.getTip(p);
				if(tip != null) return tip;
			}
		}
		return null;
	}

	/** Create a tooltip for the map */
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

	/** Set the extent of the map to display */
	protected void setExtent(final double x, final double y,
		final double width, final double height)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mapPane.setExtent(x, y, width, height);
				repaint();
			}
		});
	}

	/** Get the extent of the map */
	public Rectangle2D getExtent() {
		return mapPane.getExtent();
	}

	/**
	 * Pan the map.
	 * @param distanceX number of pixels to move in the X coordinate.
	 * @param distanceY number of pixels to move in the Y coordinate.
	 */
	public void pan(int distanceX, int distanceY) {
		Rectangle bounds = getBounds();
		if(panBuffer == null) {
			panBuffer = createImage(bounds.width, bounds.height);
		}
		Graphics pb = panBuffer.getGraphics();
		pb.setColor(getBackground());
		pb.fillRect(0, 0, bounds.width, bounds.height);
		pb.drawImage(mapPane.getImage(), distanceX, distanceY, this);
		pb.dispose();
		Graphics g = getGraphics();
		g.drawImage(panBuffer, 0, 0, this);
		g.dispose();
	}

	/** Finish panning the map */
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

	/** Zoom out from the current extent */
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

	/** Zoom to the specified extent */
	public void zoomTo( Rectangle2D extent ) {
		setExtent(extent.getX(), extent.getY(),
			extent.getWidth(), extent.getHeight());
	}

	/** Called when the map is resized or the extent is changed */
	protected void rescale() {
		mapPane.setSize(this.getSize());
		panBuffer = null;
		if(this.isShowing())
			repaint();
	}

	/**
	 * Get the transform that the map uses to convert from map coordinates
	 * to screen coordinates.
	 */
	public AffineTransform getTransform() {
		return mapPane.getTransform();
	}

	/** Paint the map */
	protected void paint(Graphics2D g) {
		Image image = mapPane.getImage();
		if(image == null) return;
		g.drawImage(image, 0, 0, this);
		g.transform(mapPane.getTransform());
		if(mapPane.antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		List themes = mapPane.getThemes();
		ListIterator li = themes.listIterator(themes.size());
		while(li.hasPrevious()) {
			Theme t = (Theme)li.previous();
			t.paintSelections(g);
		}
	}

	/** Paint the map component */
	public void paintComponent(Graphics g) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		paint((Graphics2D)g);
		setCursor(mouseMode.getCursor());
	}

	/** When map changes, MapPane updates all change listeners */
	public void mapChanged() {
		repaint();
	}

	/** Dispose of the map */
	public void dispose() {
		List list = mapPane.getThemes();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			Theme theme = (Theme)it.next();
			removeTheme(theme);
			theme.dispose();
		}
	}
}
