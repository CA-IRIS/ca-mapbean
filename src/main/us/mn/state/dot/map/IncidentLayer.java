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

//Title:        IncidentLayer
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Displays incidents as icons on map.

package us.mn.state.dot.shape;

import us.mn.state.dot.dds.client.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.ListSelectionModel;
import us.mn.state.dot.tms.toast.TMSProxy;
import us.mn.state.dot.tms.*;
import us.mn.state.dot.shape.event.*;

public final class IncidentLayer extends AbstractLayer implements
		IncidentListener {

	private Incident [] incidents = null;
	private ListSelectionModel selectionModel = null;
	private boolean directional = true;

	/* the number of map units per mile */
	private static final int MAP_UNITS_PER_MILE = 3218;
	
	private static final int[] RING_DEFAULTS = { 5, 10, 15 };
	
	private final TMSProxy proxy;

	public IncidentLayer(){
		this( null, null );
	}

	public IncidentLayer( TMSProxy tms ) {
		this( null, tms );
	}

	public IncidentLayer( ListSelectionModel m, TMSProxy tms ) {
		setStatic( false );
		proxy = tms;
		setName( "incidents" );
		selectionModel = m;
	}

	public void setSelectionModel( ListSelectionModel selectionModel ){
		this.selectionModel = selectionModel;
	}

	public void update( Incident[] incidents ){
		this.incidents = incidents;
		if ( incidents.length > 0 ){
			double maxX = incidents[ 0 ].getX();
			double maxY = incidents[ 0 ].getY();
			double minX = maxX;
			double minY = maxY;
			for ( int i = 1; i < incidents.length; i++ ) {
				if ( incidents[ i ].getX() < minX ) {
					minX = incidents[ i ].getX();
				} else if ( incidents[ i ].getX() > maxX ) {
					maxX = incidents[ i ].getX();
				}
				if ( incidents[ i ].getY() < minY ) {
					minY = incidents[ i ].getY();
				} else if ( incidents[ i ].getY() > maxY ) {
					maxY = incidents[ i ].getY();
				}
			}
			extent = new Rectangle2D.Double( minX, minY, ( maxX - minX )
				, ( maxY - minY ) );
			updateLayer();
		}
	}

	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			ArrayList selection ) {
	}
	
	public void paint( Graphics2D g, LayerRenderer renderer ){
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ){
				if ( directional ) {
					incidents[ i ].paintDirectionalIcon( g );
				} else {
					incidents[ i ].paintIncidentIcon( g );
				}
			}
		}
	}

	private boolean mouseClick( Point2D p, Graphics2D g ) {
		boolean result;
		java.util.List found = getPaths( p );
		if ( found.isEmpty() ) {
			result = false;
		} else {
			result = true;
			ListIterator it = found.listIterator();
			double xCoord = 0;
			double yCoord = 0;
			int index = -1;
			while ( it.hasNext() ){
				index = it.nextIndex();
				Incident incident = ( Incident ) it.next();
				xCoord = incident.getX();
				yCoord = incident.getY();
				break;
			}
			if ( selectionModel != null ) {
				selectionModel.clearSelection();
				selectionModel.addSelectionInterval( index, index );
			}
			g.setColor( Color.red );
			g.setXORMode( Color.white );
			int[] diameters = new int[ 3 ];
			if ( proxy == null ){
				for ( int i = 0; i < 3; i++ ){
					diameters[ i ] = RING_DEFAULTS[ i ] * MAP_UNITS_PER_MILE;
				}
			} else {
				DMSList dmsList = ( DMSList ) proxy.getDMSList().getList();
				for ( int i = 0; i < 3; i++ ){
					try {
						diameters[ i ] = dmsList.getRingRadius( i ) *
							MAP_UNITS_PER_MILE;
					} catch ( java.rmi.RemoteException ex ){
						result = false;
					}
				}
			}
			for ( int i = 0; i < 3; i++ ){
				g.draw( new Ellipse2D.Double( ( xCoord - ( diameters[ i ] /
					2 ) ), ( yCoord - ( diameters[ i ] / 2 ) ),
					diameters[ i ], diameters[ i ] ) );
			}	
		}
		return result;
	}

	public java.util.List getPaths( Point2D p ){
		ArrayList result = new ArrayList();
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( p ) ) {
					result.add( incidents[ i ] );
				}
			}
		}
		return result;
	}
	
	public final int search( Rectangle2D searchArea ) {
		int result = -1;
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( searchArea ) || r.intersects( searchArea ) ||
						searchArea.contains( r ) ) {
					result = i;
					break;
				}
			}
		}
		return result;
	}

	public void setDirectional( boolean b ) {
		directional = b;
	}
	
	public Theme getTheme() {
		Theme result = new IncidentTheme( this );
		result.setTip( new MapTip() {
			public String getTip( Layer layer, int i ) {
				String result = null;
				if ( i >= 0 && i < incidents.length ) {
					result = incidents[ i ].toString();
				}
				return result;
			}
		});
		return result;
	}
	
	private class IncidentTheme extends Theme implements MapMouseListener {
		
		public IncidentTheme( Layer layer ) {
			super( layer );
		}
		
		public MapMouseListener getMapMouseListener() {
			return this;
		}
		
		public boolean mouseMoved( final java.awt.event.MouseEvent p1 ) {
			return false;
		}
		
		public java.lang.String[] getMouseModeServiceList() {
			String[] result = { "Select" };
			return result;
		}
		
		public boolean listensToMouseMode( String modeName ) {
			boolean result = false;
			if ( modeName.equals( SelectMouseMode.MODE_ID ) ) {
				result = true;
			}
			return result;
		}
		
		public void mouseExited( final java.awt.event.MouseEvent event ) {
		}
		
		public boolean mousePressed( final java.awt.event.MouseEvent event ) {
			return false;
		}
		
		public boolean mouseDragged( final java.awt.event.MouseEvent event ) {
			return false;
		}
		
		public void mouseMoved() {
		}
		
		public void mouseEntered( final java.awt.event.MouseEvent event ) {
		}
		
		public boolean mouseReleased( final java.awt.event.MouseEvent event ) {
			return false;
		}
		
		public boolean mouseClicked( final java.awt.event.MouseEvent event ) {
			MapBean map = ( MapBean ) event.getSource();
			AffineTransform at = null;
			try {
				at = map.getTransform().createInverse();
			} catch ( NoninvertibleTransformException ex ){
				return false;
			}
			Graphics2D g = ( Graphics2D ) map.getGraphics();
			g.setTransform( map.getTransform() );
			Point2D point = null;
			point = at.transform( event.getPoint(), point );
			IncidentLayer layer = ( IncidentLayer ) getLayer();
			return layer.mouseClick( point, g );
		}
	}
}