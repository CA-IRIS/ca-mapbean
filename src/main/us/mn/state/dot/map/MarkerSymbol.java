
//Title:        Your Product Name
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

public class MarkerSymbol extends Symbol {

  private static final int style = 0;

  /** Point Style Constants */
  public static final int CIRCLE_MARKER = 0;
  public static final int SQUARE_MARKER = 1;
  public static final int TRIANGLE_MARKER = 2;
  public static final int CROSS_MARKER = 3;
  public static final int TRUE_TYPE_MARKER = 4;

	public PointSymbol() {
	}

  
 /** Sets style used to render symbol */
 public void setStyle(int style){
 	if ((style > 0) && (style <= 10)){
 		this.style = style;
   }
 }

 /** Returns style used to render symbol */
 public int getStyle(){
 	return style;
 }

 /** Draw symbol on map */
 public void draw(Graphics2D g, GeneralPath path){
   g.setColor(this.getColor());
   switch (style){
   	case SOLID_FILL:
     	g.fill(path);
       break;
     case TRANSPARENT_FILL:
     	break;
			case HORIZONTAL_FILL:
     	break;
			case VERTICAL_FILL:
     	break;
			case UPWARD_DIAGONAL_FILL:
     	break;
			case DOWNWARD_DIAGONAL_FILL:
     	break;
			case CROSS_FILL:
     	break;
			case DIAGONAL_CROSS_FILL:
     	break;
			case LIGHT_GRAY_FILL:
     	break;
			case GRAY_FILL:
     	break;
			case DARK_GRAY_FILL:
     	break;
   }
   if (this.getOutLine()){
   	g.setColor(this.getOutLineColor());
     g.draw(path);
   }
 }
} 