/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.yamaha.tx81z;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class YamahaTX81zBankDriver extends SynthDriverBank {

	public YamahaTX81zBankDriver() {
		super("Bank", "Brian Klock", 32, 4);
		sysexID = "F043**04*000";
		deviceIDoffset = 2;
		bankNumbers = new String[] { "0-Internal" };
		patchNumbers = new String[] { "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08", "I09", "I10", "I11",
				"I12", "I13", "I14", "I15", "I16", "I17", "I18", "I19", "I20", "I21", "I22", "I23", "I24", "I25",
				"I26", "I27", "I28", "I29", "I30", "I31", "I32" };
		singleSize = 142;
		singleSysexID = "F043**7E00214C4D2020383937364145";

	}

	public int getPatchStart(int patchNum) {
		int start = (128 * patchNum);
		start += 6; // sysex header

		return start;
	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		int nameStart = getPatchStart(patchNum);
		nameStart += 57; // offset of name in patch data
		try {
			StringBuffer s = new StringBuffer(new String(((PatchDataImpl) p).getSysex(), nameStart, 10, "US-ASCII"));
			return s.toString();
		} catch (UnsupportedEncodingException ex) {
			return "-";
		}

	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		patchNameSize = 10;
		patchNameStart = getPatchStart(patchNum) + 57;

		if (name.length() < patchNameSize)
			name = name + "            ";
		byte[] namebytes = new byte[64];
		try {
			namebytes = name.getBytes("US-ASCII");
			for (int i = 0; i < patchNameSize; i++)
				((PatchDataImpl) p).getSysex()[patchNameStart + i] = namebytes[i];

		} catch (UnsupportedEncodingException ex) {
			return;
		}

	}

	public void calculateChecksum(PatchDataImpl p) {
		calculateChecksum(p, 6, 4101, 4102);

	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0] = (byte) ((((PatchDataImpl) p).getSysex()[47])); // AR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1] = (byte) ((((PatchDataImpl) p).getSysex()[48])); // D1r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2] = (byte) ((((PatchDataImpl) p).getSysex()[49])); // D2r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3] = (byte) ((((PatchDataImpl) p).getSysex()[50])); // RR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4] = (byte) ((((PatchDataImpl) p).getSysex()[51])); // D1L
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5] = (byte) ((((PatchDataImpl) p).getSysex()[52])); // LS
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] = (byte) ((((PatchDataImpl) p).getSysex()[55] * 64
				+ ((PatchDataImpl) p).getSysex()[54] * 8 + ((PatchDataImpl) p).getSysex()[56]));// ame ebs kvs
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7] = (byte) ((((PatchDataImpl) p).getSysex()[57])); // out
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8] = (byte) ((((PatchDataImpl) p).getSysex()[58])); // freq
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9] = (byte) ((((PatchDataImpl) p).getSysex()[53] * 8 + ((PatchDataImpl) p).getSysex()[59])); // rs
																																				// dbt

		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 10] = (byte) ((((PatchDataImpl) p).getSysex()[47 + 13 * 1])); // AR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 11] = (byte) ((((PatchDataImpl) p).getSysex()[48 + 13 * 1])); // D1r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 12] = (byte) ((((PatchDataImpl) p).getSysex()[49 + 13 * 1])); // D2r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 13] = (byte) ((((PatchDataImpl) p).getSysex()[50 + 13 * 1])); // RR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 14] = (byte) ((((PatchDataImpl) p).getSysex()[51 + 13 * 1])); // D1L
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 15] = (byte) ((((PatchDataImpl) p).getSysex()[52 + 13 * 1])); // LS
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 16] = (byte) ((((PatchDataImpl) p).getSysex()[55 + 13 * 1] * 64
				+ ((PatchDataImpl) p).getSysex()[54 + 13 * 1] * 8 + ((PatchDataImpl) p).getSysex()[56 + 13 * 1]));// ame ebs kvs
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 17] = (byte) ((((PatchDataImpl) p).getSysex()[57 + 13 * 1])); // out
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 18] = (byte) ((((PatchDataImpl) p).getSysex()[58 + 13 * 1])); // freq
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 19] = (byte) ((((PatchDataImpl) p).getSysex()[53 + 13 * 1] * 8 + ((PatchDataImpl) p).getSysex()[59 + 13 * 1])); // rs
																																									// dbt

		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 20] = (byte) ((((PatchDataImpl) p).getSysex()[47 + 13 * 2])); // AR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 21] = (byte) ((((PatchDataImpl) p).getSysex()[48 + 13 * 2])); // D1r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 22] = (byte) ((((PatchDataImpl) p).getSysex()[49 + 13 * 2])); // D2r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 23] = (byte) ((((PatchDataImpl) p).getSysex()[50 + 13 * 2])); // RR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 24] = (byte) ((((PatchDataImpl) p).getSysex()[51 + 13 * 2])); // D1L
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 25] = (byte) ((((PatchDataImpl) p).getSysex()[52 + 13 * 2])); // LS
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 26] = (byte) ((((PatchDataImpl) p).getSysex()[55 + 13 * 2] * 64
				+ ((PatchDataImpl) p).getSysex()[54 + 13 * 2] * 8 + ((PatchDataImpl) p).getSysex()[56 + 13 * 2]));// ame ebs kvs
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 27] = (byte) ((((PatchDataImpl) p).getSysex()[57 + 13 * 2])); // out
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 28] = (byte) ((((PatchDataImpl) p).getSysex()[58 + 13 * 2])); // freq
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 29] = (byte) ((((PatchDataImpl) p).getSysex()[53 + 13 * 2] * 8 + ((PatchDataImpl) p).getSysex()[59 + 13 * 2])); // rs
																																									// dbt

		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 30] = (byte) ((((PatchDataImpl) p).getSysex()[47 + 13 * 3])); // AR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 31] = (byte) ((((PatchDataImpl) p).getSysex()[48 + 13 * 3])); // D1r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 32] = (byte) ((((PatchDataImpl) p).getSysex()[49 + 13 * 3])); // D2r
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 33] = (byte) ((((PatchDataImpl) p).getSysex()[50 + 13 * 3])); // RR
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 34] = (byte) ((((PatchDataImpl) p).getSysex()[51 + 13 * 3])); // D1L
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 35] = (byte) ((((PatchDataImpl) p).getSysex()[52 + 13 * 3])); // LS
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 36] = (byte) ((((PatchDataImpl) p).getSysex()[55 + 13 * 3] * 64
				+ ((PatchDataImpl) p).getSysex()[54 + 13 * 3] * 8 + ((PatchDataImpl) p).getSysex()[56 + 13 * 3]));// ame ebs kvs
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 37] = (byte) ((((PatchDataImpl) p).getSysex()[57 + 13 * 3])); // out
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 38] = (byte) ((((PatchDataImpl) p).getSysex()[58 + 13 * 3])); // freq
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 39] = (byte) ((((PatchDataImpl) p).getSysex()[53 + 13 * 3] * 8 + ((PatchDataImpl) p).getSysex()[59 + 13 * 3])); // rs
																																									// dbt

		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 40] = (byte) ((((PatchDataImpl) p).getSysex()[105] * 64
				+ ((PatchDataImpl) p).getSysex()[100] * 8 + ((PatchDataImpl) p).getSysex()[99]));// sync fbl alg
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 41] = (byte) ((((PatchDataImpl) p).getSysex()[101])); // lfs
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 42] = (byte) ((((PatchDataImpl) p).getSysex()[102])); // lfd
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 43] = (byte) ((((PatchDataImpl) p).getSysex()[103])); // pmd
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 44] = (byte) ((((PatchDataImpl) p).getSysex()[104])); // amd
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 45] = (byte) ((((PatchDataImpl) p).getSysex()[107] * 16
				+ ((PatchDataImpl) p).getSysex()[108] * 4 + ((PatchDataImpl) p).getSysex()[106]));// pms ams lfw
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 46] = (byte) ((((PatchDataImpl) p).getSysex()[109])); // traspose
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 47] = (byte) ((((PatchDataImpl) p).getSysex()[111])); // pbr
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48] = (byte) ((((PatchDataImpl) p).getSysex()[117] * 16
				+ ((PatchDataImpl) p).getSysex()[110] * 8 + ((PatchDataImpl) p).getSysex()[115] * 4 + ((PatchDataImpl) p).getSysex()[116]
				* 2 + ((PatchDataImpl) p).getSysex()[112]));// ch mo su po pm
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 49] = (byte) ((((PatchDataImpl) p).getSysex()[113])); // porta
																												// time
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 50] = (byte) ((((PatchDataImpl) p).getSysex()[114])); // footcontrol

		System.arraycopy(((PatchDataImpl) p).getSysex(), 118, ((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum) + 51, 22);

		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 73] = (byte) ((((PatchDataImpl) p).getSysex()[20] * 16
				+ ((PatchDataImpl) p).getSysex()[16] * 8 + ((PatchDataImpl) p).getSysex()[17]));// egsft,fix,fixrg
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 74] = (byte) ((((PatchDataImpl) p).getSysex()[19] * 16 + ((PatchDataImpl) p).getSysex()[18]));// osw
																																				// fine
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 75] = (byte) ((((PatchDataImpl) p).getSysex()[20 + 5] * 16
				+ ((PatchDataImpl) p).getSysex()[16 + 5] * 8 + ((PatchDataImpl) p).getSysex()[17 + 5]));// egsft,fix,fixrg
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 76] = (byte) ((((PatchDataImpl) p).getSysex()[19 + 5] * 16 + ((PatchDataImpl) p).getSysex()[18 + 5]));// osw
																																						// fine
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 77] = (byte) ((((PatchDataImpl) p).getSysex()[20 + 10] * 16
				+ ((PatchDataImpl) p).getSysex()[16 + 10] * 8 + ((PatchDataImpl) p).getSysex()[17 + 10]));// egsft,fix,fixrg
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 78] = (byte) ((((PatchDataImpl) p).getSysex()[19 + 10] * 16 + ((PatchDataImpl) p).getSysex()[18 + 10]));// osw
																																							// fine
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 79] = (byte) ((((PatchDataImpl) p).getSysex()[20 + 15] * 16
				+ ((PatchDataImpl) p).getSysex()[16 + 15] * 8 + ((PatchDataImpl) p).getSysex()[17 + 15]));// egsft,fix,fixrg
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 80] = (byte) ((((PatchDataImpl) p).getSysex()[19 + 15] * 16 + ((PatchDataImpl) p).getSysex()[18 + 15]));// osw
																																							// fine
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 81] = (byte) ((((PatchDataImpl) p).getSysex()[36])); // pbr
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 82] = (byte) ((((PatchDataImpl) p).getSysex()[37])); // pbr
		((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 83] = (byte) ((((PatchDataImpl) p).getSysex()[38])); // pbr

		calculateChecksum(bank);
	}

	public PatchDataImpl getPatch(PatchDataImpl bank, int patchNum) {
		try {
			byte[] sysex = new byte[142];
			// First create ACED Data
			sysex[00] = (byte) 0xF0;
			sysex[01] = (byte) 0x43;
			sysex[02] = (byte) 0x00;
			sysex[03] = (byte) 0x7E;
			sysex[04] = (byte) 0x00;
			sysex[05] = (byte) 0x21;
			sysex[06] = (byte) 0x4C;
			sysex[07] = (byte) 0x4D;
			sysex[8] = (byte) 0x20;
			sysex[9] = (byte) 0x20;
			sysex[10] = (byte) 0x38;
			sysex[11] = (byte) 0x39;
			sysex[12] = (byte) 0x37;
			sysex[13] = (byte) 0x36;
			sysex[14] = (byte) 0x41;
			sysex[15] = (byte) 0x45;

			sysex[16] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 73] & 8) / 8); // FIX
			sysex[17] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 73] & 7)); // FixRG
			sysex[18] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 74] & 15)); // FreqRangeFine
			sysex[19] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 74] & 112) / 16); // Operator
																											// WaveForm
			sysex[20] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 73] & 48) / 16); // EGShift

			sysex[21] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 75] & 8) / 8); // FIX
			sysex[22] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 75] & 7)); // FixRG
			sysex[23] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 76] & 15)); // FreqRangeFine
			sysex[24] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 76] & 112) / 16); // Operator
																											// WaveForm
			sysex[25] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 75] & 48) / 16); // EGShift

			sysex[26] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 77] & 8) / 8); // FIX
			sysex[27] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 77] & 7)); // FixRG
			sysex[28] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 78] & 15)); // FreqRangeFine
			sysex[29] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 78] & 112) / 16); // Operator
																											// WaveForm
			sysex[30] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 77] & 48) / 16); // EGShift

			sysex[31] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 79] & 8) / 8); // FIX
			sysex[32] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 79] & 7)); // FixRG
			sysex[33] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 80] & 15)); // FreqRangeFine
			sysex[34] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 80] & 112) / 16); // Operator
																											// WaveForm
			sysex[35] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 79] & 48) / 16); // EGShift

			sysex[36] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 81])); // Reverb Rate
			sysex[37] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 82])); // FC Pitch
			sysex[38] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 83])); // FC Amp
			sysex[40] = (byte) (0xF7); // (Chksm to be added later)
			// Then create VCED Data
			sysex[41] = (byte) 0xF0;
			sysex[42] = (byte) 0x43;
			sysex[43] = (byte) 0x00;
			sysex[44] = (byte) 0x03;
			sysex[45] = (byte) 0x00;
			sysex[46] = (byte) 0x5D;

			sysex[47] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0])); // AR
			sysex[48] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1])); // d1r
			sysex[49] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2])); // d2r
			sysex[50] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3])); // rr
			sysex[51] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4])); // d1l
			sysex[52] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5])); // ls
			sysex[53] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9] & 24) / 8);// rate scaling;
			sysex[54] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] & 56) / 8);// ebs
			sysex[55] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] & 64) / 64);// ame
			sysex[56] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6] & 7)); // kvs
			sysex[57] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7])); // out
			sysex[58] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8])); // frs
			sysex[59] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9] & 7)); // dbt(det)

			sysex[60] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0 + 10])); // AR
			sysex[61] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1 + 10])); // d1r
			sysex[62] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2 + 10])); // d2r
			sysex[63] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3 + 10])); // rr
			sysex[64] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4 + 10])); // d1l
			sysex[65] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5 + 10])); // ls
			sysex[66] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9 + 10] & 24) / 8);// rate
																											// scaling;
			sysex[67] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 10] & 56) / 8);// ebs
			sysex[68] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 10] & 64) / 64);// ame
			sysex[69] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 10] & 7)); // kvs
			sysex[70] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7 + 10])); // out
			sysex[71] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8 + 10])); // frs
			sysex[72] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9 + 10] & 7)); // dbt(det)

			sysex[73] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0 + 20])); // AR
			sysex[74] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1 + 20])); // d1r
			sysex[75] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2 + 20])); // d2r
			sysex[76] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3 + 20])); // rr
			sysex[77] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4 + 20])); // d1l
			sysex[78] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5 + 20])); // ls
			sysex[79] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9 + 20] & 24) / 8);// rate
																											// scaling;
			sysex[80] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 20] & 56) / 8);// ebs
			sysex[81] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 20] & 64) / 64);// ame
			sysex[82] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 20] & 7)); // kvs
			sysex[83] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7 + 20])); // out
			sysex[84] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8 + 20])); // frs
			sysex[85] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9 + 20] & 7)); // dbt(det)

			sysex[86] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 0 + 30])); // AR
			sysex[87] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 1 + 30])); // d1r
			sysex[88] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 2 + 30])); // d2r
			sysex[89] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 3 + 30])); // rr
			sysex[90] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 4 + 30])); // d1l
			sysex[91] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 5 + 30])); // ls
			sysex[92] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9 + 30] & 24) / 8);// rate
																											// scaling;
			sysex[93] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 30] & 56) / 8);// ebs
			sysex[94] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 30] & 64) / 64);// ame
			sysex[95] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 6 + 30] & 7)); // kvs
			sysex[96] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 7 + 30])); // out
			sysex[97] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 8 + 30])); // frs
			sysex[98] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 9 + 30] & 7)); // dbt(det)

			sysex[99] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 40] & 7)); // algorithem
			sysex[100] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 40] & 56) / 8); // feedback
			sysex[101] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 41])); // lfo speed
			sysex[102] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 42])); // lfo delay
			sysex[103] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 43])); // pmod depth
			sysex[104] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 44])); // amod depth
			sysex[105] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 40] & 64) / 64); // sync
			sysex[106] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 45] & 3)); // lfw
			sysex[107] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 45] & 112) / 16); // pms
			sysex[108] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 45] & 12) / 4); // ams
			sysex[109] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 46])); // transpose
			sysex[110] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48] & 8) / 8); // polymode ***
			sysex[111] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 47])); // pitchbendrange
			sysex[112] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48] & 1)); // portamento mode***
			sysex[113] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 49])); // portamento time
			sysex[114] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 50])); // foot control volume
			sysex[115] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48] & 4) / 4); // sustain
			sysex[116] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48] & 2) / 2); // portamento***
			sysex[117] = (byte) ((((PatchDataImpl) bank).getSysex()[getPatchStart(patchNum) + 48] & 16) / 16); // chorus

			sysex[141] = (byte) 0xF7;
			System.arraycopy(((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum) + 51, sysex, 118, 22);
			PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
			p.calculateChecksum();
			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in TX81z Bank Driver", e);
			return null;
		}
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[4104];
		sysex[00] = (byte) 0xF0;
		sysex[01] = (byte) 0x43;
		sysex[02] = (byte) 0x00;
		sysex[03] = (byte) 0x04;
		sysex[04] = (byte) 0x20;
		sysex[05] = (byte) 0x00;
		sysex[4103] = (byte) 0xF7;

		PatchDataImpl p = new PatchDataImpl(sysex, this);
		for (int i = 0; i < 32; i++)
			setPatchName(p, i, "NewPatch");
		calculateChecksum(p);
		return p;
	}

}
