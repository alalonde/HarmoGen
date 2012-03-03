/**
 * A class to hold all the conversion methods for HarmoGen.  Methods are static.
 *
 * @author Alec LaLonde
 */
public class Utilities {

    /**
     * Creates string representations for HarmoGen-generated Notes.
     * 
     * @return a note string, in the same format as notes that are passed in
     */
    public static String createNoteSymbol(int noteID12, int sharpOrFlatKey) 
    {	    
	    String symbol = "";
	    switch(noteID12) {
	    		case 1:
	    		    symbol = "C-";
	    		    break;
	    		case 2:
	    		    if( sharpOrFlatKey == Note.SHARP )
	    		        symbol = "C#";
	    		    else
	    		        symbol = "Db";
	    		    break;
	    		case 3:
	    		    symbol = "D-";
	    		    break;  
	    		case 4:
	    		    if( sharpOrFlatKey == Note.SHARP  )
	    		        symbol = "D#";
	    		    else
	    		        symbol = "Eb";
	    		    break; 
	    		case 5:
	    		    symbol = "E-";
	    		    break;     
	    		case 6:
	    		    symbol = "F-";
	    		    break;
	    		case 7:
	    		    if( sharpOrFlatKey == Note.SHARP  )
	    		        symbol = "F#";
	    		    else
	    		        symbol = "Gb";
	    		    break;    
	    		case 8:
	    		    symbol = "G-";
	    		    break;
	    		case 9:
	    		    if( sharpOrFlatKey == Note.SHARP )
	    		        symbol = "G#";
	    		    else
	    		        symbol = "Ab";
	    		    break;
	    		case 10:
	    		    symbol = "A-";
	    		    break;
	    		case 11:
	    		    if( sharpOrFlatKey == Note.SHARP  )
	    		        symbol = "A#";
	    		    else
	    		        symbol = "Bb";
	    		    break;
	    		case 12:
	    		    symbol = "B-";
	    		    break;    
	    }
	    return symbol;    
	}

    
    /**
	 * Finds the noteNumber on a 1-128 scale, 60 being middle C.
	 * 
	 * @param myNote the note to be identified
	 */
	public static int findNoteNumber( String noteSymbol, int octave, char noteTag ) 
	{
		char bareNote = noteSymbol.charAt(0);
		
		int noteNumber = 0;
		
		switch( bareNote ) {
			case 'C': 
				noteNumber = 36;
				break;
			case 'D':
				noteNumber = 38;
				break;
			case 'E':
				noteNumber = 40;
				break;
			case 'F':
				noteNumber = 41;
				break;
			case 'G':
				noteNumber = 43;
				break;
			case 'A':
				noteNumber = 45;
				break;
			case 'B':
				noteNumber = 47;
				break;				
		}
		for( int i = 3; i < octave; i++ ) {
			noteNumber += 12;
		}
		if( noteTag != 'n') {
			if( noteTag == 'b') noteNumber--;
			else	 noteNumber++;
		}
		
		return noteNumber;
	}
	
	/**
	 * Used by the HarmoGen note generator to find the noteNumber (MIDI representation).
	 * 
	 * @param harmonyNote the note's number in relation to key.
	 * @return the MIDI representation of the note
	 */
	public static int findNoteNumber( int octave, int noteID12 ) 
	{	    
	    return octave * 12 + noteID12 - 1; 
	}
	
	public static int findOctave(int harmonyNote)
	{
	    int octave;
	    
	    if( harmonyNote < 53 ) 
	        octave = 3;	        
	    else if( harmonyNote < 60 )
	        octave = 4;
	    else if( harmonyNote < 67)
	        octave = 5;
	    else 
	        octave = 6;
	    
	    return octave;
	}
	    
	/**
	 * Converts a given note in a 1-12 representation to it's according 1-7
	 * representation.
	 * 
	 * @param switcher the note to be converted
	 * @param sharpOrFlat if the key has sharps or flats
	 * @return the 1-8 representation of the note
	 */
	public static int convert12to8( int switcher, int sharpOrFlat ) 
	{
	    int retVal = 0;
	    
	    switch( switcher ) {
			case 1:
			    retVal = 1;
			    break;
			case 3:
			    retVal = 2;
				break;
			case 5:
			    retVal = 3;
				break;	
			case 6:
			    retVal = 4;
				break;	
			case 8:
			    retVal = 5;
				break;
			case 10:
			    retVal = 6;
			    break;
			case 12:  
			    retVal = 7;
	    }
	    if( sharpOrFlat == Note.SHARP ) 
	    {
	        switch( switcher ) {
	        		case 2:
	        		    retVal = 1;
	        		    break;
				case 4:
				    retVal = 2;
					break;
				case 7:
				    retVal = 4;
					break;	
				case 9:
				    retVal = 5;
					break;	
				case 11:
				    retVal = 6;
	        }
	    } 
	    else if( sharpOrFlat == Note.FLAT )
	    {
	        switch( switcher ) {
	        		case 2:
	        		    retVal = 2;
	        		    break;
				case 4:
				    retVal = 3;
					break;
				case 7:
				    retVal = 5;
					break;	
				case 9:
				    retVal = 6;
					break;
				case 11:
				    retVal = 7;
					break;				
	        }
	    }
	    return retVal;
	}
	
	/**
	 * Mutator for the note's 1-12 ID.  Once the key is determined, the note ID
	 * range starts on the tonic.  e.g. if the key is E major, E = 1, F# = 3, etc.
	 * 
	 * @param the root of the determined key
	 */
	public static int findScaleID12( int root, int noteID12 ) 
	{
	    int scaleID12 = noteID12 - root + 1;
	    if( scaleID12 <= 0) scaleID12 = scaleID12 + 12;
	    
	    return scaleID12;
	}
	
	/**
	 * Convert a ABC-style note to a HarmoGen-style note (e.g. c' -> C7).
	 * Only works for soprano notes in the range C5-C7.
	 * 
	 * @param ABCnote the note in ABC notation
	 * @return the note in HarmoGen notation, and capitalized
	 */
	public static String convertABCtoHG(String ABCnote)
	{
	    String harmoGenNote;
	    char bareNote;
	    
	    if(ABCnote.substring(0,1).equals("^"))
	    {
	        harmoGenNote = ABCnote.substring(1,2).toUpperCase() + "#";
	        bareNote = ABCnote.charAt(1);
	    }
	    else if(ABCnote.substring(0,1).equals("_"))
	    {
	        harmoGenNote = ABCnote.substring(1,2).toUpperCase()+ "b";
	        bareNote = ABCnote.charAt(1);
	    }
	    else
	    {
	        harmoGenNote = ABCnote.substring(0,1).toUpperCase();
	        bareNote = ABCnote.charAt(0);
	    }
	    
	    if(ABCnote.endsWith("'"))
	    {
	        harmoGenNote = harmoGenNote.concat("7");
	    }
	    else 
	    {
	        if(Character.isUpperCase(bareNote))
	            harmoGenNote = harmoGenNote.concat("5");
	        else
	            harmoGenNote = harmoGenNote.concat("6");
	    }
	    
	    return harmoGenNote;
	}
	
	/**
	 * Convert a note in HarmoGen notation to ABC notation.  Should work for any note from
	 * octaves 3-7.  This method needs to know which clef the note is in to function properly.
	 * 
	 * @param HGNote the note in HarmoGen notation
	 * 		 isTrebleClef will this note be in the treble clef?
	 * @return the note in ABC notation
	 */
	public static String convertHGtoABC(String HGNote, boolean isTrebleClef)
	{
	    String abcNote = HGNote.substring(0,1);
	    
	    if(HGNote.substring(1,2).equals("#"))
	        abcNote = "^" + abcNote;
	    else if(HGNote.substring(1,2).equals("b"))
	        abcNote = "_" + abcNote;
	    
	    int octave = Integer.parseInt(HGNote.substring(HGNote.length() - 1, HGNote.length()));
	    
	    switch(octave)
	    {
	    case 3:
	        break;
	    case 4:
	        if(!isTrebleClef)
	            abcNote = abcNote.toLowerCase();
	        else
	            abcNote = abcNote + ",";  
	        break;
	    case 5:
	        if(!isTrebleClef)
	            abcNote = abcNote.toLowerCase() + "'";
	        break;   
        case 6:
            abcNote = abcNote.toLowerCase();
            break;
        case 7:
            abcNote = abcNote.toLowerCase() + "'";
            break;
	    }
	    
	    return abcNote;
	}
}
