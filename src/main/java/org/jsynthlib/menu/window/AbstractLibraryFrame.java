/*
 * AbstractLibraryFrame.java
 *
 * Created on 24. September 2002, 10:52
 */
package org.jsynthlib.menu.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.MaskFormatter;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.PatchTransferHandler;
import org.jsynthlib.menu.helper.ProxyImportHandler;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.JSynthImportFileType;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchBank;
import org.jsynthlib.model.patch.PatchBasket;
import org.jsynthlib.model.patch.PatchSingle;
import org.jsynthlib.model.tablemodel.PatchTableModel;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.ImportUtil;
import org.jsynthlib.tools.TableUtil;

/**
 * Abstract class for unified handling of Library and Scene frames.
 * 
 * @author Gerrit.Gehnen
 * @version $Id$
 */
public abstract class AbstractLibraryFrame extends MenuFrame implements PatchBasket {

	protected JTable table;

	protected PatchTableModel myModel;

	final String TYPE;

	private PatchTransferHandler transferHandler;
	/** Has the library been altered since it was last saved? */
	protected boolean changed = false;
	private JLabel statusBar;
	private File filename;
	private NumberFormat nf = NumberFormat.getInstance();

	AbstractLibraryFrame(String title, String type, PatchTransferHandler pth) {
		super(PatchBayApplication.getDesktop(), title);

		TYPE = type;
		this.transferHandler = pth;

		// sorting
		// JTable table = new JTable(myModel);
		// table.setRowSorter(new TableRowSorter<PatchTableModel>(myModel));

		// ...Create the GUI and put it in the window...
		addJSLFrameListener(new LibraryFrameListener(this));
		// create Table
		myModel = createTableModel();

		createTable();

		setupColumns();

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
		int cc = getTable().getColumnCount();
	}

	// JSLFrame method
	public boolean canImport(DataFlavor[] flavors) {
		return transferHandler.canImport(table, flavors);
	}

	private void setDriverForPatch(Patch patch) {

		// INFO CHOOSE DRIVER

		if (patch.getDriver() == null) {
			patch.findDriver();
		}

		if (patch.hasNullDriver()) {
			// INFO Unknown patch, try to guess at least the manufacturer
			patch.setInfo(nf.format(patch.getSize()) + " " + patch.lookupManufacturer() + " [?] ");
		} else {
			patch.setInfo("");
		}
	}

	public void copySelectedPatch() {
		transferHandler.exportToClipboard(table, Toolkit.getDefaultToolkit().getSystemClipboard(), TransferHandler.COPY);
	}

	private void createTable() {
		table = new JTable(myModel);

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {

					PatchSingle myPatch = (PatchSingle) getSelectedPatch();
					String name = myPatch.getName();
					int nameSize = myPatch.getNameSize();

					// TODO ssmCurtis - address read / write access
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
							ErrorMsgUtil.reportStatus(ex);
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
		});
		// table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setTransferHandler(transferHandler);
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

		// TODO ssmCurtis - edit windows
		// Set<AWTKeyStroke> newKeys = new
		// HashSet<AWTKeyStroke>(table.getFocusTraversalKeys(FocusManager.FORWARD_TRAVERSAL_KEYS));
		// newKeys.remove(KeyStroke.getKeyStroke("ctrl TAB"));
		// table.setFocusTraversalKeys(FocusManager.FORWARD_TRAVERSAL_KEYS, newKeys);
		//
		// table.addKeyListener(new KeyAdapter() {
		// public void keyReleased(KeyEvent e) {
		// if (e.isControlDown()) {
		// if (e.getKeyCode() == KeyEvent.VK_TAB)
		// System.out.println("Endlich!!! TAB(released) mit STRG");
		// }
		//
		// }
		// });

		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ADD || e.getKeyCode() == KeyEvent.VK_PLUS) {
					getSelectedPatch().addScore(1);
				}
				if (e.getKeyCode() == KeyEvent.VK_SUBTRACT || e.getKeyCode() == KeyEvent.VK_MINUS) {
					getSelectedPatch().addScore(-1);
				}
			}
		});

		// TODO ssmCurtis .. default sort
		table.setAutoCreateRowSorter(AppConfig.getMakeTableSortable());

	}

	abstract PatchTableModel createTableModel();

	public void deleteSelectedPatches() {
		ErrorMsgUtil.reportStatus("delete patch : " + table.getSelectedRowCount());
		int[] selection = table.getSelectedRows();

		// Without this we cannot delete the patch at the bottom.
		table.clearSelection();
		// delete from bottom not to change indices to be removed
		for (int i = selection.length; i > 0; i--) {
			// ErrorMsgUtil.reportStatus("i = " + selection[i - 1]);
			myModel.removeAt(table.convertRowIndexToModel(selection[i - 1]));
		}
		setChanged();
	}

	public JSLFrame editSelectedPatch() {
		// TODO: "changed" should only be set to true if the patch was modified.
		changed = false;
		return getSelectedPatch().edit();
	}

	abstract void enableActions();

	public void exportPatch(File file) throws IOException, FileNotFoundException {
		if (table.getSelectedRowCount() == 0) {
			ErrorMsgUtil.reportError("Error", "No Patch Selected.");
			return;
		}
		FileOutputStream fileOut = new FileOutputStream(file);
		fileOut.write(getSelectedPatch().export());
		fileOut.close();
	}

	public void extractSelectedPatch() {
		if (table.getSelectedRowCount() == 0) {
			ErrorMsgUtil.reportError("Error", "No Patch Selected.");
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

	abstract void frameActivated();

	public abstract String getFileExtension();

	public abstract FileFilter getFileFilter();

	public PatchTableModel getMyModel() {
		return myModel;
	}

	public ArrayList<Patch> getPatchCollection() {
		return myModel.getList();
	}

	/**
	 * @return The abstractPatchListModel as unified source of patches in all types of Libraryframes
	 */
	public PatchTableModel getPatchTableModel() {
		return myModel;
	}

	public Patch getSelectedPatch() {

		int selectedRowRaw = table.getSelectedRow();

		if (selectedRowRaw == -1) {
			selectedRowRaw = 0;
			ErrorMsgUtil.reportStatus(getClass().getName() + " >> no selection");
		}
		// TODO sort and id
		Patch p = myModel.getPatchAt(table.convertRowIndexToModel(selectedRowRaw));
		return p;
	}

	public Patch[] getSelectedPatches() {
		int[] ia = table.getSelectedRows();

		List<Patch> li = new ArrayList<Patch>();
		for (int i = ia.length; i > 0; i--) {
			li.add(myModel.getPatchAt(table.convertRowIndexToModel(ia[i - 1])));
		}
		return li.toArray(new Patch[] {});
	}

	int getSelectedRowCount() { // not used now
		return table.getSelectedRowCount();
	}

	/**
	 * @return The visual table component for this Frame.
	 */
	public JTable getTable() { // for SearchDialog
		return table;
	}

	public void importPatch(File file, JSynthImportFileType type) throws IOException, FileNotFoundException {
		// INFO IMPORT ACTION

		Patch[] patarray = null;
		if (JSynthImportFileType.MIDI.equals(type)) {

			patarray = ImportUtil.getPatchesFromMidi(file);

		} else if (JSynthImportFileType.MICROKORG_PRG.equals(type)) {

			patarray = ImportUtil.getPatchesFromPrg(file, type);

		} else if (JSynthImportFileType.MICROKORG_SET.equals(type)) {

			patarray = ImportUtil.getPatchesFromPrg(file, type);

		} else if (JSynthImportFileType.TXTHEX.equals(type)) {

			patarray = ImportUtil.getPatchesFromTexhex(file);

		}

		// sill null - f.e. a renamed sysex file
		if (patarray == null) {

			patarray = ImportUtil.getPatchesFromSysex(file);

		}

		TableUtil.addPatchToTable(patarray);

		revalidateDrivers();
	}

	public boolean isChanged() {
		return (changed);
	}

	@SuppressWarnings("unchecked")
	public void open(File file) throws IOException, ClassNotFoundException {

		setTitle(file.getName());
		filename = file;

		FileInputStream f = new FileInputStream(file);
		ObjectInputStream s = new ObjectInputStream(f);
		myModel.setList((ArrayList<Patch>) s.readObject());
		s.close();
		f.close();

		revalidateDrivers();
		myModel.fireTableDataChanged();
		changed = false;

	}

	public void pastePatch() {
		if (transferHandler.importData(table, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this))) {
			setChanged();
		} else {
			Actions.setEnabled(false, Actions.EN_PASTE);
		}
	}

	// TODO can be remove ?
	public void pastePatch(Patch p) {
		myModel.addPatch(p);
		setChanged();
	}

	// end of PatchBasket methods

	public void pastePatch(Patch p, int bankNum, int patchNum) {// added by R. Wirski
		myModel.addPatch(p, bankNum, patchNum);
		setChanged();
	}

	@Override
	public void playAllPatches() {
		for (int row = 0; row < myModel.getList().size(); row++) {
			PatchSingle myPatch = (PatchSingle) myModel.getPatchAt(row);
			// statusBar.setText(myPatch.getName() + " " + myPatch.getFileName());
			myPatch.send();
			myPatch.play();
		}
		// TODO ssmCurtis multiple threads
		// statusBar.setText(myModel.getRowCount() + " Patches");
	}

	public void playSelectedPatch() {
		PatchSingle myPatch = (PatchSingle) getSelectedPatch();

		if (AppConfig.getSendPatchBeforePlay()) {
			myPatch.send();
		}
		myPatch.play();
	}

	public void reassignSelectedPatch() {
		new ReassignPatchDialog(getSelectedPatch());
		setChanged();
	}

	/**
	 * Re-assigns drivers to all patches in libraryframe. Called after new drivers are added or or removed
	 */
	public void revalidateDrivers() {

		// INFO UPDATE TABLE - ASSIGN DRIVER TO PATCH

		for (int i = 0; i < myModel.getRowCount(); i++) {
			setDriverForPatch(myModel.getPatchAt(i));
		}
		myModel.fireTableDataChanged();
	}

	// for open/save/save-as actions
	public void save() throws IOException {
		PatchBayApplication.showWaitDialog("Saving " + filename + "...");
		try {
			FileOutputStream f = new FileOutputStream(filename);
			ObjectOutputStream s = new ObjectOutputStream(f);
			ArrayList<Patch> li = myModel.getList();

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

	public void sendSelectedPatch() {
		((PatchSingle) getSelectedPatch()).send();
	}

	public void sendToSelectedPatch() {
		new SysexSendToDialog(getSelectedPatch());
	}

	protected void setChanged() {
		myModel.fireTableDataChanged();
	}

	public void setMyModel(PatchTableModel myModel) {
		this.myModel = myModel;
	}

	/** Before calling this method, table and myModel is setup. */
	abstract void setupColumns();

	public void storeSelectedPatch() {
		new SysexStoreDialog(getSelectedPatch(), 0, 0);
	}

}