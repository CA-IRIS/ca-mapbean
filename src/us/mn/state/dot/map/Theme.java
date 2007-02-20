/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2007  Minnesota Department of Transportation
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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

/**
 * A theme is a list of symbols for one layer of a map.
 *
 * @author Douglas Lau
 */
abstract public class Theme {

	/** Name of theme */
	protected final String name;

	/** Shape to draw with symbols */
	protected final Shape shape;

	/** List of all symbols to render */
	protected final LinkedList<Symbol> symbols = new LinkedList<Symbol>();

	/** Create a new theme */
	protected Theme(String n, Shape s) {
		name = n;
		shape = s;
	}

	/** Get a string representation of the theme */
	public String toString() {
		return name;
	}

	/** Get a list of all symbols */
	public List<Symbol> getSymbols() {
		return symbols;
	}

	/** Get the symbol to draw a given map object */
	abstract public Symbol getSymbol(MapObject o);

	/** Draw the specified map object */
	public void draw(Graphics2D g, MapObject o) {
		g.transform(o.getTransform());
		getSymbol(o).draw(g, shape);
	}

	/** Search a layer for a map object containing the given point */
	public MapObject search(Layer layer, final Point2D p) {
		return layer.forEach(new MapSearcher() {
			public boolean next(MapObject o) {
				AffineTransform t = o.getInverseTransform();
				Point2D ip = t.transform(p, null);
				return shape.contains(ip);
			}
		});
	}

	/** Get tooltip text for the given map object */
	public String getTip(MapObject o) {
		return o.toString();
	}
}
