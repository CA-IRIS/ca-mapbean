/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2004  Minnesota Department of Transportation
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
package us.mn.state.dot.map.shapefile;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.symbol.FillSymbol;

/**
 * A renderer that renders objects base on a numeric field and a set of
 * class breaks.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
abstract public class ClassBreaksRenderer extends ShapeRenderer {

	/** A field value and symbol together make up a class break */
	class ClassBreak {
		public final double value;
		public final FillSymbol symbol;
		public ClassBreak(double v, FillSymbol s) {
			value = v;
			symbol = s;
		}
	}

	/** Field used to define the breaks */
	protected final String field;

	/** List of class breaks for the renderer */
	protected final LinkedList classBreaks = new LinkedList();

	/** Create a new class breaks renderer */
	public ClassBreaksRenderer(String field, String name) {
		super(name);
		this.field = field;
	}

	/** Add a break to this renderer */
	public void addBreak(double v, FillSymbol s) {
		ClassBreak b = new ClassBreak(v, s);
		classBreaks.add(b);
	}

	/** Get components to display the legend */
	public Component[] getLegend() {
		LinkedList legend = new LinkedList();
		Iterator it = classBreaks.iterator();
		while(it.hasNext()) {
			ClassBreak b = (ClassBreak)it.next();
			legend.add(b.symbol.getLegend());
		}
		return (Component [])legend.toArray(new Component[0]);
	}

	/** Determine which class the value falls into */
	protected Symbol getSymbol(MapObject object) {
		ShapeObject shapeObject = (ShapeObject)object;
		Number number = (Number)shapeObject.getValue(field);
		if(number == null) return null;
		double value = number.doubleValue();
		Iterator it = classBreaks.iterator();
		while(it.hasNext()) {
			ClassBreak b = (ClassBreak)it.next();
			if(value <= b.value) return b.symbol;
		}
		return null;
	}

	/** Draw the object */
	public void render(Graphics2D g, MapObject object) {
		Symbol symbol = getSymbol(object);
		if(symbol != null) symbol.draw(g, object.getShape());
	}

	/** Get the shape that would be used to render this object */
	public Shape getShape(MapObject object) {
		Symbol symbol = getSymbol(object);
		if(symbol == null) return null;
		else return symbol.getShape(object);
	}

	/** Get the bounds of the specified map object */
	public Rectangle2D getBounds(MapObject object) {
		Symbol symbol = getSymbol(object);
		if(symbol == null) return null;
		else return symbol.getBounds(object);
	}
}
