
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

public final class IncidentLayer extends AbstractLayer implements
		IncidentListener {

	private Incident [] incidents = null;
	private ListSelectionModel selectionModel = null;

	/* the number of map units per mile */
	private static final int MAP_UNITS_PER_MILE = 3218;
	private final TMSProxy proxy;

	public IncidentLayer(){
		this( null, null );
	}

	public IncidentLayer( TMSProxy tms ) {
		this( null, tms );
	}

	public IncidentLayer( ListSelectionModel m, TMSProxy tms ) {
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

	public void paint( Graphics2D g ){
		if ( isVisible() ) {
			if ( incidents != null ) {
				for ( int i = 0; i < incidents.length; i++ ){
					incidents[ i ].paint( g );
				}
			}
		}
	}

	public boolean mouseClick( int clickCount, Point2D p, Graphics2D g ){
		if ( proxy == null ){
			return false;
		}
		boolean result;
		Vector found = hit( p );
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
			DMSList dmsList = ( DMSList ) proxy.getDMSList().getList();
			for ( int i = 0; i < 3; i++ ){
				int diameter = 0;
				try{
					diameter = dmsList.getRingRadius( i ) * MAP_UNITS_PER_MILE;
				} catch ( java.rmi.RemoteException ex ){
					result = false;
				}
				g.draw( new Ellipse2D.Double( ( xCoord - ( diameter / 2 ) ),
					( yCoord - ( diameter / 2 ) ), diameter, diameter ) );
			}
		}
		return result;
	}

	private Vector hit( Point2D p ){
		Vector result = new Vector();
		if ( incidents != null ) {
			for ( int i = 0; i < incidents.length; i++ ) {
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

	public String getTip( Point2D p ){
		Vector v = hit( p );
		String result = null;
		ListIterator li = v.listIterator();
		if ( li.hasNext() ){
			result = ( ( Incident ) li.next() ).toString();
		}
		return result;
	}
}
