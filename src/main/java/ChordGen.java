import java.util.*;

/**
 * The chord generator.  
 * 
 * @author Alec LaLonde
 */
public class ChordGen {

    private final int BASS = 0;
    private final int TENOR = 7;
    private final int ALTO = 7;
    public static final int LOOPS = 20;
    
    private ArrayList bassRange;
    private ArrayList tenorRange;
    private ArrayList altoRange;
    private ArrayList sopranoRange;

    /**
     * Initialize the chord generator with the voice ranges defined in the knowledge database.
     * 
     * @param bass  		all possible bass notes
     * @param tenor 		all possible tenor notes
     * @param alto		all possible alto notes
     * @param soprano		all possible soprano notes
     */
    public ChordGen(ArrayList bass, ArrayList tenor, ArrayList alto, ArrayList soprano) 
    {
        bassRange = bass;
        tenorRange = tenor;
        altoRange = alto;
        sopranoRange = soprano;
    }

    /**
     * Picks an octave for the given note and range.
     * 
     * @param note
     * @param range
     * @param rangeNums
     * @param soprano
     * @return
     */
    public Note pickNote( int note, int range, ArrayList rangeNums, Note soprano ) 
    {
        double randomNum = Math.random();
        int choiceOne = note + range + 45;
        int choiceTwo = choiceOne + 7;
        int choiceThree = choiceTwo + 7;
        int validChoiceOne = 0;
        int validChoiceTwo = 0;
        int randomChoice = 0;
        
        if( rangeNums.contains(new Integer(choiceOne)) ) 
            	validChoiceOne = choiceOne;
        else if( rangeNums.contains(new Integer(choiceThree)) ) 
            validChoiceOne = choiceThree;  
        else
            randomChoice = choiceTwo;
        if( randomChoice != choiceTwo ) {
            if( rangeNums.contains(new Integer(choiceTwo)) ) 
                validChoiceTwo = choiceTwo;
        
            if( validChoiceTwo != 0 && randomNum >= 0.5 )
                randomChoice = validChoiceTwo;
            else
                randomChoice = validChoiceOne;
        }
        //System.out.println("choice: " + randomChoice);
        Note randomNote = new Note( randomChoice, soprano.getKey());
        //System.out.println(randomNote.getHarmonyNote8());
        return randomNote;
    }

    /**
     * Generate a chord whose bass note is the root of the chord.
     * 
     * @param chordNotes		the notes that need to be in this chord
     * @param melodyNote		The melody note of this chord
     * @param previousChord
     * @return
     */
    public Chord generateRootChord(Integer[] chordNotes, Note melodyNote, 
            Chord previousChord ) 
    {
        	Chord rootChord = null;
        	double randomNum = Math.random();
        	Note bassNote = null;
        	Note tenorNote = null;
        	Note altoNote = null;
        	boolean overlap = true;
        	boolean octaveGap = true;
        	boolean parallelOctave = true;
        	boolean parallelFifth = true;
        	boolean partCross = true;
        	int loopCount = 0;
        	
        	melodyNote.setHarmonyNote8( findSopranoNum( melodyNote ) );
        	
        	while( (overlap || octaveGap || parallelOctave || parallelFifth || partCross) && 
        	        loopCount < LOOPS ) {
        	    randomNum = Math.random();
	        	bassNote = pickNote( chordNotes[0].intValue(), BASS, 
	        	        bassRange, melodyNote );
	        	if( randomNum >= (1/2) ) {
	        	    	tenorNote = pickNote( chordNotes[1].intValue(), TENOR, 
	        	    	        tenorRange, melodyNote );
	        	    	altoNote = pickNote( chordNotes[2].intValue(), ALTO, 
	        	    	        altoRange, melodyNote );
	        	} else {
	        	    tenorNote = pickNote( chordNotes[2].intValue(), TENOR, 
	        	            tenorRange, melodyNote );
	    	    		altoNote = pickNote( chordNotes[1].intValue(), ALTO, 
	    	    		        altoRange, melodyNote );
	        	}
	        	if( previousChord != null ) {
	        	    parallelOctave = checkParOctave(bassNote, tenorNote, altoNote, 
	        	            melodyNote, previousChord);
	        	    parallelFifth = checkParFifth(bassNote, tenorNote, altoNote, 
	        	            melodyNote, previousChord);
	        	    partCross = checkPartCrosses(bassNote, tenorNote, altoNote,
	        	            melodyNote, previousChord);
	        	} else {
	        	    parallelOctave = false;
	        	    parallelFifth = false;
	        	    partCross = false;
	        	}
	        	overlap = checkOverlap(bassNote, tenorNote, altoNote, melodyNote);
	        	octaveGap = checkGaps(tenorNote, altoNote, melodyNote);
	        	loopCount++;
        	}
        	
        	rootChord = new Chord(bassNote, tenorNote, altoNote, melodyNote);
        	//System.out.println("Picked bass Rootchord: " + 
        	 //       rootChord.getBassNote().getNoteAndOctave());
        	if( loopCount >= LOOPS ) 
        	{
        	    rootChord.setLoopExceeded( true );
        	    /*
        	    if(partCross)
        	        System.out.println("generateRootChord: loop boundary hit...part crossed");
        	    else
            	    System.out.println("generateRootChord: loop boundary hit");
            	    */
        	}
        	else
        	    rootChord.setLoopExceeded( false );
        
        	return rootChord;
    }
    
    /**
     * Generate a chord whose root may be for the tenor or alto.  Only used when a root chord cannot
     * be generated.
     * 
     * @param chordNotes  	the notes that need to be in this chord
     * @param melodyNote		The melody note of this chord
     * @param previousChord
     * @return
     */
    public Chord generateInvertedChord(Integer[] chordNotes, Note melodyNote, 
            Chord previousChord ) 
    {
        Chord invertedChord = null;
	    	double randomNum = Math.random();
	    	Note bassNote = null;
	    	Note tenorNote = null;
	    	Note altoNote = null;
	    	boolean overlap = true;
	    	boolean octaveGap = true;
	    	boolean parallelOctave = true;
	    	boolean parallelFifth = true;
	    	boolean partCross = true;
	    	int loopCount = 0;
	    	
	    	melodyNote.setHarmonyNote8( findSopranoNum( melodyNote ) );
	    	
	    	while( (overlap || octaveGap || parallelOctave || parallelFifth || partCross) && 
	    	        loopCount < LOOPS ) {
	    	    randomNum = Math.random();
	    	    if( randomNum < 0.25 ) {
	    	        tenorNote = pickNote( chordNotes[0].intValue(), TENOR, 
        	    	        tenorRange, melodyNote );
	    	        bassNote = pickNote( chordNotes[1].intValue(), BASS, 
	        	        bassRange, melodyNote );
	    	        altoNote = pickNote( chordNotes[2].intValue(), ALTO, 
	    	   		    altoRange, melodyNote );
	    	    } else if( randomNum < 0.5 ) {
	    	        tenorNote = pickNote( chordNotes[0].intValue(), TENOR, 
        	    	        tenorRange, melodyNote );
	    	        bassNote = pickNote( chordNotes[2].intValue(), BASS, 
	        	        bassRange, melodyNote );
	    	        altoNote = pickNote( chordNotes[1].intValue(), ALTO, 
	    	   		    altoRange, melodyNote );
	        	} else if( randomNum < 0.75 ){
	        	    tenorNote = pickNote( chordNotes[1].intValue(), TENOR, 
        	    	        tenorRange, melodyNote );
	    	        bassNote = pickNote( chordNotes[2].intValue(), BASS, 
	        	        bassRange, melodyNote );
	    	        altoNote = pickNote( chordNotes[0].intValue(), ALTO, 
	    	   		    altoRange, melodyNote );
	        	} else {
	        	    tenorNote = pickNote( chordNotes[2].intValue(), TENOR, 
        	    	        tenorRange, melodyNote );
	    	        bassNote = pickNote( chordNotes[1].intValue(), BASS, 
	        	        bassRange, melodyNote );
	    	        altoNote = pickNote( chordNotes[0].intValue(), ALTO, 
	    	   		    altoRange, melodyNote );
	        	}
	        	if( previousChord != null ) {
	        	    parallelOctave = checkParOctave(bassNote, tenorNote, altoNote, 
	        	            melodyNote, previousChord);
	        	    parallelFifth = checkParFifth(bassNote, tenorNote, altoNote, 
	        	            melodyNote, previousChord);
	        	    partCross = checkPartCrosses(bassNote, tenorNote, altoNote, 
	        	            melodyNote, previousChord);
	        	    
	        	} else {
	        	    parallelOctave = false;
	        	    parallelFifth = false;
	        	    partCross = false;
	        	}
	        	overlap = checkOverlap(bassNote, tenorNote, altoNote, melodyNote);
	        	octaveGap = checkGaps(tenorNote, altoNote, melodyNote);
	        	loopCount++;
	    	}
	    	if( overlap || octaveGap || parallelOctave || parallelFifth || partCross)
	    	{
	    	    invertedChord = null;
	    	    //System.out.println("generateInvertedChord: loop boundary hit");
	    	}
	    	else {
	    	    invertedChord = new Chord(bassNote, tenorNote, altoNote, melodyNote);
	    	    /*
	    	    System.out.println("Picked bass Invchord: " + 
	    	            invertedChord.getBassNote().getNoteAndOctave());
	    	    System.out.println("Picked tenor Invchord: " + 
	    	            invertedChord.getTenorNote().getNoteAndOctave());
	    	    System.out.println("Picked alto Invchord: " + 
	    	            invertedChord.getAltoNote().getNoteAndOctave());
	    	            */
	    	}
	    	return invertedChord;
    }
    
    public int findSopranoNum( Note soprano ) {
        int retNum = 0;
        int noteID8 = soprano.getNoteID8();
        int scaledNoteID = noteID8 + 59;
        if( soprano.getNoteNumber() < 72 && noteID8 != 1) {
            retNum = scaledNoteID;
        } else {
            retNum = scaledNoteID + 7;
        }
        return retNum;
    }
    
    //--------------------------Begin generated chord checking methods--------------------------
    
    public boolean checkOverlap(Note bass, Note tenor, Note alto, Note soprano) {
        boolean crossed = false;
        if( bass.getHarmonyNote8() > tenor.getHarmonyNote8() ) 
            crossed = true;
        if( tenor.getHarmonyNote8() > alto.getHarmonyNote8() ) 
            crossed = true;
        if( alto.getHarmonyNote8() > soprano.getHarmonyNote8() ) 
            crossed = true;
        if( bass.getHarmonyNote8() > alto.getHarmonyNote8() ) 
            crossed = true;
        if( tenor.getHarmonyNote8() > soprano.getHarmonyNote8() ) 
            crossed = true;
        if( bass.getHarmonyNote8() > soprano.getHarmonyNote8() )
            crossed = true;
        
        return crossed;
    }
    
    public boolean checkGaps(Note tenor, Note alto, Note soprano) 
    {
        boolean bigGap = false;
        if( tenor.getHarmonyNote8() + 7 < alto.getHarmonyNote8() ) 
            bigGap = true;
        if( alto.getHarmonyNote8() + 7 < soprano.getHarmonyNote8() ) 
            bigGap = true;
        return bigGap;
    }
    
    public boolean checkParOctave(Note bass, Note tenor, Note alto, Note soprano,
            Chord prevChord) 
    {
        boolean parOctave = false;
        
        if( (bass.getHarmonyNote8() - prevChord.getBassNoteNum() == 7 ||
                prevChord.getBassNoteNum() - bass.getHarmonyNote8() == 7) &&
                ( tenor.getHarmonyNote8() - prevChord.getTenorNoteNum() == 7 ||
                prevChord.getTenorNoteNum() - tenor.getHarmonyNote8() == 7 ) ) 
            parOctave = true;
        if( (bass.getHarmonyNote8() - prevChord.getBassNoteNum() == 7 ||
                prevChord.getBassNoteNum() - bass.getHarmonyNote8() == 7) &&
                ( alto.getHarmonyNote8() - prevChord.getAltoNoteNum() == 7 ||
                prevChord.getAltoNoteNum() - alto.getHarmonyNote8() == 7 ) ) 
            parOctave = true;
        if( (bass.getHarmonyNote8() - prevChord.getBassNoteNum() == 7 ||
                prevChord.getBassNoteNum() - bass.getHarmonyNote8() == 7) &&
                ( soprano.getHarmonyNote8() - prevChord.getSopranoNoteNum() == 7 ||
                prevChord.getSopranoNoteNum() - soprano.getHarmonyNote8() == 7 )  ) 
            parOctave = true;
        if( (tenor.getHarmonyNote8() - prevChord.getTenorNoteNum() == 7 ||
                prevChord.getTenorNoteNum() - tenor.getHarmonyNote8() == 7) && 
                ( alto.getHarmonyNote8() - prevChord.getAltoNoteNum() == 7 ||
                prevChord.getAltoNoteNum() - alto.getHarmonyNote8() == 7 ) )
            parOctave = true;
        if( (tenor.getHarmonyNote8() - prevChord.getTenorNoteNum() == 7 ||
                prevChord.getTenorNoteNum() - tenor.getHarmonyNote8() == 7) && 
                ( soprano.getHarmonyNote8() - prevChord.getSopranoNoteNum() == 7 ||
                prevChord.getSopranoNoteNum() - soprano.getHarmonyNote8() == 7 ) )
            parOctave = true;
        if( ( alto.getHarmonyNote8() - prevChord.getAltoNoteNum() == 7 ||
                prevChord.getAltoNoteNum() - alto.getHarmonyNote8() == 7 ) && 
                ( soprano.getHarmonyNote8() - prevChord.getSopranoNoteNum() == 7 ||
                prevChord.getSopranoNoteNum() - soprano.getHarmonyNote8() == 7 ) )
            parOctave = true;
        
        return parOctave;
    }
    
    public boolean checkParFifth(Note bass, Note tenor, Note alto, Note soprano,
            Chord prevChord) 
    {
        boolean parFifth = false;
        
        if( (bass.getHarmonyNote8() - prevChord.getBassNoteNum() == 4 ||
                prevChord.getBassNoteNum() - bass.getHarmonyNote8() == 4) &&
                ( tenor.getHarmonyNote8() - prevChord.getTenorNoteNum() == 4 ||
                prevChord.getTenorNoteNum() - tenor.getHarmonyNote8() == 4 ) ) 
            parFifth = true;
        if( (bass.getHarmonyNote8() - prevChord.getBassNoteNum() == 4 ||
                prevChord.getBassNoteNum() - bass.getHarmonyNote8() == 4) &&
                ( alto.getHarmonyNote8() - prevChord.getAltoNoteNum() == 4 ||
                prevChord.getAltoNoteNum() - alto.getHarmonyNote8() == 4 ) ) 
            parFifth = true;
        if( (bass.getHarmonyNote8() - prevChord.getBassNoteNum() == 4 ||
                prevChord.getBassNoteNum() - bass.getHarmonyNote8() == 4) &&
                ( soprano.getHarmonyNote8() - prevChord.getSopranoNoteNum() == 4 ||
                prevChord.getSopranoNoteNum() - soprano.getHarmonyNote8() == 4 )  ) 
            parFifth = true;
        if( (tenor.getHarmonyNote8() - prevChord.getTenorNoteNum() == 4 ||
                prevChord.getTenorNoteNum() - tenor.getHarmonyNote8() == 4) && 
                ( alto.getHarmonyNote8() - prevChord.getAltoNoteNum() == 4 ||
                prevChord.getAltoNoteNum() - alto.getHarmonyNote8() == 4 ) )
            parFifth = true;
        if( (tenor.getHarmonyNote8() - prevChord.getTenorNoteNum() == 4 ||
                prevChord.getTenorNoteNum() - tenor.getHarmonyNote8() == 4) && 
                ( soprano.getHarmonyNote8() - prevChord.getSopranoNoteNum() == 4 ||
                prevChord.getSopranoNoteNum() - soprano.getHarmonyNote8() == 4 ) )
            parFifth = true;
        if( ( alto.getHarmonyNote8() - prevChord.getAltoNoteNum() == 4 ||
                prevChord.getAltoNoteNum() - alto.getHarmonyNote8() == 4 ) && 
                ( soprano.getHarmonyNote8() - prevChord.getSopranoNoteNum() == 4 ||
                prevChord.getSopranoNoteNum() - soprano.getHarmonyNote8() == 4 ) )
            parFifth = true;
        
        return parFifth;
    }
    
    public boolean checkPartCrosses(Note bass, Note tenor, Note alto, Note soprano, Chord prevChord) 
    {
        boolean crossed = false;
        
        if( prevChord.getAltoNote().getHarmonyNote8() > soprano.getHarmonyNote8() ) 
            crossed = true;
        if( prevChord.getTenorNote().getHarmonyNote8() > alto.getHarmonyNote8() )
            crossed = true;
        if( prevChord.getBassNote().getHarmonyNote8() > tenor.getHarmonyNote8() ) 
            crossed = true;
        if( prevChord.getTenorNote().getHarmonyNote8() > soprano.getHarmonyNote8() ) 
            crossed = true;
        if( prevChord.getBassNote().getHarmonyNote8() > alto.getHarmonyNote8())
            crossed = true;
        return crossed;
    }
}