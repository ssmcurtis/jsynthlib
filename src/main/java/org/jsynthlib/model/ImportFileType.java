package org.jsynthlib.model;

public enum ImportFileType {
	MIDI(".mid", "MIDI Files (*.mid)", false),
	TXTHEX(".txt", "TexHex Files (*.txt)", false),
	SYX(".syx", "Sysex Files (*.syx)", true);

	private final String extension;
	private final String description;
	private final boolean defaultFilter;

	ImportFileType(String extension, String description, boolean defaultFilter) {
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

	public boolean isDefaultFilter() {
		return defaultFilter;
	}
	
	public static ImportFileType getImportFileTypeForDescription(String descritionString) {
		for (ImportFileType importFileType : ImportFileType.values()) {
			if (importFileType.getDescription().equals(descritionString)) {
				return importFileType;
			}
		}
		return ImportFileType.SYX;
	}
	
	public static ImportFileType getImportFileTypeForExtension(String extensionString) {
		for (ImportFileType importFileType : ImportFileType.values()) {
			if (importFileType.getExtension().equals(extensionString)) {
				return importFileType;
			}
		}
		return null;
	}

}
