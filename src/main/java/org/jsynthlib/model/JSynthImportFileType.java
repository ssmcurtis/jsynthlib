package org.jsynthlib.model;

public enum JSynthImportFileType {
	MIDI(".mid", "MIDI Files (*.mid)", false),
	NGF(".ngf", "Nord Generator (*.ngf)", false),
	TXTHEX(".txt", "TexHex Files (*.txt)", false),
	MICROKORG_PRG(".prg", "microKorg Files (*.prg)", false),
	MICROKORG_SET(".set", "microKorg Files (*.set)", false),
	SYX(".syx", "Sysex Files (*.syx)", true);

	private final String extension;
	private final String description;
	private final boolean defaultFilter;

	JSynthImportFileType(String extension, String description, boolean defaultFilter) {
		this.extension = extension;
		this.description = description;
		this.defaultFilter = defaultFilter;
	}

	public String getExtension() {
		return extension;
	}

	public String getDescription() {
		return description;
	}

	@Deprecated
	public boolean isDefaultFilter() {
		return defaultFilter;
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
