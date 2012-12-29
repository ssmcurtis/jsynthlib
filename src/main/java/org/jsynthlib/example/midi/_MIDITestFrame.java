package org.jsynthlib.example.midi;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.sound.midi.*;

/*
 * Testapplikation zum Erkunden der MIDI Umgebung:
 * Grafisches Fenster, welches von MIDITest instanziert wird
 * 
 * Created on 16.09.2006 / Sö
 */

public class _MIDITestFrame extends JFrame {

	private JTextArea ta;
	private JScrollPane sbrText;

	_MIDITestFrame() {

		// Grafische Oberfläche aufbauen:
		JPanel panel = (JPanel) this.getContentPane();
		panel.setLayout(new BorderLayout());


		ta = new JTextArea("", 5, 50);
		ta.setLineWrap(true);
		sbrText = new JScrollPane(ta);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel buttons = new JPanel(new GridLayout(3, 1));
		JButton button = new JButton("Sound.. ");
		buttons.add(button);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ta.setText("");
//				MidiAction.playMidi(ta);
			}
		});

		JButton button2 = new JButton("Reset all ");
		buttons.add(button2);
		button2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ta.setText("");
//				MidiAction.noterOffAll(ta);
			}
		});

		JButton button3 = new JButton("Action ... ");
		buttons.add(button3);
		button3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ta.setText("NOTING");
			}
		});

		panel.add(buttons, BorderLayout.NORTH);
		panel.add(ta, BorderLayout.CENTER);
		panel.revalidate();
		pack();
	}
}