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
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

public final class NavigationBar extends JToolBar {

	private final Cursor selectCursor;
	private final Cursor zoomCursor;
	private final Cursor panCursor;
	private final Map map;

	public NavigationBar( Map m ) {
		super();
		map = m;
		//Create custom cursors
		selectCursor = new Cursor( Cursor.DEFAULT_CURSOR );
		zoomCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			getImage( "images/zoomMod.gif" ).getImage(), new Point( 0, 0 ),
			"Zoom" );
		panCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			getImage( "images/PanMod.gif" ).getImage(),
			new Point( 0, 0 ), "Pan" );
		putClientProperty( "JToolBar.isRollover", Boolean.TRUE );
		ButtonGroup bgToolbar = new ButtonGroup();
		add( getSelectButton( bgToolbar ), null );
		add( getZoomButton( bgToolbar ), null );
		add( getPanButton( bgToolbar ), null );
		addSeparator();
		add( getHomeButton(), null );
	}

	private ImageIcon getImage( String path ){
		URL url = NavigationBar.class.getResource( path );
		ImageIcon img = new ImageIcon( url );
		return img;
	}

	private JToggleButton getSelectButton( ButtonGroup bgToolbar ){
		JToggleButton btnSelect
			= new JToggleButton( "Select", getImage( "images/arrow.gif" ));
		btnSelect.setSelected( true );
		btnSelect.setToolTipText( "Select Map Item" );
		sizeButton( btnSelect );
		btnSelect.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ) {
				map.setMouseAction( us.mn.state.dot.shape.Map.SELECT );
				map.setCursor( selectCursor );
			}
		});
		bgToolbar.add( btnSelect );
		return btnSelect;
	}

	private JToggleButton getZoomButton( ButtonGroup bgToolbar ){
		JToggleButton btnZoom = new JToggleButton( "Zoom",
			getImage( "images/zoom.gif" ));
		btnZoom.setToolTipText( "Zoom Map" );
		sizeButton( btnZoom );
		btnZoom.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ) {
				map.setMouseAction( us.mn.state.dot.shape.Map.ZOOM );
				map.setCursor( zoomCursor );
			}
		});
		bgToolbar.add( btnZoom );
		return btnZoom;
	}

	private JToggleButton getPanButton( ButtonGroup bgToolbar ){
		JToggleButton btnPan = new JToggleButton( "Pan",
			getImage( "images/pan.gif" ));
		btnPan.setToolTipText( "Pan Map" );
		sizeButton( btnPan );
		btnPan.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ) {
				map.setMouseAction( us.mn.state.dot.shape.Map.PAN );
				map.setCursor( panCursor );
			}
		});
		bgToolbar.add( btnPan );
		return btnPan;
	}

	private JButton getHomeButton(){
		JButton btnHome = new JButton( "Home", getImage( "images/globe.gif" ));
		sizeButton( btnHome );
		btnHome.setToolTipText( "Zoom Out to Full Extent" );
		btnHome.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				map.home();
			}
		});
		return btnHome;
	}

	private void sizeButton( AbstractButton b ){
		b.setMargin( new Insets( 1, 1, 1, 1 ) );
		Dimension dimension = new Dimension( 70, 25 );
		b.setPreferredSize( dimension );
		b.setMaximumSize( dimension );
		b.setMinimumSize( dimension );
	}
}
