
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.*;

public final class MapLegend extends JPanel {

	public final LegendCellRenderer cellMaker = new LegendCellRenderer();

	public MapLegend( ClassBreaksRenderer renderer ) {
		setMapRenderer( renderer );
	}

	public void setMapRenderer( ClassBreaksRenderer renderer ){
		this.removeAll();
		for ( int i = 0; i < renderer.getBreakCount(); i++ ){
			add( cellMaker.getRenderedCell( renderer.getSymbol( i ) ) );
		}
		revalidate();
		repaint();
	}
}