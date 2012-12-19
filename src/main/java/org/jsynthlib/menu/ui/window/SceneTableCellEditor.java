package org.jsynthlib.menu.ui.window;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

import org.jsynthlib.menu.patch.IPatchDriver;

/**
 * @author Gerrit Gehnen
 */
class SceneTableCellEditor implements TableCellEditor, TableModelListener {
	/**
	 * 
	 */
	private final SceneFrame sceneFrame;
	private TableCellEditor editor, defaultEditor;
	private JComboBox box;
	private int oldrow = -1;
	private int oldcol = -1;

	/**
	 * Constructs a SceneTableCellEditor. create default editor
	 * @param sceneFrame TODO
	 * 
	 * @see TableCellEditor
	 * @see DefaultCellEditor
	 */
	public SceneTableCellEditor(SceneFrame sceneFrame) {
		this.sceneFrame = sceneFrame;
		defaultEditor = new DefaultCellEditor(new JTextField());
		this.sceneFrame.table.getModel().addTableModelListener(this);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public Object getCellEditorValue() {
		// ErrorMsg.reportStatus("getCellEditorValue
		// "+box.getSelectedItem());
		return new Integer(box.getSelectedIndex());
	}

	public boolean stopCellEditing() {
		return editor.stopCellEditing();
	}

	public void cancelCellEditing() {
		editor.cancelCellEditing();
	}

	public boolean isCellEditable(EventObject anEvent) {
		selectEditor((MouseEvent) anEvent);
		return editor.isCellEditable(anEvent);
	}

	public void addCellEditorListener(CellEditorListener l) {
		editor.addCellEditorListener(l);
	}

	public void removeCellEditorListener(CellEditorListener l) {
		editor.removeCellEditorListener(l);
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		selectEditor((MouseEvent) anEvent);
		return editor.shouldSelectCell(anEvent);
	}

	protected void selectEditor(MouseEvent e) {
		IPatchDriver driver;
		int row, col;

		if (e == null) {
			row = this.sceneFrame.table.getSelectionModel().getAnchorSelectionIndex();
			col = this.sceneFrame.table.getSelectedColumn();
		} else {
			row = this.sceneFrame.table.rowAtPoint(e.getPoint());
			col = this.sceneFrame.table.columnAtPoint(e.getPoint());
		}
		// ErrorMsg.reportStatus("selectEditor "+ row);
		if ((row != oldrow) || (col != oldcol)) {
			oldrow = row;
			oldcol = col;
			box = new JComboBox();

			driver = ((SceneListModel) this.sceneFrame.table.getModel()).getPatchAt(row).getDriver();
			String[] patchNumbers = driver.getPatchNumbers();
			String[] bankNumbers = driver.getBankNumbers();
			if (patchNumbers.length > 1) {
				if (col == SceneFrame.BANK_NUM) {
					for (int i = 0; i < bankNumbers.length; i++) {
						box.addItem(bankNumbers[i]);
					}
				} else if (col == SceneFrame.PATCH_NUM)
					for (int i = 0; i < patchNumbers.length; i++) {
						box.addItem(patchNumbers[i]);
					}
			}
			editor = new DefaultCellEditor(box);
			if (editor == null) {
				editor = defaultEditor;
			}
		}
	}

	public void tableChanged(TableModelEvent tableModelEvent) {
		oldcol = -1;
		oldrow = -1;
	}
}