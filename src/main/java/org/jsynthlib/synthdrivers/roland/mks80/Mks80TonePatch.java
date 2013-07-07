package org.jsynthlib.synthdrivers.roland.mks80;

import org.jsynthlib.model.patch.PatchDataImpl;

public class Mks80TonePatch {

	public static boolean isPatchWhole(PatchDataImpl patch) {
		return (patch.getByteArray()[39] == (byte) 0x03);
	}
}
