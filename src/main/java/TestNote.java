/**
 * @author aleclalonde
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestNote {

    public static String notes = "A5 G#5 F#5 C#6 F#5 D6 C#6 F#5";
    
	public static void main(String[] args) {
	    double random = Math.random();
	    if( random < 0.25 ) System.out.println("1");
	    if( random > 0.75 ) System.out.println("3");
	    else System.out.println("2");
	    System.out.println(random);
		
	}
}
