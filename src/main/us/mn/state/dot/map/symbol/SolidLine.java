
//Title:        SignClient
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.awt.*;

public class SolidLine extends LineSymbol {

	public SolidLine() {
		this(Color.black);
	}

	public SolidLine(Color c){
		this.setColor(c);
	}

	public BasicStroke getStroke(){
		return new BasicStroke(getSize());
	}

} 