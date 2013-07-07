package org.jsynthlib.tools;

import java.util.Collection;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchBank;

public class TableUtil {

	public static void addPatchToTable(Collection<Patch> patchAarray) {
		addPatchToTable(patchAarray.toArray(new Patch[] {}));
	}

	@Deprecated
	public static void addPatchToTable(Patch[] patchAarray) {
		addPatchToTable(patchAarray, false);
	}

	@Deprecated
	public static void addPatchToTable(Patch[] patchAarray, boolean overwriteComment) {

		// INFO ADD PATCHES TO TABLE

		LibraryFrame frame = (LibraryFrame) PatchBayApplication.getDesktop().getSelectedFrame();

		// introduced for MKS80
		int patchCount = 0;

		for (int k = 0; k < patchAarray.length; k++) {

			Patch pk = patchAarray[k];
			System.out.println("Size: " + pk.getByteArray().length);
			// System.out.println(HexaUtil.hexDump(pk.getByteArray(), 0, -1, 16));

			if (pk.isBankPatch()) {

				String[] pn = pk.getDriver().getPatchNumbers();

				// TODO check use of NumPathces
				for (int j = 0; j < ((PatchBank) pk).getNumPatches(); j++) {
					Patch patchFromBank = ((PatchBank) pk).getExtractedPatch(j);

					if (patchFromBank != null) {
						System.out.println("P: \n" + HexaUtil.hexDump(patchFromBank.getByteArray(), 0, -1, 16));

						patchFromBank.setFileName(pk.getFileName());

						int nameIndex = patchCount > pn.length-1 ? j : patchCount;
						if (overwriteComment) {
							patchFromBank.setComment(pn[nameIndex]);
						} else {
							patchFromBank.setComment(pk.getComment() + " " + pn[nameIndex]);
						}
						frame.getMyModel().addPatch(patchFromBank);
						patchCount++;
					}
				}
			} else {
				frame.getMyModel().addPatch(pk);
			}
			// INFO removed .. frame.revalidateDrivers();
		}
		frame.revalidateDrivers();

	}

}
