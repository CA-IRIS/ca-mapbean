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
import java.io.*;
import java.util.*;
import javax.swing.*;
import us.mn.state.dot.dds.client.*;
import us.mn.state.dot.shape.event.*;

/**
 * A StationLayer displays detector station data represented as a gpoly.shp file.
 *
 * @author Erik Engstrom
 * @since 1.0
 */
public final class StationLayer extends ShapeLayer implements StationListener {
	/**
	 * Constructs a StationLayer.
	 * @throws IOException Will throw an IOException if the gpoly.dbf or
	 * gpoly.shp files can not be found
	 */
	public StationLayer() throws IOException {
		super( "gpoly/gpoly", "gpoly" );
		this.setStatic( false );
	}

	public StationLayer( StationClient stationClient ) throws IOException{
		this();
		stationClient.addStationListener( this );
	}

	/**
	 * updates the data of this layer
	 * @param volume an array containing the new volume values
	 * @param occupancy an array containing the new occupancy values
	 * @param status an array containing the new status values
	 */
	public final void update( int[] volume, int[] occupancy, int[] status ) {
		IntegerField v = ( IntegerField ) super.getField( "VOLUME" );
		IntegerField o = ( IntegerField ) super.getField( "OCCUPANCY" );
		IntegerField s = ( IntegerField ) super.getField( "STATUS" );
		int [] station = ( ( IntegerField ) super.getField( "STATION2"
			) ).getData();
		for ( int i = ( station.length - 1 ); i >= 0; i-- ){
			if ( station[ i ] > 0 ) {
				if ( station[ i ] - 1 < volume.length ) {
					v.setValue( i, volume[ station[ i ] - 1 ] );
					o.setValue( i, occupancy[ station[ i ] - 1 ] );
					s.setValue( i, status[ station[ i ] - 1 ] );
				}
			}
		}
		notifyLayerChangedListeners( new LayerChangedEvent( this,
			LayerChangedEvent.DATA ) );
	}
	
	public final Theme getTheme() {
		ShapeRenderer renderer = new OccupancyRenderer(
			( NumericField ) getField( "occupancy" ),
			new StationMapTip() );
		Theme result = new StationTheme( this, renderer );
		result.setTip( new StationMapTip() );
		return result;
	}
	
	private final class StationTheme extends Theme implements MapMouseListener {
		
		private final JMenu rightClickMenu = new JMenu();
		private final JMenu statusMenu = new JMenu( "Status" );
		private final JMenuItem idMenuItem = new JMenuItem( "ID" );
		private final JMenuItem volumeMenuItem = new JMenuItem( "Volume" );
		private final JMenuItem occMenuItem = new JMenuItem( "occ" );
		
		public StationTheme( Layer layer, LayerRenderer renderer ){
			super( layer, renderer );
			Symbol symbol = new FillSymbol();
			symbol.getOutLineSymbol().setColor( Color.magenta );
			symbol.getOutLineSymbol().setSize( 50 );
			symbol.setOutLined( true );
			symbol.setFilled( false );
			setSelectionRenderer( new DefaultRenderer( symbol ) );
			rightClickMenu.add( statusMenu );
			statusMenu.add( idMenuItem );
			statusMenu.add( volumeMenuItem );
			statusMenu.add( occMenuItem );
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
			boolean result = false;
			if ( SwingUtilities.isRightMouseButton( event ) ) {
				int index = getIndex( event );
				if ( index != -1 ) {
					result = true;
					idMenuItem.setText( "Station " + layer.getField( "STATION2"
						).getStringValue( index ) + ": " + layer.getField( "NAME"
						).getStringValue( index ) );
					volumeMenuItem.setText( " Volume = " + layer.getField( "VOLUME"
						).getStringValue( index ) );
					occMenuItem.setText( " Occupancy = " +
						layer.getField( "OCCUPANCY" ).getStringValue( index ) );
					JPopupMenu menu = rightClickMenu.getPopupMenu();
					menu.show( event.getComponent(), event.getX(),
						event.getY() );
				}
			}
			return result;
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
		
		private int getIndex( final java.awt.event.MouseEvent event ) {
			MapBean map = ( MapBean ) event.getSource();
			AffineTransform at = null;
			try {
				at = map.getTransform().createInverse();
			} catch ( NoninvertibleTransformException ex ){
				return -1;
			}
			Graphics2D g = ( Graphics2D ) map.getGraphics();
			g.setTransform( map.getTransform() );
			Point2D point = null;
			point = at.transform( event.getPoint(), point );
			int index = layer.search( new Rectangle2D.Double( point.getX(),
				point.getY(), 0, 0 ) );
			return index;
		}
		
		public boolean mouseClicked( final java.awt.event.MouseEvent event ) {
			int index = getIndex( event );
			if ( index == -1 ) {
				return false;
			} else {
				int[] selections = { index };
				setSelections( selections );
				notifyThemeChangedListeners( new ThemeChangedEvent( this,
					ThemeChangedEvent.SELECTION ) );
				return true;
			}
		}
	}
}