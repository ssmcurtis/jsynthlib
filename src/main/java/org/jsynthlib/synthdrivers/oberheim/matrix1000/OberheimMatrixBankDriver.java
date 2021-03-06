/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.oberheim.matrix1000;

import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsgUtil;

public class OberheimMatrixBankDriver extends SynthDriverBank {

	public OberheimMatrixBankDriver() {
		super("Bank", "Brian Klock", 100, 5);
		sysexID = "F010060**";
		// inquiryID="F07E**06021006000200*********F7";
		patchSize = 27500;
		patchNameStart = 5;
		patchNameSize = 8;
		deviceIDoffset = -1;
		bankNumbers = new String[] { "000 Bank", "100 Bank" };

		patchNumbers = DriverUtil.generateNumbers(0, 99, "00-");
		singleSize = 275;
		singleSysexID = "F010060**";
	}

	public int getPatchStart(int PatchNum) {
		return PatchNum * 275;
	}

	public void calculateChecksum(PatchDataImpl p) {
		for (int i = 0; i < 100; i++)
			calculateChecksum(p, 5 + getPatchStart(i), 272 + getPatchStart(i), 273 + getPatchStart(i));

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
		for (int i = 0; i < 100; i++) {
			((PatchDataImpl) p).getSysex()[3 + getPatchStart(i)] = 1;
			((PatchDataImpl) p).getSysex()[4 + getPatchStart(i)] = (byte) i;
		}
		sendPatchWorker(p);

	}

	public String getPatchName(PatchDataImpl p, int patchNum) {
		try {
			int start = getPatchStart(patchNum);
			byte[] b = new byte[8];
			b[0] = ((byte) (((PatchDataImpl) p).getSysex()[start + 5] + ((PatchDataImpl) p).getSysex()[start + 6] * 16));
			b[1] = ((byte) (((PatchDataImpl) p).getSysex()[start + 7] + ((PatchDataImpl) p).getSysex()[start + 8] * 16));
			b[2] = ((byte) (((PatchDataImpl) p).getSysex()[start + 9] + ((PatchDataImpl) p).getSysex()[start + 10] * 16));
			b[3] = ((byte) (((PatchDataImpl) p).getSysex()[start + 11] + ((PatchDataImpl) p).getSysex()[start + 12] * 16));
			b[4] = ((byte) (((PatchDataImpl) p).getSysex()[start + 13] + ((PatchDataImpl) p).getSysex()[start + 14] * 16));
			b[5] = ((byte) (((PatchDataImpl) p).getSysex()[start + 15] + ((PatchDataImpl) p).getSysex()[start + 16] * 16));
			b[6] = ((byte) (((PatchDataImpl) p).getSysex()[start + 17] + ((PatchDataImpl) p).getSysex()[start + 18] * 16));
			b[7] = ((byte) (((PatchDataImpl) p).getSysex()[start + 19] + ((PatchDataImpl) p).getSysex()[start + 20] * 16));
			StringBuffer s = new StringBuffer(new String(b, 0, 8, "US-ASCII"));
			return s.toString();
		} catch (Exception ex) {
			return "-";
		}
	}

	public void setPatchName(PatchDataImpl p, int patchNum, String name) {
		byte[] namebytes = new byte[32];
		try {
			int start = getPatchStart(patchNum);
			if (name.length() < 8)
				name = name + "        ";
			namebytes = name.getBytes("US-ASCII");
			((PatchDataImpl) p).getSysex()[start + 5] = ((byte) (namebytes[0] % 16));
			((PatchDataImpl) p).getSysex()[start + 6] = ((byte) (namebytes[0] / 16));
			((PatchDataImpl) p).getSysex()[start + 7] = ((byte) (namebytes[1] % 16));
			((PatchDataImpl) p).getSysex()[start + 8] = ((byte) (namebytes[1] / 16));
			((PatchDataImpl) p).getSysex()[start + 9] = ((byte) (namebytes[2] % 16));
			((PatchDataImpl) p).getSysex()[start + 10] = ((byte) (namebytes[2] / 16));
			((PatchDataImpl) p).getSysex()[start + 11] = ((byte) (namebytes[3] % 16));
			((PatchDataImpl) p).getSysex()[start + 12] = ((byte) (namebytes[3] / 16));
			((PatchDataImpl) p).getSysex()[start + 13] = ((byte) (namebytes[4] % 16));
			((PatchDataImpl) p).getSysex()[start + 14] = ((byte) (namebytes[4] / 16));
			((PatchDataImpl) p).getSysex()[start + 15] = ((byte) (namebytes[5] % 16));
			((PatchDataImpl) p).getSysex()[start + 16] = ((byte) (namebytes[5] / 16));
			((PatchDataImpl) p).getSysex()[start + 17] = ((byte) (namebytes[6] % 16));
			((PatchDataImpl) p).getSysex()[start + 18] = ((byte) (namebytes[6] / 16));
			((PatchDataImpl) p).getSysex()[start + 19] = ((byte) (namebytes[7] % 16));
			((PatchDataImpl) p).getSysex()[start + 20] = ((byte) (namebytes[7] / 16));
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in Matrix1000 Bank Driver", e);
		}
	}

	public void putPatch(PatchDataImpl bank, PatchDataImpl p, int patchNum) {
		if (!canHoldPatch(p)) {
			ErrorMsgUtil.reportError("Error", "This type of patch does not fit in to this type of bank.");
			return;
		}

		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, ((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum), 275);
		calculateChecksum(bank);
	}

	public PatchDataImpl extractPatch(PatchDataImpl bank, int patchNum) {
		try {
			byte[] sysex = new byte[275];
			System.arraycopy(((PatchDataImpl) bank).getSysex(), getPatchStart(patchNum), sysex, 0, 275);
			PatchDataImpl p = new PatchDataImpl(sysex, getDevice());
			p.calculateChecksum();
			return p;
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Error in Matrix 1000 Bank Driver", e);
			return null;
		}
	}

	// protected void sendPatch (Patch p)
	// {
	// byte []tmp=new byte[275];
	// if (deviceIDoffset>0) ((Patch)p).sysex[deviceIDoffset]=(byte)(getChannel()-1);
	// try {
	// for (int i=0;i<100;i++)
	// {
	// System.arraycopy(((Patch)p).sysex,275*i,tmp,0,275);
	// send(tmp);
	// Thread.sleep(15);
	// }
	// }catch (Exception e) {ErrorMsg.reportError("Error","Unable to send Patch",e);}
	// }
	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[27500];
		for (int i = 0; i < 100; i++) {
			sysex[0 + 275 * i] = (byte) 0xF0;
			sysex[1 + 275 * i] = (byte) 0x10;
			sysex[2 + 275 * i] = (byte) 0x06;
			sysex[3 + 275 * i] = (byte) 0x0D;
			sysex[4 + 275 * i] = (byte) 0x00;
			sysex[274 + 275 * i] = (byte) 0xF7;
		}
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		for (int i = 0; i < 100; i++)
			setPatchName(p, i, "NewPatch");
		calculateChecksum(p);
		return p;
	}

}
