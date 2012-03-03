/**
 * Contains the main() method for HarmoGen.  May be used to read in
 * a MIDI file for the expert system to harmonize, or allow for direct
 * entry of a note sequence.
 *
 * @author Andrew McCartney
 * @author Jason Morrison
 * @author Alec LaLonde
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import org.jfugue.Pattern;
import org.jfugue.Player;

public class HarmoGen {
	
	//Constants useful for outputting results.
	public final static String TEXT = "text";
	public final static String MIDI = "MIDI";
	public final static String PLAY = "Play";
	
	private final static String SOPRANO = "Soprano: ";
	private final static String ALTO = "Alto:    ";
	private final static String TENOR = "Tenor:   ";
	private final static String BASS = "Bass:    ";
	private final static String NEW_LINE = "\n";
	
	/*Constants for going from ints to string.
	private final static int C = 1;
	private final static int C_SHARP = 2;
	private final static int D = 3;
	private final static int D_SHARP = 4;
	private final static int E = 5;
	private final static int F = 6;
	private final static int F_SHARP = 7;
	private final static int G = 8;
	private final static int G_SHARP = 9;
	private final static int A = 10;
	private final static int A_SHARP = 11;
	private final static int B = 0;
	*/
	
	private final static String NOTES_AS_SHARPS[] =
		{ "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	
	private final static String NOTES_AS_FLATS[] =
		{ "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };

	//Other useful constants.
	private final static byte opcode = -112;
	private final static int octaveSize = 12;
	
	//Variables
	private static String notes = "";
	private static BufferedReader input;
	private static String[] harmonized;
	private static Player player;
	
	//GUI
	private static HarmoGenPanel view;
	private static HarmonyCompleteDialog harmonyView;
	
	//Methods
	
	/**
	 * Reads in a midi file, using the note on MidiEvent to determine
	 * what notes are being played.
	 * 
	 * @param fileName - The name of the file to read in.
	 */
	public static void readMidi(File file) throws Exception {
		Sequence sequence = MidiSystem.getSequence(file);
		Track[] tracks = sequence.getTracks();
		byte[] messages;
		int noteByte;
		int octave;
		String noteString;
		
		ArrayList noteBytes = new ArrayList();
		
		//7 is the magic number that steps past all the header/rhythm information
		for (int j = 7; j < (tracks[0].size() - 1); j++) {
            messages = tracks[0].get(j).getMessage().getMessage();
            if ((messages[0] & opcode) == opcode) {
                noteByte = messages[1];
                noteBytes.add(new Integer(noteByte));
            }
		}
		
		createNotesArray(noteBytes);
	}
	
	/**
	 * Reads a text file and returns its contents as the note string.
	 * 
	 * @param fileName - The name of the text file to be read.
	 */
	
	public static void readText(File file){
		try {
			input = new BufferedReader(new FileReader(file));
		}
		catch(FileNotFoundException e){
		    view.displayErrorMessage(e.getMessage());
		}
	
		try{
			String nextLine;
			while( (nextLine = input.readLine()) != null){
				notes = notes.concat(nextLine);
			}
		}
		catch(IOException e){
		    view.displayErrorMessage(e.getMessage());
		}
	}
	
	/**
	 * Read in an ABC file
	 * 
	 * @param file - the name of the ABC file to be read
	 */
	public static Key readABC(File file)
	{
	    String melodyString;
	    Key key = null;
	    
	    try {
			input = new BufferedReader(new FileReader(file));
		}
		catch(FileNotFoundException e){
		    view.displayErrorMessage(e.getMessage());
		}
	
		try{
			String nextLine;
			while( (nextLine = input.readLine()) != null){
			    if(nextLine.substring(0,2).equals("K:"))
			    {
			        key = readKey(nextLine.substring(2));
			    }
			    
				notes = notes.concat(nextLine);
			}
		}
		catch(IOException e){
		    view.displayErrorMessage(e.getMessage());
		}
		
		return key;
	}
	
	private static Key readKey(String keyString)
	{
	    String keyRoot;
	    String majOrMin; 
	    
        if(keyString.charAt(1) == '#' || keyString.charAt(1) == 'b')
        {
            keyRoot = keyString.substring(0,2);
            majOrMin = keyString.substring(2);
        }
        else
        {
            keyRoot = keyString.substring(0,1);
            majOrMin = keyString.substring(1);
        }
        
        if(majOrMin.equals("") || majOrMin.equals("Maj") || majOrMin.equals("maj"))
            majOrMin = Key.MAJOR;
        else if(majOrMin.equals("m") || majOrMin.equals("Min") || majOrMin.equals("min"))
            majOrMin = Key.MINOR;
        
        return new Key(keyRoot, majOrMin);
	}
	
	
	/**
	 * Saves the results to a text file.
	 * 
	 * @param results - The harmonized notes.
	 */
	private static void textOutput(String[] results){
	    String fileName = harmonyView.showFileNamePrompt(TEXT);
		try{ 
			FileWriter textFile = new FileWriter(new File(fileName));
			textFile.write(SOPRANO + results[0] + NEW_LINE, 0,
					SOPRANO.length() + results[0].length() + NEW_LINE.length());
			textFile.write(ALTO + results[1] + NEW_LINE, 0,
					ALTO.length() + results[1].length() + NEW_LINE.length());
			textFile.write(TENOR + results[2] + NEW_LINE, 0,
					TENOR.length() + results[2].length() + NEW_LINE.length());
			textFile.write(BASS + results[3] + NEW_LINE, 0,
					BASS.length() + results[3].length() + NEW_LINE.length());
			textFile.flush();
			textFile.close();
		}
		catch(Exception e)
		{
		    view.displayErrorMessage("Error while saving file: " + e.getMessage());
		}
	}
	
	/**
	 * Sets up a midi to do something useful with.
	 * 
	 * @param results - The harmonized notes.
	 * @return A Pattern representing the harmonized notes.
	 */	
	private static Pattern setupMidi(String[] results){
		Pattern pattern = new Pattern("");
		String[] soprano = results[0].split(" ");
		String[] alto = results[1].split(" ");
		String[] tenor = results[2].split(" ");
		String[] bass = results[3].split(" ");
		for(int i = 0; i < soprano.length; i++){
			pattern.add(new Pattern(soprano[i] + "+" + 
					alto[i] + "+" + tenor[i] + "+" +
					bass[i]));
		}
		return pattern;
	}
	
	public static String getNotes()
	{
	    return notes;
	}
	
	public static void setNotes(String newNotes)
	{
	    notes = newNotes;
	}
	
	/**
	 * Create the inference engine and go through the harmonization process, assuming the notes 
	 * variable contains a set of valid notes.
	 */
	public static void harmonize()
	{
	    //TODO: display progress bar
	    InferenceEngine engine = new InferenceEngine(notes, view.getKey());
	    //this starts the harmonization
				
		if (engine.checkForError()) 
		{
            view.displayErrorMessage(InferenceEngine.errorMessage());
        	} 
		else 
        	{
            harmonized = engine.printFourParts();
            
            harmonyView = new HarmonyCompleteDialog(view, harmonized);
            harmonyView.setVisible(true);  
        }
    }
	
	/**
	 * Perform the saving or playing based on what the user chose in the GUI.
	 * 
	 * @param choices an array of the two choices: 
	 * 		0 = save to text 
	 * 		1 = save to MIDI
	 * 
	 * 		-if a choice is not selected, the String will be an empty String.
	 */
	public static void performOutput(String[] choices)
	{
        if(choices[0].equals(TEXT))
        {
            HarmoGen.textOutput(harmonized);
        }
        
        if(choices[1].equals(MIDI))
        {
            //Save to MIDI file.
            Player player = new Player();
            try {
                String fileName = harmonyView.showFileNamePrompt(MIDI);
                player.save(HarmoGen.setupMidi(harmonized), fileName);
            }
            catch (Exception e) 
            {
               view.displayErrorMessage("Error while saving file: " + e.getMessage());
            }
        }
	}
	
	public static void play() 
	{
	    //Play as MIDI file.
        Player player2 = new Player();
        try {
            player2.play(HarmoGen.setupMidi(harmonized));
        } 
        catch (Exception e) 
        {
            view.displayErrorMessage("Error while playing file: " + e.getMessage());
        }
	}
	
	private static void createNotesArray(ArrayList noteBytes)
	{
	    int octave;
	    String noteString;
	    int noteByte;
	    
	    String[] NOTE_AS_STRING;
	    
	    if(view.getKey().getSharpOrFlatKey() == Note.FLAT)
	        NOTE_AS_STRING = NOTES_AS_FLATS;
	    else
	        NOTE_AS_STRING = NOTES_AS_SHARPS;
	    
	    for(int i = 0; i < noteBytes.size(); i++)
	    {
	        noteByte = ((Integer)noteBytes.get(i)).intValue();
	        octave = noteByte / octaveSize;
	        //System.out.println("Note " + i + ": " + noteByte);
	        noteString = NOTE_AS_STRING[noteByte % octaveSize];
	        notes = notes.concat(noteString + octave + " ");
	    }
	}
	
	/**
	 * Display the GUI.
	 * 
	 * @param args not used.
	 */
	public static void main( String[] args ) 
	{
	    view = new HarmoGenPanel("HarmoGen: A four-part harmony generator");
	    view.setVisible(true);
	}
}