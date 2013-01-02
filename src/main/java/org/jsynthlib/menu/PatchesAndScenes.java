package org.jsynthlib.menu;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsynthlib.model.patch.Patch;

public class PatchesAndScenes implements Transferable {

	DataFlavor[] flavors = new DataFlavor[] { PatchTransferHandler.PATCHES_FLAVOR };
	Map<Integer, Patch> patches = new HashMap<Integer, Patch>();

	public DataFlavor[] getTransferDataFlavors() {
		return (flavors);
	}

	public void add(Integer row, Patch patch) {
		patches.put(row, patch);
	}

	public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
		for (int i = 0; i < flavors.length; i++) {
			if (dataFlavor.equals(flavors[i])) {
				return (true);
			}
		}
		return (false);
	}

	public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(dataFlavor)) {
			throw new UnsupportedFlavorException(dataFlavor);
		}
		return (patches);
	}
}