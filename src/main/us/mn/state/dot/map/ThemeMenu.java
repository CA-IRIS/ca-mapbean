/*
 * ThemeMenu.java
 *
 * Created on May 19, 2000, 1:11 PM
 */
 
package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Insets;
import java.util.*;
import us.mn.state.dot.shape.*;

/** 
 *
 * @author  engs1eri
 * @version 
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