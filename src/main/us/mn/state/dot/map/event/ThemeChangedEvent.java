/*
 * ThemeChangedEvent.java
 *
 * Created on June 16, 2000, 3:56 PM
 */

package us.mn.state.dot.shape;

/**
 *
 * @author  engs1eri
 * @version 
 */

import java.util.*;

public class ThemeChangedEvent extends EventObject {

	private int reason;
	public final static int GEOGRAPHY = 1,
							DATA = 2,
							SHADE = 4,
							HIGHLIGHT = 8,
							ANIMATION = 16,
							SELECTION = 32;
	
    /** Creates new ThemeChangedEvent */
    public ThemeChangedEvent( Object source, int why ){
        super( source );
        reason = why;
    }
	
	public int getReason(){
        return reason;
    }
    
    public boolean testReason( int test ){
        return ( test & reason ) > 0;
    }

}