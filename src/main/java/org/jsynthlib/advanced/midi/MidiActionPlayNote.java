package org.jsynthlib.advanced.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.jsynthlib.tools.ErrorMsgUtil;
import org.jsynthlib.tools.MidiUtil;

public class MidiActionPlayNote implements Runnable, ThreadStop {

	private static String esi = "-M8U";
	private static String access = "LEXICON";
	private WriteOutput t;
	private int startPosition = 1;
	private boolean stopThread = false;
	private int outPort = -1;
	private int midiChannel = -1;
	private Thread thread;

	public MidiActionPlayNote(int outport, int midiChannel) {
		this.outPort = outport;
		this.midiChannel = midiChannel - 1;
		run();
	}

	public MidiActionPlayNote(WriteOutput t, int outport, int midiChannel, boolean newThread) {
		this.t = t;
		this.outPort = outport;
		this.midiChannel = midiChannel - 1;

		if (newThread) {
			thread = new Thread(this);
			thread.start();
		} else {
			run();
		}
		ErrorMsgUtil.reportStatus(" MidiPlayNote constructor: Threads " + Thread.activeCount());
	}

	public void run() {

		ShortMessage message = new ShortMessage();
		Info deviceInfo = null;
		Receiver rcvr = null;
		long timeStamp = -1;
		if (outPort != -1) {
			MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
			if (infos.length > outPort) {
				ErrorMsgUtil.reportStatus("Use device " + MidiUtil.getOutputMidiDeviceInfo(outPort).getName()
						+ (midiChannel == -1 ? "" : " channel " + (midiChannel+1)));
			}

			try {
				rcvr = MidiUtil.getReceiver(outPort);
				if (midiChannel != -1) {
					for (MidiNote note : MidiNote.getBasicValues()) {

						message.setMessage(ShortMessage.NOTE_ON, midiChannel, note.getNumber(), 93);
						rcvr.send(message, timeStamp);

						Thread.sleep(500);

						message.setMessage(ShortMessage.NOTE_OFF, midiChannel, note.getNumber(), 93);
						rcvr.send(message, timeStamp);

					}

				} else {

					for (int channel = 0; channel < 16; channel++) {
						ErrorMsgUtil.reportStatus("Channel " + channel);

						for (MidiNote note : MidiNote.getExampleValues()) {
							message.setMessage(ShortMessage.NOTE_ON, channel, note.getNumber(), 93);
							rcvr.send(message, timeStamp);

							Thread.sleep(200);

							message.setMessage(ShortMessage.NOTE_OFF, channel, note.getNumber(), 93);
							rcvr.send(message, timeStamp);

						}
					}
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			} catch (InvalidMidiDataException imde) {
				imde.printStackTrace();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}

		} else {
			MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

			for (int i = 0; i < infos.length; i++) {
				deviceInfo = infos[i];
				if (deviceInfo.getName().substring(startPosition, 5).equals(esi)) {

					try {

						MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);

						if (!device.isOpen()) {
							try {
								device.open();
							} catch (MidiUnavailableException mue) {
								// mue.printStackTrace();
							}
						}

						t.appendText("Device " + deviceInfo.getName() + " is " + (device.isOpen() ? "open" : "closed"));

						if (device.isOpen()) {
							try {
								rcvr = device.getReceiver();
							} catch (MidiUnavailableException mue) {
							}
							if (rcvr == null) {
								continue;
							}

							for (int channel = 0; channel < 16; channel++) {

								for (MidiNote note : MidiNote.values()) {
									t.appendText(deviceInfo.getName() + " channel: " + (channel + 1) + " noteId: " + note.getNumber());
									message.setMessage(ShortMessage.NOTE_ON, channel, note.getNumber(), 93);
									rcvr.send(message, timeStamp);
									// }

									Thread.sleep(100);

									// for (int channel = 0; channel < 16; channel++) {
									message.setMessage(ShortMessage.NOTE_OFF, channel, note.getNumber(), 93);
									rcvr.send(message, timeStamp);

									if (stopThread) {
										t.appendText("Play note done");
										thread = null;
										return;
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			t.appendText("Play note done");
		}

	}

	@Override
	public void sendStopSignal() {
		stopThread = true;
	}

}
