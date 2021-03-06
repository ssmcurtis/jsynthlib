package org.jsynthlib.menu.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.jsynthlib.JSynthConstants;
import org.jsynthlib.PatchBayApplication;
import org.jsynthlib.menu.Actions;
import org.jsynthlib.menu.JSLFrame;
import org.jsynthlib.menu.JSLFrameEvent;
import org.jsynthlib.menu.JSLFrameListener;
import org.jsynthlib.menu.PatchTransferHandler;
import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.menu.widgets.SysexWidget;
import org.jsynthlib.model.JSynthImportFileType;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.model.patch.PatchBasket;
import org.jsynthlib.model.patch.PatchSingle;
import org.jsynthlib.tools.ClipboardUtil;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.MidiUtil;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A base class of a patch editor.
 * 
 * @author ???
 * @version $Id$
 */
public class PatchEditorFrame extends MenuFrame implements PatchBasket {
	/** This is the patch we are working on. */
	public PatchSingle patchByParameter;
	/** Scroll Pane for the editor frame. */
	protected JPanel scrollPane;
	/** Note that calling addWidget() method may change the value of this. */
	protected GridBagConstraints gbc = new GridBagConstraints();
	/** A list of widget added by addWidget method. */
	protected ArrayList widgetList = new ArrayList();

	/** For Alignment, a size to scrollbar labels, zero disables */
	protected int forceLabelWidth = 0; // will be removed with setLongestLabel()

	/**
	 * Information about BankEditorFrame which created this PatchEditor frame (if applicable) so we can update that
	 * frame with the edited data on close.
	 */
	public BankEditorFrame bankFrame = null; // used by YamahaFS1RPerformanceEditor.java

	/** Bank of fader. Set by faderMoved method. */
	private int faderBank;
	/** Numfer of fader banks. Set by show method. */
	private int numFaderBanks;
	/** Number of patch edtor frame opened. */
	private static int nFrame = 0;
	/** send a patch when patch editor frame is activated. */
	private static boolean sendPatchOnActivated = true;

	/** which patch in bank we're editing */
	private int patchRow;
	private int patchCol;
	/** The last recently moved widget by fader. */
	private SysexWidget recentWidget;
	private int lastFader;
	private JScrollPane scroller;

	/**
	 * Creates a new <code>PatchEditorFrame</code> instance.
	 * 
	 * @param name
	 *            a name to display in the title bar.
	 * @param patch
	 *            a reference to <code>ISinglePatch</code> object stored in a patch library or a bank patch.
	 */
	public PatchEditorFrame(String name, PatchSingle patch) {
		this(name, patch, new JPanel(new GridBagLayout()));
	}

	protected PatchEditorFrame(String name, PatchSingle patch, JPanel panel) {
		super(PatchBayApplication.getDesktop(), name);

		nFrame++;
		patchByParameter = patch;

		scrollPane = panel;
		scroller = new JScrollPane(scrollPane);
		getContentPane().add(scroller);

		faderInEnable(AppConfig.getFaderEnable());

		scroller.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
//				ErrorMsgUtil.reportStatus(">>> repaint scroller released vert" );

				repaint();
			}
		});
		scroller.getHorizontalScrollBar().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
//				ErrorMsgUtil.reportStatus(">>> repaint scroller released hori" );
				repaint();
			}
		});
		addJSLFrameListener(new JSLFrameListener() {
			public void JSLFrameClosing(JSLFrameEvent e) {
				frameClosing();
			}

			public void JSLFrameOpened(JSLFrameEvent e) {
				frameOpened();
			}

			public void JSLFrameActivated(JSLFrameEvent e) {
				frameActivated();
			}

			public void JSLFrameClosed(JSLFrameEvent e) {
			}

			public void JSLFrameDeactivated(JSLFrameEvent e) {
				frameDeactivated();
			}

			public void JSLFrameDeiconified(JSLFrameEvent e) {
			}

			public void JSLFrameIconified(JSLFrameEvent e) {
			}
		});
	}

	public void setVisible(boolean b) {
		if (b) {
			numFaderBanks = getNumFaderBank();
			ErrorMsgUtil.reportStatus("PatchEditorFrame.show(): Num Fader Banks = " + numFaderBanks);
			faderHighlight();

			// first layout in full size
			pack();

			// resize if the frame size is bigger than the screen size
			Dimension screenSize = PatchBayApplication.getDesktop().getSize();
			Dimension frameSize = this.getSize();
			ErrorMsgUtil.reportStatus("PatchEditorFrame.setVisible(): scrollPane size = " + scrollPane.getSize()
					+ ", frame size = " + frameSize);

			if (frameSize.height > screenSize.height) {
				// Add necessary place for the vertical Scrollbar
				frameSize.width += scroller.getVerticalScrollBar().getPreferredSize().width;
				this.setSize(frameSize.width, screenSize.height);
			}
			if (frameSize.width > screenSize.width) {
				// Add necessary place for the horizontal Scrollbar.
				frameSize.height += scroller.getHorizontalScrollBar().getPreferredSize().height;
				// If the entire frame doen't fit in the window, then
				// rescale it to fit.
				if (frameSize.height > screenSize.height)
					frameSize.height = screenSize.height;
				this.setSize(screenSize.width, frameSize.height);
			}
		}
		super.setVisible(b);
	}

	/**
	 * Called when the frame is closed. Default ask for keep changes. May be redefined in sub-classes.
	 * ssmCurtis - changed to read only
	 */
	protected void frameClosing() {
		ErrorMsgUtil.reportStatus("###PE.FrameCloseing: nFrame = " + nFrame);
		nFrame--;
		// If zero or one frame remains, send a patch next time when a editor
		// frame is activated.
		sendPatchOnActivated = (nFrame < 2);

		// String[] choices = new String[] { "Keep Changes", "Revert to Original", "Place Changed Version on Clipboard"
		// };
		// int choice;
		// do {
		// choice = JOptionPane.showOptionDialog((Component) null,
		// "What do you wish to do with the changed copy of the Patch?", "Save Changes?",
		// JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		// } while (choice == JOptionPane.CLOSED_OPTION);
		// if (choice == 0) { // "Keep Changes"
		// if (bankFrame != null)
		// bankFrame.setPatchAt(p, patchRow, patchCol);
		// } else {
		// if (choice == 2) // "Place Changed Version on Clipboard"
		// // put on clipboard but don't 'return' just yet
		// copySelectedPatch();
		// // restore backup
		// p.useSysexFromPatch(originalPatch);
		// }
	}

	/**
	 * Called when the frame is opened. May be redefined in sub-classes.
	 */
	protected void frameOpened() {
	}

	/**
	 * Called when the frame is activated. May be redefined in sub-classes.
	 */
	protected void frameActivated() {
		// If multiple Patch Editors are opened, send patch to edit buffer every
		// time a frame is activated. This is required because multiple frames
		// for one synth may be opened and it is not easy or worth to know for
		// which synth every frame is.
		ErrorMsgUtil.reportStatus("###PE.FrameActivated: nFrame = " + nFrame);
		if (patchByParameter != null && (sendPatchOnActivated || nFrame > 1)) {
			sendPatchOnActivated = false;
			patchByParameter.send();
		}

		// enable/disable menu entries
		Actions.setEnabled(false, Actions.EN_ALL);

		Actions.setEnabled(true, Actions.EN_COPY | Actions.EN_PLAY | Actions.EN_SEND
		// Does send_to and reassgin make sense?
				| Actions.EN_SEND_TO | Actions.EN_REASSIGN);

		// enable paste if the clipboard has contents.
		
		Actions.setEnabled(
				Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this)
						.isDataFlavorSupported(PatchTransferHandler.PATCHES_FLAVOR), Actions.EN_PASTE);
	}

	/**
	 * Called when the frame is deactivated. May be redefined in sub-classes.
	 */
	protected void frameDeactivated() {
		Actions.setEnabled(false, Actions.EN_ALL);
	}

	// PatchBasket methods

	public ArrayList getPatchCollection() {
		return null;
	}

	public void importPatch(File file, JSynthImportFileType type) throws FileNotFoundException {
	}

	public void exportPatch(File file) throws FileNotFoundException {
	}

	public void deleteSelectedPatches() {
	}

	public void copySelectedPatch() {
		ClipboardUtil.storePatch(patchByParameter);
	}

	public Patch getSelectedPatch() {
		return patchByParameter;
	}

	public void sendSelectedPatch() {
		patchByParameter.send();
	}

	public void sendToSelectedPatch() {
		new SysexSendToDialog(patchByParameter);
	}

	public void reassignSelectedPatch() {
		new ReassignPatchDialog(patchByParameter);
	}

	public void playSelectedPatch() {
		patchByParameter.send();
		patchByParameter.play();
	}

	public void storeSelectedPatch() {
	}

	public JSLFrame editSelectedPatch() {
		return null;
	}

	public void pastePatch() {
	}

	public void pastePatch(Patch _p) {
	}

	public void pastePatch(Patch _p, int bankNum, int patchNum) {
	}

	// end of PatchBasket methods

	/**
	 * Add <code>SysexWidget</code> <code>widget</code> to <code>JComponent</code> <code>parent</code> by using
	 * specified GridBagConstraints.
	 * 
	 * @param parent
	 *            a parent <code>JComponent</code> to which <code>widget</code> is added.
	 * @param widget
	 *            a <code>SysexWidget</code> to be added.
	 * @param gridx
	 *            see {@link GridBagConstraints#gridx}.
	 * @param gridy
	 *            see {@link GridBagConstraints#gridy}.
	 * @param gridwidth
	 *            see {@link GridBagConstraints#gridwidth}.
	 * @param gridheight
	 *            see {@link GridBagConstraints#gridheight}.
	 * @param anchor
	 *            see {@link GridBagConstraints#anchor}.
	 * @param fill
	 *            see {@link GridBagConstraints#fill}.
	 * @param slidernum
	 *            a slider number. Only used by ScrollBar Widgets.
	 * @see GridBagConstraints
	 */
	protected void addWidget(JComponent parent, SysexWidget widget, int gridx, int gridy, int gridwidth,
			int gridheight, int anchor, int fill, int slidernum) {
		try {
			gbc.gridx = gridx;
			gbc.gridy = gridy;
			gbc.gridwidth = gridwidth;
			gbc.gridheight = gridheight;
			gbc.anchor = anchor;
			gbc.fill = fill;
			parent.add(widget, gbc);

			widgetList.add(widget);

			widget.setSliderNum(slidernum);
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
	}

	/**
	 * Add <code>SysexWidget</code> <code>widget</code> to <code>JComponent</code> <code>parent</code> by using
	 * specified GridBagConstraints.
	 * <p>
	 * 
	 * <code>NORTHEAST</code> is used for the <code>anchor</code> constraint and <code>HORIZONTAL</code> is used for the
	 * <code>fill</code> constraint.
	 */
	protected void addWidget(JComponent parent, SysexWidget widget, int gridx, int gridy, int gridwidth,
			int gridheight, int slidernum) {
		this.addWidget(parent, widget, gridx, gridy, gridwidth, gridheight, GridBagConstraints.NORTHEAST,
				GridBagConstraints.HORIZONTAL, slidernum);
	}

	/**
	 * Add <code>SysexWidget</code> <code>widget</code> to <code>scrollPane</code> by using specified
	 * GridBagConstraints.
	 * <p>
	 * 
	 * <code>EAST</code> is used for the <code>anchor</code> constraint and <code>BOTH</code> is used for the
	 * <code>fill</code> constraint.
	 */
	protected void addWidget(SysexWidget widget, int gridx, int gridy, int gridwidth, int gridheight, int slidernum) {
		this.addWidget(scrollPane, widget, gridx, gridy, gridwidth, gridheight, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, slidernum);
	}

	// //////////////////////////////////////////////////////////////////////
	// MIDI Fader Input

	private Transmitter trns;
	private Receiver rcvr;

	private void faderInEnable(boolean enable) {
		if (enable) {
			// get transmitter
			trns = MidiUtil.getTransmitter(AppConfig.getFaderPort());
			rcvr = new FaderReceiver();
			trns.setReceiver(rcvr);
		} else {
			if (trns != null)
				trns.close();
			if (rcvr != null)
				rcvr.close();
		}
	}

	protected void finalize() { // ???
		faderInEnable(false);
	}

	private class FaderReceiver implements Receiver {
		// Receiver interface
		public void close() {
		}

		/**
		 * <pre>
		 *    Control Change MIDI Message
		 *      1011nnnn : BnH, nnnn: Voice Channel number
		 *      0ccccccc : control number (0-119)
		 *      0vvvvvvv : control value
		 * </pre>
		 * 
		 * A fader number whose FaderChannel and FaderControl number matches "Voice Channel number" and "control number"
		 * in a Control Change MIDI message is selected. The faderMoved() method is called with the fader number and
		 * "control value".
		 */
		public void send(MidiMessage message, long timeStamp) {
			// ignore unless Editor Window is active.
			if (!isSelected())
				return;
			ShortMessage msg = (ShortMessage) message;
			if (msg.getCommand() == ShortMessage.CONTROL_CHANGE) {
				int channel = msg.getChannel(); // 0 <= channel < 16
				int controller = msg.getData1(); // 0 <= controller <= 119
				ErrorMsgUtil.reportStatus("FaderReceiver: channel: " + channel + ", control: " + controller + ", value: "
						+ msg.getData2());
				// use hash !!!FIXIT!!!
				for (int i = 0; i < JSynthConstants.NUM_FADERS; i++) {
					// faderChannel: 0:channel l, ..., 15:channel 16, 16:off
					// faderController: 0 <= value < 120, 120:off
					if ((AppConfig.getFaderChannel(i) == channel) && (AppConfig.getFaderControl(i) == controller)) {
						faderMoved(i, msg.getData2());
						break;
					}
				}
			}
		}
	}

	/**
	 * @param fader
	 *            fader number
	 * 
	 *            <pre>
	 *  0    : active slider
	 *  1-16 : fader 1-16
	 * 17-30 : button 1-14
	 * 31    : button 15 : prev fader bank
	 * 32    : button 16 : next fader bank
	 * </pre>
	 * @param value
	 *            data value
	 */
	private void faderMoved(int fader, int value) {
		ErrorMsgUtil.reportStatus("FaderMoved: fader: " + fader + ", value: " + value);
		if (fader == 32) { // button 16 : next fader bank
			nextFader();
			return;
		} else if (fader == 31) { // button 15 : previous fader bank
			prevFader();
			return;
		} else if (fader > 16) // 17-30 : button 1-14
			fader = (byte) (0 - (fader - 16) - (faderBank * 16));
		else
			// 0 : active slider, 1-16 : fader 1-16
			fader += (faderBank * 16);

		if (recentWidget != null) {
			SysexWidget w = recentWidget;
			if (fader == faderBank * 16)
				fader = lastFader;
			if (w.getSliderNum() == fader && w.isShowing()) {
				if (w.getNumFaders() == 1)
					w.setValue((int) (w.getValueMin() + ((float) (value) / 127 * (w.getValueMax() - w.getValueMin()))));
				else
					// EnvelopeWidget
					w.setFaderValue(fader, value);
				w.repaint();
				return;
			}
		}
		lastFader = fader;

		for (int i = 0; i < widgetList.size(); i++) {
			SysexWidget w = (SysexWidget) widgetList.get(i);
			if ((w.getSliderNum() == fader || (w.getSliderNum() < fader && w.getSliderNum() + w.getNumFaders() > fader))
					&& w.isShowing()) {
				if (w.getNumFaders() == 1)
					w.setValue((int) (w.getValueMin() + ((float) (value) / 127 * (w.getValueMax() - w.getValueMin()))));
				else
					// EnvelopeWidget
					w.setFaderValue(fader, value);
				w.repaint();
				recentWidget = w;
				return;
			}
		}
	}

	private static Color activeColor;
	private static Color inactiveColor;
	static {
		activeColor = UIManager.getColor("controlText");
		if (activeColor == null)
			activeColor = new Color(75, 75, 100);

		inactiveColor = UIManager.getColor("textInactiveText");
		if (inactiveColor == null)
			inactiveColor = new Color(102, 102, 153);
	}

	/**
	 * > P.S. btw, anyone an idea why some labels are grayed-out?
	 * <p>
	 * 
	 * If I remember correctly, the label color becomes darker if the label is currently assigned to an active fader.
	 * the addWidget calls have the last parameter which defines a fader number. By default the first 16 are active.
	 * Using the "Next Fader bank" Button on the toolbar makes the next 16 active. Its not supposed to look 'greyed out'
	 * though, I just wanted a visual cue about what bank of faders was active. Maybe the color scheme should be changed
	 * to be less confusing.
	 * <p>
	 * 
	 * Brian
	 */
	private void faderHighlight() {
		for (int i = 0; i < widgetList.size(); i++) {
			SysexWidget w = (SysexWidget) widgetList.get(i);
			if (w.getLabel() != null) {
				if (((Math.abs(w.getSliderNum() - 1) & 0xf0)) == faderBank * 16) {
					w.getJLabel().setForeground(activeColor);
				} else {
					w.getJLabel().setForeground(inactiveColor);
				}
				w.getJLabel().repaint();
			}
		}
	}

	public void nextFader() {
		faderBank = (faderBank + 1) % numFaderBanks;
		faderHighlight();
	}

	public void prevFader() {
		faderBank = faderBank - 1;
		if (faderBank < 0)
			faderBank = numFaderBanks - 1;
		faderHighlight();
	}

	/**
	 * When showing the dialog, also check how many components there are to determine the number of widget banks needed.
	 */
	private int getNumFaderBank() {
		int high = 0; // highest slider number
		for (int i = 0; i < widgetList.size(); i++) {
			SysexWidget w = (SysexWidget) widgetList.get(i);
			if ((w.getSliderNum() + w.getNumFaders() - 1) > high)
				high = w.getSliderNum() + w.getNumFaders() - 1;
		}
		return (high / 16) + 1;
	}

	/**
	 * Let bankeditorframe set information about itself when it creates a patch editor frame.
	 */
	public void setBankEditorInformation(BankEditorFrame bf, int row, int col) { // accessed by YamahaFS1RBankEditor
		bankFrame = bf;
		patchRow = row;
		patchCol = col;
	}

	public void revalidateDriver() {
		patchByParameter.findDriver();
		if (patchByParameter.hasNullDriver()) {
			try {
				setClosed(true);
			} catch (PropertyVetoException e) {
				ErrorMsgUtil.reportStatus(e);
			}
			return;
		}
	}

	/**
	 * return the Patch which is edited.
	 */
	public Patch getPatch() {
		return patchByParameter;
	}
	@Override
	public void playAllPatches() {
		throw new NotImplementedException();
	}

	@Override
	public Patch[] getSelectedPatches() {
		
		return null;
	}
}
