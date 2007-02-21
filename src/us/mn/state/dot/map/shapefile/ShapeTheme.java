/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007  Minnesota Department of Transportation
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
import java.awt.geom.Point2D;
import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Style;
import us.mn.state.dot.map.Theme;

/**
 * A simple theme which uses one symbol to draw all shape objects.
 *
 * @author Douglas Lau
 */
public class ShapeTheme extends Theme {

	/** Style to draw map objects */
	protected final Style style;

	/** Create a new shape theme */
	public ShapeTheme(String name, Style sty, Shape s) {
		super(name, s);
		style = sty;
		styles.add(style);
	}

	/** Create a new shape theme */
	public ShapeTheme(String name, Style sty) {
		this(name, sty, new Rectangle(0, 0, 200, 200));
	}

	/** Create a new shape theme */
	public ShapeTheme(Style sty) {
		this(sty.getLabel(), sty);
	}

	/** Draw the specified map object */
	public void draw(Graphics2D g, MapObject o) {
		if(o instanceof ShapeObject) {
			ShapeObject so = (ShapeObject)o;
			getStyle(so).draw(g, so.getShape());
		}
	}

	/** Draw a selected map object */
	public void drawSelected(Graphics2D g, MapObject o) {
		if(o instanceof ShapeObject) {
			ShapeObject so = (ShapeObject)o;
			Shape shape = so.getShape();
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
	}

	/** Search a layer for a map object containing the given point */
	public MapObject search(Layer layer, final Point2D p) {
		return layer.forEach(new MapSearcher() {
			public boolean next(MapObject o) {
				return ((ShapeObject)o).getShape().contains(p);
			}
		});
	}

	/** Get the style to draw a given map object */
	public Style getStyle(MapObject o) {
		return style;
	}
}
