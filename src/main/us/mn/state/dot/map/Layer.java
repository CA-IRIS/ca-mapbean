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

//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import java.util.Vector;
import java.awt.geom.*;

public interface Layer {
    public Rectangle2D getExtent();
	public void paint( Graphics2D g );
	public void paintSelections( Graphics2D g );
	public boolean mouseClick( int clickCount, Point2D point, Graphics2D g );
	public String getTip( Point2D point );
	public boolean isVisible();
	public void setVisible( boolean b );
	public String getName();
	public void addLayerListener( LayerListener listener );
	public void updateLayer();
	public void repaintLayer();
	public boolean isStatic();
	public void setStatic( boolean b );
}
