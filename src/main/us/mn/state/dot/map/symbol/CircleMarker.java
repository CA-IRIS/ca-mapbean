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
package us.mn.state.dot.map.symbol;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * A PointSymbol that renders a circle.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class CircleMarker extends PointSymbol {

	/** Shape to draw for circle markers */
	protected final Ellipse2D shape = new Ellipse2D.Double();

	/** Radius of the circle */
	protected final int radius;

	/** Create a new circle marker */
	public CircleMarker(int size) {
		this(size, Color.BLACK);
	}

	/** Create a new circle marker */
	public CircleMarker(int size, Color c) {
		this(size, c, "");
	}

	/** Create a new circle marker */
	public CircleMarker(int s, Color c, String l) {
		this(s, c, l, null);
	}

	/** Create a new circle marker */
	public CircleMarker(int s, Color c, String l, Color o) {
		super(s, c, l, o);
		radius = size / 2;
	}

	/** Get the shape translated to the given location */
	protected Shape getShape(double x, double y) {
		shape.setFrame((x - radius), (y - radius), size, size);
		return shape;
	}
}
