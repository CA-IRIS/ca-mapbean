/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2007  Minnesota Department of Transportation
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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
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
 * @author Erik Engstrom
 * @author Douglas Lau
 * @see us.mn.state.dot.map.MapPane
 */
public class MapBean extends JComponent implements MapChangedListener {

	/** Minimum size of zoomed in map */
	static protected final int ZOOM_THRESHOLD = 1000;

	/** Cursor for panning the map */
	static protected final Cursor PAN_CURSOR;
	static {
		ImageIcon i = new ImageIcon(MapBean.class.getResource(
                        "/images/pan.png"));
		PAN_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
                        i.getImage(), new Point(6, 6), "Pan");
	}

	/** Home extents */
	protected final Rectangle2D extentHome = new Rectangle2D.Double();

	/** MapPane that will create the map */
	protected final MapPane mapPane;

	/** MapBean reference for PanState inner class */
	protected final MapBean map;

	/** Current panning state */
	protected PanState pan = null;

	/** Current point selector */
	protected PointSelector pselect = null;

	/** Create a new map */
	public MapBean(boolean a) {
		map = this;
		mapPane = new MapPane(a);
		mapPane.addMapChangedListener(this);
		mapPane.setBackground(getBackground());
		setOpaque(true);
		setDoubleBuffered(false);
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
			}
			public void mouseReleased(MouseEvent e) {
				finishPan(e.getPoint());
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

	/** Add a point selector */
	public void addPointSelector(PointSelector ps) {
		pselect = ps;
		setCursor();
	}

	/** Select a point with the mouse pointer */
	protected boolean selectPoint(Point2D p) {
		PointSelector ps = pselect;
		if(ps != null) {
			ps.selectPoint(p);
			pselect = null;
			setCursor();
			return true;
		}
		return false;
	}

	/** Set the mouse cursor */
	protected void setCursor() {
		if(pselect == null)
			setCursor(null);
		else
			setCursor(Cursor.getPredefinedCursor(
				Cursor.CROSSHAIR_CURSOR));
	}

	/** Process a mouse click event */
	protected void doMouseClicked(MouseEvent e) {
		boolean consumed = false;
		Point2D p = transformPoint(e.getPoint());
		if(selectPoint(p))
			return;
		ListIterator<LayerState> it = mapPane.getLayerIterator();
		while(it.hasPrevious()) {
			LayerState s = it.previous();
			if(consumed)
				s.clearSelections();
			else
				consumed = s.doMouseClicked(e, p);
		}
	}

	/** Add a new layer to the map */
	public void addLayer(LayerState s) {
		mapPane.addLayer(s);
	}

	/** Add a List of layers to the map */
	public void addLayers(List<LayerState> layers) {
		for(LayerState s: layers)
			addLayer(s);
	}

	/** Remove a layer from the map */
	public void removeLayer(String name) {
		LayerState s = mapPane.getLayer(name);
		if(s != null)
			removeLayer(s);
	}

	/** Remove a layer from the map */
	protected void removeLayer(LayerState s) {
		mapPane.removeLayer(s);
	}

	/** Get the layer with the matching name from the Map */
	public LayerState getLayer(String name) {
		return mapPane.getLayer(name);
	}

	/** Get a list of the layers contained by this Map */
	public List<LayerState> getLayers() {
		return mapPane.getLayers();
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
		ListIterator<LayerState> it = mapPane.getLayerIterator();
		while(it.hasPrevious()) {
			LayerState t = it.previous();
			String tip = t.getTip(p);
			if(tip != null)
				return tip;
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
		/* NOTE: this trick allows us to call this method from any
		   thread, but doesn't show any painting glitches (in panning)
		   if called from the event dispatch thread. It's better
		   than using invokeAndWait. */
		Runnable echanger = new Runnable() {
			public void run() {
				mapPane.setExtent(x, y, width, height);
				repaint();
			}
		};
		if(SwingUtilities.isEventDispatchThread())
			echanger.run();
		else 
			SwingUtilities.invokeLater(echanger);
	}

	/** Get the extent of the map */
	public Rectangle2D getExtent() {
		return mapPane.getExtent();
	}

	static protected int limit(int min, int val, int max) {
		return Math.min(Math.max(val, min), max);
	}

	/** State of map panning action */
	protected class PanState {
		protected final Point start;
		protected Image buffer;
		protected int xpan, xmin, xmax, ypan, ymin, ymax;
		protected AffineTransform transform;

		protected PanState(Point s) {
			start = s;
		}

		protected boolean isStarted() {
			return buffer != null;
		}

		protected void initialize() {
			setCursor(PAN_CURSOR);
			buffer = mapPane.getImage();
			AffineTransform t = mapPane.getTransform();
			transform = AffineTransform.getScaleInstance(
				t.getScaleX(), t.getScaleY());
			calculateLimits();
		}

		protected void calculateLimits() {
			Rectangle2D e = mapPane.getExtent();
			Rectangle2D te = mapPane.getLayerExtent();
			Point2D b = new Point2D.Double(e.getX() - te.getX(),
				e.getY() - te.getY());
			transform.transform(b, b);
			xmax = (int)b.getX();
			ymin = (int)b.getY();
			b = new Point2D.Double(te.getWidth() - e.getWidth(),
				te.getHeight() - e.getHeight());
			transform.transform(b, b);
			xmin = xmax - (int)b.getX();
			ymax = ymin - (int)b.getY();
			xpan = 0;
			ypan = 0;
		}

		/** Set the X and Y pan values */
		protected void setPan(Point2D end) {
			int x = (int)(end.getX() - start.getX());
			xpan = limit(xmin, x, xmax);
			int y = (int)(end.getY() - start.getY());
			ypan = limit(ymin, y, ymax);
		}

		/** Drag the map pan */
		protected void drag(Point p) {
			if(!isStarted()) {
				pselect = null;
				initialize();
			}
			setPan(p);
			repaint();
		}

		/** Render the panning map */
		protected void renderMap(Graphics2D g) {
			Rectangle bounds = getBounds();
			g.drawImage(buffer, xpan, ypan, map);
			g.setColor(getBackground());
			if(xpan >= 0)
				g.fillRect(0, 0, xpan, bounds.height);
			else { 
				g.fillRect(bounds.width + xpan, 0,
					-xpan, bounds.height);
			}
			if(ypan >= 0)
				g.fillRect(0, 0, bounds.width, ypan);
			else { 
				g.fillRect(0, bounds.height + ypan,
					 bounds.width,-ypan);
			}
		}

		/** Finish panning the map */
		protected void finish(Point2D end) {
			setPan(end);
			Point p = new Point(xpan, ypan);
			try {
				transform.inverseTransform(p, p);
			}
			catch(NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			setCursor();
			Rectangle2D e = mapPane.getExtent();
			setExtent(e.getX() - p.getX(), e.getY() - p.getY(),
				e.getWidth(), e.getHeight());
		}
	}

	/** Start a pan of the map */
	protected void startPan(Point p) {
		pan = new PanState(p);
	}

	/** Pan the map */
	protected void doPan(Point p) {
		if(pan != null)
			pan.drag(p);
	}

	/** Finish panning the map */
	protected void finishPan(Point2D end) {
		if(pan != null) {
			if(pan.isStarted())
				pan.finish(end);
			pan = null;
		}
	}

	/** Zoom in from the current extent */
	protected void zoomIn(Point p) {
		Point2D c = transformPoint(p);
		Rectangle2D e = mapPane.getExtent();
		if(e.getWidth() < ZOOM_THRESHOLD &&
		   e.getHeight() < ZOOM_THRESHOLD)
			return;
		double x = c.getX() - 0.8 * (c.getX() - e.getX());
		double y = c.getY() - 0.8 * (c.getY() - e.getY());
		double w = e.getWidth() * 0.8;
		double h = e.getHeight() * 0.8;
		setExtent(x, y, w, h);
	}

	/** Zoom out from the current extent */
	protected void zoomOut(Point p) {
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
		if(isShowing())
			repaint();
	}

	/** Render the map */
	protected void renderMap(Graphics2D g) {
		Image image = mapPane.getImage();
		if(image == null)
			return;
		g.drawImage(image, 0, 0, this);
		g.transform(mapPane.getTransform());
		if(mapPane.antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		ListIterator<LayerState> li = mapPane.getLayerIterator();
		while(li.hasPrevious()) {
			LayerState s = li.previous();
			s.paintSelections(g);
		}
	}

	/** Paint the map component */
	public void paintComponent(Graphics g) {
		if(pan != null && pan.isStarted())
			pan.renderMap((Graphics2D)g);
		else {
			setCursor(Cursor.getPredefinedCursor(
				Cursor.WAIT_CURSOR));
			renderMap((Graphics2D)g);
			setCursor();
		}
	}

	/** When map changes, MapPane updates all change listeners */
	public void mapChanged() {
		repaint();
	}

	/** Dispose of the map */
	public void dispose() {
		mapPane.dispose();
	}
}
