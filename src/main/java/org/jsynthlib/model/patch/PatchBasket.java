package org.jsynthlib.model.patch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.JSynthImportFileType;

/**
 * This interface should be implemented by any window which serves as a holder or "basket" for patches.
 * 
 * @version $Id$
 */
public interface PatchBasket {
	/** Import a patch from a file. */
	void importPatch(File file, JSynthImportFileType type) throws IOException;

	/** Export a patch to a file. */
	void exportPatch(File file) throws IOException;

	/** Delete the selected patch. */
	void deleteSelectedPatches();

	/** Copy the selected patch. */
	void copySelectedPatch();

	/** Paste a patch from system clipboard or drag&drop buffer. */
	void pastePatch();

	/** Add a patch into the table of patches. */
	void pastePatch(Patch p); // XXX Shall we rename?

	/** Add a patch into the table of patches including bank and patch numbers. */
	void pastePatch(Patch p, int bankNum, int patchNum); 

	/** Get the selected patch. */
	Patch getSelectedPatch();

	
	/** Get the selected patch. */
	Patch[] getSelectedPatches();

	/**
	 * Send the selected patch to the Edit buffer of the synth for the patch. Only for Single Patch.
	 */
	void sendSelectedPatch();

	/**
	 * Send the selected patch to the Edit buffer of the synth specified by user. Only for Single Patch.
	 */
	void sendToSelectedPatch();

	/**
	 * Send the selected patch to a buffer of the synth specified by user. Only for Single Patch.
	 */
	void storeSelectedPatch();

	/** Reassign the driver of the selected patch. */
	void reassignSelectedPatch();

	/** Play the selected patch. */
	void playSelectedPatch();

	/** Play the selected patch. */
	void playAllPatches();

	/** Invoke an editor for the selected patch. */
	JSLFrame editSelectedPatch();

	/** Return collection of all patches in basket. */
	ArrayList<Patch> getPatchCollection();
}
