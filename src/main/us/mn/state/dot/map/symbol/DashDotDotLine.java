
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;

public class DashDotDotLine extends LineSymbol {

	public DashDotDotLine() {
		this( Color.black );
	}

	public DashDotDotLine( Color c ){
		super( c );
	}

	protected void createStroke(){
		stroke = new BasicStroke( getSize(), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, new float[]{8, 1, 2, 1, 2, 1}, 0 );
	}

} 