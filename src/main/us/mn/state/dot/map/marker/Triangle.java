/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004  Minnesota Department of Transportation
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
package us.mn.state.dot.map.marker;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A triangle marker
 *
 * @author Douglas Lau
 */
public class Triangle implements Shape {

	/** Actual path */
	protected final GeneralPath path = new GeneralPath(
		GeneralPath.WIND_NON_ZERO, 4);

	/** Create a new triangle marker */
	public Triangle(int s) {
		float length = s / 2;
		float a = (float)(length * Math.tan(60));
		float c = (float)Math.sqrt((a * a) + (length * length));
		path.moveTo(-length, -a);
		path.lineTo(0, c);
		path.lineTo(length, -a);
		path.closePath();
	}

	public boolean contains(double x, double y) {
		return path.contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return path.contains(x, y, w, h);
	}

	public boolean contains(Point2D p) {
		return path.contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return path.contains(r);
	}

	public Rectangle getBounds() {
		return path.getBounds();
	}

	public Rectangle2D getBounds2D() {
		return path.getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform t) {
		return path.getPathIterator(t);
	}

	public PathIterator getPathIterator(AffineTransform t, double f) {
		return path.getPathIterator(t, f);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return path.intersects(x, y, w, h);
	}

	public boolean intersects(Rectangle2D r) {
		return path.intersects(r);
	}
}
