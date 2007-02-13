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
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import us.mn.state.dot.map.event.MapChangedListener;

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

	/** Minimum number of pixels to pan the map */
	static protected final int PAN_THRESHOLD = 5;

	/** Cursor for panning the map */
	static protected final Cursor PAN_CURSOR;
	static {
		ImageIcon i = new ImageIcon(MapBean.class.getResource(
                        "/images/pan.png"));
		PAN_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
                        i.getImage(), new Point(6, 6), "Pan");
	}

	/** Start point of a map pan */
	protected Point start_pan = null;

	/** Buffer used to pan the map */
	protected transient Image panBuffer = null;

	/** Home extents */
	protected final Rectangle2D extentHome = new Rectangle2D.Double();

	/** MapPane that will create the map */
	protected final MapPane mapPane;

	/** Current mouse cursor */
	protected Cursor cursor = null;

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
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				doMouseClicked(e);
			}
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e))
					startPan(e.getPoint());
				else
					cancelPan();
			}
			public void mouseReleased(MouseEvent e) {
				finishPan(e.getPoint());
				cancelPan();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				doPan(e.getPoint());
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() < 0)
					zoomIn(e.getPoint());
				else
					zoomOut(e.getPoint());
			}
		});
	}

	/** Set the background color */
	public void setBackground(Color c) {
		super.setBackground(c);
		mapPane.setBackground(c);
	}

	/** Process a mouse click event */
	protected void doMouseClicked(MouseEvent e) {
		boolean consumed = false;
		Point2D p = transformPoint(e.getPoint());
		ListIterator it = mapPane.getThemeIterator();
		while(it.hasPrevious()) {
			Theme t = (Theme)it.previous();
			if(consumed)
				t.clearSelections();
			else
				consumed = t.doMouseClicked(e, p);
		}
	}

	/** Add a new theme to the map */
	public void addTheme(Theme theme) {
		mapPane.addTheme(theme);
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
				addTheme(l.createTheme());
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
		AffineTransform t = mapPane.getInverseTransform();
		return t.transform(p, null);
	}

	/** Get the tooltip text for the given mouse event */
	public String getToolTipText(MouseEvent e) {
		Point2D p = transformPoint(e.getPoint());
		ListIterator it = mapPane.getThemeIterator();
		while(it.hasPrevious()) {
			Theme t = (Theme)it.previous();
			if(t.isVisible()) {
				String tip = t.getTip(p);
				if(tip != null)
					return tip;
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

	/** Set the extent of the map */
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

	/** Start a pan of the map */
	protected void startPan(Point p) {
		start_pan = p;
		cursor = PAN_CURSOR;
		setCursor(cursor);
	}

	/** Cancel a pan of the map */
	protected void cancelPan() {
		start_pan = null;
		cursor = null;
		setCursor(null);
	}

	/** Pan the map */
	protected void doPan(Point p) {
		if(start_pan == null)
			return;
		int x = (int)(p.getX() - start_pan.getX());
		int y = (int)(p.getY() - start_pan.getY());
		Rectangle bounds = getBounds();
		if(panBuffer == null)
			panBuffer = createImage(bounds.width, bounds.height);
		Graphics pb = panBuffer.getGraphics();
		pb.setColor(getBackground());
		pb.fillRect(0, 0, bounds.width, bounds.height);
		pb.drawImage(mapPane.getImage(), x, y, this);
		pb.dispose();
		Graphics g = getGraphics();
		g.drawImage(panBuffer, 0, 0, this);
		g.dispose();
	}

	/** Finish panning the map */
	protected void finishPan(Point2D end) {
		Point start = start_pan;
		if(start == null)
			return;
		if(Math.abs(start.getX() - end.getX()) < PAN_THRESHOLD &&
		   Math.abs(start.getY() - end.getY()) < PAN_THRESHOLD)
			return;
		AffineTransform t = mapPane.getInverseTransform();
		t.transform(start_pan, start_pan);
		t.transform(end, end);
		double x = start_pan.getX() - end.getX();
		double y = start_pan.getY() - end.getY();
		Rectangle2D e = mapPane.getExtent();
		setExtent(e.getX() + x, e.getY() + y,
			e.getWidth(), e.getHeight());
	}

	/** Zoom in from the current extent */
	protected void zoomIn(Point p) {
		// FIXME: limit zooming in to some maximum value
		Point2D c = transformPoint(p);
		Rectangle2D e = mapPane.getExtent();
		double x = c.getX() - 0.8 * (c.getX() - e.getX());
		double y = c.getY() - 0.8 * (c.getY() - e.getY());
		double w = e.getWidth() * 0.8;
		double h = e.getHeight() * 0.8;
		setExtent(x, y, w, h);
	}

	/** Zoom out from the current extent */
	protected void zoomOut(Point p) {
		// FIXME: do not allow zooming out more than largest extent
		Point2D c = transformPoint(p);
		Rectangle2D e = mapPane.getExtent();
		double x = c.getX() - 1.2 * (c.getX() - e.getX());
		double y = c.getY() - 1.2 * (c.getY() - e.getY());
		double w = e.getWidth() * 1.2;
		double h = e.getHeight() * 1.2;
		setExtent(x, y, w, h);
	}

	/** Zoom to the specified extent */
	public void zoomTo(Rectangle2D e) {
		setExtent(e.getX(), e.getY(), e.getWidth(), e.getHeight());
	}

	/** Called when the map is resized or the extent is changed */
	protected void rescale() {
		mapPane.setSize(getSize());
		panBuffer = null;
		if(isShowing())
			repaint();
	}

	/** Paint the map */
	protected void paint(Graphics2D g) {
		Image image = mapPane.getImage();
		if(image == null)
			return;
		g.drawImage(image, 0, 0, this);
		g.transform(mapPane.getTransform());
		if(mapPane.antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		ListIterator li = mapPane.getThemeIterator();
		while(li.hasPrevious()) {
			Theme t = (Theme)li.previous();
			t.paintSelections(g);
		}
	}

	/** Paint the map component */
	public void paintComponent(Graphics g) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		paint((Graphics2D)g);
		setCursor(cursor);
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
