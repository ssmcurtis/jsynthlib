// written by ssmCurtis
package org.jsynthlib.synthdrivers.roland.mks80;

import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;

public class Mks80ToneBankDriver extends Mks80BankDriver {

	// private Mks80SendSysex mks80Sysex;

	public Mks80ToneBankDriver() {
		super("Patch Bank", "ssmCurtis", Mks80.PROGRAM_COUNT_IN_BANK, 3);
		sysexID = Mks80.DEVICE_SYSEX_ID;

		singleSysexID = Mks80.DEVICE_SYSEX_ID;

		singleSize = Mks80.TONE_SIZE_IN_BANK;

		patchSize = Mks80.BANK_SIZE_SYSEX_64;

		bankNumbers = Mks80.BANK_NAMES_PATCHES;
		patchNumbers = Mks80.createProgrammNumbers();

	}

	@Override
	public void addToCurrentBank(PatchDataImpl singlePatch, int patchNum) {
		if (getCurrentBank() == null) {
			setCurrentBank(createNewPatch());
		}
		putPatch(getCurrentBank(), singlePatch, patchNum);
	}

	@Override
	public boolean canHoldPatch(PatchDataImpl p) {
		ErrorMsgUtil.reportStatus("Size: " + p.getSysex().length);
		if ((Mks80.DATA_PACKAGE_IN_BANK != p.getSysex().length) && (singleSize != 0)) {
			return false;
		}
		return true;
	}

	@Override
	protected PatchDataImpl createNewPatch() {

		byte[] sysex = new byte[Mks80.BANK_SIZE_SYSEX_64];
		byte[] tonePatchSysex = Mks80.getDefaultBankPatch();

		for (int i = 0; i < Mks80.BANK_DATA_PACKAGE_COUNT; i++) {
			// 16
			sysex[(Mks80.BANK_DATA_PACKAGE_SIZE * i)] = (byte) 0xF0;
			sysex[(Mks80.BANK_DATA_PACKAGE_SIZE * i) + 1] = (byte) 0x41;
			sysex[(Mks80.BANK_DATA_PACKAGE_SIZE * i) + 2] = (byte) 0x42;
			sysex[(Mks80.BANK_DATA_PACKAGE_SIZE * i) + 3] = (byte) (getChannel() - 1);
			sysex[(Mks80.BANK_DATA_PACKAGE_SIZE * i) + 4] = (byte) 0x20;

			sysex[(Mks80.BANK_DATA_PACKAGE_SIZE * i) + Mks80.BANK_DATA_PACKAGE_SIZE - 2] = (byte) 0x00;
			sysex[(Mks80.BANK_DATA_PACKAGE_SIZE * i) + Mks80.BANK_DATA_PACKAGE_SIZE - 1] = (byte) 0xF7;
		}

		PatchDataImpl defaultBankTonePatch = new PatchDataImpl(tonePatchSysex, getDevice());
		PatchDataImpl bankPatch = new PatchDataImpl(sysex, this);

		for (int i = 0; i < getNumPatches(); i++) {
			// 64
			putPatch(bankPatch, defaultBankTonePatch, i);
		}

		calculateChecksum(bankPatch);

		return bankPatch;

	}

	@Override
	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		// default bank is 4080: 16xData

		ErrorMsgUtil.reportStatus(patchNum + " Size:" + bank.getSysex().length);

		byte[] toneSysex = new byte[Mks80.TONE_SIZE_IN_BANK_SYSEX_CONTAINER];
		byte[] patchSysex = new byte[Mks80.PATCH_SIZE_IN_BANK_SYSEX_CONTAINER];

		Mks80.wrapSingleTone(toneSysex);
		Mks80.wrapSinglePatch(patchSysex);

		int dataPackage = patchNum / 4;
		int postionInPackage = patchNum % 4;

		// check length here

		int sourceStart = (Mks80.BANK_DATA_PACKAGE_SIZE * dataPackage) + Mks80.HEADER_SIZE
				+ (Mks80.DATA_PACKAGE_IN_BANK * postionInPackage);

		ErrorMsgUtil.reportStatus(sourceStart + Mks80.DATA_PACKAGE_IN_BANK + " " + bank.getSysex().length);

		if (sourceStart + Mks80.DATA_PACKAGE_IN_BANK < bank.getSysex().length) {
			System.arraycopy(bank.getSysex(), sourceStart, toneSysex, Mks80.HEADER_SIZE, Mks80.TONE_SIZE_IN_BANK);
			System.arraycopy(bank.getSysex(), sourceStart + Mks80.TONE_SIZE_IN_BANK, patchSysex, Mks80.HEADER_SIZE,
					Mks80.PATCH_SIZE_IN_BANK);

			ErrorMsgUtil.reportStatus("From: " + sourceStart + " to " + (sourceStart + Mks80.TONE_SIZE_IN_BANK));
			ErrorMsgUtil.reportStatus("Tone: " + HexaUtil.hexDump(toneSysex, 0, -1, 64));
			ErrorMsgUtil.reportStatus("Patch: " + HexaUtil.hexDump(patchSysex, 0, -1, 64));

			return new PatchDataImpl(toneSysex);
		} else {
			return null;
		}
	}

	@Override
	public void putPatch(PatchDataImpl bank, PatchDataImpl patch, int patchNum) {

		if ((Mks80.DATA_PACKAGE_IN_BANK != patch.getSysex().length) && (singleSize != 0)) {

			if ((Mks80.TONE_SIZE_IN_BANK_SYSEX_CONTAINER == patch.getSysex().length)) {
				// onla a tone will be added - add a default patch and make tone to upper and lower in patch

				byte[] tonePatchSysex = new byte[Mks80.DATA_PACKAGE_IN_BANK];
				byte[] defaultPatchforToneNumber = Mks80.getDefaultSinglePatch(patchNum, patchNum);

				System.arraycopy(patch.getSysex(), Mks80.HEADER_SIZE, tonePatchSysex, 0, Mks80.TONE_SIZE_IN_BANK);
				System.arraycopy(defaultPatchforToneNumber, 0, tonePatchSysex, Mks80.TONE_SIZE_IN_BANK, Mks80.PATCH_SIZE_IN_BANK);

				patch = new PatchDataImpl(tonePatchSysex, getDevice());

			} else {
				ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
				return;
			}
		}

		// ErrorMsgUtil.reportStatus("Put patch: " + patchNum);

		int dataPackage = patchNum / 4;
		int postionInPackage = patchNum % 4;

		int targetStart = (Mks80.BANK_DATA_PACKAGE_SIZE * dataPackage) + Mks80.HEADER_SIZE
				+ (Mks80.DATA_PACKAGE_IN_BANK * postionInPackage);

		System.arraycopy(patch.getSysex(), 0, bank.getSysex(), targetStart, Mks80.DATA_PACKAGE_IN_BANK);

	}

	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {

		calculateChecksum(p);

		Mks80SendSysex mks80sysex = new Mks80SendSysex(p, this);
	}

	public boolean supportsPatch(String patchString, byte[] sysex) {
		switch (sysex.length) {
		case Mks80.BANK_DATA_PACKAGE_SIZE:
			break;
		case Mks80.BANK_SIZE_SYSEX_AND_EOF:
			break;
		// case Mks80.BANK_SIZE_SYSEX_128:
		// break;
		case Mks80.BANK_SIZE_SYSEX_64:
			break;
		default:
			return false;
		}

		if (sysexID == null || patchString.length() < sysexID.length())
			return false;

		StringBuffer compareString = new StringBuffer();
		for (int i = 0; i < sysexID.length(); i++) {
			switch (sysexID.charAt(i)) {
			case '*':
				compareString.append(patchString.charAt(i));
				break;
			default:
				compareString.append(sysexID.charAt(i));
			}
		}
		// ErrorMsg.reportStatus(toString());
		// ErrorMsg.reportStatus("Comp.String: " + compareString);
		// ErrorMsg.reportStatus("DriverString:" + driverString);
		// ErrorMsg.reportStatus("PatchString: " + patchString);
		return (compareString.toString().equalsIgnoreCase(patchString.substring(0, sysexID.length())));
	}

}
