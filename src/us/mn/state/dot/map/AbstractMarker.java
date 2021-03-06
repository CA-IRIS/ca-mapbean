/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2016  Minnesota Department of Transportation
 * Copyright (C) 2016       California Department of Transportation
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

import java.awt.*;
import java.awt.geom.*;

/**
 * An abstract marker which delegates all Shape methods to a general path
 *
 * @author Douglas Lau
 * @author Dan Rossiter
 */
abstract public class AbstractMarker extends Path2D.Float {

	/** Create a new abstract marker */
	protected AbstractMarker(int c) {
		super(Path2D.WIND_EVEN_ODD, c);
	}

	/** Create a new abstract marker based on a shape */
	//FIXME CA-MN-MERGE added to support Arc2D.Float objects (TimeMarker)
	protected AbstractMarker(Shape s) {
		super(s);
	}

}
