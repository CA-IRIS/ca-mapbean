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
import java.util.ListIterator;
import us.mn.state.dot.shape.*;
import javax.swing.event.*;
import javax.help.*;
import us.mn.state.dot.tms.toast.Icons;

public final class MapToolBar extends NavigationBar {

	private final us.mn.state.dot.shape.MapBean map;

	public MapToolBar( us.mn.state.dot.shape.MapBean m, String themeName ) {
		super( m );
		map = m;
		putClientProperty( "JToolBar.isRollover", Boolean.TRUE );
		addSeparator();
		JMenuBar menuBar = new JMenuBar();
		LegendMenu legend = new LegendMenu( map.getTheme(
			themeName ).getCurrentLayerRenderer() );
		menuBar.add( legend );
		menuBar.add( new JToolBar.Separator() );
		menuBar.setBorderPainted( false );
		menuBar.setAlignmentY( .5f );
		menuBar.add( new ThemeMenu( map.getThemes() ) );
		add( getPaintCombo( themeName, legend ), 0 );
		add( menuBar, 1 );
	}

	private JComboBox getPaintCombo( final String theme,
			final LegendMenu legend ){
		final JComboBox paintCombo = new JComboBox();
		paintCombo.setLightWeightPopupEnabled( false );
		ListIterator li = map.getTheme( theme ).getLayerRenderers().listIterator();
		while ( li.hasNext() ) {
			paintCombo.addItem( li.next() );
		}
		paintCombo.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				Theme temp = map.getTheme( theme );
				LayerRenderer renderer = ( LayerRenderer )
					paintCombo.getSelectedItem();
				temp.setCurrentLayerRenderer( renderer );
				legend.setMapRenderer( renderer );
			}
		});
		return paintCombo;
	}
}
