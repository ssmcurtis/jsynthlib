package org.jsynthlib.menu.window;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.sound.midi.SysexMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import org.jsynthlib.menu.preferences.AppConfig;
import org.jsynthlib.model.device.Device;
import org.jsynthlib.model.driver.SynthDriver;
import org.jsynthlib.model.driver.SynthDriverBank;
import org.jsynthlib.model.driver.SynthDriverPatch;
import org.jsynthlib.model.patch.Patch;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;
import org.jsynthlib.tools.TableUtil;
import org.jsynthlib.tools.UiUtil;

// import javax.swing.*;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of the location, where a Patch should come from. More
 * than one of each device is supported, but only devices/drivers are selectable, which support the patch.
 * 
 * @author phil@muqus.com - 07/2001
 * @version $Id$
 */
public class SysexGetDialog extends JDialog {

	private int requestPatchAgainDefault = 5;

	// ===== Instance variables
	/** timeout value (in milli second). */
	private int timeout;
	// private int timeout;
	/** number of received data bytes. */
	private int sysexSize = 0;
	/** queue to save Sysex Messages received. */
	private java.util.List<SysexMessage> queue;
	/** MIDI input port from which SysEX messages come. */
	private int inPort;

	private boolean sendPatchAcknowledge = false;
	private boolean useSendPatchAcknowledge = false;
	private boolean isEof = false;
	private boolean sendPatchRequest = true;
	private int patchSize = 1;

	private Timer readSysexTimer;
	private Timer requestSysexTimer;
	private JLabel statusLabel;
	private JComboBox<Device> deviceComboBox;
	private JComboBox<SynthDriver> driverComboBox;
	private JComboBox<String> bankNumComboBox;
	private JComboBox<String> patchNumComboBox;

	private JButton getBankAsSingles = new JButton("Get bank as singles*");
	private JButton get = new JButton("Get");
	private JButton paste = new JButton("Paste current patch");
	private JButton done = new JButton("Done");
	private JButton cancel = new JButton("Cancel");

	private int currentPatchNumber = 0;
	private int bankNumber = 0;
	private int patchCountInBank = 1;

	private SynthDriverPatch driver = null;

	private JTextArea jta = new JTextArea(null, 10, 50);

	public SysexGetDialog(JFrame parent) {
		super(parent, "Get Sysex Data", true);
		// INFO GET SYSEX ACTION

		JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

		statusLabel = new JLabel("Please select a Patch Type to Get.", JLabel.CENTER);
		dialogPanel.add(statusLabel, BorderLayout.NORTH);

		deviceComboBox = new JComboBox<Device>();
		deviceComboBox.addActionListener(new DeviceActionListener());
		driverComboBox = new JComboBox<SynthDriver>();
		driverComboBox.addActionListener(new DriverActionListener());
		bankNumComboBox = new JComboBox<String>();
		patchNumComboBox = new JComboBox<String>();

		for (int i = 1; i < AppConfig.deviceCount(); i++) {
			// skip Generic#0
			Device device = AppConfig.getDevice(i);

			for (int j = 0; j < device.driverCount(); j++) {

				SynthDriver driver = device.getDriver(j);

				if (driver.isSingleDriver() || driver.isBankDriver()) {
					deviceComboBox.addItem(device);
					break;
				}
			}
		}
		deviceComboBox.setEnabled(deviceComboBox.getItemCount() > 1);

		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		labelPanel.add(new JLabel("Device:", JLabel.LEFT));
		labelPanel.add(new JLabel("Driver:", JLabel.LEFT));
		labelPanel.add(new JLabel("Bank:", JLabel.LEFT));
		labelPanel.add(new JLabel("Patch:", JLabel.LEFT));

		JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
		fieldPanel.add(deviceComboBox);
		fieldPanel.add(driverComboBox);
		fieldPanel.add(bankNumComboBox);
		fieldPanel.add(patchNumComboBox);

		JPanel comboPanel = new JPanel(new BorderLayout());
		comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		comboPanel.add(labelPanel, BorderLayout.CENTER);
		comboPanel.add(fieldPanel, BorderLayout.EAST);

		jta.setLineWrap(true);
		jta.setEditable(false);
		jta.setWrapStyleWord(true);
		jta.setCaretPosition(0);

		comboPanel.add(jta, BorderLayout.SOUTH);

		dialogPanel.add(comboPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		getBankAsSingles.addActionListener(new GetBankAsSingleActionListener());
		buttonPanel.add(getBankAsSingles);

		get.addActionListener(new GetActionListener());
		buttonPanel.add(get);

		paste.addActionListener(new PasteActionListener());
		buttonPanel.add(paste);

		done.addActionListener(new DoneActionListener());
		buttonPanel.add(done);

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTimer();
				setVisible(false);
			}
		});

		buttonPanel.add(cancel);
		getRootPane().setDefaultButton(done);
		dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stopTimer();
			}
		});

		getContentPane().add(dialogPanel);
		pack();

		UiUtil.centerDialog(this);
		sysexSize = 0;
	}

	protected void pasteIntoSelectedFrame() {
		// SynthDriverPatch driver = (SynthDriverPatch) driverComboBox.getSelectedItem();

		if (sysexSize > 0 && queue != null && currentPatchNumber < patchCountInBank) {

			ErrorMsgUtil.reportStatus("## DAT");

			// ErrorMsgUtil.reportStatus(">>> Number: " + currentPatchNumber + " " +
			// driver.getPatchNumbers()[currentPatchNumber]);

			SysexMessage[] msgs = (SysexMessage[]) queue.toArray(new SysexMessage[] {});

			// INFO CREATE PATCHES : GET FROM SYNTH
			Patch[] patarray = driver.createPatches(msgs);

			if (patarray.length > 0) {
				// TODO ssmCurtis check banknames
				patarray[0].setComment(driver.getPatchNumbers()[currentPatchNumber]);
			}

			// TODO ssmCurtis - check overwriting comment after import from device
			TableUtil.addPatchToTable(patarray, true);

		}
	}

	public class DoneActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			stopTimer();
			pasteIntoSelectedFrame();
			setVisible(false);
		}
	}

	public class DeviceActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			driverComboBox.removeAllItems();

			Device device = (Device) deviceComboBox.getSelectedItem();
			for (int i = 0; i < device.driverCount(); i++) {
				SynthDriver driver = device.getDriver(i);
				if (driver.isSingleDriver() || driver.isBankDriver()) {
					driverComboBox.addItem(driver);
				}
			}
			driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
			// System.out.println(">>> " + device.getInfoText());
			jta.setText(device.getInfoText());
		}
	}

	public class DriverActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			driver = (SynthDriverPatch) driverComboBox.getSelectedItem();
			if (driver == null) {
				return;
			}

			bankNumComboBox.removeAllItems();
			patchNumComboBox.removeAllItems();

			String bankNumbers[] = driver.getBankNumbers();
			if (bankNumbers != null) {
				for (int i = 0; i < bankNumbers.length; i++) {
					bankNumComboBox.addItem(bankNumbers[i]);
				}
			}
			bankNumComboBox.setEnabled(bankNumComboBox.getItemCount() > 1);

			String patchNumbers[] = driver.getPatchNumbers();
			if (patchNumbers.length > 1) {
				for (int i = 0; i < patchNumbers.length; i++) {
					patchNumComboBox.addItem(patchNumbers[i]);
				}
			}

			getBankAsSingles.setEnabled(driver.isSingleDriver());
			patchNumComboBox.setEnabled(driver.isSingleDriver() && patchNumComboBox.getItemCount() > 1);

		}
	}

	public class PasteActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText(" ");
			stopTimer();
			pasteIntoSelectedFrame();
			sysexSize = 0;
		}
	}

	public class GetActionListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			driver = (SynthDriverPatch) driverComboBox.getSelectedItem();

			bankNumber = bankNumComboBox.getSelectedIndex();
			currentPatchNumber = patchNumComboBox.getSelectedIndex();
			patchCountInBank = driver.getPatchNumbers().length;

			inPort = driver.getDevice().getInPort();

			ErrorMsgUtil.reportStatus("SysexGetDialog | port: " + driver.getDevice().getDeviceName() + " | bankNum: " + bankNumber
					+ " | patchNum: " + currentPatchNumber + "|" + patchCountInBank);

			statusLabel.setText("Getting sysex dump...");

			if (driver instanceof SynthDriverBank) {

				// INFO long time added during driver development for microKorg
				timeout = driver.getPatchSize() * 5;// ((SynthDriverBank) driver).getNumPatches();
			} else {
				timeout = driver.getPatchSize();
			}

			sysexSize = 0;
			queue = new ArrayList<SysexMessage>();

			// reset midi queue
			MidiUtil.clearSysexInputQueue(inPort); // clear MIDI input buffer

			if (driver.isRequestAndAcknowledge()) {
				useSendPatchAcknowledge = true;
				requestSysexTimer = new Timer(0, new SendAcknowledgeActionListener());
				requestSysexTimer.start();
			}

			// wait for results
			readSysexTimer = new Timer(0, new SimpleSysexActionListener());
			readSysexTimer.start();
			// send driver request ... once
			ErrorMsgUtil.reportStatus("## REQ");
			driver.requestPatchDump(bankNumber, currentPatchNumber);
		}
	}

	public class GetBankAsSingleActionListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			getRootPane().setDefaultButton(cancel);

			get.setEnabled(false);
			paste.setEnabled(false);
			done.setEnabled(false);
			getBankAsSingles.setEnabled(false);

			driver = (SynthDriverPatch) driverComboBox.getSelectedItem();

			if (driver instanceof SynthDriverBank) {
				ErrorMsgUtil.reportError("Driver selection", "Only available for single driver");
			} else {
				bankNumber = bankNumComboBox.getSelectedIndex();

				// as start
				currentPatchNumber = patchNumComboBox.getSelectedIndex();

				// TODO ssmCurtis - patch count in bank
				patchCountInBank = driver.getPatchNumbers().length;

				inPort = driver.getDevice().getInPort();

				ErrorMsgUtil.reportStatus("SysexGetDialog | port: " + inPort + " | bankNum: " + bankNumber + " | patchNum: "
						+ currentPatchNumber + "|" + patchCountInBank);

				// TODO ssmCurtis - adjust timeout by user
				timeout = driver.getPatchSize();
				sysexSize = 0;
				patchSize = driver.getPatchSize();
				queue = new ArrayList<SysexMessage>();

				// reset queue
				MidiUtil.clearSysexInputQueue(inPort); // clear MIDI input buffer

				// wait for results
				readSysexTimer = new Timer(timeout, new ReadSysexCurrentReceivedPachActionListener());
				readSysexTimer.start();

				// request sysex
				RequestSysexNextPatchActionListener requestTimer = new RequestSysexNextPatchActionListener();
				requestSysexTimer = new Timer(timeout, requestTimer);
				requestSysexTimer.start();
			}

		}
	}

	public class SendAcknowledgeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {

			if (!isEof && sendPatchAcknowledge) {

				MidiUtil.clearSysexInputQueue(inPort);

				driver.sendAcknowledge();

				sendPatchAcknowledge = false;
				if (queue.size() > 0) {
					SysexMessage lastMessage = queue.get(queue.size() - 1);

					ErrorMsgUtil.reportStatus(HexaUtil.hexDumpOneLine(lastMessage.getData(), 0, -1, 16));

					isEof = driver.isEof(lastMessage.getData());
					if (isEof) {
						queue.remove(lastMessage);
					}
				}
				ErrorMsgUtil.reportStatus("Size: " + queue.size() + " eof: " + isEof + " sendAck: " + sendPatchAcknowledge);
			}
		}
	}

	public class SimpleSysexActionListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {
			// ErrorMsgUtil.reportStatus("_ RECEIVE TIMER SIMPLE.. ");

			try {
				if (!sendPatchAcknowledge && !isEof) {

					while (!MidiUtil.isSysexInputQueueEmpty(inPort)) {
						SysexMessage msg = (SysexMessage) MidiUtil.getMessage(inPort, timeout);

						queue.add(msg);
						// ErrorMsg.reportStatus
						// ("TimerActionListener | size more bytes: " +
						// msg.getLength());
						sysexSize += msg.getLength();
						statusLabel.setText(sysexSize + " Bytes Received");
						if (useSendPatchAcknowledge) {
							sendPatchAcknowledge = true;
						}
					}

				}
			} catch (Exception ex) {
				setVisible(false);
				stopTimer();
				ErrorMsgUtil.reportError("Error", "Unable to receive Sysex", ex);
			}
		}

	}

	public class RequestSysexNextPatchActionListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {

			if (sendPatchRequest) {

				sendPatchRequest = false;

				ErrorMsgUtil.reportStatus("> Try request for " + currentPatchNumber + "/" + patchCountInBank);

				if (currentPatchNumber < patchCountInBank) {
					statusLabel.setText("To go " + (patchCountInBank - currentPatchNumber));

					ErrorMsgUtil.reportStatus("> send request");

					driver.requestPatchDump(bankNumber, currentPatchNumber);

				} else {
					stopTimer();
					setVisible(false);
				}
			}
		}

	}

	public class ReadSysexCurrentReceivedPachActionListener implements ActionListener {

		// TODO ssmCurtis - preferences
		int requestPatchAgain = requestPatchAgainDefault;

		public void actionPerformed(ActionEvent evt) {

			try {
				while (!MidiUtil.isSysexInputQueueEmpty(inPort)) {
					SysexMessage msg;

					msg = (SysexMessage) MidiUtil.getMessage(inPort, timeout);
					queue.add(msg);
					sysexSize += msg.getLength();
				}
			} catch (Exception ex) {
				setVisible(false);
				stopTimer();
				ErrorMsgUtil.reportError("Error", "Unable to receive Sysex", ex);
			}

			if (sysexSize == patchSize) {
				// patch arrived
				ErrorMsgUtil.reportStatus("> patch arrived");

				pasteIntoSelectedFrame();

				sysexSize = 0;
				queue = new ArrayList<SysexMessage>();

				// reset queue
				MidiUtil.clearSysexInputQueue(inPort);
				currentPatchNumber++;
			}
			sendPatchRequest = true;
		}

	}

	private void stopTimer() {
		if (readSysexTimer != null) {
			readSysexTimer.stop();
		}

		if (requestSysexTimer != null) {
			requestSysexTimer.stop();
		}

	}

}