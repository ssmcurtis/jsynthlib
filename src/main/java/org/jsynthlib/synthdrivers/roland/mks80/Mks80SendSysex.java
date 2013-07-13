package org.jsynthlib.synthdrivers.roland.mks80;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;
import javax.swing.Timer;

import org.jsynthlib.model.driver.NameValue;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.HexaUtil;
import org.jsynthlib.tools.MidiUtil;

public class Mks80SendSysex {

	private static final SysexHandler WSF = new SysexHandler(Mks80.WSF);
	private static final SysexHandler EOF = new SysexHandler(Mks80.EOF);

	private boolean sendNextSysexPart = true;
	private boolean eof = false;

	private Mks80BankDriver driver;

	private int inPort;

	private int timeout;

	private Timer send;
	private Timer receive;

	private java.util.List<SysexMessage> queue;
	private int queuePointer;

	private NameValue midiChannel;
	private boolean second = false;

	public Mks80SendSysex(PatchDataImpl patch, Mks80BankDriver driver) {

		this.driver = driver;

		this.timeout = 500;

		this.inPort = driver.getDevice().getInPort();
		send = new Timer(0, new SendActionListener());
		receive = new Timer(0, new ReceiveActionListener());

		eof = false;

		queue = new ArrayList<SysexMessage>();
		queuePointer = 0;

		List<byte[]> sysexMsg = HexaUtil.splitSysexMessages(patch.getSysex());

		MidiUtil.clearSysexInputQueue(inPort);

		ErrorMsgUtil.reportStatus(">>>>> InPort " + driver.getDevice().getInPort());
		for (byte[] sysex : sysexMsg) {

			SysexMessage msg = new SysexMessage();
			queue.add(msg);
			// ErrorMsgUtil.reportStatus(HexaUtil.hexDump(sysex, 0, -1, 32));
			// ErrorMsgUtil.reportStatus();

			try {
				msg.setMessage(sysex, sysex.length);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
		}

		// ErrorMsgUtil.reportStatus(">>> Is input available: " + MidiUtil.isInputAvailable());
		// ErrorMsgUtil.reportStatus(">>> msg count: " + sysexMsg.size());

		sendNextSysexPart = false;

		startTimer();

		midiChannel = new NameValue("midiChannel", driver.getChannel() - 1);
		driver.send(WSF.toSysexMessage(0, midiChannel));
		ErrorMsgUtil.reportStatus("WSF");

	}

	public class SendActionListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {

			if (sendNextSysexPart) {
				MidiUtil.clearSysexInputQueue(inPort);

				sendNextSysexPart = false;

				if (queuePointer < queue.size()) {

					driver.send(queue.get(queuePointer));
					// ErrorMsgUtil.reportStatus(HexaUtil.hexDump(queue.get(queuePointer).getData(), 0, -1, 32));
					ErrorMsgUtil.reportStatus("DAT " + (queuePointer+1));

					queuePointer++;
				} else {
					MidiMessage msg = EOF.toSysexMessage(0, midiChannel);
					driver.send(msg);

					ErrorMsgUtil.reportStatus("EOF");
					eof = true;
				}
			}
		}
	}

	public class ReceiveActionListener implements ActionListener {

		public void actionPerformed(ActionEvent evt) {

			try {
				while (!MidiUtil.isSysexInputQueueEmpty(inPort)) {
					SysexMessage msg = (SysexMessage) MidiUtil.getMessage(inPort, timeout);

					sendNextSysexPart = driver.isAcknowledge(msg.getData());

					ErrorMsgUtil.reportStatus("ACK: " + sendNextSysexPart);
					// ErrorMsgUtil.reportStatus(HexaUtil.hexDump(msg.getData(), 0, -1, 32));

					if (eof) {
						ErrorMsgUtil.reportStatus("Stop timer");
						stopTimer();
					}
				}
			} catch (Exception ex) {
				stopTimer();
				ErrorMsgUtil.reportError("Error", "Unable to receive Sysex", ex);
			}

		}
	}

	private void startTimer() {
		if (send != null) {
			send.start();
		}
		if (receive != null) {
			receive.start();
		}
	}

	private void stopTimer() {
		if (send != null) {
			send.stop();
		}
		if (receive != null) {
			receive.stop();
		}
		MidiUtil.clearSysexInputQueue(inPort);
		ErrorMsgUtil.reportStatus(">>> Is input available: " + MidiUtil.isInputAvailable());
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		ErrorMsgUtil.reportStatus(">>> " + getClass().getSimpleName() + " finalize");
		stopTimer();
	}
}
