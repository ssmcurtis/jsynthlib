package org.jsynthlib.menu.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;

import javax.swing.AbstractAction;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.window.AbstractLibraryFrame;
import org.jsynthlib.menu.window.ExtractPatchesDialog;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriverPatch;
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

			byte[] dataByteArray = patch.getByteArray();

			// scan bytearray for first F0...
			ByteBuffer byteBuffer = ByteBuffer.allocate(dataByteArray.length);
			byteBuffer.put(dataByteArray);

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

					// check if header makes sense
					for (int i = 0; i < AppConfig.deviceCount(); i++) {
						Device device = AppConfig.getDevice(i);

						for (int j = 0, m = 0; j < device.driverCount(); j++) {
							SynthDriverPatch driver = (SynthDriverPatch) device.getDriver(j);

							if (knownPatch(patchHeaderString, driver.getSysexID())) {
								headerFound = true;
								ErrorMsgUtil.reportStatus(">>>> found ");
							}
						}
					}

					if (!headerFound) {
						patchHeaderString = "";
					}

				}
				cursor++;
			}

			new ExtractPatchesDialog(byteBuffer, patchHeaderString, patch.getFileName());

		} catch (Exception ex) {
			ErrorMsgUtil.reportError("Error", "Can not Extract (Maybe its not a bank?)", ex);
		}
	}

	private boolean knownPatch(String patchHeaderString, String sysexId) {

		if (sysexId == null) {
			return false;
		}

		StringBuffer compareString = new StringBuffer();

		int length = sysexId.length() > patchHeaderString.length() ? patchHeaderString.length() : sysexId.length();

		for (int j = 0; j < length; j++) {
			switch (sysexId.charAt(j)) {
			case '*':
				compareString.append(patchHeaderString.charAt(j));
				break;
			default:
				compareString.append(sysexId.charAt(j));
			}
		}

		ErrorMsgUtil.reportStatus("Compare: " + patchHeaderString + " " + compareString);

		return (compareString.toString().equalsIgnoreCase(patchHeaderString.substring(0, length)));

	}
}