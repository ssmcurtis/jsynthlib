package org.jsynthlib.tools;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchBank;

public class TableUtil {

	public static void addPatchToTable(Patch[] patchAarray) {
		LibraryFrame frame = (LibraryFrame) PatchBayApplication.getDesktop().getSelectedFrame();
		
		for (int k = 0; k < patchAarray.length; k++) {
			
			Patch pk = patchAarray[k];

			if (pk.isBankPatch()) {

				String[] pn = pk.getDriver().getPatchNumbers();

				for (int j = 0; j < ((PatchBank) pk).getNumPatches(); j++) {
					Patch q = ((PatchBank) pk).get(j);
					q.setFileName(pk.getFileName());
					q.setComment(pn[j]);

					frame.getMyModel().addPatch(q);
				}
			} else {
				frame.getMyModel().addPatch(pk);
			}
			// INFO moved .. frame.revalidateDrivers();
		}
		frame.revalidateDrivers();

	}

}
