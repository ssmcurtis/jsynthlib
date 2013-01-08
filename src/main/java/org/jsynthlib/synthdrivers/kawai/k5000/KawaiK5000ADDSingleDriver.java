/*
 * @version $Id$
 */
package org.jsynthlib.synthdrivers.kawai.k5000;

import java.io.InputStream;

import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class KawaiK5000ADDSingleDriver extends SynthDriverPatchImpl {
	final static SysexHandler SYSEX_REQUEST_A_DUMP = new SysexHandler("F0 40 @@ 00 00 0A 00 00 *patchNum* F7"); // phil@muqus.com
																												// (p23)
	final static SysexHandler SYSEX_REQUEST_D_DUMP = new SysexHandler("F0 40 @@ 00 00 0A 00 02 *patchNum* F7"); // phil@muqus.com

	public KawaiK5000ADDSingleDriver() {
		super("Add Single", "Brian Klock");
		sysexID = "F040**20000A000*";
		patchSize = 0;
		numSysexMsgs = 1; // phil@muqus.com
		patchNameStart = 49;
		patchNameSize = 8;
		deviceIDoffset = 2;
		checksumStart = 10;
		checksumEnd = 0;
		checksumOffset = 9;
		bankNumbers = new String[] { "0-Bank A", "1-Bank B", "2-Bank C", "3-Bank D" };
		patchNumbers = new String[] { "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-", "10-", "11-",
				"12-", "13-", "14-", "15-", "16-", "17-", "18-", "19-", "20-", "21-", "22-", "23-", "24-", "25-",
				"26-", "27-", "28-", "29-", "30-", "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-", "39-",
				"40-", "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-", "49-", "50-", "51-", "52-", "53-",
				"54-", "55-", "56-", "57-", "58-", "59-", "60-", "61-", "62-", "63-", "64-", "65-", "66-", "67-",
				"68-", "69-", "70-", "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-", "79-", "80-", "81-",
				"82-", "83-", "84-", "85-", "86-", "87-", "88-", "89-", "90-", "91-", "92-", "93-", "94-", "95-",
				"104-", "105-", "106-", "107-", "108-", "109-", "110-", "111-", "112-", "113-", "114-", "115-", "116-",
				"117-", "118-", "119-", "120-", "121-", "122-", "123-", "124-", "125-", "126-", "127-", "128-" };

	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		sendProgramChange(patchNum);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		((PatchDataImpl) p).getSysex()[3] = (byte) 0x20;
		((PatchDataImpl) p).getSysex()[8] = (byte) (patchNum);
		if (bankNum == 0)
			((PatchDataImpl) p).getSysex()[7] = 0; // bank a
		if (bankNum == 3)
			((PatchDataImpl) p).getSysex()[7] = 2; // bank d

		sendPatchWorker(p);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		sendProgramChange(patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		storePatch(p, 0, 0);
	}

	public void calculateChecksum(PatchDataImpl ip) {
		PatchDataImpl p = (PatchDataImpl) ip;
		calculateChecksum(p, checksumStart, 90 + p.getSysex()[60] * 86, checksumOffset);
		int sourceDataStart = 91;
		int numWaveData = 0;
		for (int i = 0; i < p.getSysex()[60]; i++) {
			if (((p.getSysex()[sourceDataStart + 28] & 7) * 128 + p.getSysex()[sourceDataStart + 29]) == 512)
				numWaveData++;
			sourceDataStart += 86;
		}
		int waveDataStart = 91 + p.getSysex()[60] * 86;
		for (int i = 0; i < numWaveData; i++) {
			calculateChecksum(p, waveDataStart + 1, waveDataStart + 805, waveDataStart);

			waveDataStart += 806;
		}
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		int sum = 0;
		for (int i = start; i <= end; i++)
			sum += p.getSysex()[i];
		sum += (byte) 0xA5;
		p.getSysex()[ofs] = (byte) (sum % 128);
		// p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
		// p.sysex[ofs]=(byte)(p.sysex[ofs]+1);

	}

	/*
	 * public Patch createNewPatch() { byte [] sysex = new byte[140]; sysex[0]=(byte)0xF0;
	 * sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x23;sysex[4]=(byte)0x00; sysex[5]=(byte)0x04;
	 * sysex[6]=(byte)0x0;sysex[139]=(byte)0xF7; Patch p = new Patch(sysex); p.ChooseDriver();
	 * setPatchName(p,"New Patch"); calculateChecksum(p); return p; }
	 */

	// ----- Start phil@muqus.com
	// ----------------------------------------------------------------------------------------------------------------------
	// KawaiK5000ADDSingleDriver->requestPatchDump
	// ----------------------------------------------------------------------------------------------------------------------

	public void requestPatchDump(int bankNum, int patchNum) {
		if (bankNum == 0)
			send(SYSEX_REQUEST_A_DUMP.toSysexMessage(getChannel(), patchNum));
		else
			send(SYSEX_REQUEST_D_DUMP.toSysexMessage(getChannel(), patchNum));
	}

	// ----- End phil@muqus.com

//	public JSLFrame editPatch(Patch p) {
//		return new KawaiK5000ADDSingleEditor((Patch) p);
//	}
//
	public PatchDataImpl createNewPatch() {
		try {
			InputStream fileIn = getClass().getResourceAsStream("k5k.syx");
			byte[] buffer = new byte[2768];
			fileIn.read(buffer);
			fileIn.close();
			return new PatchDataImpl(buffer, this);
		} catch (Exception e) {
			ErrorMsgUtil.reportError("Error", "Unable to find Defaults", e);
			return null;
		}
	}

}
