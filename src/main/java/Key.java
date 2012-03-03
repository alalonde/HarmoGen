/**
 * Represents a key in the expert system.  
 *
 * @author Alec LaLonde
 */
public class Key 
{
    //---------------Constants-----------------//
    public static final String MAJOR = "Major";
    public static final String MINOR = "Minor";
    
    public static final String BLANK = " ";
    
    //all the major keys
    public static String C_MAJOR = "C";
    public static String G_MAJOR = "G";
    public static String F_MAJOR = "F";
    public static String D_MAJOR = "D";
    public static String B_FLAT_MAJOR = "Bb";
    public static String A_MAJOR = "A";
    public static String E_FLAT_MAJOR = "Eb";
    public static String E_MAJOR = "E";
    public static String A_FLAT_MAJOR = "Ab";
    public static String B_MAJOR = "B";
    public static String D_FLAT_MAJOR = "Db";
    public static String F_SHARP_MAJOR = "F#";
    public static String G_FLAT_MAJOR = "Gb";
    public static String C_SHARP_MAJOR = "C#";
    public static String C_FLAT_MAJOR = "Cb";
    
    public static String[] MAJOR_KEYS = {BLANK, C_MAJOR, G_MAJOR, F_MAJOR, D_MAJOR, B_FLAT_MAJOR, 
            A_MAJOR, E_FLAT_MAJOR, E_MAJOR, A_FLAT_MAJOR, B_MAJOR, D_FLAT_MAJOR, F_SHARP_MAJOR, 
            G_FLAT_MAJOR, C_SHARP_MAJOR, C_FLAT_MAJOR};
    
    //all the minor keys
    public static String A_MINOR = "A";
    public static String E_MINOR = "E";
    public static String D_MINOR = "D";
    public static String B_MINOR = "B";
    public static String G_MINOR = "G";
    public static String F_SHARP_MINOR = "F#";
    public static String C_MINOR = "C";
    public static String C_SHARP_MINOR = "C#";
    public static String F_MINOR = "F";
    public static String G_SHARP_MINOR = "G#";
    public static String B_FLAT_MINOR = "Bb";
    public static String D_SHARP_MINOR = "D#";
    public static String E_FLAT_MINOR = "Eb";
    public static String A_SHARP_MINOR = "A#";
    public static String A_FLAT_MINOR = "Ab";
    
    public static String[] MINOR_KEYS = {BLANK, A_MINOR, E_MINOR, D_MINOR, B_MINOR, G_MINOR, 
            F_SHARP_MINOR, C_MINOR, C_SHARP_MINOR, F_MINOR, G_SHARP_MINOR, B_FLAT_MINOR, D_SHARP_MINOR, 
            E_FLAT_MINOR, A_SHARP_MINOR, A_FLAT_MINOR};
    
    private Integer[] notes8;			//the notes of this key on a 1-8 scale	
    private int root12;				//the root of the key on a 1-12 scale
    private int root8;				//the root of the key on a 1-8 scale
    private int sharpsOrFlats = 0;	//is this a sharp or flat scale? sharp = 1, flat = -1
    									//this is found by iterating through all the notes of the
    									//melody...
    
    private String minorOrMajor;		//minor or major?

    /**
     * Construct a new key given the key as a String.  Constructed from the key type and root provided
     * by the user in the GUI.  
     * 
     * @param root the root of the key
     * @param keyType major or minor key
     */
    public Key( String root, String keyType ) 
    {
        notes8 = new Integer[7]; 
        minorOrMajor = keyType;
        root12 = setRootKey12(root);
        fillNotes8(root12, keyType);
        sharpsOrFlats = setSharpOrFlatKey(root, keyType);
        root8 = Utilities.convert12to8(root12, sharpsOrFlats);
    }
    
    /**
     * Iterate through all the possible keys and determine whether this key contains sharps (1),
     * flats (-1), or neither (0).  Uses the glorious ternary operator.
     * 
     * @param root		the root of the key
     * @param keyType		major or minor key
     * @return			sharp (1), flat (-1), or neither (0)
     */
    public int setSharpOrFlatKey(String root, String keyType )
    {
        char letter = root.charAt(0);
        char tag = root.length() > 1 ? root.charAt(1) : 'n';
                
        int sharpOrFlat = 0;
        
        if(keyType.equals(MAJOR))
        {
            switch( letter ) {
            case 'C':
                if(tag == 'b') sharpOrFlat = Note.FLAT;
                else if(tag == '#') sharpOrFlat = Note.SHARP;
                break;
            case 'G':
                sharpOrFlat = tag == 'b' ? Note.FLAT : Note.SHARP;
                break;
            case 'D':
                sharpOrFlat = tag == 'b' ? Note.FLAT : Note.SHARP;
                break;
            case 'A':
                sharpOrFlat = tag == 'b' ? Note.FLAT : Note.SHARP;
                break;
            case 'E':
                sharpOrFlat = tag == 'b' ? Note.FLAT : Note.SHARP;
                break;
            case 'B':
                sharpOrFlat = tag == 'b' ? Note.FLAT : Note.SHARP;
                break;
            case 'F':
                sharpOrFlat = tag == '#' ? Note.SHARP : Note.FLAT;
                break;
            }
        }
        else
        {
            switch( letter ) {
            case 'A':
                if(tag == 'b') sharpOrFlat = Note.FLAT;
                else if(tag == '#') sharpOrFlat = Note.SHARP;
                break;
            case 'E':
                sharpOrFlat = tag == 'b' ? Note.FLAT : Note.SHARP;
                break;
            case 'B':
                sharpOrFlat = tag == 'b' ? Note.FLAT : Note.SHARP;
                break;
            case 'F':
                sharpOrFlat = tag == '#' ? Note.SHARP : Note.FLAT;
                break;
            case 'C':
                sharpOrFlat = tag == '#' ? Note.SHARP : Note.FLAT;
                break;
            case 'G':
                sharpOrFlat = tag == '#' ? Note.SHARP : Note.FLAT;
                break;
            case 'D':
                sharpOrFlat = tag == '#' ? Note.SHARP : Note.FLAT;
                break;
            }
        }
        
        return sharpOrFlat;
    }
    
    public int getSharpOrFlatKey() {
        return sharpsOrFlats;
    }
    
    public String getKeyType()
    {
        return minorOrMajor;
    }
    
    private void fillNotes8(int root, String major) {
        if( major.equals(MAJOR) ) {
	        notes8[0] = new Integer(root);
	        notes8[1] = new Integer(root + 2);
	        notes8[2] = new Integer(root + 4);
	        notes8[3] = new Integer(root + 5);
	        notes8[4] = new Integer(root + 7);
	        notes8[5] = new Integer(root + 9);
	        notes8[6] = new Integer(root + 11);
        } else {
            notes8[0] = new Integer(root);
	        notes8[1] = new Integer(root + 2);
	        notes8[2] = new Integer(root + 3);
	        notes8[3] = new Integer(root + 5);
	        notes8[4] = new Integer(root + 7);
	        notes8[5] = new Integer(root + 8);
	        notes8[6] = new Integer(root + 10);
        }
    }
    
    public int getNote12( int note ) {
        int note12 = notes8[note - 1].intValue();
        //System.out.println("noteArray = " + note12);
        if( note12 > 12 ) note12 = note12 % 12;
        return note12;
    }
    
    public int getRootOfKey12()
    {
        return root12;
    }
    
    /**
     * Determine the 1-12 representation of the root of this key.
     * 
     * @param keyString
     * @return
     */
    public int setRootKey12(String keyString)
    {
        char bareNote = keyString.charAt(0);
        int noteNumber = 0;
        
		switch( bareNote ) {
			case 'C': 
				noteNumber = 1;
				break;
			case 'D':
				noteNumber = 3;
				break;
			case 'E':
				noteNumber = 5;
				break;
			case 'F':
				noteNumber = 6;
				break;
			case 'G':
				noteNumber = 8;
				break;
			case 'A':
				noteNumber = 10;
				break;
			case 'B':
				noteNumber = 12;
				break;				
		}
		
		if(keyString.length() > 1)
		{
		    if(keyString.charAt(1) == ('b'))
		        noteNumber--;
		    else if(keyString.charAt(1) == ('#'))
		        noteNumber++;
		}
		
		return noteNumber;
    }
    
}
