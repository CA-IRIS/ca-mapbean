
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import javax.swing.*;

public final class LegendCombo extends JComboBox {

	public LegendCombo( ClassBreaksRenderer r ) {
		super();
		setRenderer( new LegendCellRenderer() );
		setMapRenderer( r );
		this.setMaximumRowCount(12);
	}

	public void setMapRenderer( ClassBreaksRenderer r ){
		this.removeAllItems();
		addItem( new LegendItem(){
			public Component getLegend(){
				return new JLabel( "Legend:" );
			}
		});
		Symbol[] symbols = r.getSymbols();
		for ( int i = 0; i < symbols.length; i++ ){
			addItem( symbols[i] );
		}
	}
} 