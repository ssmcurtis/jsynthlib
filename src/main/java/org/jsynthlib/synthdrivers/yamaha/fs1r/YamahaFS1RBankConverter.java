package org.jsynthlib.synthdrivers.yamaha.fs1r;

import org.jsynthlib.model.driver.ConverterImpl;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;

/**
 * Convert from FS1R bank OS9 editor into JSynthLib bank format.
 * 
 * @author Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version $Id$
 */
public class YamahaFS1RBankConverter extends ConverterImpl {
	public YamahaFS1RBankConverter() {
		super("Bank Dump Converter", "Denis Queffeulou");
		// en fait ce n'est pas un sysex donc je met le debut du fichier
		// sauf le premier caractere (F0) qui est mis en dur dans le code core...
		// Il ne semble pas prevu d'importer autre chose que des sysex !
		sysexID = "F02E3*3*";
		patchSize = 129340;
	}

	public PatchDataImpl[] extractPatch(PatchDataImpl p) {
		PatchDataImpl oBank[] = new PatchDataImpl[1];
		oBank[0] = importFSEditor(p.getByteArray());
		return oBank;
	}

	PatchDataImpl importFSEditor(byte[] aBuffer) {
		PatchDataImpl oBank = (PatchDataImpl) YamahaFS1RBankDriver.getInstance().createNewPatch();
		int oIndex = 26;
		int oIDest = YamahaFS1RBankDriver.DATA_START;
		for (int p = 0; p < 128; p++) {
			oBank.getSysex()[oIDest++] = (byte) 0xF0;
			oBank.getSysex()[oIDest++] = (byte) 0x43;
			oBank.getSysex()[oIDest++] = (byte) 0x0;
			oBank.getSysex()[oIDest++] = (byte) 0x5E;
			int oCSStart = oIDest;
			oBank.getSysex()[oIDest++] = (byte) 0x03;
			oBank.getSysex()[oIDest++] = (byte) 0x10;
			oBank.getSysex()[oIDest++] = (byte) 0x11;
			oBank.getSysex()[oIDest++] = (byte) 0;
			oBank.getSysex()[oIDest++] = (byte) p;
			for (int b = 0; b < YamahaFS1RPerformanceDriver.PATCH_SIZE; b++) {
				oBank.getSysex()[oIDest++] = aBuffer[oIndex++];
			}
			int oCSEnd = oIDest - 1;
			oIDest++;
			oBank.getSysex()[oIDest++] = (byte) 0xF7;
			DriverUtil.calculateChecksum(oBank.getSysex(), oCSStart, oCSEnd, oCSEnd + 1);
		}
		for (int v = 0; v < 128; v++) {
			oBank.getSysex()[oIDest++] = (byte) 0xF0;
			oBank.getSysex()[oIDest++] = (byte) 0x43;
			oBank.getSysex()[oIDest++] = (byte) 0x0;
			oBank.getSysex()[oIDest++] = (byte) 0x5E;
			int oCSStart = oIDest;
			oBank.getSysex()[oIDest++] = (byte) 0x04;
			oBank.getSysex()[oIDest++] = (byte) 0x60;
			oBank.getSysex()[oIDest++] = (byte) 0x51;
			oBank.getSysex()[oIDest++] = (byte) 0;
			oBank.getSysex()[oIDest++] = (byte) v;
			for (int b = 0; b < YamahaFS1RVoiceDriver.PATCH_SIZE; b++) {
				oBank.getSysex()[oIDest++] = aBuffer[oIndex++];
			}
			int oCSEnd = oIDest - 1;
			oIDest++;
			oBank.getSysex()[oIDest++] = (byte) 0xF7;
			DriverUtil.calculateChecksum(oBank.getSysex(), oCSStart, oCSEnd, oCSEnd + 1);
		}
		return oBank;
	}

}
