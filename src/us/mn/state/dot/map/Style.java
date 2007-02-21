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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.swing.Icon;

/**
 * A style for stroke/fill of map objects.
 *
 * @author Douglas Lau
 * @author Erik Engstrom
 */
public class Style {

	/** Style label */
	protected final String label;

	/** Legend icon */
	protected final Icon legend = new LegendIcon();

	/** Fill color */
	protected final Color fill_color;

	/** Outline style */
	public final Outline outline;

	/** Create a style with given label, outline and fill color */
	public Style(String l, Outline o, Color f) {
		label = l;
		outline = o;
		fill_color = f;
	}

	/** Create a new style with no outline */
	public Style(String l, Color f) {
		this(l, null, f);
	}

	/** Get the style label */
	public String getLabel() {
		return label;
	}

	/** Get the legend icon */
	public Icon getLegend() {
		return legend;
	}

	/** Draw a shape on a graphics context */
	public void draw(Graphics2D g, Shape s) {
		if(fill_color != null) {
			g.setColor(fill_color);
			g.fill(s);
		}
		if(outline != null) {
			g.setColor(outline.color);
			g.setStroke(outline.stroke);
			g.draw(s);
		}
	}

	/** Inner class for icon displayed on the legend */
	protected class LegendIcon implements Icon {

		/** Width of icon */
		static public final int WIDTH = 25;

		/** Height of icon */
		static public final int HEIGHT = 15;

		/** Paint the icon onto the given component */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if(fill_color != null) {
				g.setColor(fill_color);
				g.fillRect(x + 1, y + 1, WIDTH - 2, HEIGHT - 2);
			}
			if(outline != null) {
				g.setColor(outline.color);
				g.drawRect(x, y, WIDTH - 1, HEIGHT - 1);
			}
		}

		/** Get the icon width */
		public int getIconWidth() {
			return WIDTH;
		}

		/** Get the icon height */
		public int getIconHeight() {
			return HEIGHT;
		}
	}
}
