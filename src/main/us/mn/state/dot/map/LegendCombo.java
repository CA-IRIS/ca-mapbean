
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import javax.swing.*;

public class LegendCombo extends JComboBox {

	public LegendCombo(ClassBreaksRenderer r) {
		super(r.getSymbols());
		setRenderer(new LegendCellRenderer());
	}

	public void setMapRenderer(ClassBreaksRenderer r){
		this.removeAllItems();
		Symbol[] symbols = r.getSymbols();
		for (int i = 0; i < symbols.length; i++){
			addItem(symbols[i]);
		}
	}
} 