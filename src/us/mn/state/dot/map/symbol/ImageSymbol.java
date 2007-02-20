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
package us.mn.state.dot.map.symbol;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Symbol;

/**
 * Symol for painting MapObjects as images.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class ImageSymbol implements Symbol {

	/** Flag indicating if the image should be rotated when drawn 
	 * on the Graphics context */
	protected boolean rotate = true;
	
	protected final String label;

	protected final ImageIcon icon;

	protected Dimension size;

	/** Create a new image symbol */
	public ImageSymbol(ImageIcon icon, String label) {
		this(icon, label, null);
	}

	public ImageSymbol(ImageIcon icon, String label, Dimension size) {
		this.icon = icon;
		this.label = label;
		this.size = size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public String getLabel() {
		return label;
	}

	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}

	public void draw(Graphics2D g, Shape s) {
	}
	
	/**
	 * Draw the ImageSymbol.
	 * If size == null then the image is drawn at full size.
	 */
	public void draw(Graphics2D g, MapObject o) {
		AffineTransform t = (AffineTransform)o.getTransform().clone();
		if(!rotate) {
			t.setToTranslation(t.getTranslateX(),
				t.getTranslateY());
		}
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		if(size != null) {
			width = (int)size.getWidth();
			height = -(int)size.getHeight();
		}
		t.scale(width, height);
		t.translate(icon.getIconWidth() / -2,
			icon.getIconHeight() / -2);
		g.drawImage(icon.getImage(), t, null);
	}

	public Dimension getSize() {
		return size;
	}

	public Icon getLegend() {
		return icon;
	}
}
