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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Interface for symbols used by themes to draw layer data.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.18 $ $Date: 2001/08/14 22:37:04 $ 
 */
public interface Symbol {
	
	public void draw( Graphics2D g, Shape shape );

	public void setColor( Color color );
	
	public Color getColor();
	
	public boolean isFilled();
	
	public void setFilled( boolean f );

	public void setSize( int size );
	
	public int getSize();
	
	public void setOutLined( boolean outlined );
	
	public boolean isOutLined();
	
	public void setOutLineSymbol( LineSymbol symbol );
	
	public LineSymbol getOutLineSymbol();
	
	public String getLabel();
	
	public void setLabel( String l );

	public Component getLegend();
	
	public Rectangle2D getBounds( MapObject object );
}
