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

import us.mn.state.dot.shape.shapefile.ShapeObject;

/**
 * Displays the name of the tunnel.
 *
 * @author <a href="mailto:timothy.a.johnson@dot.state.mn.us">Tim Johnson</a>
 * @version $Revision: 1.1 $ $Date: 2004/05/07 12:20:01 $
 */
public final class TunnelMapTip implements MapTip {

	public String getTip( MapObject object ){
		ShapeObject shapeObject = ( ShapeObject ) object;
		return (String)( shapeObject.getValue( "NAME" ) );
	}
}