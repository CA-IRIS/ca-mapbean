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
package us.mn.state.dot.shape.symbol;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * A PointSymbol that renders a circle.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class CircleMarker extends PointSymbol {

	private final Ellipse2D shape = new Ellipse2D.Double();

	private int radius;

	/** Create a new CircleMarker */
	public CircleMarker() {
		this(Color.BLACK);
	}

	/**
	 * Constructs a CircleMarker with the color set to c.
	 * @param c The color of the Circle Marker.
	 */
	public CircleMarker(Color c) {
		super(c);
	}

	/**
	 * Create a new CircleMarker with the given color and the given legend
	 * lable.
	 * @param c, color to use.
	 * @param label, the label to use in this symbols legend.
	 */
	public CircleMarker(Color c, String label) {
		super(c, label);
	}

	/**
	 * Create a new CircleMarker with the given color, and label.
	 * @param c, color to use.
	 * @param lable, the label to use in this symbols legend.
	 * @param outlined, the symbol is outlined if true.
	 */
	public CircleMarker(Color c, String label, boolean outlined) {
		super(c, label, outlined);
	}

	public void setSize(int size) {
		super.setSize(size);
		radius = this.size / 2;
	}

	protected Shape getShape(double x, double y) {
		shape.setFrame( ( x - radius ), ( y - radius ),
			size, size );
		return shape;
	}
}
