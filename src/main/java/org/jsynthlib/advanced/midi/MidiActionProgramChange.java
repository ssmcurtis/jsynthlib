package org.jsynthlib.advanced.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiActionProgramChange implements Runnable, ThreadStop {

	private static String esi = "4-M8U";
	private static String access = "LEXICON";
	private WriteOutput t;

	private boolean stopThread = false;

	private Thread thread;

	public MidiActionProgramChange(WriteOutput t, boolean newThread) {
		this.t = t;
		if (newThread) {
			thread = new Thread(this);
			thread.start();
		} else {
			run();
		}
		System.out.println(" MidiActionReset constructor: Threads " + Thread.activeCount());
	}

	public void run() {

		// MIDI:

		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

		// MIDI:
		// MID - Meldung definieren:
		ShortMessage message = new ShortMessage();
		Info deviceInfo = null;
		Receiver rcvr = null;

		for (int i = 0; i < infos.length; i++) {
			deviceInfo = infos[i];

			if (deviceInfo.getName().contains("exicon")) {
				try {
					MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);
					if (!device.isOpen()) {
						try {
							device.open();
						} catch (MidiUnavailableException mue) {
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

							ShortMessage programChange = new ShortMessage();
							try {
								for (int j = 0; j < 10; j++) {
									t.appendText(deviceInfo.getName() + " channel: " + (channel + 1) + " programm " + j);
									programChange.setMessage(ShortMessage.PROGRAM_CHANGE, channel, j, 0);
									rcvr.send(programChange, -1);
									Thread.sleep(200);
								}
							} catch (InvalidMidiDataException e) {
								e.printStackTrace();
							}

							if (stopThread) {
								t.appendText("Device reset stopped");
								thread = null;
								return;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		t.appendText("Done");
	}

	@Override
	public void sendStopSignal() {
		stopThread = true;
	}

}
