
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

public abstract class NumericField extends Field {

	public NumericField(int type, String name, int offset, int length){
		super(type, name, offset, length);
	}

	public abstract int getRenderingClass(int index, double[] classBreaks );

} 