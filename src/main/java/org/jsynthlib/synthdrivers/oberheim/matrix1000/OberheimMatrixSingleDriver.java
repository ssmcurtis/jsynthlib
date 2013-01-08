/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.oberheim.matrix1000;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;

public class OberheimMatrixSingleDriver extends SynthDriverPatchImpl {

	public OberheimMatrixSingleDriver() {
		super("Single", "Brian Klock");
		sysexID = "F010060**";
		// inquiryID="F07E**0602100600*************F7";
		sysexRequestDump = new SysexHandler("F0 10 06 04 01 *patchNum* F7");

		patchSize = 275;
		patchNameStart = 5;
		patchNameSize = 8;
		deviceIDoffset = -1;
		bankNumbers = new String[] { "000 Bank", "100 Bank" };

		patchNumbers = new String[] { "00-", "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-", "10-",
				"11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-", "19-", "20-", "21-", "22-", "23-", "24-",
				"25-", "26-", "27-", "28-", "29-", "30-", "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-",
				"39-", "40-", "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-", "49-", "50-", "51-", "52-",
				"53-", "54-", "55-", "56-", "57-", "58-", "59-", "60-", "61-", "62-", "63-", "64-", "65-", "66-",
				"67-", "68-", "69-", "70-", "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-", "79-", "80-",
				"81-", "82-", "83-", "84-", "85-", "86-", "87-", "88-", "89-", "90-", "91-", "92-", "93-", "94-",
				"95-", "96-", "97-", "98-", "99-" };
	}

	public void calculateChecksum(PatchDataImpl p) {
		calculateChecksum(p, 5, 272, 273);

	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++)
			if (i % 2 != 0)
				sum += p.getSysex()[i];
			else
				sum += (p.getSysex()[i] * 16);
		p.getSysex()[ofs] = (byte) (sum % 128);

	}

	public void setBankNum(int bankNum) {
		try {
			send(new byte[] { (byte) 0xF0, (byte) 0x10, (byte) 0x06, (byte) 0x0A, (byte) bankNum, (byte) 0xF7 });
		} catch (Exception e) {
		}
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		sendProgramChange(patchNum);
		((PatchDataImpl) p).getSysex()[3] = 1;
		((PatchDataImpl) p).getSysex()[4] = (byte) patchNum;
		sendPatchWorker(p);

	}

	public void sendPatch(PatchDataImpl p) {
		((PatchDataImpl) p).getSysex()[3] = 0x0D;
		((PatchDataImpl) p).getSysex()[4] = 0;
		sendPatchWorker(p);
	}

	public String getPatchName(PatchDataImpl p) {
		PatchDataImpl ip = (PatchDataImpl) p;
		try {
			byte[] b = new byte[8];
			b[0] = ((byte) (ip.getSysex()[5] + ip.getSysex()[6] * 16));
			b[1] = ((byte) (ip.getSysex()[7] + ip.getSysex()[8] * 16));
			b[2] = ((byte) (ip.getSysex()[9] + ip.getSysex()[10] * 16));
			b[3] = ((byte) (ip.getSysex()[11] + ip.getSysex()[12] * 16));
			b[4] = ((byte) (ip.getSysex()[13] + ip.getSysex()[14] * 16));
			b[5] = ((byte) (ip.getSysex()[15] + ip.getSysex()[16] * 16));
			b[6] = ((byte) (ip.getSysex()[17] + ip.getSysex()[18] * 16));
			b[7] = ((byte) (ip.getSysex()[19] + ip.getSysex()[20] * 16));
			StringBuffer s = new StringBuffer(new String(b, 0, 8, "US-ASCII"));
			return s.toString();
		} catch (Exception ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, String name) {
		byte[] namebytes = new byte[32];
		try {
			if (name.length() < 8)
				name = name + "        ";
			namebytes = name.getBytes("US-ASCII");
			((PatchDataImpl) p).getSysex()[5] = ((byte) (namebytes[0] % 16));
			((PatchDataImpl) p).getSysex()[6] = ((byte) (namebytes[0] / 16));
			((PatchDataImpl) p).getSysex()[7] = ((byte) (namebytes[1] % 16));
			((PatchDataImpl) p).getSysex()[8] = ((byte) (namebytes[1] / 16));
			((PatchDataImpl) p).getSysex()[9] = ((byte) (namebytes[2] % 16));
			((PatchDataImpl) p).getSysex()[10] = ((byte) (namebytes[2] / 16));
			((PatchDataImpl) p).getSysex()[11] = ((byte) (namebytes[3] % 16));
			((PatchDataImpl) p).getSysex()[12] = ((byte) (namebytes[3] / 16));
			((PatchDataImpl) p).getSysex()[13] = ((byte) (namebytes[4] % 16));
			((PatchDataImpl) p).getSysex()[14] = ((byte) (namebytes[4] / 16));
			((PatchDataImpl) p).getSysex()[15] = ((byte) (namebytes[5] % 16));
			((PatchDataImpl) p).getSysex()[16] = ((byte) (namebytes[5] / 16));
			((PatchDataImpl) p).getSysex()[17] = ((byte) (namebytes[6] % 16));
			((PatchDataImpl) p).getSysex()[18] = ((byte) (namebytes[6] / 16));
			((PatchDataImpl) p).getSysex()[19] = ((byte) (namebytes[7] % 16));
			((PatchDataImpl) p).getSysex()[20] = ((byte) (namebytes[7] / 16));
		} catch (Exception e) {
		}
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[275];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x10;
		sysex[2] = (byte) 0x06;
		sysex[3] = (byte) 0x0D;
		sysex[4] = (byte) 0x00;
		sysex[274] = (byte) 0xF7;
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		setPatchName(p, "NewPatch");
		calculateChecksum(p);
		return p;
	}

	public JSLFrame editPatch(PatchDataImpl p) {
		return new OberheimMatrixSingleEditor((PatchDataImpl) p);
	}
}
