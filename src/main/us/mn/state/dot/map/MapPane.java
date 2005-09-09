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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.mn.state.dot.map.event.MapChangedListener;
import us.mn.state.dot.map.event.ThemeChangedEvent;
import us.mn.state.dot.map.event.ThemeChangedListener;

/**
 * This class can be used to generate map graphics when access to the graphics
 * subsystem is not available.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class MapPane implements ThemeChangedListener {

	/** Minimum width/height of map pane */
	static protected final int MIN_SIZE = 1;

	/** Buffer for static themes in map */
	protected BufferedImage staticBuffer;

	/** Buffer for map */
	protected BufferedImage screenBuffer;

	/** List of all themes */
	protected final List themes = new ArrayList();

	/** Transform from world to screen coordinates */
	protected final AffineTransform transform = new AffineTransform();

	/** Transform from screen to world coordinates */
	protected AffineTransform inverseTransform = new AffineTransform();

	/** Bounding box */
	protected final Rectangle2D extent = new Rectangle2D.Double();

	/** Does the buffer need to be updated? */
	protected boolean bufferDirty = true;

	/** Does the static buffer need to be updated? */
	protected boolean staticBufferDirty = true;

	/** Background color of map */
	protected Color backgroundColor = Color.GRAY;

	/** Listeners for map changed events */
	protected List listeners = new ArrayList();

	/** Draw map antialiased */
	public final boolean antialiased;

	/** Create a new map pane */
	public MapPane(boolean a) {
		antialiased = a;
		setSize(new Dimension(MIN_SIZE, MIN_SIZE));
	}

	/** Add a MapChangedListener to the MapPane */
	public void addMapChangedListener(MapChangedListener l) {
		listeners.add(l);
	}

	/** Get the list of themes contained in the MapPane */
	public List getThemes() {
		return new ArrayList(themes);
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

	/** Add a new theme to the map */
	public void addTheme(Theme theme) {
		themes.add(theme);
		theme.addThemeChangedListener(this);
		theme.layer.addLayerChangedListener(theme);
	}

	/** Remove a theme from the map */
	public void removeTheme(Theme theme) {
		themes.remove(theme);
		theme.removeThemeChangedListener(this);
		theme.layer.removeLayerChangedListener(theme);
	}

	/** Change the scale of the map panel */
	protected void rescale() {
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
		try { inverseTransform = transform.createInverse(); }
		catch(NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		bufferDirty = true;
		staticBufferDirty = true;
	}

	/** Set the background color of the map */
	public void setBackground(Color color) {
		backgroundColor = color;
	}

	/** Update the static buffer */
	protected void updateStaticBuffer() {
		Graphics2D g = staticBuffer.createGraphics();
		g.setBackground(backgroundColor);
		g.clearRect(0, 0, staticBuffer.getWidth(),
			staticBuffer.getHeight());
		g.transform(transform);
		if(antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Iterator it = themes.iterator();
		while(it.hasNext()) {
			Theme t = (Theme)it.next();
			if(!(t.layer instanceof DynamicLayer))
				t.paint(g);
		}
		g.dispose();
		staticBufferDirty = false;
	}

	/** Update the screen buffer */
	protected void updateScreenBuffer() {
		Graphics2D g = screenBuffer.createGraphics();
		if(staticBufferDirty)
			updateStaticBuffer();
		g.drawImage(staticBuffer, 0, 0, null);
		g.transform(transform);
		if(antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Iterator it = themes.iterator();
		while(it.hasNext()) {
			Theme t = (Theme)it.next();
			if(t.layer instanceof DynamicLayer)
				t.paint(g);
		}
		g.dispose();
		bufferDirty = false;
	}

	/** Get the current image for the map panel */
	public BufferedImage getImage() {
		if(bufferDirty || staticBufferDirty)
			updateScreenBuffer();
		return screenBuffer;
	}

	/** Change a theme on the map panel */
	public void themeChanged(final ThemeChangedEvent event) {
		switch(event.getReason()) {
			case ThemeChangedEvent.DATA:
			case ThemeChangedEvent.SHADE:
				Theme theme = (Theme)event.getSource();
				if(theme.layer instanceof DynamicLayer)
					bufferDirty = true;
				else
					staticBufferDirty = true;
				break;
			default:
				break;
		}
		notifyMapChangedListeners();
	}

	/**
	 * Get the theme with the specified name.
	 * @param name the name of layer to return.
	 * @return Theme or null if not found.
	 */
	public Theme getTheme(String name) {
		Iterator it = themes.iterator();
		while(it.hasNext()) {
			Theme t = (Theme)it.next();
			if(name.equals(t.layer.getName()))
				return t;
		}
		return null;
	}

	/**
	 * Notify all registered MapChangedListeners that the map image has
	 * changed.
	 */
	protected void notifyMapChangedListeners() {
		Iterator it = listeners.iterator();
		while(it.hasNext()) {
			MapChangedListener l = (MapChangedListener)it.next();
			l.mapChanged();
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

	/** Get the extent of the map */
	public Rectangle2D getExtent() {
		return extent;
	}

	/**
	 * Set the bounding box for display
	 * @param x the new x-coordinate for the map.
	 * @param y the new y-coordinate for the map.
	 * @param width the new width for the map.
	 * @param height the new height for the map.
	 */
	public void setExtent(double x, double y, double width, double height) {
		extent.setFrame(x, y, width, height);
		rescale();
	}
}
