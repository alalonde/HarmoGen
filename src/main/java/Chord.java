/**
 * Chord.java
 * 
 * @author Alec LaLonde
 * 
 *         Represents a chord in the Expert System.
 */
public class Chord {

	private int chordCount;
	private String chordString;

	private boolean loopExceeded;
	private Note bass;
	private Note tenor;
	private Note alto;
	private Note soprano;

	public Chord(Note bassNote, Note tenorNote, Note altoNote, Note sopranoNote) {
		bass = bassNote;
		tenor = tenorNote;
		alto = altoNote;
		soprano = sopranoNote;
	}

	public int getBassNoteNum() {
		return bass.getHarmonyNote8();
	}

	public int getTenorNoteNum() {
		return tenor.getHarmonyNote8();
	}

	public int getAltoNoteNum() {
		return alto.getHarmonyNote8();
	}

	public int getSopranoNoteNum() {
		return soprano.getHarmonyNote8();
	}

	public Note getBassNote() {
		return bass;
	}

	public Note getTenorNote() {
		return tenor;
	}

	public Note getAltoNote() {
		return alto;
	}

	public Note getSopranoNote() {
		return soprano;
	}

	public void setLoopExceeded(boolean loop) {
		loopExceeded = loop;
	}

	public boolean getLoopExceeded() {
		return loopExceeded;
	}

	public String printNotes() {
		return bass.getNoteAndOctave() + tenor.getNoteAndOctave() + alto.getNoteAndOctave()
				+ soprano.getNoteAndOctave();
	}
}
