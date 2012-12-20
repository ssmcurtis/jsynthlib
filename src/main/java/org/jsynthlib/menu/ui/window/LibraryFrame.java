package org.jsynthlib.menu.ui.window;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;

import org.jsynthlib.menu.action.Actions;
import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.ui.ExtensionFilter;
import org.jsynthlib.menu.ui.PatchTransferHandler;
import org.jsynthlib.tools.ErrorMsg;

/**
 * @version $Id$
 */
public class LibraryFrame extends AbstractLibraryFrame {
	private static int openFrameCount = 0;

	public static final String FILE_EXTENSION = ".patchlib";
	private static final FileFilter FILE_FILTER = new ExtensionFilter("PatchEdit Library Files (*" + FILE_EXTENSION
			+ ")", FILE_EXTENSION);
	private static final PatchTransferHandler pth = new PatchListTransferHandler();

	public LibraryFrame(File file) {
		super(file.getName(), "Library", pth);
	}

	public LibraryFrame() {
		super("Unsaved Library #" + (++openFrameCount), "Library", pth);
	}

	public PatchTableModel createTableModel() {
		return new PatchListModel(/* false */);
	}

	void setupColumns() {
		TableColumn column = null;
		for (LibraryColumn col : LibraryColumn.values()) {
			column = table.getColumnModel().getColumn(col.ordinal());
			column.setPreferredWidth(50);
		}
		// column.setPreferredWidth(50);
		// column = table.getColumnModel().getColumn(TYPE);
		// column.setPreferredWidth(50);
		// column = table.getColumnModel().getColumn(PATCH_NAME);
		// column.setPreferredWidth(100);
		// column = table.getColumnModel().getColumn(FIELD1);
		// column.setPreferredWidth(50);
		// column = table.getColumnModel().getColumn(FIELD2);
		// column.setPreferredWidth(50);
		// column = table.getColumnModel().getColumn(FILENAME);
		// column.setPreferredWidth(50);
		// column = table.getColumnModel().getColumn(PATCHID);
		// column.setPreferredWidth(50);
		// column = table.getColumnModel().getColumn(COMMENT);
		// column.setPreferredWidth(200);
	}

	void frameActivated() {
		Actions.setEnabled(false, Actions.EN_ALL);
		// always enabled
		Actions.setEnabled(true, Actions.EN_GET | Actions.EN_IMPORT | Actions.EN_IMPORT_ALL);
		enableActions();
	}

	/** change state of Actions based on the state of the table. */
	void enableActions() {
		// one or more patches are included.
		Actions.setEnabled(table.getRowCount() > 0, Actions.EN_SAVE | Actions.EN_SAVE_AS | Actions.EN_SEARCH);

		// // more than one patches are included.
		// Actions.setEnabled(table.getRowCount() > 1,
		// Actions.EN_DELETE_DUPLICATES | Actions.EN_SORT);

		// one or more patches are selected
		Actions.setEnabled(table.getSelectedRowCount() > 0, Actions.EN_DELETE);

		// one patch is selected
		// Actions.setEnabled(table.getSelectedRowCount() == 1,
		// Actions.EN_COPY
		// | Actions.EN_CUT | Actions.EN_EXPORT | Actions.EN_REASSIGN
		// | Actions.EN_STORE | Actions.EN_UPLOAD | Actions.EN_CROSSBREED);

		// one signle patch is selected
		Actions.setEnabled(table.getSelectedRowCount() == 1
				&& myModel.getPatchAt(table.getSelectedRow()).isSinglePatch(), Actions.EN_SEND | Actions.EN_SEND_TO
				| Actions.EN_PLAY);

		// one bank patch is selected
		Actions.setEnabled(
				table.getSelectedRowCount() == 1 && myModel.getPatchAt(table.getSelectedRow()).isBankPatch(),
				Actions.EN_EXTRACT);

		// one patch is selected and it implements patch
		Actions.setEnabled(table.getSelectedRowCount() == 1 && myModel.getPatchAt(table.getSelectedRow()).hasEditor(),
				Actions.EN_EDIT);

		// enable paste if the clipboard has contents.
		Actions.setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this) != null, Actions.EN_PASTE);
	}

	@Deprecated
	void deleteDuplicates() {
		Collections.sort(myModel.getList(), new SysexSort());
		int numDeleted = 0;
		Iterator it = myModel.getList().iterator();
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
		changed();
	}

	// This is a comparator class used by the delete duplicated action
	// to sort based on the sysex data
	// Sorting this way makes the Dups search much easier, since the
	// dups must be next to each other
	private static class SysexSort implements Comparator {
		public int compare(Object a1, Object a2) {
			String s1 = new String(((IPatch) (a1)).getByteArray());
			String s2 = new String(((IPatch) (a2)).getByteArray());
			return s1.compareTo(s2);
		}
	}

	// for SortDialog
	public void sortPatch(Comparator c) {
		Collections.sort(myModel.getList(), c);
		changed();
	}

	public FileFilter getFileFilter() {
		return FILE_FILTER;
	}

	public String getFileExtension() {
		return FILE_EXTENSION;
	}

	/**
	 * Refactored from PerformanceListModel
	 * 
	 * @author Gerrit Gehnen
	 */
	private class PatchListModel extends PatchTableModel {
		private final String[] columnNames = LibraryColumn.getColumNames();

		private ArrayList list = new ArrayList();

		PatchListModel() {
		}

		public int getRowCount() {
			return list.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			IPatch myPatch = (IPatch) list.get(row);
			try {

				LibraryColumn column = LibraryColumn.getLibraryColumn(col);

				switch (column) {
				case SYNTH:
					return myPatch.getDevice().getSynthName();
				case TYPE:
					return myPatch.getType();
				case PATCH_NAME:
					return myPatch.getName();
				case FIELD1:
					return myPatch.getDate();
				case FIELD2:
					return myPatch.getAuthor();
				case COMMENT:
					return myPatch.getComment();
				case FILENAME:
					return myPatch.getFileName();
				case PATCHID:
					return myPatch.getPatchId();
				case INFO:
					return myPatch.getInfo();
				default:
					ErrorMsg.reportStatus("LibraryFrame.getValueAt: internal error.");
					return null;
				}
			} catch (NullPointerException e) {
				ErrorMsg.reportStatus("LibraryFrame.getValueAt: row=" + row + ", col=" + col + ", Patch=" + myPatch);
				ErrorMsg.reportStatus("row count =" + getRowCount());
				// e.printStackTrace();
				return null;
			}
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for each cell. If we didn't implement this
		 * method, then the last column would contain text ("true"/"false"), rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return String.class;
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		public boolean isCellEditable(int row, int col) {
			return (col > LibraryColumn.TYPE.ordinal());
		}

		/*
		 * Don't need to implement this method unless your table's data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			// TODO: Remove these comments after June, 2006
			// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
			// - Emenaker 2006-02-21
			// changed = true;
			IPatch myPatch = (IPatch) list.get(row);

			LibraryColumn column = LibraryColumn.getLibraryColumn(col);

			switch (column) {
			case PATCH_NAME:
				myPatch.setName((String) value);
				break;
			case FIELD1:
				myPatch.setDate((String) value);
				break;
			case FIELD2:
				myPatch.setAuthor((String) value);
				break;
			case COMMENT:
				myPatch.setComment((String) value);
				break;
			case FILENAME:
				myPatch.setFileName((String) value);
			case PATCHID:
				myPatch.setPatchId((String) value);
			case INFO:
				myPatch.setInfo((String) value);
			default:
				ErrorMsg.reportStatus("LibraryFrame.setValueAt: internal error.");
			}
			fireTableCellUpdated(row, col);
		}

		// begin PatchTableModel interface methods
		// It is caller's responsibility to update Table.
		public void addPatch(IPatch p) {
			ErrorMsg.reportStatus("LibraryFrame.addPatch: Patch=" + p);
			list.add(p);
		}

		public void addPatch(IPatch p, int bankNum, int patchNum) {// wirski@op.pl
			ErrorMsg.reportStatus("LibraryFrame.addPatch: Patch=" + p);
			list.add(p);
		}

		public void setPatchAt(IPatch p, int row, int bankNum, int patchNum) {// wirski@op.pl
			ErrorMsg.reportStatus("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
			list.set(row, p);
		}

		public void setPatchAt(IPatch p, int row) {
			ErrorMsg.reportStatus("LibraryFrame.setPatchAt: row=" + row + ", Patch=" + p);
			list.set(row, p);
		}

		public IPatch getPatchAt(int row) {
			return (IPatch) list.get(row);
		}

		public String getCommentAt(int row) {
			return getPatchAt(row).getComment();
		}

		public void removeAt(int row) {
			this.list.remove(row);
		}

		public ArrayList getList() {
			return this.list;
		}

		public void setList(ArrayList newList) {
			this.list = newList;
		}
		// end PatchTableModel interface methods
	}

	private static class PatchListTransferHandler extends PatchTransferHandler {

		protected boolean storePatch(IPatch p, JComponent c) {
			PatchTableModel model = (PatchTableModel) ((JTable) c).getModel();
			model.addPatch(p);
			// TODO This method shouldn't have to worry about calling fireTableDataChanged(). Find a better way.
			model.fireTableDataChanged();
			return true;
		}

		// only for debugging
		// protected void exportDone(JComponent source, Transferable data, int action) {
		// ErrorMsg.reportStatus("PatchListTransferHandler.exportDone " + data);
		// }
	}
}
