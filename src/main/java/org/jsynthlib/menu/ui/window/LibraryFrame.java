package org.jsynthlib.menu.ui.window;

import java.awt.Toolkit;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.menu.action.Actions;
import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.ui.ExtensionFilter;
import org.jsynthlib.menu.ui.PatchTransferHandler;
import org.jsynthlib.tools.ErrorMsg;

/**
 * @version $Id$
 */
public class LibraryFrame extends AbstractLibraryFrame {
	// This is a comparator class used by the delete duplicated action to sort based on the sysex data
	// Sorting this way makes the Dups search much easier, since the dups must be next to each other
	private static class SysexSort implements Comparator<Object> {
		public int compare(Object a1, Object a2) {
			String s1 = new String(((IPatch) (a1)).getByteArray());
			String s2 = new String(((IPatch) (a2)).getByteArray());
			return s1.compareTo(s2);
		}
	}

	private static int openFrameCount = 0;

	public static final String FILE_EXTENSION = ".patchlib";

	private static final FileFilter FILE_FILTER = new ExtensionFilter("PatchEdit Library Files (*" + FILE_EXTENSION + ")", FILE_EXTENSION);

	private static final PatchTransferHandler pth = new PatchListTransferHandler();

	public LibraryFrame() {
		super("Unsaved Library #" + (++openFrameCount), "Library", pth);
	}

	public LibraryFrame(File file) {
		super(file.getName(), "Library", pth);
	}

	public PatchTableModel createTableModel() {
		return new PatchListModel(/* false */);
	}

	@Deprecated
	void deleteDuplicates() {
		Collections.sort(myModel.getList(), new SysexSort());
		int numDeleted = 0;
		Iterator<IPatch> it = myModel.getList().iterator();
		byte[] p = ((IPatch) it.next()).getByteArray();
		while (it.hasNext()) {
			byte[] q = ((IPatch) it.next()).getByteArray();
			if (Arrays.equals(p, q)) {
				it.remove();
				numDeleted++;
			} else
				p = q;
		}
		JOptionPane.showMessageDialog(null, numDeleted + " PatchesAndScenes were Deleted", "Delete Duplicates",
				JOptionPane.INFORMATION_MESSAGE);
		setChanged();
	}

	/** change state of Actions based on the state of the table. */
	void enableActions() {
		// one or more patches are included.
		Actions.setEnabled(table.getRowCount() > 0, Actions.EN_PLAY_ALL | Actions.EN_SAVE | Actions.EN_SAVE_AS | Actions.EN_SEARCH);

		// // more than one patches are included.
		// Actions.setEnabled(table.getRowCount() > 1,
		// Actions.EN_DELETE_DUPLICATES | Actions.EN_SORT);

		// one or more patches are selected
		Actions.setEnabled(table.getSelectedRowCount() > 0, Actions.EN_DELETE);

		// TODO ssmcurtis check one patch is selected
		Actions.setEnabled(table.getSelectedRowCount() == 1, Actions.EN_COPY | Actions.EN_CUT | Actions.EN_EXPORT | Actions.EN_REASSIGN
				| Actions.EN_STORE | Actions.EN_UPLOAD );

		// one signle patch is selected
		Actions.setEnabled(table.getSelectedRowCount() == 1
				&& myModel.getPatchAt(table.convertRowIndexToModel(table.getSelectedRow())).isSinglePatch(), Actions.EN_SEND
				| Actions.EN_SEND_TO | Actions.EN_PLAY);

		// one bank patch is selected
		Actions.setEnabled(table.getSelectedRowCount() == 1
				&& myModel.getPatchAt(table.convertRowIndexToModel(table.getSelectedRow())).isBankPatch(), Actions.EN_EXTRACT);

		// one patch is selected and it implements patch
		Actions.setEnabled(table.getSelectedRowCount() == 1
				&& myModel.getPatchAt(table.convertRowIndexToModel(table.getSelectedRow())).hasEditor(), Actions.EN_EDIT);

		Actions.setEnabled(
				Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this)
						.isDataFlavorSupported(PatchTransferHandler.PATCHES_FLAVOR), Actions.EN_PASTE);
	}

	void frameActivated() {
		Actions.setEnabled(false, Actions.EN_ALL);
		// always enabled
		Actions.setEnabled(true, Actions.EN_GET | Actions.EN_IMPORT | Actions.EN_IMPORT_ALL);
		enableActions();
	}

	public String getFileExtension() {
		return FILE_EXTENSION;
	}

	public FileFilter getFileFilter() {
		return FILE_FILTER;
	}

	void setupColumns() {
		// TableColumn column = null;
		// for (LibraryColumn col : LibraryColumn.values()) {
		// if (col.isVisible()) {
		// column = table.getColumnModel().getColumn(col.ordinal());
		// column.setPreferredWidth(50);
		// }
		// }
	}

	// for SortDialog
	public void sortPatch(Comparator<IPatch> c) {
		Collections.sort(myModel.getList(), c);
		setChanged();
	}

	// @Override
	// public void playAllPatches() {
	// throw new NotImplementedException();
	// }

	@Override
	@Deprecated
	public void splitSelectedPatches() {
		ErrorMsg.reportStatus("split patch : " + table.getSelectedRowCount());

		int[] selectedRows = table.getSelectedRows();

		// Without this we cannot delete the patch at the bottom.
		table.clearSelection();

		// delete from bottom not to change indices to be removed
		for (int i = selectedRows.length; i > 0; i--) {

			ErrorMsg.reportStatus("i = " + table.convertRowIndexToModel(selectedRows[i - 1]));

			// getPatch

			// split patch

			// add patch

			// delete patch
			// myModel.removeAt(table.convertRowIndexToModel(selectedRows[i - 1]));
		}
		setChanged();
	}
}
