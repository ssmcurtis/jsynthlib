/*
 * AbstractLibraryFrame.java
 *
 * Created on 24. September 2002, 10:52
 */
package org.jsynthlib.menu.ui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.action.Actions;
import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.patch.PatchBank;
import org.jsynthlib.menu.patch.PatchBasket;
import org.jsynthlib.menu.patch.PatchSingle;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.ui.JSLFrame;
import org.jsynthlib.menu.ui.JSLFrameEvent;
import org.jsynthlib.menu.ui.JSLFrameListener;
import org.jsynthlib.menu.ui.PatchTransferHandler;
import org.jsynthlib.menu.ui.ProxyImportHandler;
import org.jsynthlib.model.ImportFileType;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.ErrorMsg;
import org.jsynthlib.tools.ImportUtils;
import org.jsynthlib.tools.Utility;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Abstract class for unified handling of Library and Scene frames.
 * 
 * @author Gerrit.Gehnen
 * @version $Id$
 */
public abstract class AbstractLibraryFrame extends MenuFrame implements PatchBasket {
	protected JTable table;

	protected PatchTableModel myModel;

	private final String TYPE;
	private PatchTransferHandler pth;
	/** Has the library been altered since it was last saved? */
	protected boolean changed = false;
	private JLabel statusBar;
	private File filename;

	private NumberFormat nf = NumberFormat.getInstance();

	AbstractLibraryFrame(String title, String type, PatchTransferHandler pth) {
		super(PatchBayApplication.getDesktop(), title);
		TYPE = type;
		this.pth = pth;

		// sorting
		// JTable table = new JTable(myModel);
		// table.setRowSorter(new TableRowSorter<PatchTableModel>(myModel));

		// ...Create the GUI and put it in the window...
		addJSLFrameListener(new MyFrameListener());

		// create Table
		myModel = createTableModel();
		createTable();

		// Create the scroll pane and add the table to it.
		final JScrollPane scrollPane = new JScrollPane(table);
		// Enable drop on scrollpane
		scrollPane.getViewport().setTransferHandler(new ProxyImportHandler(table, pth));
		// commented out by Hiroo
		// scrollPane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
		// public void mousePressed(MouseEvent e) {
		// }
		//
		// public void mouseReleased(MouseEvent e) {
		// //myModel.fireTableDataChanged();
		// }
		// });

		// Add the scroll pane to this window.
		JPanel statusPanel = new JPanel();
		statusBar = new JLabel(myModel.getRowCount() + " Patches");
		statusPanel.add(statusBar);

		// getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);

		// ...Then set the window size or call pack...
		setSize(800, 500);

		table.setAutoCreateRowSorter(true);
	}

	abstract PatchTableModel createTableModel();

	/** Before calling this method, table and myModel is setup. */
	abstract void setupColumns();

	abstract void frameActivated();

	abstract void enableActions();

	private void createTable() {
		table = new JTable(myModel);

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {

					// System.out.println(">>> Mouse pressed");
					Actions.showMenuPatchPopup(table, e.getX(), e.getY());
					table.setRowSelectionInterval(table.rowAtPoint(new Point(e.getX(), e.getY())),
							table.rowAtPoint(new Point(e.getX(), e.getY())));
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					// System.out.println(">>> Mouse released");
					Actions.showMenuPatchPopup(table, e.getX(), e.getY());
					table.setRowSelectionInterval(table.rowAtPoint(new Point(e.getX(), e.getY())),
							table.rowAtPoint(new Point(e.getX(), e.getY())));
				}
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// System.out.println(">>> Mouse double");
					PatchSingle myPatch = (PatchSingle) getSelectedPatch();
					String name = myPatch.getName();
					int nameSize = myPatch.getNameSize();
					if (myPatch.hasEditor()) {
						Actions.EditActionProc();
						setChanged();
					} else if (nameSize != 0) {
						final JOptionPane optionPane;
						String maskStr = "";
						for (int i = 0; i < nameSize; i++) {
							maskStr += "*";
						}
						MaskFormatter Mask = new MaskFormatter();
						try {
							Mask.setMask(maskStr);
						} catch (Exception ex) {
							ErrorMsg.reportStatus(ex);
						}
						JFormattedTextField patchName = new JFormattedTextField(Mask);
						patchName.setValue(name);
						Object[] options = { new String("OK"), new String("Cancel") };
						optionPane = new JOptionPane(patchName, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null, options,
								options[0]);
						JDialog dialog = optionPane.createDialog(table, "Edit patch name");
						dialog.setVisible(true);
						if (optionPane.getValue() == options[0]) {
							String newName = (String) patchName.getValue();
							myPatch.setName(newName);
							setChanged();
						}
					}
				}
			}
		});
		// table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setTransferHandler(pth);
		table.setDragEnabled(true);

		// setupColumns();

		table.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				changed = true;
				statusBar.setText(myModel.getRowCount() + " Patches");
				enableActions();
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				enableActions();
			}
		});
	}

	private class MyFrameListener implements JSLFrameListener {

		public void JSLFrameClosing(JSLFrameEvent e) {
			if (!changed)
				return;

			// close Patch/Bank Editor editing a patch in this frame.
			JSLFrame[] jList = PatchBayApplication.getDesktop().getAllFrames();
			System.out.println("Frames: " + jList.length);
			for (int j = 0; j < jList.length; j++) {
				if (jList[j] instanceof BankEditorFrame) {
					for (int i = 0; i < myModel.getRowCount(); i++)
						if (((BankEditorFrame) (jList[j])).bankData == myModel.getPatchAt(i)) {
							jList[j].moveToFront();
							try {
								jList[j].setSelected(true);
								jList[j].setClosed(true);
							} catch (Exception e1) {
								ErrorMsg.reportStatus(e1);
							}
							break;
						}
				}
				if (jList[j] instanceof PatchEditorFrame) {
					for (int i = 0; i < myModel.getRowCount(); i++)
						if (((PatchEditorFrame) (jList[j])).p == myModel.getPatchAt(i)) {
							jList[j].moveToFront();
							try {
								jList[j].setSelected(true);
								jList[j].setClosed(true);
							} catch (Exception e1) {
								ErrorMsg.reportStatus(e1);
							}
							break;
						}
				}
			}

			if (JOptionPane.showConfirmDialog(null, "This " + TYPE + " may contain unsaved data.\nSave before closing?", "Unsaved Data",
					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				return;

			moveToFront();
			Actions.saveFrame();
		}

		public void JSLFrameOpened(JSLFrameEvent e) {
		}

		public void JSLFrameActivated(JSLFrameEvent e) {
			frameActivated();
		}

		public void JSLFrameClosed(JSLFrameEvent e) {
			if (PatchBayApplication.getDesktop().getAllFrames().length == 0) {
				Actions.setEnabled(false, Actions.EN_PASTE);
			}
			System.out.println(">>>> Closed");
		}

		public void JSLFrameDeactivated(JSLFrameEvent e) {
			Actions.setEnabled(false, Actions.EN_ALL);
		}

		public void JSLFrameDeiconified(JSLFrameEvent e) {
		}

		public void JSLFrameIconified(JSLFrameEvent e) {
		}
	}

	// INFO import patch
	// begin PatchBasket methods
	public void importPatch(File file, ImportFileType type) throws IOException, FileNotFoundException {

		IPatch[] patarray = null;
		if (ImportFileType.MIDI.equals(type)) {
			patarray = ImportUtils.getPatchesFromMidi(file);
		} else if (ImportFileType.TXTHEX.equals(type)) {
			System.out.println("IMPORT TEXT");
			patarray = ImportUtils.getPatchesFromTexhex(file);
		}

		// Default is sysex
		if (patarray == null) {
			FileInputStream fileIn = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			fileIn.read(buffer);
			fileIn.close();

			// ErrorMsg.reportStatus("Buffer length:" + buffer.length);
			patarray = DriverUtil.createPatches(buffer, file.getName());

		}
		// INFO import will never overwrite existing patches in table
		for (int k = 0; k < patarray.length; k++) {
			IPatch pk = patarray[k];
			// if (putName == 1)
			// pk.setDate(pk.getDate() + currentFile.getName());
			// if (putName == 2)
			// pk.setAuthor(pk.getAuthor() + currentFile.getName());
			LibraryFrame frame = (LibraryFrame) PatchBayApplication.getDesktop().getSelectedFrame();

			System.out.println("IMPORT " + Utility.hexDump(pk.getByteArray(), 0, -1, -1));

			if (pk.isBankPatch()) {

				String[] pn = pk.getDriver().getPatchNumbers();

				for (int j = 0; j < ((PatchBank) pk).getNumPatches(); j++) {
					IPatch q = ((PatchBank) pk).get(j);
					q.setFileName(pk.getFileName());
					// if (putName == 1)
					// q.setDate(q.getDate() + currentFile.getName() + " " + pn[j]);
					// if (putName == 2)
					// q.setAuthor(q.getAuthor() + currentFile.getName() + " " + pn[j]);
					frame.myModel.addPatch(q);
				}
			} else {
				frame.myModel.addPatch(pk);
			}
			frame.revalidateDrivers();
			// if (table.getSelectedRowCount() == 0) {
			// myModel.addPatch(patarray[j]);
			// } else {
			// myModel.setPatchAt(patarray[j], table.getSelectedRow());
			// }
		}

		revalidateDrivers();
		// setChanged();
	}

	protected void setChanged() {
		myModel.fireTableDataChanged();
		// This is done in tableChanged for the TableModelListener
		// changed = true;
	}

	public boolean isChanged() {
		return (changed);
	}

	public void exportPatch(File file) throws IOException, FileNotFoundException {
		System.out.println("Export - Selected row: " + table.getSelectedRow());
		System.out.println("Export - Converted row: " + table.convertRowIndexToModel(table.getSelectedRow()));
		if (table.getSelectedRowCount() == 0) {
			ErrorMsg.reportError("Error", "No Patch Selected.");
			return;
		}
		FileOutputStream fileOut = new FileOutputStream(file);
		fileOut.write(getSelectedPatch().export());
		fileOut.close();
	}

	public void deleteSelectedPatches() {
		ErrorMsg.reportStatus("delete patch : " + table.getSelectedRowCount());
		int[] ia = table.getSelectedRows();
		// Without this we cannot delete the patch at the bottom.
		table.clearSelection();
		// delete from bottom not to change indices to be removed
		for (int i = ia.length; i > 0; i--) {
			ErrorMsg.reportStatus("i = " + table.convertRowIndexToModel(ia[i - 1]));
			myModel.removeAt(table.convertRowIndexToModel(ia[i - 1]));
		}
		setChanged();
	}

	public void copySelectedPatch() {
		pth.exportToClipboard(table, Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
	}

	public void pastePatch() {
		if (pth.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this))) {
			setChanged();
		} else {
			Actions.setEnabled(false, Actions.EN_PASTE);
		}
	}

	// TODO can be remove ?
	public void pastePatch(IPatch p) {
		myModel.addPatch(p);
		setChanged();
	}

	public void pastePatch(IPatch p, int bankNum, int patchNum) {// added by R. Wirski
		myModel.addPatch(p, bankNum, patchNum);
		setChanged();
	}

	public IPatch getSelectedPatch() {
		System.out.println("Get - Selected row: " + table.getSelectedRow());
		System.out.println("Get - Converted row: " + table.convertRowIndexToModel(table.getSelectedRow()));
		System.out.println("Coubn: " + myModel.getRowCount());

		// TODO sort and id
		IPatch p = myModel.getPatchAt(table.convertRowIndexToModel(table.getSelectedRow()));
		System.out.println(">>>>> " + p.getFileName());
		return p;
	}

	public void sendSelectedPatch() {
		((PatchSingle) getSelectedPatch()).send();
	}

	public void sendToSelectedPatch() {
		new SysexSendToDialog(getSelectedPatch());
	}

	public void reassignSelectedPatch() {
		new ReassignPatchDialog(getSelectedPatch());
		setChanged();
	}

	public void playSelectedPatch() {
		PatchSingle myPatch = (PatchSingle) getSelectedPatch();

		if (AppConfig.getSendPatchBeforePlay()) {
			myPatch.send();
		}
		myPatch.play();
	}

	public void storeSelectedPatch() {
		new SysexStoreDialog(getSelectedPatch(), 0, 0);
	}

	public JSLFrame editSelectedPatch() {
		// TODO: "changed" should only be set to true if the patch was modified.
		changed = false;
		return getSelectedPatch().edit();
	}

	public ArrayList<IPatch> getPatchCollection() {
		return myModel.getList();
	}

	// end of PatchBasket methods

	/**
	 * @return The abstractPatchListModel as unified source of patches in all types of Libraryframes
	 */
	public PatchTableModel getPatchTableModel() {
		return myModel;
	}

	/**
	 * @return The visual table component for this Frame.
	 */
	public JTable getTable() { // for SearchDialog
		return table;
	}

	public void extractSelectedPatch() {
		if (table.getSelectedRowCount() == 0) {
			ErrorMsg.reportError("Error", "No Patch Selected.");
			return;
		}
		PatchBank myPatch = (PatchBank) getSelectedPatch();
		for (int i = 0; i < myPatch.getNumPatches(); i++) {
			PatchSingle p = myPatch.get(i);
			if (p != null)
				myModel.addPatch(p);
		}
		setChanged();
	}

	// for open/save/save-as actions
	public void save() throws IOException {
		PatchBayApplication.showWaitDialog("Saving " + filename + "...");
		try {
			FileOutputStream f = new FileOutputStream(filename);
			ObjectOutputStream s = new ObjectOutputStream(f);
			List li = myModel.getList();

			s.writeObject(li);
			s.flush();
			s.close();
			f.close();
			changed = false;

		} catch (IOException e) {
			throw e;
		} finally {
			PatchBayApplication.hideWaitDialog();
		}
	}

	public void save(File file) throws IOException {
		filename = file;
		setTitle(file.getName());
		save();
		changed = false;
	}

	public void open(File file) throws IOException, ClassNotFoundException {

		setTitle(file.getName());
		filename = file;

		FileInputStream f = new FileInputStream(file);
		ObjectInputStream s = new ObjectInputStream(f);
		myModel.setList((ArrayList) s.readObject());
		s.close();
		f.close();

		revalidateDrivers();
		myModel.fireTableDataChanged();
		changed = false;

	}

	public abstract FileFilter getFileFilter();

	public abstract String getFileExtension();

	/**
	 * Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
	 */
	public void revalidateDrivers() {
		for (int i = 0; i < myModel.getRowCount(); i++) {
			chooseDriver(myModel.getPatchAt(i));
		}
		myModel.fireTableDataChanged();
	}

	private void chooseDriver(IPatch patch) {
		patch.setDriver();
		if (patch.hasNullDriver()) {
			// INFO Unknown patch, try to guess at least the manufacturer
			patch.setInfo(nf.format(patch.getSize()) + " " + patch.lookupManufacturer() + " [?] ");
		} else {
			patch.setInfo("");
		}
	}

	// JSLFrame method
	public boolean canImport(DataFlavor[] flavors) {
		return pth.canImport(table, flavors);
	}

	int getSelectedRowCount() { // not used now
		return table.getSelectedRowCount();
	}

	@Override
	public void playAllPatches() {
		for (int row = 0; row < myModel.getList().size(); row++) {
			PatchSingle myPatch = (PatchSingle) myModel.getPatchAt(row);
			// statusBar.setText(myPatch.getName() + " " + myPatch.getFileName());
			myPatch.send();
			myPatch.play();
		}
		// TODO ssmcurtis multiple threads
		// statusBar.setText(myModel.getRowCount() + " Patches");
	}

	// private void newFilter() {
	// RowFilter<PatchTableModel, Object> rf = null;
	// try {
	// rf = RowFilter.regexFilter(filterText.getText(), 0);
	// } catch (java.util.regex.PatternSyntaxException e) {
	// return;
	// }
	// sorter.setRowFilter(rf);
	// }
}