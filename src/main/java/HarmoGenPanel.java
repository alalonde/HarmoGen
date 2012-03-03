import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

/**
 * @author Alec LaLonde
 *
 * The main frame for the HarmoGen GUI.
 */
public class HarmoGenPanel extends JFrame implements ActionListener 
{
    public static final int INPUT_MANUAL = 0;
    public static final int INPUT_MIDI = 1;
    public static final int INPUT_TEXT = 2;
    
    public static final int BORDER_SPACING = 10;
    public static final String NEW_LINE = "\n";
    
    private Container content;
    private JPanel framePanel;
    private JPanel logoPanel;
    private JPanel mainPanel;
    private JPanel displayPanel;
    private JPanel harmonizePanel;
    private JPanel selectKeyPanel;
    private JPanel importPanel;
 
    private String manualDirections = "Enter notes as a note followed by its octave." + NEW_LINE +
		"Examples:" + NEW_LINE + "  D5 F#6 E6 D6" + NEW_LINE + "  Eb5 F5 Bb5 Eb5" + NEW_LINE + 
		"Melody must be at least four notes and be within a soprano's range (D5 - G6)." + NEW_LINE + 
		"Finally, select the melody's key below.";
    private String importMidiDirections = "Choose a MIDI file to import: ";
    private String importTextDirections = "Choose an ABC file to import: ";
    
    private ImageIcon logo;
    private JButton btnHarmonize;
    private JButton btnImportMidi;
    private JButton btnImportText;
    private JRadioButton rbManual;
    private JRadioButton rbImportMidi;
    private JRadioButton rbImportText;
    private JLabel logoLabel;
    private JTextArea lblManualDirections;
    private JLabel lblImportMidiDirections;
    private JLabel lblImportTextDirections;
    //private JLabel lblSelectedKey;
    private JLabel lblSelectAKey;
    //private JLabel lblKey;
    private JLabel manual;
    private JLabel importFile;
    
    private JComboBox cbKeyType;
    private JComboBox cbKeyList;
    
    private JScrollPane spNoteArea;
    private JTextArea taNoteArea;
    
    private JFileChooser chooser;
    
    private Key key;						//the current selected key
    
    public HarmoGenPanel(String name) 
    {
        super(name);
        content = this.getContentPane();
  
        createComponents();
        layoutComponents();   
        
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    private void createComponents() 
    {        
        lblManualDirections = new JTextArea(manualDirections);
        lblManualDirections.setOpaque(false);
        lblManualDirections.setEditable(false);
        lblImportMidiDirections = new JLabel(importMidiDirections);
        lblImportTextDirections = new JLabel(importTextDirections);
        lblSelectAKey = new JLabel("Select a key:");
        
        btnHarmonize = new JButton("Harmonize...");
        btnImportMidi = new JButton("Import MIDI...");
        btnImportText = new JButton("Import ABC file...");
        
        cbKeyType = new JComboBox();
        cbKeyType.addItem(Key.BLANK);
        cbKeyType.addItem(Key.MAJOR);
        cbKeyType.addItem(Key.MINOR);
        
        cbKeyList = new JComboBox();
        
        rbManual = new JRadioButton("Manual Input");
        rbImportMidi = new JRadioButton("Import MIDI");
        rbImportText = new JRadioButton("Import ABC file");
        
        ButtonGroup group = new ButtonGroup();
        group.add(rbManual);
        group.add(rbImportMidi);
        group.add(rbImportText);
        rbManual.setSelected(true);
        
        rbManual.addActionListener(this);
        rbImportMidi.addActionListener(this);
        rbImportText.addActionListener(this);
        btnImportMidi.addActionListener(this);
        btnImportText.addActionListener(this);
        btnHarmonize.addActionListener(this);
        cbKeyType.addActionListener(this);
        cbKeyList.addActionListener(this);
        
        taNoteArea = new JTextArea(4, 30);
        spNoteArea = new JScrollPane(taNoteArea);
        spNoteArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spNoteArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        logo = new ImageIcon("src/main/resources/HarmoGenLogo.jpg");
        logoLabel = new JLabel(logo);
        
        framePanel = new JPanel(new GridBagLayout());
        framePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        content.add(framePanel);

        chooser = new JFileChooser();   
    }
    
    private void layoutComponents() 
    {
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridy = 0;
        gbc.insets = new Insets(BORDER_SPACING, BORDER_SPACING, 0, BORDER_SPACING);
        framePanel.add(createLogoPanel(), gbc);
        
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(BORDER_SPACING, BORDER_SPACING, BORDER_SPACING, BORDER_SPACING);
        framePanel.add(createMainPanel(INPUT_MANUAL), gbc);
        
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, BORDER_SPACING, BORDER_SPACING, BORDER_SPACING);
        framePanel.add(createBottomPanel(), gbc);
    }
    
    private JPanel createLogoPanel() 
    {
        logoPanel = new JPanel(new FlowLayout());
        logoPanel.add(logoLabel);
        return logoPanel;
    }
    
    private JPanel createMainPanel(int inputType) 
    {        
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(createImportPanel(), gbc);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(BORDER_SPACING, 0, 0, 0);
        mainPanel.add(createDisplayPanel(inputType), gbc);
        return mainPanel;
    }
    
    private JPanel createImportPanel() 
    {
        importPanel = new JPanel(new FlowLayout());
        importPanel.add(rbManual);
        importPanel.add(rbImportMidi);
        importPanel.add(rbImportText);
        return importPanel;
    }
    
    private JPanel createDisplayPanel(int inputMode) 
    {
        displayPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, BORDER_SPACING, BORDER_SPACING, BORDER_SPACING);
        
        if(inputMode == INPUT_MANUAL)
        {
            displayPanel.add(lblManualDirections, gbc);  
        }
        else if(inputMode == INPUT_MIDI)
        {
            displayPanel.add(lblImportMidiDirections, gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.insets = new Insets(0, BORDER_SPACING, 0, 0);
            displayPanel.add(btnImportMidi, gbc);
        }
        else if(inputMode == INPUT_TEXT)
        {
            displayPanel.add(lblImportTextDirections, gbc);
            
            gbc.gridx = 1;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.insets = new Insets(0, BORDER_SPACING, 0, 0);
            displayPanel.add(btnImportText, gbc);
        }
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(BORDER_SPACING, BORDER_SPACING, BORDER_SPACING, BORDER_SPACING);
        displayPanel.add(spNoteArea, gbc);
        
        return displayPanel;
    }
    
    private JPanel createBottomPanel() 
    {
        harmonizePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
       
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;        
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, BORDER_SPACING);
        harmonizePanel.add(lblSelectAKey, gbc);
        
        gbc.insets = new Insets(0, 0, 0, 0);
        harmonizePanel.add(cbKeyList, gbc);
        harmonizePanel.add(cbKeyType, gbc);
        
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        harmonizePanel.add(new JLabel(), gbc);
        
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; 
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        harmonizePanel.add(btnHarmonize, gbc);
        
        cbKeyList.setEnabled(false);
        
        return harmonizePanel;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        JComponent selected = (JComponent)e.getSource();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(BORDER_SPACING, 0, 0, 0);
        
        if(selected == rbManual)
        {        
            mainPanel.remove(displayPanel);
            mainPanel.add(createDisplayPanel(INPUT_MANUAL), gbc);
            taNoteArea.setText("");
            HarmoGen.setNotes("");
            validate();
        }
        else if(selected == rbImportMidi)
        {
            mainPanel.remove(displayPanel);
            mainPanel.add(createDisplayPanel(INPUT_MIDI), gbc);
            taNoteArea.setText("");
            HarmoGen.setNotes("");
            validate();
        }
        else if(selected == rbImportText)
        {
            mainPanel.remove(displayPanel);
            mainPanel.add(createDisplayPanel(INPUT_TEXT), gbc);
            taNoteArea.setText("");
            HarmoGen.setNotes("");
            validate();
        }
        else if(selected == btnHarmonize)
        {
            if(rbManual.isSelected())
            {
                HarmoGen.setNotes(taNoteArea.getText());
            }           
            HarmoGen.harmonize();
        }
        else if(selected == btnImportMidi || selected == btnImportText)
        {
            HarmoGen.setNotes("");
            
            if(selected == btnImportMidi && key == null)
            {
                displayErrorMessage("Please enter a key before importing a MIDI file");
            }
            else
            {
                displayFileChooser();
                taNoteArea.setText(HarmoGen.getNotes());
                
            }
        }
        else if(selected == cbKeyType)
        {
            fillComboBox((String)cbKeyType.getSelectedItem());
            cbKeyList.setEnabled(true);
        }
        else if(selected == cbKeyList)
        {
            //create the key now
            key = new Key((String)cbKeyList.getSelectedItem(), 
                    (String)cbKeyType.getSelectedItem());
        }
    }
    
    private void clearKey()
    {
        cbKeyType.setSelectedIndex(0);
        key = null;
        cbKeyList.setEnabled(false);
    }
    
    /**
     * Populate the second combo box with either all the minor or major keys.
     * 
     * @param keyType major or minor
     */
    private void fillComboBox(String keyType)
    {
        if(keyType.equals(Key.MAJOR))
        {
            cbKeyList.setModel(new DefaultComboBoxModel(Key.MAJOR_KEYS));
        }
        else if(keyType.equals(Key.MINOR))
        {
            cbKeyList.setModel(new DefaultComboBoxModel(Key.MINOR_KEYS));
        }
        else //clear the key
        {
            cbKeyList.setModel(new DefaultComboBoxModel());
            clearKey();
            //cbKeyList.validate();
        }
        //harmonizePanel.validate();
    }
    
    /**
     * Displays a JFileChooser, a GUI which can navigate the user's file system and select either a 
     * text or midi file.  An error is displayed if the chosen file is invalid.
     */
    private void displayFileChooser() 
    {
        int returnVal = chooser.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String fileName = selectedFile.getName();
            
            try{
                if(fileName.endsWith("mid"))
                    HarmoGen.readMidi(selectedFile);
                else if(fileName.endsWith("abc"))
                    key = HarmoGen.readABC(selectedFile);
                else
                    HarmoGen.readText(selectedFile);
            }
            catch(InvalidMidiDataException imde)
            {
                displayErrorMessage(fileName + " is an invalid Midi file.");
            }
            catch (FileNotFoundException fnfe) //tell them if the filename's invalid
            { 
                displayErrorMessage(fileName + " does not contain a valid set of notes.");
            } 
            catch (Exception e) 
            {
                displayErrorMessage("Unknown Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Display a simple error dialog.
     * 
     * @param errorText
     */
    public void displayErrorMessage(String errorText)
    {
        JOptionPane.showMessageDialog(this, errorText, "HarmoGen error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Return the currently selected key.
     * 
     * @return
     */
    public Key getKey()
    {
        return key;
    }
}
