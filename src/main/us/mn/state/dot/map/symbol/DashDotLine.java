
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;

public class DashDotLine extends LineSymbol {

	public DashDotLine() {
		this( Color.black );
	}

	public DashDotLine( Color c ){
		super( c );
	}

	protected void createStroke(){
		stroke = new BasicStroke( getSize(), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, new float[]{8, 2, 2, 4}, 0 );
	}
}
