/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2010  Minnesota Department of Transportation
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * This class can be used to generate map graphics when access to the graphics
 * subsystem is not available.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
class MapPane extends Thread implements LayerChangedListener {

	/** Minimum width/height of map pane */
	static protected final int MIN_SIZE = 1;

	/** Buffer for static layers on map */
	protected BufferedImage staticBuffer;

	/** Buffer for map */
	protected BufferedImage screenBuffer;

	/** Transform from world to screen coordinates */
	protected final AffineTransform transform = new AffineTransform();

	/** Transform from screen to world coordinates */
	protected AffineTransform inverseTransform = new AffineTransform();

	/** Does the buffer need to be updated? */
	protected boolean bufferDirty = true;

	/** Does the static buffer need to be updated? */
	protected boolean staticBufferDirty = true;

	/** Background color of map */
	protected Color background = Color.GRAY;

	/** Map bean */
	protected final MapBean mapbean;

	/** Draw map antialiased */
	public final boolean antialiased;

	/** Create a new map pane */
	public MapPane(MapBean b, boolean a) {
		mapbean = b;
		antialiased = a;
		setSize(new Dimension(MIN_SIZE, MIN_SIZE));
		setDaemon(true);
		start();
	}

	/** Thread running flag */
	protected boolean running = true;

	/** Thread run method */
	public void run() {
		while(running) {
			if(staticBufferDirty) {
				staticBufferDirty = false;
				updateStaticBuffer();
			} else {
				try {
					Thread.sleep(250);
				}
				catch(InterruptedException e) {	}
			}
		}
	}

	/** Set the pixel size of the map panel */
	public void setSize(Dimension d) {
		screenBuffer = createImage(d.width, d.height);
		staticBuffer = createImage(d.width, d.height);
		rescale();
	}

	/** Create a buffered image of the specified size */
	protected BufferedImage createImage(int width, int height) {
		return new BufferedImage(
			Math.max(width, MIN_SIZE),
			Math.max(height, MIN_SIZE),
			BufferedImage.TYPE_INT_RGB);
	}

	/** Get the size of the map */
	public Dimension getSize() {
		return new Dimension(screenBuffer.getWidth(),
			screenBuffer.getHeight());
	}

	/** Dispose of the map pane */
	public void dispose() {
		running = false;
	}

	/** Change the scale of the map panel */
	protected void rescale() {
		Rectangle2D extent = mapbean.getExtent();
		int height = screenBuffer.getHeight();
		int width = screenBuffer.getWidth();
		double mapWidth = Math.max(extent.getWidth(), MIN_SIZE);
		double mapHeight = Math.max(extent.getHeight(), MIN_SIZE);
		double scale = 0;
		double shiftX = 0;
		double shiftY = 0;
		double scaleX = width / mapWidth;
		double scaleY = height / mapHeight;
		if(scaleX > scaleY) {
			scale = scaleY;
			shiftX = (width - (mapWidth * scale)) / 2;
		} else {
			scale = scaleX;
			shiftY = (height - (mapHeight * scale)) / 2;
		}
		transform.setToTranslation(
			-(extent.getMinX() * scale) + shiftX,
			(extent.getMaxY() * scale) + shiftY
		);
		transform.scale(scale, -scale);
		try {
			inverseTransform = transform.createInverse();
		}
		catch(NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		bufferDirty = true;
		staticBufferDirty = true;
	}

	/** Set the background color of the map */
	public void setBackground(Color color) {
		background = color;
	}

	/** Update the static buffer */
	protected void updateStaticBuffer() {
		BufferedImage sbuffer = staticBuffer;
		Graphics2D g = sbuffer.createGraphics();
		g.setBackground(background);
		g.clearRect(0, 0, sbuffer.getWidth(), sbuffer.getHeight());
		g.transform(transform);
		if(antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		for(LayerState s: mapbean.getLayers()) {
			if(staticBufferDirty)
				break;
			if(!(s.layer instanceof DynamicLayer))
				s.paint(g);
		}
		g.dispose();
		if(!staticBufferDirty) {
			bufferDirty = true;
			mapbean.repaint();
		}
	}

	/** Update the screen buffer */
	protected BufferedImage updateScreenBuffer() {
		BufferedImage sbuffer = screenBuffer;
		Graphics2D g = sbuffer.createGraphics();
		if(staticBufferDirty) {
			g.setBackground(background);
			g.clearRect(0, 0, sbuffer.getWidth(),
				sbuffer.getHeight());
		} else
			g.drawImage(staticBuffer, 0, 0, null);
		g.transform(transform);
		if(antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		for(LayerState s: mapbean.getLayers()) {
			if(s.layer instanceof DynamicLayer)
				s.paint(g);
		}
		g.dispose();
		bufferDirty = false;
		return sbuffer;
	}

	/** Get the current image for the map panel */
	public BufferedImage getImage() {
		BufferedImage sbuffer = screenBuffer;
		if(bufferDirty)
			return updateScreenBuffer();
		else
			return sbuffer;
	}

	/** Map model has changed */
	public void layerChanged(LayerChangedEvent ev) {
		Object source = ev.getSource();
		if(source instanceof DynamicLayer)
			bufferDirty = true;
		else
			staticBufferDirty = true;
		switch(ev.getReason()) {
		case model:
		case extent:
			rescale();
		}
	}

	/** Get the transform from world to screen coordinates */
	public AffineTransform getTransform() {
		return transform;
	}

	/** Get the transform from screen to world coordinates */
	public AffineTransform getInverseTransform() {
		return inverseTransform;
	}
}
