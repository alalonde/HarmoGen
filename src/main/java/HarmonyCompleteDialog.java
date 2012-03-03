import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

/**
 * This is the dialog that gets displayed after a successful harmony generation.  It displays the harmony
 * and prompts the user for any additional action.
 *
 * @author Alec LaLonde
 */
public class HarmonyCompleteDialog extends JDialog implements ActionListener 
{
    private Container content;
    
    private JPanel dialogPanel;
    private JPanel harmonyPanel;
    private JPanel choicesPanel;
    private JPanel checkBoxesPanel;
    
    private JLabel lblStatus;
    private JLabel lblSoprano;
    private JLabel lblAlto;
    private JLabel lblTenor;
    private JLabel lblBass;
    private JLabel lblPrompt;
    
    private JScrollPane spHarmony;
    private JTextArea tfHarmony;
    
    private JCheckBox xbSaveText;
    private JCheckBox xbSaveMidi;
    private JButton btnPlay;
    private JButton btnSaveClose;
    
    private String[] arrNotes;
    
    /**
     * Constructor.  Completed harmony is passed in, along with the parent frame.
     * 
     * @param parent the parent frame 
     * @param notes	an array of four note strings, the generated harmony
     */
    public HarmonyCompleteDialog(JFrame parent, String[] notes)
    {
        super(parent, "Generated Harmony", true);
        content = this.getContentPane();
        
        arrNotes = notes;
        
        createComponents();
        layoutComponents();   
        
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(800, 285);
        //this.pack();
        this.setLocationRelativeTo(parent);
    }
    
    /**
     * Initialize the GUI components for this dialog.
     *
     */
    private void createComponents()
    {
        lblStatus = new JLabel("Harmonization complete.");
        lblSoprano = new JLabel("Soprano: ");
        lblAlto = new JLabel("Alto: ");
        lblTenor = new JLabel("Tenor: ");
        lblBass = new JLabel("Bass: ");
        lblPrompt = new JLabel("What would you like to do with the generated harmony?");
        
        tfHarmony = new JTextArea();
        tfHarmony.setText(arrNotes[0] + HarmoGenPanel.NEW_LINE +
                			arrNotes[1] + HarmoGenPanel.NEW_LINE +
                			arrNotes[2] + HarmoGenPanel.NEW_LINE +
                			arrNotes[3]);
        spHarmony = new JScrollPane(tfHarmony);
        spHarmony.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spHarmony.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        Dimension size = new Dimension(800, 100);
        spHarmony.setMaximumSize(size);
        //spHarmony.setPreferredSize(size);
        
        xbSaveText = new JCheckBox("Save as text");
        xbSaveMidi = new JCheckBox("Save as MIDI");
        btnPlay = new JButton("Play");
        btnPlay.addActionListener(this);
        btnSaveClose = new JButton("Save and Close");
        btnSaveClose.addActionListener(this);
        
        dialogPanel = new JPanel(new GridBagLayout());
        dialogPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        content.add(dialogPanel, BorderLayout.CENTER);
    }
    
    /**
     * Lay out the main panel for this dialog.
     *
     */
    private void layoutComponents()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        dialogPanel.add(createHarmonyPanel(), gbc);
        
        gbc.gridy = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(HarmoGenPanel.BORDER_SPACING, HarmoGenPanel.BORDER_SPACING, 
                HarmoGenPanel.BORDER_SPACING, HarmoGenPanel.BORDER_SPACING);
        dialogPanel.add(createChoicesPanel(), gbc);
    }
    
    private JPanel createHarmonyPanel() 
    {
        harmonyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(HarmoGenPanel.BORDER_SPACING, HarmoGenPanel.BORDER_SPACING, 0, 
                HarmoGenPanel.BORDER_SPACING);
        harmonyPanel.add(lblStatus, gbc);
        
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(HarmoGenPanel.BORDER_SPACING, HarmoGenPanel.BORDER_SPACING, 0, 0);
        harmonyPanel.add(lblSoprano, gbc);

        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, HarmoGenPanel.BORDER_SPACING, 0, 0);
        harmonyPanel.add(lblAlto, gbc);
        
        gbc.gridy = 3;
        harmonyPanel.add(lblTenor, gbc);
        
        gbc.gridy = 4;
        harmonyPanel.add(lblBass, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(HarmoGenPanel.BORDER_SPACING, 0, 0, HarmoGenPanel.BORDER_SPACING);
        harmonyPanel.add(spHarmony, gbc);
        
        return harmonyPanel;
    }
    
    private JPanel createChoicesPanel() 
    {
        choicesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        choicesPanel.add(lblPrompt, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridy = 1;
        gbc.insets = new Insets(HarmoGenPanel.BORDER_SPACING, HarmoGenPanel.BORDER_SPACING, 
                HarmoGenPanel.BORDER_SPACING, HarmoGenPanel.BORDER_SPACING);
        choicesPanel.add(createCheckBoxesPanel(), gbc);
        
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        choicesPanel.add(btnPlay, gbc);
        
        return choicesPanel;
    }
    
    private JPanel createCheckBoxesPanel() 
    {
        checkBoxesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        checkBoxesPanel.add(xbSaveText, gbc);
        
        gbc.gridy = 1;
        checkBoxesPanel.add(xbSaveMidi, gbc);
        
        gbc.gridy = 2;
        checkBoxesPanel.add(btnSaveClose, gbc);
        
        return checkBoxesPanel;
    }
    
    /**
     * Display a dialog prompting for a file name.
     * 
     * @param saveType either a midi or text file save
     * @return the name picked by the user with an extension appended
     */
    public String showFileNamePrompt(String saveType)
    {
        String input = (String)JOptionPane.showInputDialog(this, 
                "Enter a name for the saved " + saveType + " file", 
                "File name prompt", JOptionPane.QUESTION_MESSAGE, null, null, "");
        
        if(input.equals(""))
            showFileNamePrompt(saveType);
        
        if(saveType == HarmoGen.MIDI)
            input = input + ".mid";
        else if(saveType == HarmoGen.TEXT)
            input = input + ".txt";
            
        return input;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        
        if(event.getSource() == btnSaveClose)
        {
            String[] choices = {"", ""};
            
            if(xbSaveText.isSelected())
                choices[0] = HarmoGen.TEXT;
            if(xbSaveMidi.isSelected())
                choices[1] = HarmoGen.MIDI;
            
            HarmoGen.performOutput(choices);
            
            this.dispose();
        }
        else if(event.getSource() == btnPlay)
        {
            HarmoGen.play();
        }
        
    }
}
