
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.*;

public final class LegendCellRenderer implements ListCellRenderer {

	public Component getListCellRendererComponent( JList list, Object value,
			 int index, boolean isSelected, boolean cellHasFocus ) {
		return ( ( LegendItem ) value ).getLegend();
	}

	public Component getRenderedCell( Object value ){
		return getListCellRendererComponent( null, value, 0, false, false );
	}
}