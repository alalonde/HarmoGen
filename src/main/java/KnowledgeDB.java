import java.util.*;

/**
 * @author Alec LaLonde
 *
 * The home for all of the musical knowledge the system will possess.  Most of the 
 * knowledge is set up in the constructor.  Most of the methods are called by the 
 * inference engine.
 */
public class KnowledgeDB 
{	
	private final int BASS = 1;
	private final int TENOR = 2;
	private final int ALTO = 3;
	
	private final int BASS_LOW = 40;		//E3
	private final int BASS_HIGH = 64;		//E5
	private final int TENOR_LOW = 48;		//C4
	private final int TENOR_HIGH = 69;	//A5
	private final int ALTO_LOW = 53;		//F4
	private final int ALTO_HIGH = 74;		//D6
	private final int SOPRANO_LOW = 60;  	//C5
	private final int SOPRANO_HIGH = 84; 	//C7
	
	private final int random = 0;
	private final int tonic = 1;
	private final int supertonic = 2;
	private final int submediant = 3;
	private final int subdominant = 4;
	private final int dominant = 5;
	private final int mediant = 6;
	private final int leadingTone = 7;
	
	private Note[] notes;     		//the inputted notes
	private Key key;					//the deduced key
	private int root; 				//the root of the key
	private String keyType;     		//indicates major or minor key
	private int loops = 0;
	private ArrayList majorScale;		//holds the notes of a major scale
	private ArrayList minorScale;		//holds the notes of a minor scale
	private ArrayList bassRange;		//holds the range for the bass voicing
	private ArrayList tenorRange;		//holds the range for the tenor voicing
	private ArrayList altoRange;		//holds the range for the alto voicing
	private ArrayList sopranoRange;	//holds the range for the soprano voicing
	private ChordGen chordGen;
	
	private boolean badChordChosen = false;
	
	/**
	 * Constructor called by InferenceEngine.  Sets up the knowledge indicated
	 * by the constants above.
	 * 
	 * @param myNotes an array of Note objects inputted by the user.
	 */
	public KnowledgeDB( Note[] myNotes, Key key ) {
		notes = myNotes;
	    
		this.key = key;
		root = key.getRootOfKey12();
		keyType = key.getKeyType();
		
		majorScale = new ArrayList();
		minorScale = new ArrayList();
		
		bassRange = new ArrayList();
		tenorRange = new ArrayList();
		altoRange = new ArrayList();
		sopranoRange = new ArrayList();
		
		for( int i = BASS_LOW; i <= SOPRANO_HIGH; i++ ) {
			if(i <= BASS_HIGH ) bassRange.add(new Integer(i));
			if(i <= TENOR_HIGH && i >= TENOR_LOW) tenorRange.add(new Integer(i));
			if(i <= ALTO_HIGH && i >= ALTO_LOW) altoRange.add(new Integer(i));
			if(i >= SOPRANO_LOW) sopranoRange.add(new Integer(i));
		}
		
		chordGen = new ChordGen(bassRange, tenorRange, altoRange, sopranoRange);
		
		majorScale.add(new Integer(1));
		majorScale.add(new Integer(3));
		majorScale.add(new Integer(5));
		majorScale.add(new Integer(6));
		majorScale.add(new Integer(8));
		majorScale.add(new Integer(10));
		majorScale.add(new Integer(12));
		
		minorScale.add(new Integer(1));
		minorScale.add(new Integer(3));
		minorScale.add(new Integer(4));
		minorScale.add(new Integer(6));
		minorScale.add(new Integer(8));
		minorScale.add(new Integer(9));
		minorScale.add(new Integer(11));
	}
	
	/**
	 * First check if the input notes are all within a soprano's range.  Then check
	 * to see if there are any nonharmonic tones.  Finally, check to see if a 
	 * cadence is possible.
	 * 
	 * @return false if any check fails.
	 */
	public boolean checkInput() {
		boolean fail = false;
		
		int lastNote = notes.length - 1;
		
		for( int i = 0; i < notes.length; i++ ) {
			if( !sopranoRange.contains(new Integer(notes[i].getNoteNumber())) ) {
				InferenceEngine.setError(notes[i].getNoteAndOctave() + 
				        						" is not in Soprano's range");
				fail = true;
			}
			if( keyType.equals(Key.MAJOR)) {
				if( !majorScale.contains(new Integer(notes[i].getScaleID12())) ) {
				    InferenceEngine.setError(notes[i].getNoteAndOctave() + 
								" is a nonharmonic tone in a major scale");
				    	fail = true;	
				    	
				}
			} else {
			    if( !minorScale.contains(new Integer(notes[i].getScaleID12())) ) {
			        InferenceEngine.setError(notes[i].getNoteAndOctave() + 
								" is a nonharmonic tone in a minor scale");
				    	fail = true;		
				}
			}
		}
		if( notes[lastNote].getScaleID8() == subdominant) 
		{
		    //if the last melody note is a 4 in it's key, we can't generate a proper cadence
		    InferenceEngine.setError(notes[lastNote].getNoteAndOctave() + 
		            			" at the end does not allow for a proper cadence");
		    	fail = true;		
		} else if( notes[lastNote].getScaleID8() == tonic ||
		            notes[lastNote].getScaleID8() == submediant  ||
		            notes[lastNote].getScaleID8() == dominant ) {
		    //ending melody of 3, [1, 3, 5] doesn't allow for a proper cadence 
		    	if( notes[lastNote - 1].getScaleID8() == submediant ) {
		    	    InferenceEngine.setError(notes[notes.length - 2].getNoteAndOctave() + 
		    	            " " + notes[lastNote].getNoteAndOctave() +
        					" at the end does not allow for a proper cadence");
		    	    	fail = true;
		    	}
		}		
		return fail;
	}
		
	/**
	 * Returns a cadence of two chords.  Cadence preference is in this order:
	 * Authentic, Plagal, Half, Deceptive.  
	 * 
	 * @return the two last chords of the harmony.
	 */
	public Chord[] findCadence() 
	{	  
	    Chord[] cadenceChords = new Chord[2];
	    int lastNoteID = notes[notes.length - 1].getScaleID8();
	    int secondToLastNoteID = notes[notes.length - 2].getScaleID8();
	    
	    do{
	        if( lastNoteID == supertonic || lastNoteID == leadingTone ) 
	        {    
	            //if the last melody note is a 2nd or 7th in the key, must use a half cadence
	            cadenceChords[0] = findChord( notes[notes.length - 2], random, null); 
	            cadenceChords[1] = findChord( notes[notes.length - 1], dominant,
	                    cadenceChords[0]);         			
	        } else if( lastNoteID == mediant ) 
	        {
	            //	      if the last melody note is a 6th in the key, must use a deceptive cadence
	            cadenceChords[0] = findChord( notes[notes.length - 2], random, null );
	            cadenceChords[1] = findChord( notes[notes.length - 1], mediant,
	                    cadenceChords[0]);
	        } else {
	            //if the last melody note is anything else, we can end on a I chord.
	            if( secondToLastNoteID == dominant || secondToLastNoteID == leadingTone 
	                    || secondToLastNoteID == supertonic ) {
	                //if the 2nd-to-last melody note is a 2nd, 5th, or 7th in the key, use a full cadence
	                cadenceChords[0] = findChord( notes[notes.length - 2], 
	                        dominant, null); 
	                cadenceChords[1] = findChord( notes[notes.length - 1], tonic,
	                        cadenceChords[0]);
	            } else {
	                //else use a plagal cadence
	                cadenceChords[0] = findChord( notes[notes.length - 2], 
	                        subdominant, null);
	                cadenceChords[1] = findChord( notes[notes.length - 1], tonic,
	                        cadenceChords[0]);
	            }
	        }
	    } while(badChordChosen);
	    
	    return cadenceChords;
	}
	
	/**
	 * Utility function for findCadence(), determines the roots of the chords that
	 * need to be generated and passes the generation logic to the ChordGen class. 
	 * 
	 * @param melodyNote the note of melody that needs to be chorded
	 * @param chordType  the type of chord   i.e. 5 = dominant chord
	 * @return a cadential chord
	 */
	public Chord findChord( Note melodyNote, int chordType, Chord prevChord ) {
	    Chord retChord = null;
	    Integer[] chordNotes = null;
	    boolean errors = false;
	    loops = 0;
	    badChordChosen = false;
	    
	    int chordRoot = melodyNote.getRootKey8() + chordType - 1;
	    if( chordRoot >= 8 ) chordRoot = chordRoot % 7;
	    
	    do {
	        double randomNum2 = Math.random();
		    if( chordType == random ) {
		        double randomNum = Math.random();
		        
		        Integer[] choices = findChordContainingNote( melodyNote.getNoteID8() );
	
		        int choice = 0;
		        if( randomNum < 0.33 )
		            choice = choices[0].intValue();
		        else if( randomNum < 0.67 )
		            choice = choices[1].intValue();
		        else 
		            choice = choices[2].intValue();
		        
		        if( randomNum2 > 0.5 ) 
			        chordNotes = findDoubledNotes( choice );
			    else 
			        chordNotes = findNotesOfChord( choice );	        
		    } else { 
		        if( randomNum2 > 0.5 ) 
			        chordNotes = findDoubledNotes( chordRoot );
			    else 
			        chordNotes = findNotesOfChord( chordRoot );
		    }
		    
		    retChord = chordGen.generateRootChord( chordNotes, melodyNote, prevChord );
		    
		    if( retChord.getLoopExceeded() == true ) {
		        retChord = chordGen.generateInvertedChord( chordNotes, melodyNote,
		                prevChord);
		    }
		    if( retChord == null ) 
		        errors = true;
		    else
		        errors = false;
		    loops++;
	    } while( errors && loops < 100); 
	    
	    if(errors) 
	    {
	        badChordChosen = true;
	        System.out.println("findChord: error found");
	    }
	   
	    return retChord;
	}
	
	/**
	 * Determines the best chord given the previous chord and previous soprano note
	 * 
	 * @param melodyNote
	 * @param previousChord
	 * @return
	 */
	public Chord findBestChord( Note melodyNote, Chord previousChord ) {
	    Chord retChord = null;
	    Integer[] chordNotes = null;
	    double randomNum = Math.random();
	    double randomNum2 = Math.random();
	    int chordRootForKey;
	    int chordType = 0;
	    int chordRoot;
	    boolean validChord = false;
	    boolean errors = false;
	    loops = 0;
	    
	    int prevNote = 0;
	    int bass = previousChord.getBassNote().getNoteID8();
	    int tenor = previousChord.getTenorNote().getNoteID8();
	    int alto = previousChord.getAltoNote().getNoteID8();
	    
	    //find the root of the previous chord
	    if( ((tenor + 2 == bass) && (tenor + 4 == alto)) ||
	            ((tenor + 2 == alto) && (tenor + 4 == bass)) )
	        prevNote = previousChord.getTenorNote().getScaleID8();
	    else if( ((bass + 2 == tenor) && (bass + 4 == alto)) ||
	            ((bass + 2 == alto) && (bass + 4 == tenor)) )
	        prevNote = previousChord.getBassNote().getScaleID8();
	    else if( ((alto + 2 == tenor) && (alto + 4 == bass)) ||
	            ((alto + 2 == bass) && (alto + 4 == tenor)) )
	        prevNote = previousChord.getAltoNote().getScaleID8();
	    else if( ((tenor + 2 == alto) && (tenor - 3 == bass)) ||
	            ((tenor + 2 == bass) && (tenor - 3 == alto)) )
	        prevNote = previousChord.getTenorNote().getScaleID8();
	    else if( ((alto + 2 == bass) && (alto - 3 == tenor)) ||
	            ((alto + 2 == tenor) && (alto - 3 == bass)) )
	        prevNote = previousChord.getAltoNote().getScaleID8();
	    else if( ((bass + 2 == tenor) && (bass - 3 == alto)) ||
	            ((bass + 2 == alto) && (bass - 3 == tenor)) )
	        prevNote = previousChord.getBassNote().getScaleID8();
	    else if( ((tenor - 5 == alto) && (tenor - 3 == bass)) ||
	            ((tenor - 5 == bass) && (tenor - 3 == alto)) )
	        prevNote = previousChord.getTenorNote().getScaleID8();
	    else if( ((alto - 5 == bass) && (alto - 3 == tenor)) ||
	            ((alto - 5 == tenor) && (alto - 3 == bass)) )
	        prevNote = previousChord.getAltoNote().getScaleID8();
	    else if( ((bass - 5 == tenor) && (bass - 3 == alto)) ||
	            ((bass - 5 == alto) && (bass - 3 == tenor)) )
	        prevNote = previousChord.getBassNote().getScaleID8();
	    
	    //TODO: there are only three possible chords we can pick that contain the melody note.  
	    //This should influence our chord picking technique here.
	    
	    do {
		    while( !validChord ) {	       
		        randomNum = Math.random();
		        randomNum2 = Math.random();
		        
		        //Pick a chord (chordType of 4 = fourth chord)
			    if( randomNum < 0.60 ) {
			        if( randomNum2 < 0.25 ) chordType = 4;
			        else if( randomNum2 < 0.5 ) chordType = -5;
			        else if( randomNum2 < 0.75 ) chordType = 5;
			        else chordType = -4;
			    } else if( randomNum < 0.80 ) {
			        if( randomNum2 < 0.25 ) chordType = 2;
				    else if( randomNum2 < 0.5 ) chordType = -7;
				    else if( randomNum2 < 0.75 ) chordType = 7;
				    else chordType = -2;
			    } else if( randomNum < 0.95 ) {
			        chordType = 1;	        
			    } else {
			        if( randomNum2 < 0.25 ) chordType = 3;
			        else if( randomNum2 < 0.5 ) chordType = -6;
			        else if( randomNum2 < 0.75 ) chordType = 6;
			        else chordType = -3;
			    }
			    //System.out.println("chordType " + chordType);
			    if( chordType < 0 ) chordRootForKey = prevNote + chordType + 1;
			    else chordRootForKey = prevNote + chordType - 1;
			    //System.out.println("prevNote in key" + prevNote);
			    if( chordRootForKey - 1 >= 8 ) chordRootForKey = chordRootForKey % 7;
			    else if( chordRootForKey + 1 <= 0 ) chordRootForKey = chordRootForKey + 7;
			    
			    chordRoot = chordRootForKey + previousChord.getSopranoNote().getRootKey8() - 1;
			    //System.out.println("RootKey: " + previousChord.getSopranoNote().getRootKey8());
			    
			    if( chordRoot > 7 ) chordRoot = chordRoot % 7;		    
			    if( errors ) 
			        chordNotes = findDoubledNotes( chordRoot );
			    else 
			        chordNotes = findNotesOfChord( chordRoot );
			    //System.out.println("chordRoot: " + chordRoot);
			    for(int i = 0; i < 3; i++) {
			        if( chordNotes[i].intValue() == melodyNote.getNoteID8() )
			            validChord = true;
			    }	
		    }
		    /*
		    for(int i = 0; i < 3; i++) {
		        System.out.println(chordNotes[i]);
		    }	
		    */
		    retChord = chordGen.generateRootChord( chordNotes, melodyNote, 
		            previousChord );
		    if( retChord.getLoopExceeded() == true ) {
		        retChord = chordGen.generateInvertedChord( chordNotes, melodyNote,
		                previousChord);		        
		    }
		    
		    if( retChord == null ) 
		        errors = true;
		    else
		        errors = false;
		    //System.out.println(errors);
		    loops++;
	    } while( errors && loops < 100 ); 
	    
	    if(errors) 
	    {
	        System.out.println("findBestChord: error found");
	        System.out.println("type of chord was: " + chordType);
	    }
	    
	    return retChord;
	}
	
	/**
	 * Finds the first chord of the harmony, simply a I chord.
	 * 
	 * @return first chord of the harmony
	 */
	public Chord findFirstChord() {
	    Chord retChord = null;
	    Note firstNote = notes[0];
	    retChord = findChord( firstNote, 1, null );
	    return retChord;
	}
	
	/**
	 * Find the second through third to last chords.  
	 * 
	 * @param firstChord
	 * @return
	 */
	public Chord[] findBodyChords( Chord firstChord ) {
	    Chord[] bodyChords = new Chord[notes.length - 2];
	    Note currentNote;
	    Integer[] chordNotes = new Integer[3];
	    Chord previousChord = firstChord;
	    int limitCounter = 0;
	    
	    for( int i = 0; i < notes.length - 3; i++ ) {
	        currentNote = notes[i+1];
	        bodyChords[i] = findBestChord( currentNote, previousChord );  
	        
	        //If the last chord failed to generate successfully, replace the previous chord and try again
	        if(bodyChords[i] == null)
	        {
	            limitCounter++;
	            i = i - 2;
	            if(limitCounter > 50) i--;  //if replacing the previous chord causes 50 failures, go back 
	            							  //one more and replace that chord
	        }
	        previousChord = bodyChords[i];
	        System.out.println("chord number = " + (i+2));
	    }
	    
	    return bodyChords;
	}
	/**
	 * Utility function for findCadenceChord.  Determines the actual notes of the 
	 * chord in relation to the chord root.
	 * 
	 * @param chordRoot the root note of the chord
	 * @return an array containing the 3 notes of the chord
	 */
	public Integer[] findNotesOfChord( int chordRoot ) {
	    Integer[] chordNotes = new Integer[3];
	    chordNotes[0] = new Integer(chordRoot);
	    if( chordRoot + 2 > 7 ) 
	        chordNotes[1] = new Integer((chordRoot + 2) % 7);
	    else 
	        chordNotes[1] = new Integer(chordRoot + 2);
	    if( chordRoot + 4 > 7 ) 
	        chordNotes[2] = new Integer((chordRoot + 4) % 7);
	    else 
	        chordNotes[2] = new Integer(chordRoot + 4);
	    
	    return chordNotes;
	}
	
	public Integer[] findDoubledNotes( int chordRoot ) {
	    Integer[] chordNotes = new Integer[3];
	    double random = Math.random();
	    chordNotes[0] = new Integer(chordRoot);
	    if( random < 0.5 ) {
		    if( chordRoot + 2 > 7 ) 
		        chordNotes[1] = new Integer((chordRoot + 2) % 7);
		    else 
		        chordNotes[1] = new Integer(chordRoot + 2);
		    chordNotes[2] = new Integer(chordRoot);
	    } else {
	        if( chordRoot + 2 > 7 ) 
		        chordNotes[1] = new Integer((chordRoot + 2) % 7);
		    else 
		        chordNotes[1] = new Integer(chordRoot + 2);
		    chordNotes[2] = chordNotes[1];
	    }
	    
	    return chordNotes;
	}
	
	public static Integer[] findChordContainingNote( int note ) {
	    Integer[] chordNotes = new Integer[3];
	    chordNotes[0] = new Integer(note);
	    if( note - 2 <= 0 ) 
	        chordNotes[1] = new Integer(note - 2 + 7);
	    else 
	        chordNotes[1] = new Integer(note - 2);
	    if( note - 4 <= 0 ) 
	        chordNotes[2] = new Integer(note - 4 + 7);
	    else 
	        chordNotes[2] = new Integer(note - 4);
	    
	    return chordNotes;
	}
}
