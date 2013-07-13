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

public class SelectByFilterDialog extends JDialog {
	// private JRadioButton button2;
	// private JRadioButton button3;

	public SelectByFilterDialog(JFrame parent) {

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
				if (column.equals(JSynthLibraryColumn.PATCH_NAME)) {
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

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			JButton findFirst = new JButton("Select");

			findFirst.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String command = group.getSelection().getActionCommand();
					String text = textField.getText();
					selectByString(text, command, true);
				}
			});

			buttonPanel.add(findFirst);

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

	void selectByString(String text, String command, boolean restart) {
		JSLFrame frame = PatchBayApplication.getDesktop().getSelectedFrame();
		if (frame == null || !(frame instanceof AbstractLibraryFrame)) {
			ErrorMsgUtil.reportError("Error", "Library to search in must be focused." + getClass().getSimpleName());
			return;
		}

		AbstractLibraryFrame lf = (AbstractLibraryFrame) frame;
		PatchTableModel tableModel = lf.getPatchTableModel();
		if (tableModel.getRowCount() == 0)
			return;

		Patch p;
		text = text.toLowerCase();
		// String s;
		int i;
		boolean match = false;

		JSynthLibraryColumn columnToSearch = JSynthLibraryColumn.getLibraryColumnForActionCommand(command);

		
		for (i = 0; i < tableModel.getRowCount(); i++) {

			p = tableModel.getPatchAt(i);
			
			match = false;
			
			if (columnToSearch != null) {
				match = JSynthLibraryColumn.getPropertyValue(p, columnToSearch).toLowerCase().indexOf(text) != -1;
			} else {
				// ErrorMsgUtil.reportStatus("columnToSearch " + columnToSearch);
				for (JSynthLibraryColumn col : JSynthLibraryColumn.values()) {
					if (col.isVisible()) {
						// ErrorMsgUtil.reportStatus("column " + col);
						match = JSynthLibraryColumn.getPropertyValue(p, col).toLowerCase().indexOf(text) != -1;
					}
				}
			}
			p.setSelected(match);
		}
		lf.setChanged();

		// lf.getTable().changeSelection(i, 0, false, false);
	}
}
