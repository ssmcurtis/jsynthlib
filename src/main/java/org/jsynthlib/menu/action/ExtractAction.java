package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.window.AbstractLibraryFrame;
import org.jsynthlib.menu.window.ExtractPatchesDialog;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;

@SuppressWarnings("serial")
public class ExtractAction extends AbstractAction {
	public ExtractAction(Map<Serializable, Integer> mnemonics) {
		super("Extract", null);
		mnemonics.put(this, new Integer('E'));
		this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			Patch patch = ((AbstractLibraryFrame) Actions.getSelectedFrame()).getSelectedPatch();

			byte[] sysexByteArray = patch.getByteArray();

			// scan bytearray for first F0...
			ByteBuffer byteBuffer = ByteBuffer.allocate(sysexByteArray.length);
			byteBuffer.put(sysexByteArray);

			String patchHeaderString = "";
			
			int bufSize = byteBuffer.capacity();
			
			boolean headerFound = false;
			
			int cursor = 0;
			
			while (!headerFound && cursor < bufSize) {
				if (HexaUtil.isStartSysex(byteBuffer.get(cursor))) {
					int end = cursor + 16;
					if (end < bufSize) {
						for (int bid = cursor; bid < end; bid++) {
							patchHeaderString += HexaUtil.byteToHexString(byteBuffer.get(bid));
						}
					}
					headerFound = true;
				}
				cursor++;
			}

			new ExtractPatchesDialog(byteBuffer, patchHeaderString, patch.getFileName());
		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Can not Extract (Maybe its not a bank?)", ex);
		}
	}
}