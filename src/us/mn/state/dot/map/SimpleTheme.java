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
package us.mn.state.dot.map;

/**
 * A simple theme which uses one symbol to draw all map objects.
 *
 * @author Douglas Lau
 */
public class SimpleTheme extends Theme {

	/** Symbol to render */
	protected final Symbol symbol;

	/** Create a new simple theme */
	public SimpleTheme(Symbol s) {
		super(s.getLabel());
		symbol = s;
		symbols.add(symbol);
	}

	/** Get the symbol to draw a given map object */
	public Symbol getSymbol(MapObject o) {
		return symbol;
	}
}
