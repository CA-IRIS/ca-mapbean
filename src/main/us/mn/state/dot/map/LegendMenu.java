/*
 * LegendMenu.java
 *
 * Created on May 19, 2000, 10:38 AM
 */
 
package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.*;

/** 
 *
 * @author  engs1eri
 * @version 
 */
public class LegendMenu extends javax.swing.JMenu {

  	/** Creates new LegendMenu */
 	public LegendMenu( LayerRenderer r ) {
		super( "Legend:" );
		setMapRenderer( r );
		this.setBorder( BorderFactory.createEtchedBorder() );
		this.setBorderPainted( true );
	}
	
	public void setMapRenderer( LayerRenderer r ) {
		this.removeAll();
		Symbol[] symbols = r.getSymbols();
		for ( int i = 0; i < symbols.length; i++ ){
			add( symbols[ i ].getLegend() );
		} 
	}
}