package org.jsynthlib.menu.window;

// import core.PatchEditorFrame;
// import java.awt.*;
// import synthdrivers.AlesisSR16.DataModel;

import javax.sound.midi.SysexMessage;

import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;

/**
 * This class serves as a initial substitute for a patch editor. It is to be used for verifying that your driver is
 * properly getting and decoding the sysex message from the device (and that the bytes in the sysex message are what you
 * expect them to be).
 * 
 * To use it, add this to your driver:
 * 
 * public JSLFrame editPatch(Patch p) { return (new synthdrivers.Generic.HexDumpEditorFrame(p)); }
 * 
 * and then you can use the "Edit" menu option on the patch in your library to inspect the sysex message.
 */
public class HexDumpEditorFrame extends SingleTextAreaFrame {

	static final int bytesperline = 16;

	public HexDumpEditorFrame(byte[] bytes) {
		super("Hex dump of sysex message byte[]", bytesperline * 4 + 12);
		appendBytes(bytes);
	}

	/*
	 * public HexDumpEditorFrame(DataModel dm) { this(dm.getDecodedBytes()); }
	 */
	public HexDumpEditorFrame(PatchDataImpl p) {
		super("Hex dump of sysex message patch ", bytesperline * 4 + 12);
		SysexMessage[] messages = p.getMessages();
		for (int i = 0; i < messages.length; i++) {
			append("Message " + i + ":\n");
			appendBytes(messages[i].getMessage());
		}
	}

	public void appendBytes(byte[] bytes) {
		append(HexaUtil.hexDump(bytes, 0, -1, bytesperline, true, true));
	}
	
}
