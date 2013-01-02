// written by Kenneth L. Martinez
package org.jsynthlib.synthdrivers.access.virus;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.helper.SysexHandler;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * @version $Id$
 * @author Kenneth L. Martinez
 */
public class VirusProgSingleDriver extends SynthDriverPatchImpl {
	// static final String BANK_LIST[] = new String[] { "Bank A", "Bank B", "Bank C", "Bank D", "Bank E", "Bank F",
	// "Bank G", "Bank H" };
//	static final String PATCH_LIST[] = new String[] { "000", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011",
//			"012", "013", "014", "015", "016", "017", "018", "019", "020", "021", "022", "023", "024", "025", "026", "027", "028", "029",
//			"030", "031", "032", "033", "034", "035", "036", "037", "038", "039", "040", "041", "042", "043", "044", "045", "046", "047",
//			"048", "049", "050", "051", "052", "053", "054", "055", "056", "057", "058", "059", "060", "061", "062", "063", "064", "065",
//			"066", "067", "068", "069", "070", "071", "072", "073", "074", "075", "076", "077", "078", "079", "080", "081", "082", "083",
//			"084", "085", "086", "087", "088", "089", "090", "091", "092", "093", "094", "095", "096", "097", "098", "099", "100", "101",
//			"102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119",
//			"120", "121", "122", "123", "124", "125", "126", "127" };
	static final int BANK_NUM_OFFSET = 7;
	static final int PATCH_NUM_OFFSET = 8;

	public VirusProgSingleDriver() {
		super("Prog Single", "Kenneth L. Martinez, ssmCurtis");
		sysexID = Virus.DEVICE_SYSEX_ID;
		sysexRequestDump = new SysexHandler("F0 00 20 33 01 @@ 30 *bankNum* *patchNum* F7");

		patchSize = 267;
		patchNameStart = 249;
		patchNameSize = 10;
		deviceIDoffset = 5;
		checksumOffset = 265;
		checksumStart = 5;
		checksumEnd = 264;
		bankNumbers = Virus.BANK_NAMES_SINGLE;
		patchNumbers = Virus.createPatchNumbers();
	}

	@Override
	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += p.getSysex()[i];
		}
		p.getSysex()[ofs] = (byte) (sum & 0x7F);
	}

	@Override
	public void sendPatch(PatchDataImpl p) {
		sendPatch((PatchDataImpl) p, 0, 64); // using single mode edit buffer
	}

	// Sends a patch to a set location in the user bank
	@Override
	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		if (bankNum > 1) {
			JOptionPane.showMessageDialog(PatchBayApplication.getInstance(), "Cannot send to a preset bank", "Store Patch",
					JOptionPane.WARNING_MESSAGE);
		} else {
			sendPatch((PatchDataImpl) p, bankNum + 1, patchNum);
		}
	}

	@Override
	public void playPatch(PatchDataImpl p) {
		PatchDataImpl p2 = new PatchDataImpl(((PatchDataImpl) p).getSysex());
		p2.getSysex()[deviceIDoffset] = (byte) (getDeviceID() - 1);
		p2.getSysex()[BANK_NUM_OFFSET] = 0; // edit buffer
		p2.getSysex()[PATCH_NUM_OFFSET] = 64; // single mode
		calculateChecksum(p2);
		super.playPatch(p2);
	}

	@Override
	public PatchDataImpl createNewPatch() {
		return new PatchDataImpl(Virus.NEW_PROGRAM_PATCH, this);
	}

	@Override
	public void requestPatchDump(int bankNum, int patchNum) {
		send(sysexRequestDump.toSysexMessage(getDeviceID(), new SysexHandler.NameValue("bankNum", bankNum + 1), new SysexHandler.NameValue(
				"patchNum", patchNum)));
	}

	private void sendPatch(PatchDataImpl p, int bankNum, int patchNum) {
		PatchDataImpl p2 = new PatchDataImpl(p.getSysex());
		p2.getSysex()[deviceIDoffset] = (byte) (getDeviceID() - 1);
		p2.getSysex()[BANK_NUM_OFFSET] = (byte) bankNum;
		p2.getSysex()[PATCH_NUM_OFFSET] = (byte) patchNum;
		calculateChecksum(p2);
		sendPatchWorker(p2);
	}

}
