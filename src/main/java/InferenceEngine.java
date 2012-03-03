/**
 * InferenceEngine.java
 * 
 * @author Alec LaLonde
 *
 * The inference engine for the expert system.  Calls methods in the knowledge base
 * almost exclusively, and deals with I/O with the InitSystem class.
 */
public class InferenceEngine {
	
	private Note[] notes;
	private KnowledgeDB knowledge;
	private boolean error = false;
	private Chord firstChord;
	private Chord[] cadenceChords;
	private Chord[] allChords;
	private Chord[] bodyChords;
	private static String errorMsg = "";
	
	/**
	 * Constructor, called by InitSystem.
	 * 
	 * @param myNotes a string of the user's inputted notes, separated by spaces.
	 */
	public InferenceEngine( String myNotes, Key key ) {
	    String[] notesAsStrings = myNotes.split(" ");
		notes = new Note[notesAsStrings.length];
		
		if(key != null)
		{
			try {
			    for( int i = 0; i < notesAsStrings.length; i++ ) {
			        if("".equals(notesAsStrings[i]) )
			            throw new NumberFormatException();
			        else
			            notes[i] = new Note( notesAsStrings[i], key );
				}
			    
			    allChords = new Chord[notesAsStrings.length];
				bodyChords = new Chord[allChords.length - 3];
				knowledge = new KnowledgeDB( notes, key );
				
				error = knowledge.checkInput();
			}
			catch(NumberFormatException e)
			{
			    error = true;
			    setError("Invalid note syntax. \nPlease format notes according to example.");
			}	
		}
		else
		{
		    error = true;
		    setError("Please enter a key.");
		}
		    
		if( !error ) {
		    harmonize();	    
		    printFourParts();	    
		}
	}
	
	public boolean checkForError() {
	    return error;
	}
	
	public static void setError( String msg ) {
	    errorMsg = msg;
	}
	
	public static String errorMessage() {
	    return errorMsg;
	}
	
	public void harmonize() {
	    cadenceChords = knowledge.findCadence();
	    firstChord = knowledge.findFirstChord();
	    allChords[0] = firstChord;
	    bodyChords = knowledge.findBodyChords( firstChord );
	    for( int i = 0; i < bodyChords.length; i++ ) {
	        allChords[i + 1] = bodyChords[i];
	    }
	    allChords[notes.length - 2] = cadenceChords[0];
	    allChords[notes.length - 1] = cadenceChords[1];
	}
	
	public String[] printFourParts() {
	    String soprano = "";
	    String alto = "";
	    String tenor = "";
	    String bass = "";
	    String[] results = new String[4];
	    for( int i = 0; i < allChords.length; i++) {
	        soprano = soprano + allChords[i].getSopranoNote().getNoteAndOctave() + " ";
	        alto = alto + allChords[i].getAltoNote().getNoteAndOctave() + " ";
	        tenor = tenor  + allChords[i].getTenorNote().getNoteAndOctave() + " ";
	        bass = bass + allChords[i].getBassNote().getNoteAndOctave() + " ";
	    }
	    results[0] = soprano;
	    results[1] = alto;
	    results[2] = tenor;
	    results[3] = bass;	    
	    return results;
	}
}
