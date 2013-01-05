package org.jsynthlib.menu.window;

import java.awt.Toolkit;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;

import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.PatchListTransferHandler;
import org.jsynthlib.menu.PatchTransferHandler;
import org.jsynthlib.menu.helper.ExtensionFilter;
import org.jsynthlib.menu.helper.SysexSort;
import org.jsynthlib.model.JSynthLibraryColumn;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.tablemodel.LibraryModel;
import org.jsynthlib.model.tablemodel.PatchTableModel;

/**
 * @version $Id$
 */
public class LibraryFrame extends AbstractLibraryFrame {
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
		return new LibraryModel();
	}

	public void deleteDuplicates() {
		Collections.sort(myModel.getList(), new SysexSort());
		int numDeleted = 0;
		
		Iterator<Patch> it = myModel.getList().iterator();
		
		Patch stayPatch = it.next(); 
		byte[] stay = stayPatch.getByteArray();
		
		while (it.hasNext()) {
			Patch deletePatch = it.next();
			byte[] delete = deletePatch.getByteArray();
			if (Arrays.equals(stay, delete)) {
				// TODO ssmCurtis - enable delete
				// deletePatch.setComment(deletePatch.getComment() + " #same as " + stayPatch.getComment()) ;
				it.remove();
				
				numDeleted++;
			} else {
				stay = delete;
				stayPatch = deletePatch;
			}
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
		Actions.setEnabled(table.getRowCount() > 1, Actions.EN_DELETE_DUPLICATES);

		// one or more patches are selected
		Actions.setEnabled(table.getSelectedRowCount() > 0, Actions.EN_DELETE);

		// TODO ssmCurtis check one patch is selected
		Actions.setEnabled(table.getSelectedRowCount() == 1, Actions.EN_COPY | Actions.EN_CUT | Actions.EN_EXPORT | Actions.EN_REASSIGN
				| Actions.EN_STORE | Actions.EN_UPLOAD);

		// one signle patch is selected
		Actions.setEnabled(table.getSelectedRowCount() == 1
				&& myModel.getPatchAt(table.convertRowIndexToModel(table.getSelectedRow())).isSinglePatch(), Actions.EN_SEND
				| Actions.EN_SEND_TO | Actions.EN_PLAY);

		// extract from generic
		Actions.setEnabled(table.getSelectedRowCount() == 1, Actions.EN_EXTRACT);

		// one patch is selected and it implements patch
		Actions.setEnabled(table.getSelectedRowCount() == 1
				&& myModel.getPatchAt(table.convertRowIndexToModel(table.getSelectedRow())).hasEditor(), Actions.EN_EDIT);

		// two patches are enabled
		if (table.getSelectedRowCount() == 2) {
			int[] selecteRows = table.getSelectedRows();
			if (myModel.getPatchAt(table.convertRowIndexToModel(selecteRows[0])).getPatchSize() == myModel.getPatchAt(
					table.convertRowIndexToModel(selecteRows[1])).getPatchSize()) {
				Actions.setEnabled(true, Actions.EN_COMPARE_PATCH);
			}
		} else {
			Actions.setEnabled(false, Actions.EN_COMPARE_PATCH);
		}
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
		TableColumn column = null;
		for (JSynthLibraryColumn col : JSynthLibraryColumn.values()) {
			if (col.isVisible()) {
				column = table.getColumnModel().getColumn(col.ordinal());
				column.setPreferredWidth(col.getPreferredWidth());
				if (JSynthLibraryColumn.SCORE.equals(col)) {
					column.setMaxWidth(col.getPreferredWidth());
				}
			}
		}
	}

	// for SortDialog
	public void sortPatch(Comparator<Patch> c) {
		Collections.sort(myModel.getList(), c);
		setChanged();
	}

	// @Override
	// public void playAllPatches() {
	// throw new NotImplementedException();
	// }

}
