
//Title:        ClassBreaksRenderer
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Renderers shapes based on the values of a field.

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;
import us.mn.state.dot.shape.DbaseReader.*;

public class ClassBreaksRenderer extends ShapeRenderer {

	private double [] classBreaks;
	private Symbol [] symbols;
	private Field field;
	private String name = null;

	public ClassBreaksRenderer(Field field, int breakCount) {
		this.field = field;
		classBreaks = new double[breakCount];
		symbols = new Symbol[breakCount + 1];
	}

	public ClassBreaksRenderer(Field field, int breakCount, String name){
		this(field, breakCount);
		this.name = name;
	}

	/** overrides Object.toString() */
	public String toString(){
		String result = null;
		if (name == null) {
			result = super.toString();
		} else {
			result = name;
		}
		return result;
	}

	public void setBreak(int index, double value){
		classBreaks[index] = value;
	}

	public void setSymbol(int index, Symbol s){
		symbols[index] = s;
	}

	public void paint(Graphics2D g, GeneralPath path, int index){
		switch(field.getType()){
		case Field.DOUBLE_FIELD:
			double dValue = ((DoubleField)field).getValue(index);
			for (int i = 0; i < classBreaks.length ; i++){
				if (( dValue <= classBreaks[i] ) && ( symbols[i] != null )) {
					symbols[i].draw(g, path);
					break;
				}
			}
			if ((dValue > classBreaks[classBreaks.length - 1])
				&& (symbols[symbols.length - 1] != null)) {
				symbols[symbols.length - 1].draw(g, path);
			}
			break;
		case Field.INTEGER_FIELD:
			int iValue = ((IntegerField) field).getValue(index);
			for (int i = 0; i < classBreaks.length ; i++) {
				if (( iValue <= classBreaks[i] ) && ( symbols[i] != null )) {
					symbols[i].draw(g, path);
					break;
				}
			}
			if ((iValue > classBreaks[classBreaks.length - 1])
					&& (symbols[symbols.length - 1] != null)) {
				symbols[symbols.length - 1].draw(g, path);
			}
		}
	}

	public void setField(Field f){
		field = f;
	}

	public Field getField(){
		return field;
	}
}