package us.mn.state.dot.shape;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.2 $ $Date: 2001/05/07 22:16:24 $
 */
public class LayerChangedEvent extends java.util.EventObject {
    
	private int reason;
    public final static int GEOGRAPHY = 1;
	public final static int	DATA = 2;
	public final static int	ANIMATION = 16;

	/** Creates new LayerChangedEvent */
    public LayerChangedEvent( Object source, int why ){
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