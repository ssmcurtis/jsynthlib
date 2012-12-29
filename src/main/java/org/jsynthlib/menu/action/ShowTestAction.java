package org.jsynthlib.menu.action;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.jsynthlib.Constants;
import org.jsynthlib.example.midi.MidiWindow;
import org.jsynthlib.example.style.TextComponentDemo;
import org.jsynthlib.example.style.XMEditor;
import org.jsynthlib.menu.preferences.DeviceDialog;
import org.jsynthlib.menu.preferences.TestDialog;
import org.jsynthlib.menu.ui.DeviceSelectionTree;
import org.jsynthlib.tools.Utility;


public class ShowTestAction extends AbstractAction {
	public ShowTestAction(Map<Serializable, Integer> mnemonics) {
		super("InterfaceTest");
		mnemonics.put(this, new Integer('S'));

	}

	public void actionPerformed(ActionEvent e) {

//		final JFrame frame = new JFrame("Viewer");
//		// Create and set up the window.
//		final XMEditor editor = new XMEditor();
//		JScrollPane scrollPane = new JScrollPane(editor);
//		scrollPane.setPreferredSize(new Dimension(500, 300));
//
////		// Create the text area for the status log and configure it.
////		editor.changeLog = new JTextArea(5, 30);
////		editor.changeLog.setEditable(false);
//
//		// Add the components.
//		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
//
//		// Put the initial text into the text pane.
//		editor.initDocument();
//
//		// Display the window.
//		frame.pack();
//		frame.setVisible(true);
//
//		Utility.centerDialog(frame);

		MidiWindow gui = new MidiWindow();
		gui.launchFrame();
		
	}
}