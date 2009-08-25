/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2009  Minnesota Department of Transportation
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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;

/**
 * A vector symbol is draws a Shape with a specific Style.
 *
 * @author Douglas Lau
 */
public class VectorSymbol implements Symbol {

	/** Style to draw symbol */
	public final Style style;

	/** Shape to draw legend */
	protected final Shape lshape;

	/** Create a new vector symbol */
	public VectorSymbol(Style sty, Shape shp) {
		style = sty;
		lshape = shp;
	}

	/** Get the symbol label */
	public String getLabel() {
		return style.getLabel();
	}

	/** Draw the symbol */
	public void draw(Graphics2D g, Shape shp, float scale) {
		if(style.outline != null) {
			g.setColor(style.outline.color);
			g.setStroke(style.outline.getStroke(scale));
			g.draw(shp);
		}
		if(style.fill_color != null) {
			g.setColor(style.fill_color);
			g.fill(shp);
		}
	}

	/** Get the legend icon */
	public Icon getLegend() {
		return new LegendIcon();
	}

	/** Inner class for icon displayed on the legend */
	protected class LegendIcon implements Icon {

		/** Size of icon */
		static public final int SIZE = 24;

		/** Transform to draw the legend */
		protected final AffineTransform transform;

		/** Create a new legend icon */
		protected LegendIcon() {
			Rectangle2D b = lshape.getBounds2D();
			double x = b.getX() + b.getWidth() / 2;
			double y = b.getY() + b.getHeight() / 2;
			double scale = (SIZE - 2) /
				Math.max(b.getWidth(), b.getHeight());
			transform = new AffineTransform();
			transform.translate(SIZE / 2, SIZE / 2);
			transform.scale(scale, -scale);
			transform.translate(-x, -y);
		}	

		/** Paint the icon onto the given component */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D)g;
			AffineTransform t = g2.getTransform();
			g2.transform(transform);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			draw(g2, lshape, 1);
			g2.setTransform(t);
		}

		/** Get the icon width */
		public int getIconWidth() {
			return SIZE;
		}

		/** Get the icon height */
		public int getIconHeight() {
			return SIZE;
		}
	}
}
