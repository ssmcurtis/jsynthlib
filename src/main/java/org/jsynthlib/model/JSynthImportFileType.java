package org.jsynthlib.model;

public enum JSynthImportFileType {
	MIDI(".mid", "MIDI Files (*.mid)"),
	NGF(".ngf", "Nord Generator (*.ngf)"),
	TXTHEX(".txt", "TexHex Files (*.txt)"),
	MICROKORG_PRG(".prg", "microKorg Files (*.prg)"),
	MICROKORG_SET(".set", "microKorg Files (*.set)"),
	WRK(".wrk", "Cakewalk Files* (*.wrk)"),
	SYX(".syx", "Sysex Files (*.syx)");

	private final String extension;
	private final String description;

	JSynthImportFileType(String extension, String description) {
		this.extension = extension;
		this.description = description;
	}

	public String getExtension() {
		return extension;
	}

	public String getDescription() {
		return description;
	}

	
	public static JSynthImportFileType getImportFileTypeForDescription(String descritionString) {
		for (JSynthImportFileType importFileType : JSynthImportFileType.values()) {
			if (importFileType.getDescription().equals(descritionString)) {
				return importFileType;
			}
		}
		return JSynthImportFileType.SYX;
	}
	
	public static JSynthImportFileType getImportFileTypeForExtension(String extensionString) {
		for (JSynthImportFileType importFileType : JSynthImportFileType.values()) {
			if (importFileType.getExtension().equals(extensionString.toLowerCase())) {
				return importFileType;
			}
		}
		return null;
	}

}
