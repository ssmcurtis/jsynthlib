package org.jsynthlib.menu.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public class ProxyImportHandler extends TransferHandler {
	protected JComponent comp;
	protected TransferHandler proxy;

	public ProxyImportHandler(JComponent c, TransferHandler th) {
		comp = c;
		proxy = th;
	}

	public boolean importData(JComponent c, Transferable t) {
		return proxy.importData(comp, t);
	}

	public int getSourceActions(JComponent c) {
		return NONE;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		return proxy.canImport(comp, flavors);
	}

}