/*
 * $Id$
 */
package org.jsynthlib.menu.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.model.JSynthLibraryColumn;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.tablemodel.PatchTableModel;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.UiUtil;

public class ComparePatchDialog extends JDialog {
	// private JRadioButton button2;
	// private JRadioButton button3;

	public ComparePatchDialog(JFrame parent) {

		super(parent, "Search Library", false);

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JPanel searchFor = new JPanel();
		JLabel label = new JLabel("Search For");
		final JTextField textField = new JTextField(30);
		searchFor.setLayout(new FlowLayout());
		searchFor.add(label);
		searchFor.add(textField);
		try {
			searchFor.setLayout(new FlowLayout());
			searchFor.add(label);
			searchFor.add(textField);
			container.add(searchFor, BorderLayout.NORTH);
			final ButtonGroup group = new ButtonGroup();

			JPanel radioPanel = new JPanel();
			radioPanel.setLayout(new FlowLayout());

			for (JSynthLibraryColumn column : JSynthLibraryColumn.getSeachableColumn()) {
				JRadioButton button = new JRadioButton(column.getTitle());
				if (column.equals(JSynthLibraryColumn.FILENAME)) {
					button.setSelected(true);
				}
				button.setActionCommand(column.getActionCommand());
				group.add(button);
				radioPanel.add(button);
			}
			JRadioButton buttonAll = new JRadioButton("All Fields");
			group.add(buttonAll);
			radioPanel.add(buttonAll);

			container.add(radioPanel, BorderLayout.CENTER);

			// JRadioButton button1 = new JRadioButton("Patch Name");
			// button2 = new JRadioButton("Field 1");
			// button3 = new JRadioButton("Field 2");
			// JRadioButton button4 = new JRadioButton("Comment");
			// JRadioButton button5 = new JRadioButton("All Fields");
			// button1.setActionCommand("P");
			// button2.setActionCommand("1");
			// button3.setActionCommand("2");
			// button4.setActionCommand("C");
			// button5.setActionCommand("A");
			// group.add(button1);
			// group.add(button2);
			// group.add(button3);
			// group.add(button4);
			// group.add(button5);

			// if (PatchBayApplication.getDesktop().getSelectedFrame() instanceof SceneFrame) {
			// button2.setEnabled(false);
			// button3.setEnabled(false);
			// }

			// button1.setSelected(true);
			// JPanel radioPanel = new JPanel();
			// radioPanel.setLayout(new FlowLayout());
			// radioPanel.add(button1);
			// radioPanel.add(button2);
			// radioPanel.add(button3);
			// radioPanel.add(button4);
			// radioPanel.add(button5);
			// container.add(radioPanel, BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			JButton findFirst = new JButton(" Find First ");
			JButton findNext = new JButton(" Find Next ");

			findFirst.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String command = group.getSelection().getActionCommand();
					String text = textField.getText();
					findString(text, command, true);
				}
			});

			findNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String text = textField.getText();
					String command = group.getSelection().getActionCommand();
					findString(text, command, false);

				}
			});

			buttonPanel.add(findFirst);
			buttonPanel.add(findNext);
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			buttonPanel.add(cancel);

			getRootPane().setDefaultButton(findFirst);

			container.add(buttonPanel, BorderLayout.SOUTH);
			getContentPane().add(container);
			pack();
			UiUtil.centerDialog(this);
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	public void setVisible(boolean b) {
		if (b) {
			// if (PatchBayApplication.getDesktop().getSelectedFrame() instanceof SceneFrame) {
			// button2.setEnabled(false);
			// button3.setEnabled(false);
			// } else {
			// button2.setEnabled(true);
			// button3.setEnabled(true);
			// }
		}
		super.setVisible(b);
	}

	void findString(String text, String command, boolean restart) {
		JSLFrame frame = PatchBayApplication.getDesktop().getSelectedFrame();
		if (frame == null || !(frame instanceof AbstractLibraryFrame)) {
			ErrorMsgUtil.reportError("Error", "Library to search in must be focused." + getClass().getSimpleName());
			return;
		}

		AbstractLibraryFrame lf = (AbstractLibraryFrame) frame;
		PatchTableModel tableModel = lf.getPatchTableModel();
		if (tableModel.getRowCount() == 0)
			return;

		int searchFrom;

		if (restart || lf.getTable().getSelectedRow() == -1)
			searchFrom = 0;
		else
			searchFrom = lf.getTable().getSelectedRow() + 1;

		Patch p;
		// int field = 0;
		// if (command.equals("P"))
		// field = 0;
		// if (command.equals("1"))
		// field = 1;
		// if (command.equals("2"))
		// field = 2;
		// if (command.equals("C"))
		// field = 3;
		// if (command.equals("A"))
		// field = 4;
		text = text.toLowerCase();
		// String s;
		int i;
		boolean match = false;

		JSynthLibraryColumn columnToSearch = JSynthLibraryColumn.getLibraryColumnForActionCommand(command);

		for (i = searchFrom; i < tableModel.getRowCount(); i++) {
			p = tableModel.getPatchAt(i);

			match = false;
			if (columnToSearch != null) {
				match = JSynthLibraryColumn.getPropertyValue(p, columnToSearch).toLowerCase().indexOf(text) != -1;
			} else {
				System.out.println("columnToSearch " + columnToSearch);
				for (JSynthLibraryColumn col : JSynthLibraryColumn.values()) {
					if (col.isVisible()) {
						System.out.println("column " + col);
						match = JSynthLibraryColumn.getPropertyValue(p, col).toLowerCase().indexOf(text) != -1;
						if (match) {
							break;
						}
					}
				}
			}

			if (match) {
				break;
			}
			// if (columnToSearch == null || LibraryColumn.SYNTH.equals(columnToSearch)) {
			// s = p.getName().toLowerCase();
			// match = (s.indexOf(text) != -1);
			// if (match) {
			// break;
			// }
			// }
			// if ((field == 1 || field == 4) && (lf instanceof LibraryFrame)) {
			// s = p.getDate().toLowerCase();
			// match = (s.indexOf(text) != -1);
			// if (match)
			// break;
			// }
			// if ((field == 2 || field == 4) && (lf instanceof LibraryFrame)) {
			// s = p.getAuthor().toLowerCase();
			// match = (s.indexOf(text) != -1);
			// if (match)
			// break;
			// }
			// if (field == 3 || field == 4) {
			// s = tableModel.getCommentAt(i).toLowerCase();
			// match = (s.indexOf(text) != -1);
			// if (match)
			// break;
			// }
		}

		if (!match) {
			ErrorMsgUtil.reportError("Search Complete", "Not Found.");
			return;
		}
		lf.getTable().changeSelection(i, 0, false, false);
	}
}
