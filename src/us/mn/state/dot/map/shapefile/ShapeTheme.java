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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.Theme;

/**
 * A simple theme which uses one symbol to draw all shape objects.
 *
 * @author Douglas Lau
 */
public class ShapeTheme extends Theme {

	/** Symbol to render */
	protected final Symbol symbol;

	/** Create a new shape theme */
	public ShapeTheme(String name, Symbol sym, Shape s) {
		super(name, s);
		symbol = sym;
		symbols.add(symbol);
	}

	/** Create a new shape theme */
	public ShapeTheme(String name, Symbol sym) {
		this(name, sym, new Rectangle(0, 0, 200, 200));
	}

	/** Create a new shape theme */
	public ShapeTheme(Symbol sym) {
		this(sym.getLabel(), sym);
	}

	/** Draw the specified map object */
	public void draw(Graphics2D g, MapObject o) {
		getSymbol(o).draw(g, o.getShape());
	}

	/** Search a layer for a map object containing the given point */
	public MapObject search(Layer layer, final Point2D p) {
		return layer.forEach(new MapSearcher() {
			public boolean next(MapObject o) {
				return o.getShape().contains(p);
			}
		});
	}

	/** Get the symbol to draw a given map object */
	public Symbol getSymbol(MapObject o) {
		return symbol;
	}
}
