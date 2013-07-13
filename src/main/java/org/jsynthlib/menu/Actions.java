package org.jsynthlib.menu;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.JSynthResource;
import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.action.AboutAction;
import org.jsynthlib.menu.action.ComparePatchAction;
import org.jsynthlib.menu.action.CopyAction;
import org.jsynthlib.menu.action.CutAction;
import org.jsynthlib.menu.action.DeleteAction;
import org.jsynthlib.menu.action.DocsAction;
import org.jsynthlib.menu.action.EditAction;
import org.jsynthlib.menu.action.ExitAction;
import org.jsynthlib.menu.action.ExportAction;
import org.jsynthlib.menu.action.ExtractAction;
import org.jsynthlib.menu.action.GetAction;
import org.jsynthlib.menu.action.HomePageAction;
import org.jsynthlib.menu.action.ImportAction;
import org.jsynthlib.menu.action.ImportAllAction;
import org.jsynthlib.menu.action.LicenseAction;
import org.jsynthlib.menu.action.MonitorAction;
import org.jsynthlib.menu.action.NewAction;
import org.jsynthlib.menu.action.OpenAction;
import org.jsynthlib.menu.action.PasteAction;
import org.jsynthlib.menu.action.PlayAction;
import org.jsynthlib.menu.action.PlayCompleteAction;
import org.jsynthlib.menu.action.PrefsAction;
import org.jsynthlib.menu.action.PrintAction;
import org.jsynthlib.menu.action.ReassignAction;
import org.jsynthlib.menu.action.SaveAction;
import org.jsynthlib.menu.action.SaveAsAction;
import org.jsynthlib.menu.action.SearchAction;
import org.jsynthlib.menu.action.SelectByFilterAction;
import org.jsynthlib.menu.action.SelectClearAction;
import org.jsynthlib.menu.action.SelectDuplicateAction;
import org.jsynthlib.menu.action.SendAction;
import org.jsynthlib.menu.action.SendToAction;
import org.jsynthlib.menu.action.ShowDevicesAction;
import org.jsynthlib.menu.action.StoreAction;
import org.jsynthlib.menu.action.StoreLibraryAction;
import org.jsynthlib.menu.action.UpdateCommentAction;
import org.jsynthlib.menu.action.UploadAction;
import org.jsynthlib.menu.action._ShowTestAction;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.window.AbstractLibraryFrame;
import org.jsynthlib.menu.window.BankEditorFrame;
import org.jsynthlib.menu.window.CompatibleFileDialog;
import org.jsynthlib.menu.window.DocumentationWindow;
import org.jsynthlib.menu.window.LibraryFrame;
import org.jsynthlib.menu.window.MenuFrame;
import org.jsynthlib.menu.window.MidiMonitorDialog;
import org.jsynthlib.menu.window.PatchEditorFrame;
import org.jsynthlib.menu.window.SearchDialog;
import org.jsynthlib.menu.window.SelectByFilterDialog;
import org.jsynthlib.model.JSynthBpm;
import org.jsynthlib.model.JSynthOctave;
import org.jsynthlib.model.JSynthSequence;
import org.jsynthlib.model.patch.PatchBasket;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.MacUtil;

/**
 * Define Action classes
 * 
 * @version $Id$
 */

@SuppressWarnings("serial")
final public class Actions {
	// I hope 64bit is enough for a while.
	public static final long EN_ABOUT = 0x0000000000000001L;
	public static final long EN_COPY = 0x0000000000000002L;
	public static final long EN_CUT = 0x0000000000000008L;
	public static final long EN_DELETE = 0x0000000000000010L;
	public static final long EN_DOCS = 0x0000000000000040L;
	public static final long EN_EDIT = 0x0000000000000080L;
	public static final long EN_EXIT = 0x0000000000000100L;
	public static final long EN_EXPORT = 0x0000000000000200L;
	public static final long EN_EXTRACT = 0x0000000000000400L;
	public static final long EN_GET = 0x0000000000000800L;
	public static final long EN_HOME_PAGE = 0x0000000000001000L;
	public static final long EN_IMPORT = 0x0000000000002000L;
	public static final long EN_IMPORT_ALL = 0x0000000000004000L;
	public static final long EN_LICENSE = 0x0000000000008000L;
	public static final long EN_MONITOR = 0x0000000000010000L;
	public static final long EN_NEW = 0x0000000000020000L;
	public static final long EN_NEW_SCENE = 0x0000000000080000L;
	public static final long EN_NEXT_FADER = 0x0000000000100000L;
	public static final long EN_OPEN = 0x0000000000200000L;
	public static final long EN_PASTE = 0x0000000000400000L;
	public static final long EN_PLAY = 0x0000000000800000L;
	public static final long EN_PREFS = 0x0000000001000000L;
	public static final long EN_REASSIGN = 0x0000000002000000L;
	public static final long EN_SAVE = 0x0000000004000000L;
	public static final long EN_SAVE_AS = 0x0000000008000000L;
	public static final long EN_SEARCH = 0x0000000010000000L;
	public static final long EN_SEND = 0x0000000020000000L;
	public static final long EN_SEND_TO = 0x0000000040000000L;
	public static final long EN_SELECT_DUPLICATE = 0x0000000080000000L;
	public static final long EN_STORE = 0x0000000100000000L;
	public static final long EN_TRANSFER_SCENE = 0x0000000200000000L;
	public static final long EN_UPLOAD = 0x0000000400000000L;
	public static final long EN_PREV_FADER = 0x0000000800000000L;
	public static final long EN_PRINT = 0x0000001000000000L;
	public static final long EN_UPDATE_SCENE = 0x0000002000000000L;
	public static final long EN_UPDATE_SELECTED = 0x0000004000000000L;
	public static final long EN_SHOW_DEVICE = 0x0000008000000000L;
	public static final long EN_PLAY_ALL = 0x0000010000000000L;
	public static final long EN_COMPARE_PATCH = 0x0000020000000000L;
	public static final long EN_SELECT_BYFILTER = 0x0000040000000000L;
	public static final long EN_SELECT_CLEAR = 0x0000080000000000L;
	public static final long EN_STORE_LIBRARY = 0x0000100000000000L;
	public static final long EN_UPDATE_COMMENT = 0x0000200000000000L;

	/** All actions excluding ones which are always enabled. */
	public static final long EN_ALL = (// EN_ABOUT
	EN_COPY | EN_CUT | EN_DELETE
			| EN_STORE_LIBRARY
			// | EN_DOCS
			| EN_EDIT
			| EN_UPDATE_COMMENT
			// | EN_EXIT
			| EN_EXPORT
			| EN_EXTRACT
			| EN_GET
			| EN_HOME_PAGE
			| EN_IMPORT
			| EN_IMPORT_ALL
			| EN_COMPARE_PATCH
			// | EN_LICENSE
			// | EN_MONITOR
			// | EN_NEW
			// | EN_NEW_PATCH
			// | EN_NEW_SCENE
			// | EN_NEXT_FADER
			// | EN_PREV_FADER
			// | EN_OPEN
			// | EN_PASTE : 'paste' needs special handling
			| EN_PLAY | EN_PLAY_ALL | EN_SELECT_DUPLICATE | EN_SELECT_BYFILTER | EN_SELECT_CLEAR
			// | EN_PREFS
			| EN_REASSIGN | EN_SAVE | EN_SAVE_AS | EN_SEARCH | EN_SEND | EN_SEND_TO | EN_STORE | EN_TRANSFER_SCENE | EN_UPDATE_SCENE
			| EN_UPDATE_SELECTED | EN_UPLOAD);

	private static JPopupMenu menuPatchPopup;
	private static MidiMonitorDialog midiMonitor;
	private static SearchDialog searchDialog;
	private static SelectByFilterDialog selectByFilterDialog;
	private static DocumentationWindow docWin;
	private static DocumentationWindow licWin;
	private static DocumentationWindow hpWin;

	private static HashMap<Serializable, Integer> mnemonics = new HashMap<Serializable, Integer>();

	private static Action newAction = new NewAction(mnemonics);
	private static Action openAction = new OpenAction(mnemonics);
	private static Action saveAction = new SaveAction(mnemonics);
	private static Action saveAsAction = new SaveAsAction(mnemonics);
	// private static Action newSceneAction = new NewSceneAction(mnemonics);
	// private static Action txransferSceneAction = new TransferSceneAction(mnemonics);
	// private static Action updateSceneAction = new UpdateSceneAction(mnemonics);
	// private static Action sortAction = new SortAction(mnemonics);
	private static Action searchAction = new SearchAction(mnemonics);
	public static Action exitAction = new ExitAction(mnemonics);

	private static Action copyAction = new CopyAction(mnemonics);
	private static Action cutAction = new CutAction(mnemonics);
	private static Action pasteAction = new PasteAction(mnemonics);
	private static Action deleteAction = new DeleteAction(mnemonics);
	private static Action importAction = new ImportAction(mnemonics);
	private static Action exportAction = new ExportAction(mnemonics);
	private static Action importAllAction = new ImportAllAction(mnemonics);
	private static Action sendAction = new SendAction(mnemonics);
	private static Action sendToAction = new SendToAction(mnemonics);
	private static Action storeLibraryAction = new StoreLibraryAction(mnemonics);
	private static Action selectDuplicatedAction = new SelectDuplicateAction(mnemonics);
	// private static Action updateSelectedAction = new UpdateSelectedAction(mnemonics);// R. Wirski
	private static Action printAction = new PrintAction(mnemonics);
	private static Action storeAction = new StoreAction(mnemonics);
	private static Action getAction = new GetAction(mnemonics);
	private static Action selectByFilterAction = new SelectByFilterAction(mnemonics);
	private static Action selectClearAction = new SelectClearAction(mnemonics);

	private static Action playAction = new PlayAction(mnemonics);
	private static Action playAllAction = new PlayCompleteAction(mnemonics);
	private static Action editAction = new EditAction(mnemonics);
	private static Action updateCommentAction = new UpdateCommentAction(mnemonics);
	private static Action reassignAction = new ReassignAction(mnemonics);
	private static Action extractAction = new ExtractAction(mnemonics);

	private static Action prefsAction = new PrefsAction(mnemonics);
	private static Action monitorAction = new MonitorAction(mnemonics);

	private static Action aboutAction = new AboutAction(mnemonics);
	private static Action docsAction = new DocsAction(mnemonics);
	private static Action licenseAction = new LicenseAction(mnemonics);
	private static Action homePageAction = new HomePageAction(mnemonics);
	private static Action uploadAction = new UploadAction(mnemonics);
	private static Action showDevice = new ShowDevicesAction(mnemonics);
	private static Action showTestAction = new _ShowTestAction(mnemonics);
	private static Action comparePatch = new ComparePatchAction(mnemonics);

	public static Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
	/** just for efficiency. */
	public static boolean isMac = MacUtil.isMac();

	private static JComboBox<String> sequence = new JComboBox<String>(JSynthSequence.getNames());
	private static JComboBox<String> bpm = new JComboBox<String>(JSynthBpm.getNames());
	private static JComboBox<String> octave = new JComboBox<String>(JSynthOctave.getNames());
	private static JComboBox<Integer> loop = new JComboBox<Integer>(new Integer[] { 0, 1, 4, 8, 16, 32 });

	private static JMenu menuSpecial = new JMenu("Special");

	// don't have to call constructor for Utility class.
	private Actions() {
	}

	public static void createAllActions() {

		if (isMac) {
			MacUtil.init(exitAction, prefsAction, aboutAction);
		} else {
			setMnemonics(mnemonics); // set keyboard short cut
		}
	}

	/** This sets up the Menubar. Called from JSLDesktop. */
	public static JMenuBar createMenuBar() {
		HashMap<Serializable, Integer> mnemonics = new HashMap<Serializable, Integer>();
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu(mnemonics, mask));

		// menuBar.add(createLibraryMenu(mnemonics, mask));

		menuBar.add(createEditMenu(mnemonics, mask));

		menuBar.add(createPatchMenu(mnemonics, mask));

		// menuBar.add(createSpecialMenu(mnemonics, mask));

		menuBar.add(createWindowMenu(mnemonics, mask));
		menuBar.add(createHelpMenu(mnemonics, mask));

		// set keyboard short cut
		if (!isMac)
			setMnemonics(mnemonics);

		return menuBar;
	}

	private static JMenu createFileMenu(HashMap<Serializable, Integer> mnemonics, int mask) {
		JMenuItem mi;
		JMenu menuFile = new JMenu("File");
		mnemonics.put(menuFile, new Integer(KeyEvent.VK_F));

		mi = menuFile.add(newAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, mask));
		// mi = menuFile.add(newSceneAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));

		mi = menuFile.add(openAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
		mi = menuFile.add(saveAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
		menuFile.add(saveAsAction);

		menuFile.addSeparator();
		menuFile.add(importAction);
		menuFile.add(importAllAction);
		menuFile.add(exportAction);

		if (!isMac) {
			menuFile.addSeparator();
			menuFile.add(exitAction);
		}
		return menuFile;
	}

	private static JMenu createEditMenu(HashMap<Serializable, Integer> mnemonics, int mask) {
		JMenuItem mi;
		JMenu menuPatch = new JMenu("Edit");
		mnemonics.put(menuPatch, new Integer(KeyEvent.VK_L));
		// mi = menuPatch.add(copyAction);
		// mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0));
		// mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
		// mi = menuPatch.add(cutAction);
		// mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CUT, 0));
		// mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, mask));
		// mi = menuPatch.add(pasteAction);
		// mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0));
		// mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
		mi = menuPatch.add(deleteAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		menuPatch.addSeparator();

		mi = menuPatch.add(editAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, mask));
		menuPatch.add(updateCommentAction);

		menuPatch.addSeparator();
		mi = menuPatch.add(searchAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, mask));

		return menuPatch;
	}

	private static JMenu createPatchMenu(HashMap<Serializable, Integer> mnemonics, int mask) {
		JMenuItem mi;
		JMenu menuPatch = new JMenu("Patches");
		mnemonics.put(menuPatch, new Integer(KeyEvent.VK_P));

		mi = menuPatch.add(sendAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, 0));
		// menuPatch.add(updateSelectedAction);
		menuPatch.add(sendToAction);
		menuPatch.add(storeLibraryAction);
		menuPatch.add(storeAction);
		mi = menuPatch.add(getAction); // phil@muqus.com
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, mask));
		menuPatch.addSeparator();

		// mi = menuPatch.add(printAction);
		// mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));

		mi = menuPatch.add(playAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));

		menuPatch.add(playAllAction);
		menuPatch.addSeparator();

		menuPatch.add(reassignAction);
		menuPatch.add(extractAction);
		menuPatch.add(comparePatch);

		menuPatch.addSeparator();
		menuPatch.add(selectDuplicatedAction);
		menuPatch.add(selectByFilterAction);
		menuPatch.add(selectClearAction);

		// menuPatch.addSeparator();
		// menuPatch.add(uploadAction);

		// menuPatch.addMenuListener(new MenuListener() { // need this???
		// public void menuCanceled(MenuEvent e) {
		// }
		//
		// public void menuDeselected(MenuEvent e) {
		// }
		//
		// public void menuSelected(MenuEvent e) {
		// pasteAction.setEnabled(true);
		// }
		// });
		return menuPatch;
	}

	private static JMenu createSpecialMenu(HashMap<Serializable, Integer> mnemonics, int mask) {

		mnemonics.put(menuSpecial, new Integer(KeyEvent.VK_S));
		return menuSpecial;
	}

	/** List of JSLWindowMenus including one for invisible frame. */
	public static ArrayList<JSLWindowMenu> windowMenus = new ArrayList<JSLWindowMenu>();

	private static JMenu createWindowMenu(HashMap<Serializable, Integer> mnemonics, int mask) {
		JSLWindowMenu wm = new JSLWindowMenu("Window");
		windowMenus.add(wm);

		if (!isMac) {
			wm.add(prefsAction);
			wm.setMnemonic(KeyEvent.VK_W);
		}
		wm.add(monitorAction);
		// wm.add(JSLDesktop.toolBarAction);
		wm.addSeparator();

		wm.add(arrangeAction);

		wm.add(tilingAction);

		JMenuItem mi = wm.add(closeAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask));
		wm.addSeparator();

		// add menu entries of existing frames
		JSLDesktop desktop = PatchBayApplication.getDesktop();
		if (desktop != null) {
			// when desktop is created, 'desktop' is still null.
			Iterator<?> it = desktop.getJSLFrameIterator();
			while (it.hasNext()) {
				wm.add((JSLFrame) it.next());
			}
		}
		return wm;
	}

	private static Action tilingAction = new AbstractAction("Tiling") {
		public void actionPerformed(ActionEvent ex) {
			JSLDesktop desktop = PatchBayApplication.getDesktop();
			if (desktop != null) {
				desktop.tiling();
			}

		}
	};

	private static Action arrangeAction = new AbstractAction("Arrange") {
		public void actionPerformed(ActionEvent ex) {
			JSLDesktop desktop = PatchBayApplication.getDesktop();
			if (desktop != null) {
				desktop.cascade();
			}

		}
	};

	private static Action closeAction = new AbstractAction("Close") {
		public void actionPerformed(ActionEvent ex) {
			try {
				MenuDesktop desktop = PatchBayApplication.getDesktop();
				JSLFrame frame = desktop.getSelectedFrame();
				if (frame != null) {
					frame.setClosed(true);
				} else {
					PatchBayApplication.getDesktop().closingProc();
				}
			} catch (PropertyVetoException e) {
				// don't know how to handle this.
				e.printStackTrace();
			}
		}
	};

	/** to keep track the relation between a JSLMenuBar and a JSLFrame. */
	public static HashMap<MenuFrame, JMenuBar> frames = new HashMap<MenuFrame, JMenuBar>();

	private static JMenu createHelpMenu(HashMap<Serializable, Integer> mnemonics, int mask) {
		JMenuItem mi;
		JMenu menuHelp = new JMenu("Help");
		mnemonics.put(menuHelp, new Integer(KeyEvent.VK_H));
		if (ErrorMsgUtil.getDebugLevel() > 0) {
			mi = menuHelp.add(showTestAction);
		}
		mi = menuHelp.add(showDevice);

		mi = menuHelp.add(docsAction);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HELP, 0));
		// mi = menuHelp.add(homePageAction);
		mi = menuHelp.add(licenseAction);

		if (!isMac) {
			menuHelp.add(aboutAction);
		}
		return menuHelp;
	}

	/** This sets up the mnemonics */
	private static void setMnemonics(Map<Serializable, Integer> mnemonics) {
		Iterator<Serializable> it = mnemonics.keySet().iterator();
		Object key, value;
		while (it.hasNext()) {
			key = it.next();
			value = mnemonics.get(key);
			if (key instanceof JMenuItem)
				((JMenuItem) key).setMnemonic(((Integer) value).intValue());
			else if (key instanceof Action)
				((Action) key).putValue(Action.MNEMONIC_KEY, value);
		}
	}

	public static void createPopupMenu() {
		// create popup menu
		menuPatchPopup = new JPopupMenu();

		menuPatchPopup.add(extractAction);
		menuPatchPopup.add(getAction);

		// menuPatchPopup.add(playAction);
		// menuPatchPopup.add(playAllAction);
		// menuPatchPopup.add(editAction);
		// menuPatchPopup.add(updateSelectedAction);
		menuPatchPopup.addSeparator();

		menuPatchPopup.add(playAction);
		menuPatchPopup.add(sendAction);
		menuPatchPopup.add(storeAction);
		menuPatchPopup.add(storeLibraryAction);
		menuPatchPopup.add(reassignAction);
		menuPatchPopup.addSeparator();

		menuPatchPopup.add(copyAction);
		menuPatchPopup.add(cutAction);
		menuPatchPopup.add(pasteAction);
		menuPatchPopup.add(deleteAction);
		menuPatchPopup.addSeparator();

		menuPatchPopup.add(searchAction);

		// menuPatchPopup.add(uploadAction);
	}

	/** show popup menu for patch. */
	public static void showMenuPatchPopup(JTable tbl, int x, int y) {
		if (menuPatchPopup == null) {
			createPopupMenu();
		}

		menuPatchPopup.show(tbl, x, y);
	}

	public static JToolBar createToolBar() {
		// create tool bar
		JToolBar toolBar = new JToolBar();
		toolBar.setPreferredSize(new Dimension(500, 35));
		toolBar.setFloatable(true);

		toolBar.add(createToolBarButton(newAction, "New", "New Library"));
		toolBar.add(createToolBarButton(openAction, "Open", "Open Library"));
		toolBar.add(createToolBarButton(saveAction, "Save", "Save Library"));

		toolBar.addSeparator();

		// toolBar.add(createToolBarButton(copyAction, "Copy", "Copy Patch"));
		// toolBar.add(createToolBarButton(cutAction, "Cut", "Cut Patch"));
		// toolBar.add(createToolBarButton(pasteAction, "Paste", "Paste Patch"));
		toolBar.add(createToolBarButton(importAction, "Import", "Import Patch"));
		toolBar.add(createToolBarButton(exportAction, "Export", "Export Patch"));

		toolBar.addSeparator();

		toolBar.add(createToolBarButton(playAction, "Play", "Play Patch"));

		octave.setToolTipText("Select Octave");
		octave.setSelectedIndex(AppConfig.getOctaveOrdinal());
		octave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppConfig.setOctaveOrdinal(octave.getSelectedIndex());
			}
		});
		toolBar.add(octave);

		// TODO ssmCurtis setValue for comboBox
		loop.setToolTipText("Loops");
		AppConfig.setLoopcount(0);
		loop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppConfig.setLoopcount(loop.getItemAt(loop.getSelectedIndex()));
			}
		});
		toolBar.add(loop);

		sequence.setToolTipText("Select Sequence");
		sequence.setSelectedIndex(AppConfig.getSequenceOrdinal());
		sequence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppConfig.setSequenceOrdinal(sequence.getSelectedIndex());
			}
		});
		toolBar.add(sequence);

		bpm.setToolTipText("Select bpm");
		bpm.setSelectedIndex(AppConfig.getBpmOrdinal());
		bpm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppConfig.setBpmOrdinal(bpm.getSelectedIndex());
			}
		});
		toolBar.add(bpm);

		toolBar.add(createToolBarButton(playAllAction, "Play", "Play library"));
		toolBar.add(createToolBarButton(storeAction, "Store", "Store Patch"));
		toolBar.add(createToolBarButton(editAction, "Edit", "Edit Patch"));

		// toolBar.addSeparator();

		// toolBar.add(createToolBarButton(prevFaderAction, "Prev", "Go to Previous Fader Bank"));
		// toolBar.add(createToolBarButton(nextFaderAction, "Next", "Go to Next Fader Bank"));
		setEnabledAfterPreferences();
		return toolBar;
	}

	private static JButton createToolBarButton(Action a, String label, String tooltip) {
		String label2 = label.toLowerCase();
		URL u1 = PatchBayApplication.class.getResource(JSynthResource.RESOURCES_PACKAGE.getUri() + "images/" + label2 + ".png");
		URL u2 = PatchBayApplication.class.getResource(JSynthResource.RESOURCES_PACKAGE.getUri() + "images/disabled-" + label2 + ".png");

		// Create and initialize the button.
		JButton button = new JButton(a);
		button.setToolTipText(tooltip);

		if (u1 != null) { // image found
			button.setText(null);
			button.setIcon(new ImageIcon(u1, label));
			if (u2 != null) {
				button.setDisabledIcon(new ImageIcon(u2, label));
			}
		} else { // no image found
			button.setText(label);
			ErrorMsgUtil.reportStatus("Resource not found: " + JSynthResource.RESOURCES_PACKAGE.getUri() + "images/" + label + ".png");
		}
		return button;
	}

	/**
	 * Enable/disable Actions.
	 * 
	 * @param b
	 *            <code>true</code> to enable Actions, <code>false</code> to disable them.
	 * @param v
	 *            Specify Actions to be enabled/disabled. Use constants <code>EN_*</code>.
	 */
	public static void setEnabledAfterPreferences() {
		playAllAction.setEnabled(!AppConfig.getSequencerEnable());
		sequence.setEnabled(AppConfig.getSequencerEnable());
		bpm.setEnabled(AppConfig.getSequencerEnable());
	}

	public static void setEnabled(boolean b, long v) {
		sequence.setEnabled(AppConfig.getSequencerEnable());
		bpm.setEnabled(AppConfig.getSequencerEnable());

		if ((v & EN_ABOUT) != 0)
			aboutAction.setEnabled(b);
		if ((v & EN_COPY) != 0)
			copyAction.setEnabled(b);
		// if ((v & EN_CROSSBREED) != 0)
		// crossBreedAction.setEnabled(b);
		if ((v & EN_CUT) != 0)
			cutAction.setEnabled(b);
		if ((v & EN_DELETE) != 0)
			deleteAction.setEnabled(b);
		// if ((v & EN_DELETE_DUPLICATES) != 0)
		// deleteDuplicatesAction.setEnabled(b);
		if ((v & EN_DOCS) != 0)
			docsAction.setEnabled(b);
		if ((v & EN_EDIT) != 0)
			editAction.setEnabled(b);
		if ((v & EN_UPDATE_COMMENT) != 0)
			updateCommentAction.setEnabled(b);
		if ((v & EN_EXIT) != 0)
			exitAction.setEnabled(b);
		if ((v & EN_EXPORT) != 0)
			exportAction.setEnabled(b);
		if ((v & EN_EXTRACT) != 0)
			extractAction.setEnabled(b);
		if ((v & EN_SELECT_BYFILTER) != 0)
			selectByFilterAction.setEnabled(b);
		if ((v & EN_SELECT_CLEAR) != 0)
			selectClearAction.setEnabled(b);
		if ((v & EN_GET) != 0)
			getAction.setEnabled(b);
		if ((v & EN_HOME_PAGE) != 0)
			homePageAction.setEnabled(b);
		if ((v & EN_IMPORT) != 0)
			importAction.setEnabled(b);
		if ((v & EN_IMPORT_ALL) != 0)
			importAllAction.setEnabled(b);
		if ((v & EN_LICENSE) != 0)
			licenseAction.setEnabled(b);
		if ((v & EN_MONITOR) != 0)
			monitorAction.setEnabled(b);
		if ((v & EN_NEW) != 0)
			newAction.setEnabled(b);
		if ((v & EN_COMPARE_PATCH) != 0)
			comparePatch.setEnabled(b);
		// if ((v & EN_NEW_PATCH) != 0)
		// newPatchAction.setEnabled(b);
		// if ((v & EN_NEW_SCENE) != 0)
		// newSceneAction.setEnabled(b);
		// if ((v & EN_NEXT_FADER) != 0)
		// nextFaderAction.setEnabled(b);
		// if ((v & EN_PREV_FADER) != 0)
		// prevFaderAction.setEnabled(b);
		if ((v & EN_OPEN) != 0)
			openAction.setEnabled(b);
		if ((v & EN_PASTE) != 0)
			pasteAction.setEnabled(b);
		if ((v & EN_PLAY) != 0)
			playAction.setEnabled(b);
		if (((v & EN_PLAY_ALL) != 0))
			playAllAction.setEnabled(b && !AppConfig.getSequencerEnable());
		if ((v & EN_PREFS) != 0)
			prefsAction.setEnabled(b);
		if ((v & EN_REASSIGN) != 0)
			reassignAction.setEnabled(b);
		if ((v & EN_SAVE) != 0)
			saveAction.setEnabled(b);
		if ((v & EN_SAVE_AS) != 0)
			saveAsAction.setEnabled(b);
		if ((v & EN_SEARCH) != 0)
			searchAction.setEnabled(b);
		if ((v & EN_SEND) != 0)
			sendAction.setEnabled(b);
		if ((v & EN_SEND_TO) != 0)
			sendToAction.setEnabled(b);
		if ((v & EN_STORE_LIBRARY) != 0)
			storeLibraryAction.setEnabled(b);
		if ((v & EN_SELECT_DUPLICATE) != 0)
			selectDuplicatedAction.setEnabled(b);
		if ((v & EN_STORE) != 0)
			storeAction.setEnabled(b);
		// if ((v & EN_TRANSFER_SCENE) != 0)
		// transferSceneAction.setEnabled(b);
		// if ((v & EN_UPDATE_SCENE) != 0)
		// updateSceneAction.setEnabled(b);
		// if ((v & EN_UPDATE_SELECTED) != 0)
		// updateSelectedAction.setEnabled(b);
		if ((v & EN_UPLOAD) != 0)
			uploadAction.setEnabled(b);
		if ((v & EN_PRINT) != 0)
			printAction.setEnabled(b);
	}

	/**
	 * Create a new Library Window and load a Library from disk to fill it! Fun!
	 */
	public static void openFrame(File file) {
		// PatchEdit.showWaitDialog("Loading " + file + " ...");
		// The previous line won't do anything unless we use a new thread to do
		// the loading.
		// Right now, it's using the GUI event thread (since it was triggered
		// from a GUI button
		// so the GUI never gets a chance to make the WaitDialog until we've
		// finished loading.
		// - Emenaker, 2006-02-02
		AbstractLibraryFrame frame;
		if (file.exists()) {
			// try LibraryFrame then SceneFrame
			frame = new LibraryFrame(file);
			try {
				frame.open(file);
			} catch (Exception e) {
				e.printStackTrace();
				// frame = new SceneFrame(file);
				// try {
				// frame.open(file);
				// } catch (Exception e1) {
				// // PatchEdit.hideWaitDialog();
				// // See comment at beginning of this method
				// ErrorMsg.reportError("Error", "Error Loading Library:\n " + file.getAbsolutePath(), e1);
				// return;
				// }
			}
		} else {
			// PatchEdit.hideWaitDialog();
			// See comment at beginning of this method
			ErrorMsgUtil.reportError("Error", "File Does Not Exist:\n" + file.getAbsolutePath());
			return;
		}
		// PatchEdit.hideWaitDialog();
		// See comment at beginning of this method

		addLibraryFrame(frame);
	}

	/** add and show a Library/Scene Window */
	public static void addLibraryFrame(AbstractLibraryFrame frame) {
		PatchBayApplication.getDesktop().add(frame);
		frame.moveToDefaultLocation();
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			// I don't *actually* know what this is for :-)
			ErrorMsgUtil.reportStatus(e);
		}
	}

	/** This one saves a Library to Disk */
	public static void saveFrame() {
		try {
			AbstractLibraryFrame oFrame = (AbstractLibraryFrame) getSelectedFrame();
			if (oFrame.getTitle().startsWith("Unsaved ")) {
				File fn = showSaveDialog(oFrame);
				if (fn != null)
					oFrame.save(fn);
			} else {
				oFrame.save();
			}
		} catch (IOException e) {
			ErrorMsgUtil.reportError("Error", "Unable to Save Library", e);
		}
	}

	/** Save and specify a file name */
	public static void saveFrameAs() {
		try {
			AbstractLibraryFrame oFrame = (AbstractLibraryFrame) getSelectedFrame();
			File fn = showSaveDialog(oFrame);
			if (fn != null)
				oFrame.save(fn);
		} catch (IOException ex) {
			ErrorMsgUtil.reportError("Error", "Unable to Save Library", ex);
		}
	}

	private static File showSaveDialog(AbstractLibraryFrame oFrame) {
		CompatibleFileDialog fc = new CompatibleFileDialog();
		FileFilter filter = oFrame.getFileFilter();

		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		fc.setCurrentDirectory(new File(AppConfig.getLibPath()));
		if (fc.showSaveDialog(PatchBayApplication.getInstance()) != JFileChooser.APPROVE_OPTION)
			return null;

		File file = fc.getSelectedFile();

		if (!file.getName().toLowerCase().endsWith(oFrame.getFileExtension())) {
			file = new File(file.getPath() + oFrame.getFileExtension());
		}
		if (file.isDirectory()) {
			ErrorMsgUtil.reportError("Error", "Can not Save over a Directory");
			return null;
		}
		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(null, "Are you sure?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				return null;
			}
		}
		return file;
	}

	public static PatchBasket getSelectedFrame() {
		if (PatchBayApplication.getDesktop().getSelectedFrame() instanceof AbstractLibraryFrame) {
			// ErrorMsgUtil.reportStatus("Actions.getSelectedFrame: AbstractLibraryFrame");
		} else if (PatchBayApplication.getDesktop().getSelectedFrame() instanceof BankEditorFrame) {
			// ErrorMsgUtil.reportStatus("Actions.getSelectedFrame: BankEditorFrame");
		} else if (PatchBayApplication.getDesktop().getSelectedFrame() instanceof PatchEditorFrame) {
			// ErrorMsgUtil.reportStatus("Actions.getSelectedFrame: PatchEditorFrame");
		} else {
			// ErrorMsgUtil.reportStatus("Actions.getSelectedFrame: unknown");
		}
		return (PatchBasket) PatchBayApplication.getDesktop().getSelectedFrame();
	}

	// TODO ssmCurtis - check .. new thread
	public static void EditActionProc() {

		class Worker extends Thread {

			public void run() {
				try {
					JSLFrame frm = getSelectedFrame().editSelectedPatch();
					if (frm != null) {
						PatchBayApplication.getDesktop().add(frm);
						frm.moveToDefaultLocation();
						frm.setVisible(true);
						// hack for old Java bug
						/*
						 * if (frm instanceof PatchEditorFrame) for (int i = 0; i < ((PatchEditorFrame)
						 * frm).sliderList.size(); i++) { JSlider slider = (JSlider) ((PatchEditorFrame)
						 * frm).sliderList.get(i); Dimension dim = slider.getSize(); if (dim.width > 0) { dim.width++;
						 * slider.setSize(dim); } }
						 */
						try {
							frm.setSelected(true);
						} catch (PropertyVetoException e) {
							ErrorMsgUtil.reportStatus(e);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					// ErrorMsg.reportError("Error", "Error in PatchEditor.", ex);
				}
			}
		}

		Worker w = new Worker();
		w.setDaemon(true);
		w.start();
	}

	/**
	 * Output string to MIDI Monitor Window. Use MidiUtil.log() instead of this.
	 * 
	 * @param s
	 *            string to be output
	 */
	public static void midiMonitorLog(String s) {
		if (getMidiMonitor() != null && getMidiMonitor().isVisible())
			getMidiMonitor().log(s);
	}

	public static DocumentationWindow getHpWin() {
		return hpWin;
	}

	public static void setHpWin(DocumentationWindow hpWin) {
		Actions.hpWin = hpWin;
	}

	public static DocumentationWindow getLicWin() {
		return licWin;
	}

	public static void setLicWin(DocumentationWindow licWin) {
		Actions.licWin = licWin;
	}

	public static MidiMonitorDialog getMidiMonitor() {
		return midiMonitor;
	}

	public static void setMidiMonitor(MidiMonitorDialog midiMonitor) {
		Actions.midiMonitor = midiMonitor;
	}

	public static SearchDialog getSearchDialog() {
		return searchDialog;
	}

	public static void setSearchDialog(SearchDialog searchDialog) {
		Actions.searchDialog = searchDialog;
	}

	public static DocumentationWindow getDocWin() {
		return docWin;
	}

	public static void setDocWin(DocumentationWindow docWin) {
		Actions.docWin = docWin;
	}

	public static SelectByFilterDialog getSelectByFilterDialog() {
		return selectByFilterDialog;
	}

	public static void setSelectByFilterDialog(SelectByFilterDialog selectByFilterDialog) {
		Actions.selectByFilterDialog = selectByFilterDialog;
	}

	public static void addSpecialMenuAction(Action specialAction) {
		ErrorMsgUtil.reportStatus(">>>> Add special action");
		menuSpecial.add(specialAction);
	}

	public static HashMap<Serializable, Integer> getMnemonics() {
		return mnemonics;
	}

	public static void setMnemonics(HashMap<Serializable, Integer> mnemonics) {
		Actions.mnemonics = mnemonics;
	}

}
