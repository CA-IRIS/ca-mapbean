/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2011  Minnesota Department of Transportation
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
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

/**
 * The MapBean class is a container for a MapPane which allows the pane to be
 * scrolled and zoomed.  It has several convenience methods giving access to
 * the internal MapPane.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 * @see us.mn.state.dot.map.MapPane
 */
public class MapBean extends JComponent implements LayerChangedListener {

	/** Run something on the swing thread */
	static protected void runOnSwingThread(Runnable r) {
		if(SwingUtilities.isEventDispatchThread())
			r.run();
		else 
			SwingUtilities.invokeLater(r);
	}

	/** Cursor for panning the map */
	static protected final Cursor PAN_CURSOR;
	static {
		ImageIcon i = new ImageIcon(MapBean.class.getResource(
                        "/images/pan.png"));
		PAN_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
                        i.getImage(), new Point(6, 6), "Pan");
	}

	/** Listeners for map changed events */
	protected Set<LayerChangedListener> listeners =
		new HashSet<LayerChangedListener>();

	/** Add a LayerChangedListener to the map model */
	public void addLayerChangedListener(LayerChangedListener l) {
		listeners.add(l);
	}

	/** Remove a LayerChangedListener from the map model */
	public void removeLayerChangedListener(LayerChangedListener l) {
		listeners.remove(l);
	}

	/** Notify registered LayerChangedListeners that a layer has changed */
	protected void notifyLayerChangedListeners(LayerChangedEvent event) {
		for(LayerChangedListener l: listeners)
			l.layerChanged(event);
	}

	/** Map model */
	protected MapModel model = new MapModel();

	/** Set the map model */
	public void setModel(MapModel m) {
		model.removeLayerChangedListener(this);
		model = m;
		model.addLayerChangedListener(this);
		layerChanged(new LayerChangedEvent(this, LayerChange.model));
	}

	/** Get the map model */
	public MapModel getModel() {
		return model;
	}

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
		mapPane = new MapPane(this, a);
		mapPane.setBackground(getBackground());
		model.addLayerChangedListener(this);
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
				Point2D p = transformPoint(e.getPoint());
				if(e.getWheelRotation() < 0)
					zoomIn(p);
				else
					zoomOut(p);
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
		ListIterator<LayerState> it = model.getLayerIterator();
		while(it.hasPrevious()) {
			LayerState s = it.previous();
			if(consumed)
				s.clearSelections();
			else
				consumed = s.doMouseClicked(e, p);
		}
	}

	/** Get a list of the layers contained by this Map */
	public List<LayerState> getLayers() {
		return model.getLayers();
	}

	/** Sets extent to home coordinates */
	public void home() {
		model.home();
	}

	/** Transform a point from screen to world coordinates */
	public Point2D transformPoint(Point p) {
		PanState ps = pan;
		if(ps != null)
			p = ps.start;
		AffineTransform t = mapPane.getInverseTransform();
		return t.transform(p, null);
	}

	/** Get the size of a pixel in world coordinates */
	public double getScale() {
		return model.getZoomLevel().scale;
	}

	/** Get the tooltip text for the given mouse event */
	public String getToolTipText(MouseEvent e) {
		Point2D p = transformPoint(e.getPoint());
		ListIterator<LayerState> it = model.getLayerIterator();
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

	/** Set the extent of the map */
	public void setExtent(Rectangle2D r) {
		setExtent(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/** Set the extent of the map */
	protected void setExtent(final double x, final double y,
		final double width, final double height)
	{
		runOnSwingThread(new Runnable() {
			public void run() {
				model.setExtent(x, y, width, height);
			}
		});
	}

	/** Set the center */
	protected void setCenter(double x, double y) {
		final Point2D.Double c = new Point2D.Double(x, y);
		runOnSwingThread(new Runnable() {
			public void run() {
				model.setCenter(c);
			}
		});
	}

	/** State of map panning action */
	protected class PanState {
		protected final Point start;
		protected Image buffer;
		protected AffineTransform transform;
		protected int xpan, ypan;

		protected PanState(Point s) {
			start = s;
		}

		protected boolean isStarted() {
			return buffer != null;
		}

		protected void initialize() {
			setCursor(PAN_CURSOR);
			buffer = mapPane.getBufferedImage();
			AffineTransform t = mapPane.getTransform();
			transform = AffineTransform.getScaleInstance(
				t.getScaleX(), t.getScaleY());
			xpan = 0;
			ypan = 0;
		}

		/** Set the X and Y pan values */
		protected void setPan(Point2D end) {
			xpan = (int)(end.getX() - start.getX());
			ypan = (int)(end.getY() - start.getY());
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
			Point2D center = model.getCenter();
			setCenter(center.getX() - p.getX(),
			          center.getY() - p.getY());
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

	/** Zoom in or out from the current extent. */
	public void zoom(boolean zoomin) {
		Point2D center = model.getCenter();
		if(zoomin)
			zoomIn(center);
		else
			zoomOut(center);
	}

	/** Zoom in on the map.
	 * @param p Point in user coordinates. */
	protected void zoomIn(final Point2D p) {
		runOnSwingThread(new Runnable() {
			public void run() {
				model.zoomIn(p);
			}
		});
	}

	/** Zoom out on the map.
	 * @param p Point in user coordinates. */
	protected void zoomOut(final Point2D p) {
		runOnSwingThread(new Runnable() {
			public void run() {
				model.zoomOut(p);
			}
		});
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
		if(image != null)
			g.drawImage(image, 0, 0, this);
		paintSelections(g);
	}

	/** Paint the current selections */
	protected void paintSelections(Graphics2D g) {
		g.transform(mapPane.getTransform());
		if(mapPane.antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		ListIterator<LayerState> li = model.getLayerIterator();
		while(li.hasPrevious())
			li.previous().paintSelections(g);
	}

	/** Paint the map component */
	public void paintComponent(Graphics g) {
		if(pan != null && pan.isStarted())
			pan.renderMap((Graphics2D)g);
		else
			renderMap((Graphics2D)g);
	}

	/** When map changes, the map model updates all change listeners */
	public void layerChanged(LayerChangedEvent ev) {
		notifyLayerChangedListeners(ev);
		mapPane.layerChanged(ev);
		repaint();
	}

	/** Dispose of the map */
	public void dispose() {
		mapPane.dispose();
		model.dispose();
		listeners.clear();
	}
}
