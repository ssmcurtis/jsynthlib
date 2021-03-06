package org.jsynthlib.synthdrivers.yamaha.fs1r;

import java.awt.Dimension;

import javax.swing.JTable;

import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.window.BankEditorFrame;
import org.jsynthlib.menu.window.PatchEditorFrame;
import org.jsynthlib.model.patch.PatchDataImpl;

/**
 * Specific bank editor for YamahaFS1R. This bank holds 128 voices + 128 performances.
 * 
 * @author denis queffeulou mailto:dqueffeulou@free.fr
 */
public class YamahaFS1RBankEditor extends BankEditorFrame {
	{
		preferredScrollableViewportSize = new Dimension(100, 100);
		autoResizeMode = JTable.AUTO_RESIZE_OFF;
		preferredColumnWidth = 130;
	}

	public YamahaFS1RBankEditor(PatchDataImpl p) {
		super(p);
	}

	/**
	 * Edit a patch without select it. This allow the performance to edit a patch from the bank when the user clic on
	 * Edit in a performance part.
	 * 
	 * @param aPart
	 *            performance part number 1..4
	 * @see YamahaFS1RPerformanceEditor
	 */
	public JSLFrame EditPatch(int aNumPatch, int aPart) {
		PatchDataImpl p = (PatchDataImpl) bankData.getExtractedPatch(aNumPatch);
		if (p == null) {
			return null;
		}
		PatchEditorFrame pf = (PatchEditorFrame) (YamahaFS1RVoiceDriver.getInstance().editPatch(p, aPart,
				aNumPatch - 128));
		pf.setBankEditorInformation(this, aNumPatch % YamahaFS1RBankDriver.NB_ROWS, aNumPatch
				/ YamahaFS1RBankDriver.NB_ROWS);
		return pf;
	}

	public PatchDataImpl getBankPatch() {
		return (PatchDataImpl) bankData;
	}

}