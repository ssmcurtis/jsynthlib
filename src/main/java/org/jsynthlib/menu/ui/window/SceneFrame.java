package org.jsynthlib.menu.ui.window;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;

import org.jsynthlib.menu.action.Actions;
import org.jsynthlib.menu.patch.IPatchDriver;
import org.jsynthlib.menu.patch.IPatch;
import org.jsynthlib.menu.patch.Scene;
import org.jsynthlib.menu.ui.ExtensionFilter;
import org.jsynthlib.menu.ui.PatchTransferHandler;
import org.jsynthlib.menu.ui.SwingWorker;
import org.jsynthlib.tools.ErrorMsg;
import org.jsynthlib.tools.midi.MidiUtil;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

//import core.SysexGetDialog.TimerActionListener;

/**
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class SceneFrame { //extends AbstractLibraryFrame {
//	private static int openFrameCount = 0;
//	// column indices
//	static final int SYNTH = 0;
//	static final int TYPE = 1;
//	static final int PATCH_NAME = 2;
//	static final int BANK_NUM = 3;
//	static final int PATCH_NUM = 4;
//	static final int COMMENT = 5;
//
//	public static final String FILE_EXTENSION = ".scenelib";
//	private static final FileFilter FILE_FILTER = new ExtensionFilter("PatchEdit Scene Files (*"
//			+ FILE_EXTENSION + ")", FILE_EXTENSION);
//	private static final PatchTransferHandler pth = new SceneListTransferHandler();
//
//	public SceneFrame(File file) {
//		super(file.getName(), "Scene", pth);
//	}
//
//	public SceneFrame() {
//		super("Unsaved Scene #" + (++openFrameCount), "Scene", pth);
//	}
//
//	PatchTableModel createTableModel() {
//		return new SceneListModel(/* false */);
//	}
//
//	void setupColumns() {
//		SceneTableCellEditor rowEditor = new SceneTableCellEditor(this);
//
//		TableColumn column = null;
//		column = table.getColumnModel().getColumn(SYNTH);
//		column.setPreferredWidth(150); 
//		column = table.getColumnModel().getColumn(TYPE);
//		column.setPreferredWidth(100); 
//		column = table.getColumnModel().getColumn(PATCH_NAME);
//		column.setPreferredWidth(150); 
//		column = table.getColumnModel().getColumn(BANK_NUM);
//		column.setPreferredWidth(150); 
//		// Set the special pop-up Editor for Bank numbers
//		column.setCellEditor(rowEditor);
//		column = table.getColumnModel().getColumn(PATCH_NUM);
//		column.setPreferredWidth(100); 
//		// Set the special pop-up Editor for Patch Numbers
//		column.setCellEditor(rowEditor);
//		column = table.getColumnModel().getColumn(COMMENT);
//		column.setPreferredWidth(200);
//	}
//
//	void frameActivated() {
//		Actions.setEnabled(false, Actions.EN_ALL);
//
//		// always enabled
//		Actions.setEnabled(true, Actions.EN_GET | Actions.EN_IMPORT | Actions.EN_IMPORT_ALL);
//		enableActions();
//	}
//
//	/** change state of Actions based on the state of the table. */
//	void enableActions() {
//		// one or more patches are included.
//		Actions.setEnabled(table.getRowCount() > 0, Actions.EN_PLAY_ALL |Actions.EN_SAVE | Actions.EN_SAVE_AS | Actions.EN_SEARCH
//				| Actions.EN_TRANSFER_SCENE | Actions.EN_UPDATE_SCENE);
//
//		// one or more patches are selected
//		Actions.setEnabled(table.getSelectedRowCount() > 0, Actions.EN_DELETE | Actions.EN_UPDATE_SELECTED);
//
//		Actions.setEnabled(table.getSelectedRowCount() == 1, Actions.EN_COPY | Actions.EN_EDIT 
//				| Actions.EN_CUT | Actions.EN_EXPORT | Actions.EN_REASSIGN | Actions.EN_STORE | Actions.EN_UPLOAD);
//
//		// one single patch is selected
//		Actions.setEnabled(table.getSelectedRowCount() == 1
//				&& myModel.getPatchAt(table.convertRowIndexToModel(table.getSelectedRow())).isSinglePatch(), Actions.EN_SEND | Actions.EN_SEND_TO
//				| Actions.EN_PLAY);
//
//		// enable paste if the clipboard has contents.
//		Actions.setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this) != null, Actions.EN_PASTE);
//	}
//
//	// begin PatchBasket methods
//	public ArrayList getPatchCollection() {
//		ArrayList ar = new ArrayList();
//		for (int i = 0; i < myModel.getRowCount(); i++)
//			ar.add(myModel.getPatchAt(i));
//		return ar;
//	}
//
//	// end of PatchBasket methods
//
//	/**
//	 * Send all patches of the scene to the configured places in the synth's.
//	 */
//	public void sendScene() {
//		// ErrorMsg.reportStatus("Transfering Scene");
//		for (int i = 0; i < myModel.getRowCount(); i++) {
//			Scene scene = ((SceneListModel) myModel).getSceneAt(i);
//			scene.getPatch().send(scene.getBankNumber(), scene.getPatchNumber());
//		}
//	}
//
//	public void storeSelectedPatch() {
//		Scene scene = ((SceneListModel) myModel).getSceneAt(table.getSelectedRow());
//		new SysexStoreDialog(scene.getPatch(), scene.getBankNumber(), scene.getPatchNumber()); 
//	}
//
//	void UpdatePatch(int row) { 
//		int repeat;
//		java.util.List queue = new ArrayList();
//		Scene scene = ((SceneListModel) myModel).getSceneAt(row);
//		IPatchDriver driver = scene.getPatch().getDriver();
//		int inPort = driver.getDevice().getInPort();
//		long patchSize = driver.getPatchSize();
//		long sysexSize = 0;
//		boolean variableSize = false;
//		long timeout;
//		if (patchSize == 0) {
//			variableSize = true;
//			timeout = 5000;
//		} else {
//			timeout = patchSize + 500;
//		}
//		;
//		do {
//			repeat = JOptionPane.NO_OPTION;
//			sysexSize = 0;
//			MidiUtil.clearSysexInputQueue(inPort);
//			driver.requestPatchDump(scene.getBankNumber(), scene.getPatchNumber());
//			try {
//				do {
//					SysexMessage msg;
//					msg = (SysexMessage) MidiUtil.getMessage(inPort, timeout);
//					queue.add(msg);
//					sysexSize += msg.getLength();
//				} while ((variableSize) || (sysexSize < patchSize));
//			} catch (MidiUtil.TimeoutException ex) {
//				if (!variableSize) {
//					repeat = JOptionPane.showConfirmDialog(null, "Cannot receive sysex from " + driver
//							+ ".\nDo you want to try again?", "Scene Update Warning", JOptionPane.YES_NO_OPTION);
//				}
//			} catch (InvalidMidiDataException ex) {
//				ErrorMsg.reportError("Error", "Invalid MIDI data error");
//			}
//		} while (repeat == JOptionPane.YES_OPTION);
//		if ((variableSize) || (sysexSize == patchSize)) {
//			SysexMessage[] msgs = (SysexMessage[]) queue.toArray(new SysexMessage[0]);
//			IPatch[] patarray = driver.createPatches(msgs);
//			if (patarray.length == 1) {
//				myModel.setPatchAt(patarray[0], row, scene.getBankNumber(), scene.getPatchNumber());
//			} else {
//				ErrorMsg.reportError("Error", "To many patches received");
//			}
//		} else {
//			ErrorMsg.reportError("Error", "Incorrect patch size received");
//		}
//	}
//
//	public class UpdateSceneTask { 
//		private ArrayList Undo;
//		private int lengthOfTask;
//		private int current = 0;
//		private boolean done = false;
//		private boolean canceled = false;
//		private String statMessage;
//		private int noOfPatches;
//		private int[] sceneIndx;
//		private int[] syxArray;
//		int waitToClose = 0;
//
//		public UpdateSceneTask(PatchTableModel model, int[] indx) {
//			Undo = (ArrayList) ((SceneListModel) myModel).getList().clone();
//			sceneIndx = indx;
//			noOfPatches = sceneIndx.length;
//			syxArray = new int[noOfPatches];
//			for (int i = 0; i < noOfPatches; i++) {
//				syxArray[i] = ((SceneListModel) myModel).getSceneAt(i).getPatch().getDriver().getPatchSize() / 10;
//				lengthOfTask += syxArray[i];
//			}
//			lengthOfTask += 1; // to force progessMonitor not to quit at end of the task
//		}
//
//		public void go() {
//			final SwingWorker worker = new SwingWorker() {
//				public Object construct() {
//					current = 0;
//					done = false;
//					canceled = false;
//					statMessage = null;
//					return new ActualTask();
//				}
//			};
//			worker.start();
//		}
//
//		/**
//		 * Called from ProgressBarDemo to find out how much work needs to be done.
//		 */
//		public int getLengthOfTask() {
//			return lengthOfTask;
//		}
//
//		/**
//		 * Called from ProgressBarDemo to find out how much has been done.
//		 */
//		public int getCurrent() {
//			return current;
//		}
//
//		public void cancel() {
//			canceled = true;
//			statMessage = null;
//		}
//
//		/**
//		 * Called from ProgressBarDemo to find out if the task has completed.
//		 */
//		public boolean isDone() {
//			return done;
//		}
//
//		/**
//		 * Returns the most recent status message, or null if there is no current status message.
//		 */
//		public String getMessage() {
//			return statMessage;
//		}
//
//		public void undo() {
//			myModel.setList(Undo);
//			myModel.fireTableDataChanged();
//		}
//
//		/**
//		 * The actual long running task. This runs in a SwingWorker thread.
//		 */
//		class ActualTask {
//			ActualTask() {
//				for (int i = 0; i < noOfPatches; i++) {
//					if (canceled) {
//						break;
//					}
//					IPatch patch = ((SceneListModel) myModel).getSceneAt(i).getPatch();
//					statMessage = patch.getDriver() + ":" + patch.getName();
//					UpdatePatch(sceneIndx[i]);
//					current += syxArray[i];
//				}
//				statMessage = "Finished";
//				done = true;
//				changed = true;
//				myModel.fireTableDataChanged();
//			}
//		}
//	}
//
//	public void updateScene() { 
//		int len = myModel.getRowCount();
//		int[] indx = new int[len];
//		for (int i = 0; i < len; i++) {
//			indx[i] = i;
//		}
//		updateScene(indx);
//	}
//
//	public void updateSelected() {
//		int[] indx = table.getSelectedRows();
//		updateScene(indx);
//	}
//
//	void updateScene(int[] indx) {
//		final Timer timer;
//		final UpdateSceneTask task;
//		ActionListener TimerListener;
//		// TODO ssmcurtis ... sort
//		task = new UpdateSceneTask(myModel, indx);
//		final ProgressMonitor progressMonitor = new ProgressMonitor(table, "Update Scene", "", 0,
//				task.getLengthOfTask());
//		progressMonitor.setProgress(0);
//		progressMonitor.setMillisToDecideToPopup(0);
//		progressMonitor.setMillisToPopup(0);
//		timer = new Timer(100, null);
//
//		TimerListener = new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				progressMonitor.setProgress(task.getCurrent());
//				String s = task.getMessage();
//				if (s != null) {
//					progressMonitor.setNote(s);
//				}
//				if (progressMonitor.isCanceled()) {
//					timer.stop();
//					progressMonitor.close();
//					task.cancel();
//					task.undo();
//				}
//				if (task.isDone()) {
//					if (task.waitToClose > 10) {
//						timer.stop();
//						progressMonitor.setProgress(task.getLengthOfTask());
//					} else {
//						task.waitToClose++;
//					}
//				}
//			}
//		};
//		timer.addActionListener(TimerListener);
//
//		task.go();
//		timer.start();
//	}
//
//	public FileFilter getFileFilter() {
//		return FILE_FILTER;
//	}
//
//	public String getFileExtension() {
//		return FILE_EXTENSION;
//	}

}
