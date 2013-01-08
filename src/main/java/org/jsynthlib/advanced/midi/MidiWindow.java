package org.jsynthlib.advanced.midi;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsynthlib.tools.UiUtil;

public class MidiWindow implements WriteOutput {
	private JFrame f; // Main frame
	private JTextArea ta; // Text area
	private JScrollPane sbrText; // Scroll pane for text area
//	private JButton btnQuit; // Quit Program
	private JPanel buttons = new JPanel(new GridLayout(3, 1));

	private Thread midiThread;
	private MidiActionReset resetAll;
	private MidiActionProgramChange changeLexicon;
	private MidiActionPlayNote playNote;

	public MidiWindow() { // Constructor
		// Create Frame
		f = new JFrame("Test UI");
		f.getContentPane().setLayout(new FlowLayout());

		// Create Scrolling Text Area in Swing
		ta = new JTextArea("", 20, 50);
		ta.setLineWrap(true);
		sbrText = new JScrollPane(ta);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Create Quit Button
//		btnQuit = new JButton("Quit");
//		btnQuit.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				System.exit(0);
//			}
//		});

		JButton button = new JButton("Sound.. ");
		buttons.add(button);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int countBefore = Thread.activeCount();
				ta.setText("");
				playNote = new MidiActionPlayNote(MidiWindow.this, -1, -1, true);

				int countAfter = Thread.activeCount();
				System.out.println("Thread before: " + countBefore + " Thread after: " + countAfter);

			}
		});

		JButton button2 = new JButton("Reset all ");
		buttons.add(button2);
		button2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int countBefore = Thread.activeCount();
				ta.setText("");
				resetAll = new MidiActionReset(MidiWindow.this, false);

				int countAfter = Thread.activeCount();
				System.out.println("Thread before: " + countBefore + " Thread after: " + countAfter);
			}
		});

		JButton button3 = new JButton("Change Programm Lexcion ");
		buttons.add(button3);
		button3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int countBefore = Thread.activeCount();
				ta.setText("");
				changeLexicon = new MidiActionProgramChange(MidiWindow.this, false);

				int countAfter = Thread.activeCount();
				System.out.println("Thread before: " + countBefore + " Thread after: " + countAfter);
			}
		});

		JButton button4 = new JButton("Stop ... ");
		buttons.add(button4);
		button4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (resetAll != null) {
					ThreadStop midiStop = (ThreadStop) resetAll;
					midiStop.sendStopSignal();
					resetAll = null;
				}

				if (playNote != null) {
					ThreadStop midiStop = (ThreadStop) playNote;
					midiStop.sendStopSignal();
					playNote = null;
				}

				System.out.println(" MidiWindows: Threads " + Thread.activeCount());
			}
		});

	}

	public void launchFrame() { // Create Layout
		// Add text area and button to frame
		f.getContentPane().add(sbrText);
		f.getContentPane().add(buttons);

		// Close when the close button is clicked
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Display Frame
		f.pack(); // Adjusts frame to size of components
		UiUtil.centerDialog(f);
		f.setVisible(true);
	}

	public static void main(String args[]) {
		MidiWindow gui = new MidiWindow();
		gui.launchFrame();
	}

	@Override
	public synchronized void appendText(String textToAppend) {
		ta.append(textToAppend + "\n");
		ta.setCaretPosition(ta.getText().length() - 1);
	}
}