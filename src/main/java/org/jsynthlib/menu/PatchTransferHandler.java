package org.jsynthlib.menu;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.tablemodel.PatchTableModel;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsgUtil;

@SuppressWarnings("serial")
public abstract class PatchTransferHandler extends TransferHandler {
	public static final DataFlavor PATCHES_FLAVOR = new DataFlavor(PatchesAndScenes.class, "Patch Array");
	// new DataFlavor(IPatch[].class, "Patch Array");

	public static final DataFlavor PATCH_FLAVOR = new DataFlavor(Patch[].class, "Patch Array");

	// public static final DataFlavor SCENE_FLAVOR = new DataFlavor(Scene[].class, "Scene Array");

	public static final DataFlavor TEXT_FLAVOR = new DataFlavor(String.class, "String");

	// private DataFlavor[] flavorsAccepted = new DataFlavor[] { PATCHES_FLAVOR, PATCH_FLAVOR, SCENE_FLAVOR,
	// TEXT_FLAVOR, };

	// TODO ssmCurtis - drag and drop
	private DataFlavor[] flavorsAccepted = new DataFlavor[] { PATCHES_FLAVOR, PATCH_FLAVOR, TEXT_FLAVOR, };

	protected abstract boolean storePatch(Patch p, JComponent c);

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	protected Transferable createTransferable(JComponent c) {
		PatchesAndScenes patchesAndScenes = new PatchesAndScenes();

		if (c instanceof JTable) {
			ErrorMsgUtil.reportStatus("Create transferable ...");

			JTable table = (JTable) c;
			PatchTableModel pm = (PatchTableModel) table.getModel();
			int[] rowIdxs = table.getSelectedRows();
			for (int i = 0; i < rowIdxs.length; i++) {
				Patch patch = pm.getPatchAt(table.convertRowIndexToModel(rowIdxs[i]));
				patchesAndScenes.add(rowIdxs[i], patch);
			}
		} else {
			ErrorMsgUtil.reportStatus("PatchTransferHandler.createTransferable doesn't recognize the component it was given");
		}
		return (patchesAndScenes);
	}

	// Used by LibraryFrame and BankEditorFrame.
	// SceneFrame overrides this.
	public boolean importData(JComponent targetComponent, Transferable transferable) {

		if (canImport(targetComponent, transferable.getTransferDataFlavors())) {

			try {
				if (transferable.isDataFlavorSupported(PATCHES_FLAVOR)) {

					ErrorMsgUtil.reportStatus("target: " + targetComponent.getClass().getName());

					boolean isMoveInside = false;

					LibraryFrame libraryFrame = null;

					if (PatchBayApplication.getDesktop().getSelectedFrame() instanceof LibraryFrame) {
						libraryFrame = (LibraryFrame) PatchBayApplication.getDesktop().getSelectedFrame();
						isMoveInside = libraryFrame.getTable().equals(targetComponent);
					}

					ErrorMsgUtil.reportStatus("is move inside " + isMoveInside);

					@SuppressWarnings("unchecked")
					Map<Integer, Patch> patches = (Map<Integer, Patch>) transferable.getTransferData(PATCHES_FLAVOR);

					if (isMoveInside) {
						if (patches.size() == 1) {
							for (Map.Entry<Integer, Patch> entry : patches.entrySet()) {

								Patch newPatch = (Patch) entry.getValue().clone();
								newPatch.findDriver();

								// INFO ssmCurtis - Q&D
								try {
									int row = libraryFrame.getTable().convertRowIndexToModel(entry.getKey());

									ErrorMsgUtil.reportStatus("Remove now original row " + row + "(" + entry.getKey() + ")");
									libraryFrame.getMyModel().removeAt(row);

									int targetRow = libraryFrame.getTable().getSelectedRow();
									ErrorMsgUtil.reportStatus("Add patch at row " + targetRow);

									libraryFrame.getMyModel().addPatch(targetRow, newPatch);

									libraryFrame.getMyModel().fireTableDataChanged();
									Patch px = libraryFrame.getPatchCollection().get(targetRow);

									// ErrorMsgUtil.reportStatus(px.getDevice().getModelName() + " " + px.getFileName());

									libraryFrame.getTable().getSelectionModel().setSelectionInterval(targetRow, targetRow);
								} catch (IndexOutOfBoundsException iobe) {
									// cut ... row already removed...
									libraryFrame.getMyModel().addPatch(newPatch);
								}

							}

						}
					} else {

						for (Map.Entry<Integer, Patch> entry : patches.entrySet()) {

							Patch patch = entry.getValue();
							/**
							 * Once we get the patch, we need to clone it for the recipient of the paste. Otherwise, it
							 * would be possible for the user to make multiple pastes from a single cut/copy and each
							 * window could be altering the *same* object. - Emenaker - 2006-02-26
							 */
							ErrorMsgUtil.reportStatus("Cloning: " + patch);
							Patch newPatch = (Patch) patch.clone();
							// Serialization loses a transient field, driver.
							newPatch.findDriver();

							if (!storePatch(newPatch, targetComponent)) {
								return (false);
							}
						}
					}

					// libraryFrame.getTable().clearSelection();

					return true;
				} else if (transferable.isDataFlavorSupported(TEXT_FLAVOR)) {
					// String s = (String) t.getTransferData(TEXT_FLAVOR);
					// IPatch p = getPatchFromUrl(s);
					// if (p != null)
					// return storePatch(p, c);
				}
			} catch (UnsupportedFlavorException e) {
				ErrorMsgUtil.reportStatus(e);
			} catch (IOException e) {
				ErrorMsgUtil.reportStatus(e);
			}
		}
		return false;
	}

	protected Patch getPatchFromUrl(String s) {

		try {
			ErrorMsgUtil.reportStatus("S = " + s);
			URL u = new URL(s);
			InputStream in = u.openStream();
			int b;
			int i = 0;
			byte[] buff = new byte[65536];
			do {
				b = in.read();
				if (b != -1) {
					buff[i] = (byte) b;
					i++;
				}
			} while (b != -1 && i < 65535);
			in.close();
			byte[] sysex = new byte[i];
			System.arraycopy(buff, 0, sysex, 0, i);
			return (DriverUtil.createPatch(sysex, s));
		} catch (MalformedURLException e) {
			ErrorMsgUtil.reportError("Data Paste Error", "Malformed URL", e);
		} catch (IOException e) {
			ErrorMsgUtil.reportError("Data Paste Error", "Network I/O Error", e);
		}
		return null;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavorsOffered) {
		for (int i = 0; i < flavorsOffered.length; i++) {
			// ErrorMsgUtil.reportStatus("PatchTransferHandler.canImport(" + flavorsOffered[i].getMimeType() + ")");
			// ErrorMsg.reportStatus(TEXT_FLAVOR.getMimeType());
			for (int j = 0; j < flavorsAccepted.length; j++) {
				if (flavorsAccepted[j].match(flavorsOffered[i])) {
					// ErrorMsg.reportStatus("PatchTransferHandler CAN import");
					return true;
				}
			}
		}
		// ErrorMsg.reportStatus("PatchTransferHandler can't import");
		return false;
	}

	/* Enable paste action when copying to clipboard. */
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
		super.exportToClipboard(comp, clip, action);
		Actions.setEnabled(true, Actions.EN_PASTE);
	}
}
