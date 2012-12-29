package org.jsynthlib.menu.ui.window;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.jsynthlib.menu.patch.IPatch;

/**
 * This is the general interface to unify the handling of the LibraryTable and SceneTable.
 * 
 * @author Gerrit
 */
public abstract class PatchTableModel extends AbstractTableModel {

	/**
	 * Add a patch to the end of the internal list.
	 * 
	 * @param p
	 *            The patch to add
	 */
	public abstract void addPatch(IPatch p);

	/**
	 * Add a patch to the end of the internal list. and sets bank and patch numbers
	 * 
	 * @param p
	 *            The patch to add
	 */
	public abstract void addPatch(IPatch p, int bankNum, int patchNum);// added by R. Wirski

	/**
	 * Set (and replace) the patch at the specified row of the list.
	 * 
	 * @param p
	 *            The patch to set
	 * @param row
	 *            The row of the table.
	 * @param bankNum
	 *            patch bank number
	 * @param patchNum
	 *            patch number
	 */
	public abstract void setPatchAt(IPatch p, int row, int bankNum, int patchNum); // added by R. Wirski

	/**
	 * Set (and replace) the patch at the specified row of the list.
	 * 
	 * @param p
	 *            The patch to set
	 * @param row
	 *            The row of the table.
	 */
	public abstract void setPatchAt(IPatch p, int row);

	/**
	 * Get the patch at the specified row.
	 * 
	 * @param row
	 *            The row specified
	 * @return The patch
	 */
	public abstract IPatch getPatchAt(int row);

	/**
	 * Get the comment at the specified row.
	 * 
	 * @param row
	 *            The row specified
	 * @return The comment.
	 */
	public abstract String getCommentAt(int row);

	public abstract void removeAt(int row);

	public abstract ArrayList<IPatch> getList();

	public abstract void setList(ArrayList<IPatch> newList);
}