/**
 * Note.java
 * 
 * <p><br>Class representing a note object in the Expert System.  Contains several 
 * parameters for the inputted note for several different uses by the ES.
 * 
 * <br><p>Numeric representations of this note include:
 * <p>
 * <li>note number 		(between 1 and 120, 60 being middle C)
 * <li>noteID12			the note's ID on a 1-12 range, where 1 is a C.  e.g. 4 = D#/Eb	
 * <li>noteID8			same idea as noteID except on a 1-8 range
 * <li>scaleID12			the note's ID in relation to key, still on a 1-12 scale	
 * <li>scaleID8			What note is this in the scale for the key?  (1-8)  e.g. 5 = dominant
 * <li>rootKey12			the root of the key we're in (on the same 1-12 basis as noteID)
 * <li>rootKey8			same as rootKey12, except on a 1-8 scale
 * <br>
 *  
 * @author Alec LaLonde
 */
public class Note 
{
	public final static int SHARP = 1;
	public final static int FLAT = -1;
	
	private int octave;				//the note's octave: middle C is oct. 5
	
	private Key key;					//the key this note is part of
	
	private String noteSymbol;		//note minus the octave  e.g. Cb
	
	private String noteAndOctave;		//the inputted note   e.g. A#5
	
	private char noteTag = 'n';		//b or #  (flat or sharp)
	
	private int noteNumberMidi = 60;		//the numeric representation of the note,
									//defaults to middle C (60)
	private int noteID12;				//the note's ID on a 1-12 range,
									//where 1 is a C.  e.g. 4 = D#/Eb
	private int noteID8;				//same idea as noteID except on a 1-8 range
	
	private int scaleID12;			//the note's ID in relation to key.
									//still on a 1-12 scale
	private int scaleID8;				//What note is this in the scale for the
									//key?  (1-8)  e.g. 5 = dominant
	private String scaleType;			//Is this note in a major or minor key?
	
	private int rootKey12;			//the root of the key we're in
									//(on the same 1-12 basis as noteID)
	private int rootKey8;				//same as rootKey12, except on a 1-8 scale
	
	private int sharpOrFlatKey = 0;   //indicates if the key has sharps or flats
	
	private int noteNumberHarmoGen;    //harmoGen's numeric note representation, octave 
									//accounted for. (48-71)
	
	/**
	 * Constructor, called by the inference engine.  Initializes the note's
	 * many characteristics.
	 * 
	 * @param myNote the individual notestring
	 */
	public Note( String myNote, Key thisKey ) throws NumberFormatException 
	{
	    String octaveChar;  						//temporary storage
	    
		if( myNote.length() > 2) {				//indicates a sharp or flat
			octaveChar = myNote.substring(2);
			noteSymbol = myNote.substring(0,2);
			noteTag = myNote.charAt(1);	
			noteAndOctave = myNote;
		} else {
			octaveChar = myNote.substring(1);
			noteSymbol = myNote.substring(0,1);
			noteAndOctave = myNote.charAt(0) + "-" + myNote.charAt(1);
		}
		octave = Integer.parseInt(octaveChar);
		
		this.key = thisKey;
		
		sharpOrFlatKey = key.getSharpOrFlatKey();
		scaleType = key.getKeyType();
		
		noteNumberMidi = Utilities.findNoteNumber( noteSymbol, octave, noteTag );
		noteID12 = noteNumberMidi % 12 + 1;
		noteID8 = Utilities.convert12to8(noteID12, sharpOrFlatKey );	
		rootKey12 = key.getRootOfKey12();
		rootKey8 = Utilities.convert12to8(rootKey12, sharpOrFlatKey);
		scaleID12 = Utilities.findScaleID12(rootKey12, noteID12);		
		scaleID8 = Utilities.convert12to8( scaleID12, sharpOrFlatKey );
	}
	
	/**
	 * Alternate constructor used by the harmonizer.  
	 * 
	 * @param myNoteID8    noteNumberMidi (1-128 range)
	 * @param myKey        if the key contains sharps or flats  
	 * @param key8         the root of the key
	 */
	public Note( int myNoteID8, Key currentKey ) 
	{
	    scaleType = currentKey.getKeyType();
	    
	    noteNumberHarmoGen = myNoteID8;
	    noteID8 = (myNoteID8 - 45) % 7;
	    if( noteID8 == 0)  noteID8 = 7;

	    sharpOrFlatKey = currentKey.getSharpOrFlatKey();
	    rootKey12 = currentKey.getRootOfKey12();
	    rootKey8 = Utilities.convert12to8(rootKey12, sharpOrFlatKey);
	    
	    scaleID8 = noteID8 - rootKey8 + 1;
	    scaleID12 = Utilities.findScaleID12(rootKey12, noteID12);
	    
	    if( scaleID8 <= 0 ) scaleID8 = scaleID8 + 7;
	    if( scaleID8 >= 8 ) scaleID8 = scaleID8 - 7;

	    noteID12 =  currentKey.getNote12(scaleID8);
	    octave = Utilities.findOctave(noteNumberHarmoGen);
	    noteNumberMidi = Utilities.findNoteNumber( octave, noteID12 );
	    noteSymbol = Utilities.createNoteSymbol(noteID12, sharpOrFlatKey);
	    noteAndOctave = noteSymbol + octave;
	}
	
	
	/**
	 * Accessor for octave.
	 * 
	 * @return the note's octave   e.g. 6
	 */
	public int getOctave() {
		return octave;
	}
	
	/**
	 * Accessor for the note's symbol.
	 * 
	 * @return the note's symbol   e.g. Cb
	 */
	public String getNoteSymbol() {
		return noteSymbol;
	}
	
	/**
	 * Accessor for the note's numberic representation.
	 * 
	 * @return the note's number   e.g. 61
	 */
	public int getNoteNumber() {
		return noteNumberMidi;
	}
	
	/**
	 * returns the note's tag (sharp or flat)
	 * 
	 * @return the note's tag
	 */
	public char getNoteTag() {
	    return noteTag;
	}
	
	/**
	 * Accessor for the note's 1-12 ID.
	 * 
	 * @return the note's ID   e.g. 4 = D#/Eb
	 */
	public int getNoteID12() {
		return noteID12;
	}
		
	/**
	 * Returns the note's 1-12 ID in relation to the tonic.
	 * 
	 * @return the note's 1-12 ID
	 */
	public int getScaleID12() {
	    return scaleID12;
	}
	
	/**
	 * Accessor for note's identity and octave.
	 * 
	 * @return the note's identity and octave.    e.g. C#5
	 */
	public String getNoteAndOctave() {
		return noteAndOctave;
	}
	
	
	
	/**
	 * Mutator for scaleID
	 * 
	 * @param scaleID the note's number relative to the key
	 */
	public void setScaleID8( int scaleID ) {
	    scaleID8 = scaleID;
	}
	
	/**
	 * Returns the note's number relative to the key
	 * 
	 * @return the note's number relative to the key
	 */
	public int getScaleID8() {
	    return scaleID8;
	}
	
	/**
	 * Sets if the key is major or minor
	 * 
	 * @param scaleType the type of key
	 */
	public void setScaleType( String scaleType ) {
	    this.scaleType = scaleType;
	}
	
	/**
	 * returns the scale's type
	 * 
	 * @return the scale's type
	 */
	public String getScaleType() {
	    return scaleType;
	}
	
	/**
	 * Sets the root of the key in 1-12 representation
	 *
	 */
	public void setRootKey12() {
	    rootKey12 = noteID12 - scaleID12 + 1;
	    if( noteID12 - scaleID12 < 0 ) rootKey12 = rootKey12 + 12;
	}
	
	/**
	 * Returns the root of the key (1-12)
	 * 
	 * @return the root of the key
	 */
	public int getRootKey12() {
	    return rootKey12;
	}
	
	/**
	 * Sets the root of the key in a 1-7 representation
	 * 
	 * @param rootKey12 the given root in 1-12
	 */
	public void setRootKey8(int rootKey12) {
	    rootKey8 = Utilities.convert12to8( rootKey12, sharpOrFlatKey );
	}
	
	/**
	 * Returns the root of the key in a 1-7 representation
	 * 
	 * @return the root of the key
	 */
	public int getRootKey8() {
	    return rootKey8;
	}
	
	/**
	 * Sets the note's 1-7 number
	 * 
	 * @param note the note's 1-7 number
	 */
	public void setNoteID8( int note ) {
	    noteID8 = note;
	}
	
	/**
	 * returns the note's 1-7 number
	 * 
	 * @return the note's 1-7 number
	 */
	public int getNoteID8() {
	    return noteID8;
	}
	
	/**
	 * Returns the 1-7 representation of the number including octave
	 *  
	 * @return the harmonic number
	 */
	public int getHarmonyNote8() {
	    return noteNumberHarmoGen;
	}
	
	/**
	 * Sets the harmonic 1-7 representation of the number
	 * 
	 * @param note the harmonic number
	 */
	public void setHarmonyNote8( int note ) {
	    noteNumberHarmoGen = note;
	}
	
	/**
	 * Indicates a key with sharps or flats
	 * 
	 * @param sharpOrFlat a number representing sharps or flats
	 */
	public void setSharpOrFlatKey( int sharpOrFlat ) {
	    sharpOrFlatKey = sharpOrFlat;
	}
	
	/**
	 * Returns a number indicating if the key has sharps or flats
	 * 
	 * @return 1 if sharp, -1 if flat
	 */
	public int getSharpOrFlatKey() {
	    return sharpOrFlatKey;
	}
	
	public Key getKey()
	{
	    return key;
	}
} //Note.java
