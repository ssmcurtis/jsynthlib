package core;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The panel that configures the MIDI layer. Taken out of PrefsDialog.
 * @author Joe Emenaker
 * @version $Id$
 */
class MidiConfigPanel extends ConfigPanel {
    {
	panelName = "MIDI";
	nameSpace = "midi";
    }

    /** CheckBox for MIDI */
    private JCheckBox cbxEnMidi;
    /** ComboBox for MIDI Out port. */
    private JComboBox cbOut;
    /** ComboBox for MIDI In port. */
    private JComboBox cbIn;
    /** ComboBox for MIDI In port for Master Controller. */
    private JComboBox cbMC;
    /** CheckBox for Master Controller. */
    private JCheckBox cbxEnMC;
    /** button for loop-back test. */
    private JButton testButton;

    MidiConfigPanel(PrefsDialog parent) {
	super(parent);

	setLayout(new ColumnLayout());

	cbxEnMidi = new JCheckBox("Enable MIDI Interface");
	add(cbxEnMidi);

	// panel for other settings
	JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new ColumnLayout());

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	JPanel cbPanel = new JPanel(gridbag);
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.ipadx = 1;
	gbc.anchor = GridBagConstraints.WEST;


	// make a space
	gbc.gridx = 0; gbc.gridy = 2; gbc.gridheight = 1; gbc.gridwidth = 1;
	JLabel l3 = new JLabel(" ");
	gridbag.setConstraints(l3, gbc);
        cbPanel.add(l3);
	// Output Port/Input Port selection
	/*
	gbc.gridx = 0; gbc.gridy = 3; gbc.gridheight = 1; gbc.gridwidth = 3;
	//gbc.gridwidth=gbc.REMAINDER;
        JLabel cbLabel = new JLabel("Run Startup Initialization on MIDI Ports:");
	gridbag.setConstraints(cbLabel, gbc);
	cbPanel.add(cbLabel);
	*/
	gbc.gridx = 0; gbc.gridy = 4; gbc.gridheight = 1; gbc.gridwidth = 1;
	JLabel cbOutLabel = new JLabel("Output Port:");
	gridbag.setConstraints(cbOutLabel, gbc);
	cbPanel.add(cbOutLabel);
	cbOut = new JComboBox(MidiUtil.getOutputMidiDeviceInfo());
	gbc.gridx = 1; gbc.gridy = 4; gbc.gridheight = 1;
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cbOut, gbc);
        cbPanel.add(cbOut);

	gbc.gridx = 0; gbc.gridy = 5; gbc.gridheight = 1; gbc.gridwidth = 1;
	JLabel cbInLabel = new JLabel("Input Port:");
	gridbag.setConstraints(cbInLabel, gbc);
        cbPanel.add(cbInLabel);
	cbIn = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	gbc.gridx = 1; gbc.gridy = 5; gbc.gridheight = 1;
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cbIn, gbc);
        cbPanel.add(cbIn);

	// master controller selection
	/*
	gbc.gridx = 0; gbc.gridy = 6; gbc.gridheight = 1; // gbc.gridwidth=3;
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	JLabel l1 = new JLabel("Receive from Master Controller on MIDI Port:");
	gridbag.setConstraints(l1, gbc);
        cbPanel.add(l1);
	*/
	cbxEnMC = new JCheckBox("Enable Master Controller Input Port");
	gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.gridheight = 1;
	cbPanel.add(cbxEnMC, gbc);

	gbc.gridx = 0; gbc.gridy = 8; gbc.gridheight = 1; gbc.gridwidth = 1;
	JLabel cbMInLabel = new JLabel("Master Input Port:");
	cbMInLabel.setToolTipText("MIDI notes from this port are echoed to the output MIDI port.");
	gridbag.setConstraints(cbMInLabel, gbc);
        cbPanel.add(cbMInLabel);
	gbc.gridx = 1; gbc.gridy = 8; gbc.gridheight = 1;
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	cbMC = new JComboBox(MidiUtil.getInputMidiDeviceInfo());
	gridbag.setConstraints(cbMC, gbc);
        cbPanel.add(cbMC);

	// make a space
	gbc.gridx = 0; gbc.gridy = 9; gbc.gridheight = 1; gbc.gridwidth = 1;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	JLabel l0 = new JLabel(" ");
	gridbag.setConstraints(l0, gbc);
        cbPanel.add(l0);

	// MIDI loopback test
	gbc.gridx = 1; gbc.gridy = 10; gbc.gridheight = 1; gbc.gridwidth = 1;
	gbc.fill = GridBagConstraints.NONE;
        testButton = new JButton("MIDI Loopback Test...");
	gridbag.setConstraints(testButton, gbc);
	cbPanel.add(testButton);

	mainPanel.add(cbPanel);
	add(mainPanel);

	// add actionListeners
	cbxEnMidi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setEnable(cbxEnMidi.isSelected());
		    setModified(true);
		}
	    });

	cbxEnMC.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    cbMC.setEnabled(cbxEnMC.isSelected());
		    setModified(true);
		}
	    });

	ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setModified(true);
		}
	    };
	cbOut.addActionListener(al);
	cbIn.addActionListener(al);
	cbMC.addActionListener(al);

	testButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    MidiTest.runLoopbackTest(cbIn.getSelectedIndex(),
					     cbOut.getSelectedIndex());
		}
	    });
    }

    void init() {
	cbxEnMidi.setSelected(AppConfig.getMidiEnable());
	cbxEnMC.setSelected(AppConfig.getMasterInEnable());

	try {
	    cbOut.setSelectedIndex(AppConfig.getInitPortOut());
	    cbIn.setSelectedIndex(AppConfig.getInitPortIn());
	    cbMC.setSelectedIndex(AppConfig.getMasterController());
	} catch (IllegalArgumentException e) {
	    ErrorMsg.reportStatus(e);
	}

	// disable MIDI when either MIDI input or MIDI output is unavailable.
	cbxEnMidi.setEnabled(MidiUtil.isOutputAvailable()
			     || MidiUtil.isInputAvailable());
	setEnable(AppConfig.getMidiEnable());
    }

    /**
     * enable/disable widgets according to the various settings..
     */
    private void setEnable(boolean midiEn) {
	cbxEnMC.setEnabled(midiEn
			   && MidiUtil.isOutputAvailable()
			   && MidiUtil.isInputAvailable());
	testButton.setEnabled(midiEn
			      && MidiUtil.isOutputAvailable()
			      && MidiUtil.isInputAvailable());

	cbOut.setEnabled(midiEn && MidiUtil.isOutputAvailable());
	cbIn.setEnabled(midiEn && MidiUtil.isInputAvailable());
	cbMC.setEnabled(midiEn && cbxEnMC.isSelected());
    }

    void commitSettings() {
	if (cbxEnMidi.isSelected()) {
	    AppConfig.setMidiEnable(true);
	    AppConfig.setMasterController(cbMC.getSelectedIndex());
	    AppConfig.setMasterInEnable(cbxEnMC.isSelected());

	    int out = cbOut.getSelectedIndex();
	    int in  = cbIn.getSelectedIndex();
	    AppConfig.setInitPortOut(out);
	    AppConfig.setInitPortIn(in);
	    if (!AppConfig.getMultiMIDI()) {
		// change MIDI ports of all Devices
		for (int i = 0; i < AppConfig.deviceCount(); i++) {
		    AppConfig.getDevice(i).setPort(out);
		    AppConfig.getDevice(i).setInPort(in);
		}
	    }
	} else {
	    AppConfig.setMidiEnable(false);
	}
	setModified(false);
    }
}
