package org.jsynthlib.menu.window;

import javax.swing.JOptionPane;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.JSLFrameEvent;
import org.jsynthlib.menu.JSLFrameListener;
import org.jsynthlib.tools.ErrorMsgUtil;

class LibraryFrameListener implements JSLFrameListener {

	/**
	 * 
	 */
	private final AbstractLibraryFrame abstractLibraryFrame;

	/**
	 * @param abstractLibraryFrame
	 */
	LibraryFrameListener(AbstractLibraryFrame abstractLibraryFrame) {
		this.abstractLibraryFrame = abstractLibraryFrame;
	}

	public void JSLFrameActivated(JSLFrameEvent e) {
		this.abstractLibraryFrame.frameActivated();
	}

	public void JSLFrameClosed(JSLFrameEvent e) {
		if (PatchBayApplication.getDesktop().getAllFrames().length == 0) {
			Actions.setEnabled(false, Actions.EN_PASTE);
		}
		System.out.println(">>>> Closed");
	}

	public void JSLFrameClosing(JSLFrameEvent e) {
		if (!this.abstractLibraryFrame.changed)
			return;

		// close Patch/Bank Editor editing a patch in this frame.
		JSLFrame[] jList = PatchBayApplication.getDesktop().getAllFrames();
		System.out.println("Frames: " + jList.length);
		for (int j = 0; j < jList.length; j++) {
			if (jList[j] instanceof BankEditorFrame) {
				for (int i = 0; i < this.abstractLibraryFrame.myModel.getRowCount(); i++)
					if (((BankEditorFrame) (jList[j])).bankData == this.abstractLibraryFrame.myModel.getPatchAt(i)) {
						jList[j].moveToFront();
						try {
							jList[j].setSelected(true);
							jList[j].setClosed(true);
						} catch (Exception e1) {
							ErrorMsgUtil.reportStatus(e1);
						}
						break;
					}
			}
			if (jList[j] instanceof PatchEditorFrame) {
				for (int i = 0; i < this.abstractLibraryFrame.myModel.getRowCount(); i++)
					if (((PatchEditorFrame) (jList[j])).p == this.abstractLibraryFrame.myModel.getPatchAt(i)) {
						jList[j].moveToFront();
						try {
							jList[j].setSelected(true);
							jList[j].setClosed(true);
						} catch (Exception e1) {
							ErrorMsgUtil.reportStatus(e1);
						}
						break;
					}
			}
		}

		if (JOptionPane.showConfirmDialog(null, "This " + this.abstractLibraryFrame.TYPE + " may contain unsaved data.\nSave before closing?", "Unsaved Data",
				JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			return;

		this.abstractLibraryFrame.moveToFront();
		Actions.saveFrame();
	}

	public void JSLFrameDeactivated(JSLFrameEvent e) {
		Actions.setEnabled(false, Actions.EN_ALL);
	}

	public void JSLFrameDeiconified(JSLFrameEvent e) {
	}

	public void JSLFrameIconified(JSLFrameEvent e) {
	}

	public void JSLFrameOpened(JSLFrameEvent e) {
	}
}