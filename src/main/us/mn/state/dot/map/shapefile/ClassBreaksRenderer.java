
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
	private NumericField field;
	private String name = null;

	public ClassBreaksRenderer(NumericField field, int breakCount) {
		this.field = field;
		classBreaks = new double[breakCount];
		symbols = new Symbol[breakCount + 1];
		//labels = new String[breakCount + 1];
	}

	public ClassBreaksRenderer(NumericField field, int breakCount, String name){
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

	public double getBreak(int index){
		return classBreaks[index];
	}

	public void setBreaks(double[] values){
		classBreaks = values;
	}

	public int getBreakCount(){
		return classBreaks.length;
	}

	public void setSymbol(int index, Symbol s){
		symbols[index] = s;
	}

	public Symbol getSymbol(int index){
		return symbols[index];
	}

	public Symbol[] getSymbols(){
		return symbols;
	}

	/** Paints a shape. */
	public void paint(Graphics2D g, GeneralPath path, int index){
		int classBreak = field.getRenderingClass(index, classBreaks);
		if ((classBreak > -1) && (symbols[classBreak] != null)){
			symbols[classBreak].draw(g, path);
		}
	}

	public void setField(NumericField f){
		field = f;
	}

	public NumericField getField(){
		return field;
	}
}