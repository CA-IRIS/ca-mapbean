/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000  Minnesota Department of Transportation
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

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;

/**
 * A LayerRenderer determines how a Layers data is rendered on the map.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.4 $ $Date: 2001/08/09 20:43:43 $ 
 */
public interface LayerRenderer {
	
	/** 
	 * Renders the MapObject on the graphics.
	 * @param object, the MapObject to render.
	 */
    public void render( Graphics2D g, MapObject object );
	
	/**
	 * Gets the shape that would be used to render this object.
	 */
	public Shape getShape( MapObject object );
	
	/**
	 * Get they symbols used by this renderer.
	 */
	public Symbol[] getSymbols();
	
}