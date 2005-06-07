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

	/** buffer for map */
	private transient BufferedImage screenBuffer;

	/** buffer for static themes in map */
	private transient BufferedImage staticBuffer;

	/** List of dynamic themes */
	protected final List themes = new ArrayList();

	/** List of static themes */
	protected final List staticThemes = new ArrayList();

	/** Transformation to draw shapes on the map */
	private final AffineTransform screenTransform = new AffineTransform();

	/** Bounding box */
	protected Rectangle2D extent = new Rectangle2D.Double();

	/** does the buffer need to be updated? */
	private boolean bufferDirty = true;

	/** does the static buffer need to be updated? */
	private boolean staticBufferDirty = false;

	/** Background color of map */
	private Color backgroundColor = Color.gray;

	/** height of map image */
	private int height = 0;

	/** width of map image */
	private int width = 0;

	private List listeners = new ArrayList();

	/** Draw map antialiased */
	public final boolean antialiased;

	/** Create a new MapPane without any themes */
	public MapPane(boolean a) {
		antialiased = a;
	}

	/**
	 * Add a MapChangedListener to the listeners of this MapPane.
	 * @param l the listener to add.
	 */
	public void addMapChangedListener( MapChangedListener l ) {
		listeners.add( l );
	}

	/**
	 * Gets the list of themes contained in the MapPane.
	 * @return the list of themes contained by the MapPane
	 */
	public List getThemes() {
		ArrayList result = new ArrayList( staticThemes );
		result.addAll( themes );
		return result;
	}

	/**
	 * Set the size of the map.
	 * @param d the new dimension of the image.
	 */
	public void setSize( Dimension d ) {
		int height = d.height;
		int width = d.width;
		if(height < 1)
			height = 1;
		if(width < 1)
			width = 1;
		screenBuffer = createImage(width, height);
		staticBuffer = createImage(width, height);
		rescale();
	}

	/** Create a buffered image of the specified size */
	protected BufferedImage createImage(int width, int height) {
		return new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);
	}

	/** Get the size of the map */
	public Dimension getSize() {
		if(screenBuffer == null) {
			return null;
		} else {
			return new Dimension(screenBuffer.getWidth(),
				screenBuffer.getHeight());
		}
	}

	/** Add a new theme to the map */
	public void addTheme(Theme theme) {
		if(theme.layer instanceof DynamicLayer) {
			themes.add(theme);
		} else {
			staticThemes.add(theme);
		}
		theme.addThemeChangedListener(this);
		theme.layer.addLayerChangedListener(theme);
	}

	/** Remove a theme from the map */
	public void removeTheme(Theme theme) {
		if(theme.layer instanceof DynamicLayer) {
			themes.remove(theme);
		} else {
			staticThemes.remove(theme);
		}
		theme.removeThemeChangedListener(this);
		theme.layer.removeLayerChangedListener(theme);
	}

	/** Called when the map is resized or the extent is changed */
	protected void rescale() {
		if(screenBuffer == null)
			return;
		height = screenBuffer.getHeight();
		width = screenBuffer.getWidth();
		if(height == 0 || width == 0 || extent == null)
			return;
		double mapWidth = extent.getWidth();
		double mapHeight = extent.getHeight();
		double scale = 0;
		double shiftX = 0;
		double shiftY = 0;
		double scaleX = width / mapWidth;
		double scaleY = height / mapHeight;
		if ( scaleX > scaleY ) {
			scale = scaleY;
			shiftX = ( width - ( mapWidth * scale ) ) / 2;
		} else {
			scale = scaleX;
			shiftY = ( height - ( mapHeight * scale ) ) / 2;
		}
		screenTransform.setToTranslation(
			-(extent.getMinX() * scale) + shiftX,
			(extent.getMaxY() * scale) + shiftY
		);
		screenTransform.scale(scale, -scale);
		bufferDirty = true;
		staticBufferDirty = true;
	}

	/**
	 * Sets the background color of the map.
	 * @param color new color for the backgound.
	 */
	public void setBackground( Color color ) {
		backgroundColor = color;
	}

	/** Update the staticBuffer */
	private void updateStaticBuffer() {
		if(staticBuffer == null) return;
		Graphics2D g = staticBuffer.createGraphics();
		g.setBackground(backgroundColor);
		g.clearRect(0, 0, staticBuffer.getWidth(),
			staticBuffer.getHeight());
		g.transform(screenTransform);
		if(antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Iterator it = staticThemes.iterator();
		while(it.hasNext()) {
			((Theme)it.next()).paint(g);
		}
		g.dispose();
		staticBufferDirty = false;
	}

	/** Update the screen buffer */
	protected void updateScreenBuffer() {
		if(screenBuffer == null) return;
		Graphics2D g = screenBuffer.createGraphics();
		if(staticBufferDirty) updateStaticBuffer();
		g.drawImage(staticBuffer, 0, 0, null);
		g.transform(screenTransform);
		if(antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Iterator it = themes.iterator();
		while(it.hasNext()) {
			((Theme)it.next()).paint(g);
		}
		g.dispose();
		bufferDirty = false;
	}

	public BufferedImage getImage() {
		if ( bufferDirty || staticBufferDirty ) {
			updateScreenBuffer();
		}
		return screenBuffer;
	}

	public void themeChanged(final ThemeChangedEvent event) {
		switch(event.getReason()) {
			case ThemeChangedEvent.DATA:
			case ThemeChangedEvent.SHADE:
				Theme theme = (Theme)event.getSource();
				if(!(theme.layer instanceof DynamicLayer)) {
					staticBufferDirty = true;
				}
				bufferDirty = true;
				break;
			case ThemeChangedEvent.SELECTION:
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
			if(name.equals(t.layer.getName())) return t;
		}
		return null;
	}

	/**
	 * Notify all registered MapChangedListeners that the map image has
	 * changed.
	 */
	private void notifyMapChangedListeners() {
		Iterator it = listeners.iterator();
		while(it.hasNext()) {
			MapChangedListener l = (MapChangedListener)it.next();
			l.mapChanged();
		}
	}

	/** Get the AffineTransform of the map */
	public AffineTransform getTransform() {
		return screenTransform;
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
