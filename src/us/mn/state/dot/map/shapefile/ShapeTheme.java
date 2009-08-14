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
package us.mn.state.dot.map.shapefile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Style;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.StyledTheme;
import us.mn.state.dot.map.VectorSymbol;

/**
 * A simple theme which uses one symbol to draw all shape objects.
 *
 * @author Douglas Lau
 */
public class ShapeTheme extends StyledTheme {

	/** Style to draw map objects */
	protected final Style style;

	/** Create a new shape theme */
	public ShapeTheme(String name, Style sty) {
		super(name, new Rectangle(0, 0, 200, 200));
		addStyle(sty);
		style = sty;
	}

	/** Create a new shape theme */
	public ShapeTheme(Style sty) {
		this(sty.getLabel(), sty);
	}

	/** Draw a selected map object */
	public void drawSelected(Graphics2D g, MapObject mo) {
		Shape shape = mo.getShape();
		Outline outline = Outline.createDashed(Color.WHITE, 20);
		g.setColor(outline.color);
		g.setStroke(outline.stroke);
		g.draw(shape);
		outline = Outline.createSolid(Color.WHITE,
			getThickness(shape));
		Shape ellipse = createEllipse(shape);
		g.setStroke(outline.stroke);
		g.draw(ellipse);
	}

	/** Get the style to draw a given map object */
	public Style getStyle(MapObject mo) {
		return style;
	}
}
