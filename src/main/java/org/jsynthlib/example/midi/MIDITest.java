package org.jsynthlib.example.midi;

/*
 * Testapplikation zum Erkunden der MIDI Umgebung:
 * Main - Klasse, welche Fenster - Klasse MIDITestFrame aufruft
 * 
 * Created on 16.09.2006 / Sö
 */

public class MIDITest {

	public static void main(String[] args) {
		_MIDITestFrame frame = new _MIDITestFrame();
		frame.setSize(400, 500);
		frame.setVisible(true);
	}
}
