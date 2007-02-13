/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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

import java.util.Iterator;
import java.util.LinkedList;
import us.mn.state.dot.map.AbstractRenderer;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.symbol.PenSymbol;

/**
 * A renderer that renders objects base on a numeric field and a set of
 * class breaks.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
abstract public class ClassBreaksRenderer extends AbstractRenderer {

	/** A field value and symbol together make up a class break */
	class ClassBreak {
		public final double value;
		public final PenSymbol symbol;
		public ClassBreak(double v, PenSymbol s) {
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
	public void addBreak(double v, PenSymbol s) {
		ClassBreak b = new ClassBreak(v, s);
		classBreaks.add(b);
		symbols.add(s);
	}

	/** Get the symbol for the specified map object */
	protected Symbol getSymbol(MapObject o) {
		ShapeObject shapeObject = (ShapeObject)o;
		Number number = (Number)shapeObject.getValue(field);
		if(number == null)
			return null;
		double value = number.doubleValue();
		Iterator it = classBreaks.iterator();
		while(it.hasNext()) {
			ClassBreak b = (ClassBreak)it.next();
			if(value <= b.value)
				return b.symbol;
		}
		return null;
	}
}
