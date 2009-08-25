/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Symol for drawing MapObjects as raster images.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class RasterSymbol implements Symbol {

	/** Symbol label */
	protected final String label;

	/** Image icon raster graphic */
	protected final ImageIcon icon;

	/** Symbol size */
	protected Dimension size;

	/** Create a new raster symbol */
	public RasterSymbol(ImageIcon icon, String label) {
		this(icon, label, null);
	}

	/** Create a new raster symbol */
	public RasterSymbol(ImageIcon icon, String label, Dimension size) {
		this.icon = icon;
		this.label = label;
		this.size = size;
	}

	/** Set the size of the raster symbol */
	public void setSize(Dimension size) {
		this.size = size;
	}

	/** Get the size of the raster symbol */
	public Dimension getSize() {
		return size;
	}

	/** Get the symbol label */
	public String getLabel() {
		return label;
	}

	/** Draw the raster symbol */
	public void draw(Graphics2D g, Shape shp, float scale) {
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		if(size != null) {
			width = (int)size.getWidth();
			height = -(int)size.getHeight();
		}
		AffineTransform t = new AffineTransform();
		t.scale(width, height);
		t.translate(icon.getIconWidth() / -2,
			icon.getIconHeight() / -2);
		g.drawImage(icon.getImage(), t, null);
	}

	/** Get the symbol legend */
	public Icon getLegend() {
		return icon;
	}
}
