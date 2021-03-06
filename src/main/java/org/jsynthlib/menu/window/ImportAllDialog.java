package org.jsynthlib.menu.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.jsynthlib.JSynthConstants;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.helper.ColumnLayout;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.JSynthImportFileType;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.UiUtil;

public class ImportAllDialog extends JDialog {
	public ImportModel myModel;
	private int counter = 0;
	public ImportAllDialog(JFrame Parent, final File file) {

		super(Parent, "Import All Files In Directory", true);

		JPanel container = new JPanel();
		container.setLayout(new ColumnLayout());

		try {
			myModel = new ImportModel();
			JTable table = new JTable(myModel);
			TableColumn column = null;
			column = table.getColumnModel().getColumn(0);
			column.setPreferredWidth(25);
			column = table.getColumnModel().getColumn(1);
			column.setPreferredWidth(250);
			table.setPreferredScrollableViewportSize(new Dimension(500, 250));
			JScrollPane scrollPane = new JScrollPane(table);
			container.add(scrollPane);

			// final ButtonGroup group = new ButtonGroup();
			// JRadioButton button1 = new JRadioButton("Nowhere");
			// button1.setActionCommand("0");
			// JRadioButton button2 = new JRadioButton("in Field 1");
			// JRadioButton button3 = new JRadioButton("in Field 2");
			// button2.setActionCommand("1");
			// button3.setActionCommand("2");
			// group.add(button1);
			// group.add(button2);
			// group.add(button3);
			// button1.setSelected(true);
			// JPanel radioPanel = new JPanel();
			// JLabel myLabel = new JLabel("Place the File name for each Patch:          ", JLabel.CENTER);
			// radioPanel.setLayout(new FlowLayout());
			// radioPanel.add(myLabel, BorderLayout.NORTH);
			// radioPanel.add(button1);
			// radioPanel.add(button2);
			// radioPanel.add(button3);
			// container.add(radioPanel);

			// final ButtonGroup group2 = new ButtonGroup();
			// JRadioButton button4 = new JRadioButton("No");
			// button4.setActionCommand("0");
			// JRadioButton button5 = new JRadioButton("Yes");
			//
			// button5.setActionCommand("1");
			// group2.add(button4);
			// group2.add(button5);
			//
			// button4.setSelected(true);
			// JPanel radioPanel2 = new JPanel();
			// JLabel myLabel2 = new JLabel("Automatically Extract Patches from Banks?   ", JLabel.CENTER);
			// radioPanel2.setLayout(new FlowLayout());
			// radioPanel2.add(myLabel2, BorderLayout.NORTH);
			// radioPanel2.add(button4);
			// radioPanel2.add(button5);
			//
			// container.add(radioPanel2);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			JButton done = new JButton(" OK ");
			done.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					// String command1 = group.getSelection().getActionCommand();
					// String command2 = group2.getSelection().getActionCommand();

					// boolean extract = (command2 == "1");
					//
					// int putName = 0;
					// if (command1 == "1")
					// putName = 1;
					// if (command1 == "2")
					// putName = 2;
					doImport(file, file.getName());
				}
			});
			buttonPanel.add(done);

			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			buttonPanel.add(cancel);

			getRootPane().setDefaultButton(done);

			container.add(buttonPanel, BorderLayout.SOUTH);
			getContentPane().add(container);
			pack();
			UiUtil.centerDialog(this);
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	public void doImport(File directory, String parrent) {
		counter++;
		if (counter > JSynthConstants.maxCountForImportFromDirectory) {
			return;
		}

		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File currentFile = files[i];
			ErrorMsgUtil.reportStatus("> " + currentFile.getName() + " dir:" +  currentFile.isDirectory());
			if (AppConfig.getImportDirectoryRecursive() && currentFile.isDirectory()) {
				doImport(currentFile, currentFile.getName());
			} else {
				String name = currentFile.getName();

				final int lastPeriodPos = name.lastIndexOf('.');

				if (lastPeriodPos > -1) {
					String extension = name.substring(lastPeriodPos);

					JSynthImportFileType fileType = JSynthImportFileType.getImportFileTypeForExtension(extension);

					if (fileType != null) {
						try {
							// import all supported filetypes
							Actions.getSelectedFrame().importPatch(currentFile, fileType);
							ErrorMsgUtil.reportStatus(">> Imported " + name);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					ErrorMsgUtil.reportStatus(">> Ignored " + name);
				}
			}
		}
	}

	static class myOrder implements Comparator {
		int field = 0;

		public myOrder(String s) {
			if (s.equals("P"))
				field = 0;
			if (s.equals("1"))
				field = 1;
			if (s.equals("2"))
				field = 2;
			if (s.equals("S"))
				field = 3;
			if (s.equals("T"))
				field = 4;
		}

		public int compare(Object a1, Object a2) {
			String s1;
			String s2;
			if (field == 0) {
				s1 = ((Patch) a1).getName();
				s2 = ((Patch) a2).getName();
			} else if (field == 1) {
				s1 = ((Patch) a1).getDate().toLowerCase();
				s2 = ((Patch) a2).getDate().toLowerCase();
			} else if (field == 2) {
				s1 = ((Patch) a1).getAuthor().toLowerCase();
				s2 = ((Patch) a2).getAuthor().toLowerCase();
			} else if (field == 3) {
				s1 = ((Patch) a1).getDevice().getSynthName();
				s2 = ((Patch) a2).getDevice().getSynthName();
			} else {
				s1 = ((Patch) a1).getType();
				s2 = ((Patch) a2).getType();
			}

			return s1.compareTo(s2);
		}

		public boolean equals(java.lang.Object obj) {
			return false;
		}

	}

	class ImportModel extends AbstractTableModel {
		final String[] columnNames = { "Include?", "Driver" };
		Boolean[] includeDevice = new Boolean[AppConfig.deviceCount()];

		public ImportModel() {
			super();
			for (int i = 0; i < includeDevice.length; i++)
				includeDevice[i] = new Boolean(true);
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return AppConfig.deviceCount();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			Device myDevice = AppConfig.getDevice(row);
			if (col == 1)
				return myDevice.getManufacturerName() + " " + myDevice.getModelName()/* +" "+myDriver.getPatchType () */;
			else
				return includeDevice[row];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for each cell. If we didn't implement this
		 * method, then the last column would contain text ("true"/"false"), rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			if (col == 0)
				return true;
			else
				return false;
		}

		/*
		 * Don't need to implement this method unless your table's data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			includeDevice[row] = (Boolean) value;
			fireTableCellUpdated(row, col);
		}
	}

}
