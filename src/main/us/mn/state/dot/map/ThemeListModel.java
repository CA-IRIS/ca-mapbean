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

import java.awt.event.*;
import javax.swing.JCheckBox;
import us.mn.state.dot.shape.Theme;
import java.util.*;
import java.awt.Color;

/**
 * ThemeListModel provides a way to display themes as a list of JCheckBoxs
 * that set the visible property.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.2 $ $Date: 2001/04/19 16:49:31 $ 
 */
public final class ThemeListModel extends CheckListModel {

	
	/**
	 * Constructs a new empty ThemeListModel
	 */
	public ThemeListModel() {
	}
	
	/**
	 * Creates a new ThemeListModel and populates it with the layers contained
	 * in the ArrayList themes.
	 * @param themes <CODE>ArrayList</CODE> of <CODE>Themes</CODE> to add to the
	 * ThemeListModel
	 */
	public ThemeListModel( List themes ){
		super();
		Iterator li = themes.iterator();
		while ( li.hasNext() ) {
			addTheme( ( Theme ) li.next() );
		}
	}
	
	/** 
	 * Sets the Themes in the ThemeListModel.
	 * @param themes ArrayList of Themes to add to the ThemeListModel
	 */
	public void setThemes( List themes ) {
		removeAllThemes();
		Iterator li = themes.iterator();
		while ( li.hasNext() ) {
			addTheme( ( Theme ) li.next() );
		}
	}
	
	/**
	 * Overiden from CheckListModel
	 * @param e ActionPerformedEvent
	 */
	public void actionPerformed( ActionEvent e ){
		Object o = e.getSource();
		int index = checks.indexOf( o );
		boolean visible = ( ( JCheckBox ) o ).isSelected();
		( ( Theme ) items.get( index ) ).setVisible( visible );
	}

	/**
	 * Adds a Theme to the ThemeListModel.
	 * @param theme Theme to be added to the ThemeListModel
	 */
	public void addTheme( Theme theme ){
		JCheckBox newBox = new JCheckBox( theme.getName(), theme.isVisible() );
		newBox.setBackground( Color.white );
		newBox.addActionListener( this );
		checks.add( newBox );
		items.add( theme );
		fireIntervalAdded( this, items.lastIndexOf( theme ),
			items.lastIndexOf( theme ) );
	}

	/**
	 * Removes a Theme to the ThemeListModel.
	 * @param theme Theme to be removed from the ThemeListModel
	 */
	public void removeTheme( Theme theme ){
		int index = items.indexOf( theme );
		items.removeElementAt( index );
		checks.removeElementAt( index );
		fireIntervalRemoved( this, index, index );
	}
	
	/**
	 * Removes all Themes from the ThemeListModel.
	 */
	public void removeAllThemes() {
		int last = items.size();
		checks.removeAllElements();
		items.removeAllElements();
		fireIntervalRemoved( this, 0, last );
	}
}