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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

/**
 * A symbol outline
 *
 * @author Douglas Lau
 */
public class Outline {

	/** Cap to render endpoints */
	static protected final int CAP = BasicStroke.CAP_ROUND;

	/** Join to render line joins */
	static protected final int JOIN = BasicStroke.JOIN_MITER;

	/** Color to render the outline */
	public final Color color;

	/** Width of the outline */
	public final float width;

	/** Stroke to render the outline */
	public final Stroke stroke;

	/** Create a new outline */
	protected Outline(Color c, float w, Stroke s) {
		color = c;
		width = w;
		stroke = s;
	}

	/** Create a solid outline */
	static public Outline createSolid(Color c, float w) {
		Stroke s = new BasicStroke(w, CAP, JOIN);
		return new Outline(c, w, s);
	}

	/** Create a dotted outline */
	static public Outline createDotted(Color c, float w) {
		float[] dash = new float[] { w, w * 2 };
		Stroke s = new BasicStroke(w, CAP, JOIN, w, dash, 0);
		return new Outline(c, w, s);
	}

	/** Create a dashed outline */
	static public Outline createDashed(Color c, float w) {
		float[] dash = new float[] { w * 3, w * 2 };
		Stroke s = new BasicStroke(w, CAP, JOIN, w, dash, 0);
		return new Outline(c, w, s);
	}

	/** Create a dash-dot outline */
	static public Outline createDashDotted(Color c, float w) {
		float[] dash = new float[] { w * 3, w * 2, w, w * 2 };
		Stroke s = new BasicStroke(w, CAP, JOIN, w, dash, 0);
		return new Outline(c, w, s);
	}

	/** Create a dash-dot-dot outline */
	static public Outline createDashDotDotted(Color c, float w) {
		float[] dash = new float[] { w * 3, w * 2, w, w * 2, w, w * 2 };
		Stroke s = new BasicStroke(w, CAP, JOIN, w, dash, 0);
		return new Outline(c, w, s);
	}
}
