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

import javax.swing.*;
import java.awt.event.*;
import java.awt.Insets;
import java.util.*;
import us.mn.state.dot.shape.*;

/** 
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.2 $ $Date: 2001/04/19 16:49:31 $ 
 */
public class ThemeMenu extends JMenu {

  	/** Creates new ThemeMenu */
  	public ThemeMenu( List themes ) {
		super( "Themes:" );
		this.setMargin( new Insets( 0, 0, 0, 0 ) );
		this.setBorder( BorderFactory.createEtchedBorder() );
		Iterator it = themes.iterator();
		while ( it.hasNext() ) {
			add( new ThemeMenuItem( ( Theme ) it.next() ) );
		}
  	}
	
	class ThemeMenuItem extends JCheckBox {
		
		private final Theme theme;
		
		public ThemeMenuItem( Theme t ) {
			super( t.getName() );
			theme = t;
			this.setSelected( theme.isVisible() );
			this.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ) {
					theme.setVisible( ! theme.isVisible() );
				}
			});
		}
	}
  
}