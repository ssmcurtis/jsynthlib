package org.jsynthlib.synthdrivers.kawai.k4;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Driver Set Patch Driver for Kawai K4.
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KawaiK4DrumsetDriver extends SynthDriverPatchImpl {
	/** Header Size */
	private static final int HSIZE = 8;
	/** Single Patch size */
	private static final int SSIZE = 682;

	private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 00 00 04 *bankNum* 20 F7");

	public KawaiK4DrumsetDriver() {
		super("Drumset", "Gerrit Gehnen");
		sysexID = "F040**2*0004**20";
		patchSize = HSIZE + SSIZE + 1;
		patchNameStart = 0;
		patchNameSize = 0;
		deviceIDoffset = 2;
		bankNumbers = new String[] { "0-Internal", "1-External" };
		patchNumbers = new String[] { "Drumset" };
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		p.getSysex()[3] = (byte) 0x20;
		p.getSysex()[6] = (byte) ((bankNum << 1) + 1);
		p.getSysex()[7] = (byte) 0x20;
		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
	}

	public void sendPatch(PatchDataImpl p) {
		p.getSysex()[3] = (byte) 0x23;
		p.getSysex()[7] = (byte) 0x20;
		sendPatchWorker(p);
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		// a litte strange this, but there is a checksum for each key!
		for (int i = 8; i < HSIZE + SSIZE - 1; i += 11) {
			int sum = 0;
			for (int j = i; j < i + 10; j++) {
				sum += p.getSysex()[j];
			}
			sum += 0xA5;
			p.getSysex()[i + 10] = (byte) (sum % 128);
		}
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[HSIZE + SSIZE + 1];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x40;
		sysex[2] = (byte) 0x00;
		sysex[3] = (byte) 0x23;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x04;
		sysex[6] = (byte) 0x01;
		sysex[7] = 0x20;

		for (int i = 0; i < 61; i++) {
			sysex[8 + 11 + 6 + i * 11] = 50;
			sysex[8 + 11 + 7 + i * 11] = 50;
		}

		sysex[HSIZE + SSIZE] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new KawaiK4DrumsetEditor(p);
	}

	public String getPatchName(PatchDataImpl ip) {
		return "Drumset";
	}

	public void requestPatchDump(int bankNum, int patchNum) {
		send(SYS_REQ.toSysexMessage(getChannel(), new NameValue("bankNum", (bankNum << 1) + 1)));
	}
}
