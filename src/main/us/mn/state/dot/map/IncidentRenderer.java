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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import us.mn.state.dot.dds.client.Incident;
import us.mn.state.dot.shape.IncidentLayer.IncidentWrapper;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.4 $ $Date: 2003/05/19 21:23:41 $
 */
public class IncidentRenderer implements LayerRenderer {
	
	protected final Map symbols = new HashMap();
	
	/** Creates new IncidentRenderer */
    public IncidentRenderer() {
		ImageIcon icon = null;
		URL url = null;
		url = this.getClass().getClassLoader().getResource( 
			"images/accident.gif" );
		icon = new ImageIcon( url );
		symbols.put( "accident", new ImageSymbol( icon, "Accident" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/blocked.gif" );
		icon = new ImageIcon( url );
		symbols.put( "blocked", new ImageSymbol( icon, "Lane Blocked" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/closed.gif" );
		icon = new ImageIcon( url );
		symbols.put( "closed", new ImageSymbol( icon, "Road Closed" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/const.gif" );
		icon = new ImageIcon( url );
		symbols.put( "const", new ImageSymbol( icon, "Road Construction" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/danger.gif" );
		icon = new ImageIcon( url );
		symbols.put( "danger", new ImageSymbol( icon, "Danger" ) );
		url = this.getClass().getClassLoader().getResource( "images/info.gif" );
		icon = new ImageIcon( url );
		symbols.put( "info", new ImageSymbol( icon, "Information" ) );
		url = this.getClass().getClassLoader().getResource( "images/jam.gif" );
		icon = new ImageIcon( url );
		symbols.put( "jam", new ImageSymbol( icon, "Traffic Jam" ) );
		url = this.getClass().getClassLoader().getResource( "images/park.gif" );
		icon = new ImageIcon( url );
		symbols.put( "park", new ImageSymbol( icon, "Parking" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/slippery.gif" );
		icon = new ImageIcon( url );
		symbols.put( "slippery", new ImageSymbol( icon, "Slippery" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/stall.gif" );
		icon = new ImageIcon( url );
		symbols.put( "stall", new ImageSymbol( icon, "Stall" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/two way.gif" );
		icon = new ImageIcon( url );
		symbols.put( "two way", new ImageSymbol( icon, "Two Way" ) );
		url = this.getClass().getClassLoader().getResource(
			"images/weather.gif" );
		icon = new ImageIcon( url );
		symbols.put( "weather", new ImageSymbol( icon, "Bad Weather" ) );
    }

	/**
	 * Get the bounds of the rendered MapObject.
	 */
	public Rectangle2D getBounds( MapObject object ) {
		return getSymbol( object ).getBounds( object );
	}
	
	/**
	 * Gets the shape that would be used to render this object.
	 */
	public Shape getShape( MapObject object ) {
		return getSymbol( object ).getShape( object );
	}
	
	/**
	 * Renders the MapObject on the graphics.
	 * @param object, the MapObject to render.
	 */
	public void render( Graphics2D g, MapObject object ) {
		Symbol symbol = getSymbol( object );
		symbol.draw( g, object.getShape() );
	}
	
	protected Symbol getSymbol( MapObject object ) {
		Incident incident = ( ( IncidentWrapper ) object ).getIncident();
		return ( Symbol ) symbols.get( incident.getDescription().getSign() );
	}
	
	/**
	 * Get they symbols used by this renderer.
	 */
	public Symbol[] getSymbols() {
		Symbol[] result = null;
		return ( Symbol[] ) symbols.values().toArray( result );
	}
	
}
