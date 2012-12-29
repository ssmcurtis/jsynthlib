package org.jsynthlib.example.midi;

public enum MidiNote {
	C_2(0),
	C_1(12),
	C0(24),
	C1(36),
	C2(48),
	C3(60),
	C4(72),
	C5(84),
	C6(96),
	C7(108),
	C8(120);

	private final int number;

	MidiNote(int midiNoteNumber) {
		this.number = midiNoteNumber;
	}

	public int getNumber() {
		return number;
	}

	public static MidiNote[] getExampleValues() {
		return new MidiNote[] { MidiNote.C0, MidiNote.C2, MidiNote.C4 };
	}
	public static MidiNote[] getBasicValues() {
		return new MidiNote[] { MidiNote.C0, MidiNote.C1, MidiNote.C2, MidiNote.C3, MidiNote.C4, MidiNote.C5, MidiNote.C6,MidiNote.C7 };
	}
}
