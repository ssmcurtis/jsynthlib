package org.jsynthlib.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.jsynthlib.menu.PatchTransferHandler;
import org.jsynthlib.model.patch.Patch;

public class ClipboardUtil implements ClipboardOwner {
	protected final static ClipboardUtil instance = new ClipboardUtil();

	protected final static Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

	public static void storePatch(Patch p) {
		try {
			c.setContents(p, instance);
		} catch (IllegalStateException e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	public static Patch getPatch() { // not used
		try {
			Transferable t = c.getContents(instance);
			return (Patch) t.getTransferData(PatchTransferHandler.PATCH_FLAVOR);
		} catch (IllegalStateException e) {
			ErrorMsgUtil.reportStatus(e);
		} catch (ClassCastException e) {
			ErrorMsgUtil.reportStatus(e);
		} catch (UnsupportedFlavorException e) {
			ErrorMsgUtil.reportStatus(e);
		} catch (java.io.IOException e) {
			ErrorMsgUtil.reportStatus(e);
		}
		return null;
	}

	// ClipboardOwner method
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

}