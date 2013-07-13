package org.jsynthlib.synthdrivers.korg.wavestation;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * Driver for Korg Wavestation Micro Tuning Scales
 * 
 * Be carefull: Untested, because I only have access to a file containing some WS patches....
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KorgWavestationMicroTuneScaleDriver extends SynthDriverPatchImpl {

	public KorgWavestationMicroTuneScaleDriver() {
		super("Micro Tune Scale", "Gerrit Gehnen");
		sysexID = "F0423*285A";
		sysexRequestDump = new SysexHandler("F0 42 @@ 28 08 F7");

		trimSize = 297;
		patchNameStart = 0;
		patchNameSize = 0;
		deviceIDoffset = 0;
		checksumStart = 5;
		checksumEnd = 294;
		checksumOffset = 295;
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {

		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}

		((PatchDataImpl) p).getSysex()[2] = (byte) (0x30 + getChannel() - 1);
		try {
			send(((PatchDataImpl) p).getSysex());
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}

	}

	public void sendPatch(PatchDataImpl p) {
		((PatchDataImpl) p).getSysex()[2] = (byte) (0x30 + getChannel() - 1); // the only thing to do is to set the byte to 3n
																		// (n = channel)

		try {
			send(((PatchDataImpl) p).getSysex());
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[297];
		sysex[00] = (byte) 0xF0;
		sysex[01] = (byte) 0x42;
		sysex[2] = (byte) (0x30 + getChannel() - 1);
		sysex[03] = (byte) 0x28;
		sysex[04] = (byte) 0x5A;

		/* sysex[295]=checksum; */
		sysex[296] = (byte) 0xF7;

		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "New Patch");
		calculateChecksum(p);
		return p;
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int i;
		int sum = 0;

		// ErrorMsgUtil.reportStatus("Checksum was" + p.sysex[ofs]);
		for (i = start; i <= end; i++) {
			sum += p.getSysex()[i];
		}
		p.getSysex()[ofs] = (byte) (sum % 128);
		// ErrorMsgUtil.reportStatus("Checksum new is" + p.sysex[ofs]);

	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(getChannel(), 0));
	}
}
