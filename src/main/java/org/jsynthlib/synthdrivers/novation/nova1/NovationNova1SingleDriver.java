/* Made by Yves Lefebvre
   email : ivanohe@abacom.com
   www.abacom.com/~ivanohe

   @version $Id$
 */

package org.jsynthlib.synthdrivers.novation.nova1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jsynthlib.menu.helper.ColumnLayout;
import org.jsynthlib.model.driver.SynthDriverPatchImpl;
import org.jsynthlib.model.driver.SysexHandler;
import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.ErrorMsgUtil;

public class NovationNova1SingleDriver extends SynthDriverPatchImpl {

	public NovationNova1SingleDriver() {
		super("Single", "Yves Lefebvre");
		sysexID = "F000202901210*0009";
		sysexRequestDump = new SysexHandler("F0 00 20 29 01 21 @@ 03 F7");
		patchSize = 296;
		patchNameStart = 9;
		patchNameSize = 16;
		deviceIDoffset = 6;
		checksumStart = 0;
		checksumEnd = 0;
		checksumOffset = 0;
		bankNumbers = new String[] { "Bank A", "Bank B" };
		patchNumbers = new String[] { "00-", "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-", "09-", "10-",
				"11-", "12-", "13-", "14-", "15-", "16-", "17-", "18-", "19-", "20-", "21-", "22-", "23-", "24-",
				"25-", "26-", "27-", "28-", "29-", "30-", "31-", "32-", "33-", "34-", "35-", "36-", "37-", "38-",
				"39-", "40-", "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-", "49-", "50-", "51-", "52-",
				"53-", "54-", "55-", "56-", "57-", "58-", "59-", "60-", "61-", "62-", "63-", "64-", "65-", "66-",
				"67-", "68-", "69-", "70-", "71-", "72-", "73-", "74-", "75-", "76-", "77-", "78-", "79-", "80-",
				"81-", "82-", "83-", "84-", "85-", "86-", "87-", "88-", "89-", "90-", "91-", "92-", "93-", "94-",
				"95-", "96-", "97-", "98-", "99-", "100-", "101-", "102-", "103-", "104-", "105-", "106-", "107-",
				"108-", "109-", "110-", "111-", "112-", "113-", "114-", "115-", "116-", "117-", "118-", "119-", "120-",
				"121-", "122-", "123-", "124-", "125-", "126-", "127" };
	}

	public void storePatch(PatchDataImpl p, int bankNum, int patchNum) {
		byte[] newsysex = new byte[297]; // a dump and write format is one byte longer than an edit buffer dump
		System.arraycopy(((PatchDataImpl) p).getSysex(), 0, newsysex, 0, 7);
		newsysex[7] = (byte) 0x02;
		newsysex[8] = (byte) (0x05 + bankNum);
		newsysex[9] = (byte) (patchNum);
		System.arraycopy(((PatchDataImpl) p).getSysex(), 9, newsysex, 10, 296 - 9); // -10);
		PatchDataImpl patchtowrite = new PatchDataImpl(newsysex, this);
		// need to convert to a "patch dump and write" format
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		sendPatchWorker(patchtowrite);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		setBankNum(bankNum);
		sendProgramChange(patchNum);
	}

	public void sendPatch(PatchDataImpl p) {
		if (NovationNova1PatchSender.bShowMenu == true) {
			NovationNova1PatchSender.deviceIDoffset = deviceIDoffset;
			NovationNova1PatchSender.channel = getChannel();
			NovationNova1PatchSender nps = new NovationNova1PatchSender(null, (PatchDataImpl) p, this);
			nps.setVisible(true);
		} else {
			sendPatchWorker(p);
		}
	}

	protected void calculateChecksum(PatchDataImpl p, int start, int end, int ofs) {
		// no checksum
	}

	public PatchDataImpl createNewPatch() {
		byte[] sysex = new byte[296];
		System.arraycopy(NovationNova1InitPatch.initpatch, 0, sysex, 0, 296);
		sysex[6] = (byte) (getChannel() - 1);
		PatchDataImpl p = new PatchDataImpl(sysex, this);
		calculateChecksum(p);
		return p;
	}

	// public JSLFrame editPatch(Patch p)
	// {
	//
	// }

	public void setBankNum(int bankNum) {
		try {
			send(0xB0 + (getChannel() - 1), 0x20, bankNum + 5);
		} catch (Exception e) {
		}
		;
	}
}

class NovationNova1PatchSender extends JDialog {
	public static boolean bShowMenu = true;
	public PatchDataImpl localPatch;
	public static int deviceIDoffset = 0;
	public static int channel = 0;

	public NovationNova1PatchSender(JFrame Parent, PatchDataImpl p, SynthDriverPatchImpl driver) {
		super(Parent, "Nova1 Patch Sender", true);

		byte[] newsysex = new byte[296];
		System.arraycopy(p.getSysex(), 0, newsysex, 0, 296);
		localPatch = new PatchDataImpl(newsysex, driver);

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		JButton ProgButton = new JButton("Prog");
		ProgButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProgPressed();
			}
		});
		buttonPanel.add(ProgButton);

		JButton Part1Button = new JButton("Part 1");
		Part1Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				SendPatchToPerfBufferPart(1);
			}
		});
		buttonPanel.add(Part1Button);

		JButton Part2Button = new JButton("Part 2");
		Part2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				SendPatchToPerfBufferPart(2);
			}
		});
		buttonPanel.add(Part2Button);

		JButton Part3Button = new JButton("Part 3");
		Part3Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				SendPatchToPerfBufferPart(3);
			}
		});
		buttonPanel.add(Part3Button);

		JButton Part4Button = new JButton("Part 4");
		Part4Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				SendPatchToPerfBufferPart(4);
			}
		});
		buttonPanel.add(Part4Button);

		JButton Part5Button = new JButton("Part 5");
		Part5Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				SendPatchToPerfBufferPart(5);
			}
		});
		buttonPanel.add(Part5Button);

		JButton Part6Button = new JButton("Part 6");
		Part6Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				SendPatchToPerfBufferPart(6);
			}
		});
		buttonPanel.add(Part6Button);

		JButton HidePannel = new JButton("Hide this");
		HidePannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoPannel info = new infoPannel(null);
				info.setVisible(true);
				setVisible(false);
			}
		});
		buttonPanel.add(HidePannel);

		getRootPane().setDefaultButton(ProgButton);

		container.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(container);
		setSize(640, 80);

		centerDialog();
	}

	protected void centerDialog() {
		Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(x, y);
	}

	void ProgPressed() {
		this.setVisible(false);
		if (deviceIDoffset > 0)
			localPatch.getSysex()[deviceIDoffset] = (byte) (channel - 1);
		try {
			localPatch.send();
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
		return;
	}

	void SendPatchToPerfBufferPart(int partnumber) {

		localPatch.getSysex()[8] = (byte) (partnumber - 1);
		if (deviceIDoffset > 0)
			localPatch.getSysex()[deviceIDoffset] = (byte) (channel - 1);
		try {
			localPatch.send();
		} catch (Exception e) {
			ErrorMsgUtil.reportStatus(e);
		}
		return;
	}
}

class infoPannel extends JDialog {
	public infoPannel(JFrame Parent) {
		super(Parent, "Hide Nova1 Patch Sender", true);
		final JLabel l1;
		final JLabel l2;
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		l1 = new JLabel("This will default to send patch to prog buffer only. The Nova");
		l2 = new JLabel("Patch sender panel will only appear next time you start JSynthLib");

		JPanel p4 = new JPanel();
		p4.setLayout(new ColumnLayout());

		p4.add(l1);
		p4.add(l2);

		JButton OkButton = new JButton("OK");
		OkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NovationNova1PatchSender.bShowMenu = false;
				setVisible(false);
			}
		});
		buttonPanel.add(OkButton);

		JButton CancelButton = new JButton("Cancel");
		CancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonPanel.add(CancelButton);
		getRootPane().setDefaultButton(CancelButton);

		container.add(p4, BorderLayout.NORTH);
		container.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(container);
		setSize(400, 180);

		centerDialog();

	}

	protected void centerDialog() {
		Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(x, y);
	}

}
