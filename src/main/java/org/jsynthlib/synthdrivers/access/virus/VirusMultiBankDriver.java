package org.jsynthlib.synthdrivers.access.virus;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * @author Kenneth L. Martinez
 */
public class VirusMultiBankDriver extends SynthDriverBank {
	static final int BANK_NUM_OFFSET = 7;
	static final int PATCH_NUM_OFFSET = 8;
	static final int NUM_IN_BANK = 128;

	public VirusMultiBankDriver() {
		super("Multi Bank", "Kenneth L. Martinez", Virus.PATCH_COUNT_IN_BANK, 4);
		sysexID = "F000203301**11";
		sysexRequestDump = new SysexHandler("F0 00 20 33 01 10 33 01 F7");
		singleSysexID = "F000203301**11";
		singleSize = 267;
		patchSize = singleSize * NUM_IN_BANK;
		patchNameStart = 13;
		patchNameSize = 10;
		deviceIDoffset = 5;
		checksumOffset = 265;
		checksumStart = 5;
		checksumEnd = 264;
		bankNumbers = Virus.BANK_NAMES_MULTI;
		patchNumbers = Virus.createPatchNumbers();
	}

	@Override
	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		calculateChecksum(p.getSysex(), start, end, ofs);
	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		sendPatchWorker((PatchDataImpl) p, 1);
	}

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), patchNum * singleSize, singleSize);
		((PatchDataImpl) bank).getSysex()[patchNum * singleSize + PATCH_NUM_OFFSET] = (byte) patchNum; // set multi #
	}

	@Override
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		byte sysex[] = new byte[singleSize];
		System.arraycopy(((PatchDataImpl) bank).getSysex(), patchNum * singleSize, sysex, 0, singleSize);
		return new PatchDataImpl(sysex, getDevice());
	}

	@Override
	public String getPatchName(PatchDataImpl p, int patchNum) {
		PatchDataImpl pgm = (PatchDataImpl) extractPatch(p, patchNum);
		try {
			char c[] = new char[patchNameSize];
			for (int i = 0; i < patchNameSize; i++)
				c[i] = (char) pgm.getSysex()[i + patchNameStart];
			return new String(c);
		} catch (Exception ex) {
			return "-";
		}
	}

	@Override
	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		PatchDataImpl pgm = (PatchDataImpl) extractPatch(p, patchNum);
		if (name.length() < patchNameSize + 4) {
			name = name + "                ";
		}
		byte nameByte[] = name.getBytes();
		for (int i = 0; i < patchNameSize; i++) {
			pgm.getSysex()[i + patchNameStart] = nameByte[i];
		}
		putPatch(p, pgm, patchNum);
	}

	@Override
	public PatchDataImpl createNewPatch() {
		byte tmp[] = new byte[singleSize];
		byte sysex[] = new byte[patchSize];
		System.arraycopy(Virus.NEW_MULTI_PATCH, 0, tmp, 0, singleSize);
		for (int i = 0; i < NUM_IN_BANK; i++) {
			tmp[PATCH_NUM_OFFSET] = (byte) i; // multi #
			System.arraycopy(tmp, 0, sysex, i * singleSize, singleSize);
		}
		return new PatchDataImpl(sysex, this);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(getDeviceID(), new NameValue("bankNum", 1)));
	}

	private void sendPatchWorker(PatchDataImpl p, int bankNum) {
		byte tmp[] = new byte[singleSize]; // send in 128 single-multi messages
		try {
			PatchBayApplication.showWaitDialog();
			for (int i = 0; i < NUM_IN_BANK; i++) {
				System.arraycopy(p.getSysex(), i * singleSize, tmp, 0, singleSize);
				tmp[deviceIDoffset] = (byte) (getDeviceID() - 1);
				tmp[BANK_NUM_OFFSET] = (byte) 1;
				tmp[PATCH_NUM_OFFSET] = (byte) i; // multi #
				calculateChecksum(tmp, checksumStart, checksumEnd, checksumOffset);
				send(tmp);
				Thread.sleep(50);
			}
			PatchBayApplication.hideWaitDialog();
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
			ErrorMsgUtil.reportError("Error", "Unable to send Patch");
		}
	}

	private static void calculateChecksum(byte sysex[], int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += sysex[i];
		}
		sysex[ofs] = (byte) (sum & 0x7F);
	}

}
