
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.awt.*;

public class DottedLine extends LineSymbol {

	public DottedLine() {
		this(Color.black);
	}

	public DottedLine(Color c){
		this.setColor(c);
	}

	public BasicStroke getStroke(){
		return new BasicStroke(getSize(), BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 10, new float[]{120, 40}, 0);
	}
} 