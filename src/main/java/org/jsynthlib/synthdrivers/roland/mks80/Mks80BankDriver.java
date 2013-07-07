package org.jsynthlib.synthdrivers.roland.mks80;


import javax.sound.midi.MidiMessage;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.HexaUtil;

public abstract class Mks80BankDriver extends SynthDriverBank {

	private static final SysexHandler RQF = new SysexHandler(Mks80.RQF);
	private static final SysexHandler ACK = new SysexHandler(Mks80.ACK);


	public Mks80BankDriver(String patchType, String authors, int numPatches, int numColumns) {
		super(patchType, authors, numPatches, numColumns);
	}

	@Override
	public void calculateChecksum(PatchDataImpl p) {
		// complete different
		for (int i = 0; i < Mks80.BANK_DATA_PACKAGE_COUNT; i++) {

			int start = (Mks80.BANK_DATA_PACKAGE_SIZE * i) + Mks80.HEADER_SIZE;
			int end = (Mks80.BANK_DATA_PACKAGE_SIZE * i) + Mks80.HEADER_SIZE + Mks80.BANK_DATA_PROGRAM_DATA_SIZE - 1;
			int offset = (Mks80.BANK_DATA_PACKAGE_SIZE * i) + Mks80.HEADER_SIZE + Mks80.BANK_DATA_PROGRAM_DATA_SIZE;

			// System.out.println("start: " + start + " end: " + end + " offset: " + offset);

			this.calculateChecksum(p, start, end, offset);
		}
	}

	public boolean isAcknowledge(byte[] sysex) {
		if (Mks80OperationCode.ACK.equals(Mks80OperationCode.getOpCode(sysex))) {
			return true;
		}
		return false;
	}


	public boolean isEof(byte[] sysex) {
		if (Mks80OperationCode.ACK.equals(Mks80OperationCode.getOpCode(sysex))) {
			return true;
		}
		return false;
	}

	
	@Override
	public boolean isRequestAndAcknowledge() {
		return true;
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {

		NameValue midiChannel = new NameValue("midiChannel", getChannel() - 1);
		MidiMessage msg = RQF.toSysexMessage(0, midiChannel);

		send(msg);

		System.out.println("RQF: " + HexaUtil.hexDumpOneLine(msg.getMessage()));
	}

	@Override
	public void sendAcknowledge() {
		NameValue midiChannel = new NameValue("midiChannel", getChannel() - 1);
		MidiMessage msg = ACK.toSysexMessage(0, midiChannel);

		send(msg);

		System.out.println("ACK: " + HexaUtil.hexDumpOneLine(msg.getMessage()));
	}

	@Override
	public String getPatchName(PatchDataImpl bank, int patchNum) {
		return "-";
	}

	@Override
	public void setPatchName(PatchDataImpl bank, int patchNum, String name) {

	}


}
