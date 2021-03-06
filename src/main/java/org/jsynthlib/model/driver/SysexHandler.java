package org.jsynthlib.model.driver;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.tools.ErrorMsgUtil;

/**
 * A class for efficient and convenient creatation of sysex messages. The basic concept is to store a sysex as a byte
 * array, using an index to the special bytes (eg <code>@@</code>, <code>*patchNum*</code>, ..) to insert values.
 * 
 * <OL>
 * <LI>Understands space seperated "Sysex" strings of the form:
 * 
 * <pre>
 * &quot;F0 00 00 1B 0B @@ 14 *patchNum* 00 *bankNum* 00 F7&quot;
 * </pre>
 * 
 * Or dense hex strings of the form:
 * 
 * <pre>
 * &quot;F000001B*B0015**00**0*F7&quot;
 * </pre>
 * 
 * (though this syntax can not handled multiple values such as <code>*patchNum*</code>, <code>*bankNum*</code>)
 * <LI>On conversation to byte array (prior to being sent as a sysex message): <DIR>
 * <LI><code>**</code> - Replace by the value of argument <code>value</code>.
 * <LI><code>@@</code> - Replaced by the value of argument <code>deviceID</code> argument.
 * <LI><code>##</code> - Replaced by the value of argument <code>deviceID</code> argument + 16 - change value by using
 * setAddToBankByte(int).
 * <LI><code>*name*</code> - Replaced by the appropriate value by using a <code>NameValue</code> instance. This is
 * useful if you have to replace multiple values. </DIR>
 * </OL>
 * Example:
 * 
 * <pre>
 * sysexHandler = new SysexHandler(&quot;F0 00 00 1B 0B @@ 14 *patchNum* 00 *bankNum* 00 F7&quot;);
 * send(sysexHandler.toSysexMessage(getDeviceID(), new SysexHander.NameValue(&quot;patchNum&quot;, patchNum), new SysexHander.NameValue(&quot;bankNum&quot;,
 * 		bankNum)));
 * </pre>
 * 
 * @see NameValue
 * @see SynthDriverPatchImpl#sysexRequestDump
 * @see SynthDriverPatch#requestPatchDump
 */
// Modifications by: phil@muqus.com - 07/2001
public class SysexHandler /* implements Serializable */{
	/** Sysex byte array. */
	private byte[] sysex = null;
	/** Vector for "<code>*patchNum*</code>" form value. */
	private Vector<NameValue> vNameValueIndex = new Vector<NameValue>();
	/** Index for "<code>@@</code>". */
	private int deviceIdIndex = -1;
	/** Index for "<code>##</code>". */
	private int deviceId16Index = -1;

	private int addToBankByte = 0x1;

	/**
	 * Creates a new <code>SysexHandler</code> instance.
	 * 
	 * @param src
	 *            a Sysex String
	 * @see NameValue
	 */
	public SysexHandler(String src) {
		setSysex(src);
	}

	/**
	 * Set Sysex String.
	 * 
	 * @see #SysexHandler(String)
	 */
	private void setSysex(String src) {
		// ----- Reset instance variables
		deviceIdIndex = -1;
		deviceId16Index = -1;
		vNameValueIndex.removeAllElements();

		if (src.length() < 3 || src.charAt(2) != ' ') { // src is a dense hex
														// string
			setSysexFromDenseHexStr(src);
			return;
		}

		StringTokenizer st = new StringTokenizer(src);
		sysex = new byte[st.countTokens()];

		for (int iByte = 0; st.hasMoreTokens(); iByte++) {
			String sToken = st.nextToken();

			switch (sToken.charAt(0)) {
			case '*':
				vNameValueIndex.addElement(new NameValue(sToken.substring(1, sToken.length() - 1), iByte));
				sysex[iByte] = (byte) 0;
				break;

			case '@':
				deviceIdIndex = iByte;
				sysex[iByte] = (byte) 0;
				break;

			case '#':
				deviceId16Index = iByte;
				sysex[iByte] = (byte) 0;
				break;

			default:
				try {
					sysex[iByte] = (byte) Integer.parseInt(sToken, 16);
				} catch (Exception ex) {
					ErrorMsgUtil.reportError("SysexHandler", "Invalid number, " + sToken + " in space separated hex string: " + src);
					return;
				}
				break;
			}
		}
	}

	/**
	 * Convert dense format hex string to a byte array.
	 */
	// 1) Dense hex strings of the form:
	// "F000001B0B@@15**00**00F7" (though this syntax can not
	// handled multiple values such as *patchNum*, *bankNum*)
	// 2) The ** values end up with the name ** such that they can be
	// replaced using:
	// Either - sysexHandler.toByteArray(deviceID, new
	// SysexHandler.NameValue("**", patchNum));
	// Or(simpler) - sysexHandler.toByteArray(deviceID, patchNum);
	// 3) private as setSysex initialises channelIndex etc..
	private void setSysexFromDenseHexStr(String src) {
		int nBytes = src.length() / 2;
		sysex = new byte[nBytes];

		int iSrc = 0;
		for (int iByte = 0; iByte < nBytes; iByte++, iSrc += 2) {
			switch (src.charAt(iSrc)) {
			case '*':
				vNameValueIndex.addElement(new NameValue(src.substring(iSrc, iSrc + 2), iByte));
				break;

			case '@':
				deviceIdIndex = iSrc;
				break;

			case '#':
				deviceId16Index = iSrc;
				break;

			default:
				if (src.charAt(iSrc + 1) == '*') { // eg 0*
					vNameValueIndex.addElement(new NameValue(src.substring(iSrc, iSrc + 2), iByte));
				} else {
					try {
						sysex[iByte] = (byte) Integer.parseInt(src.substring(iSrc, iSrc + 2), 16);
					} catch (Exception ex) {
						ErrorMsgUtil.reportError("SysexHandler", "Invalid number, " + src.substring(iSrc, iSrc + 2) + " in dense hex string: "
								+ src);
						return;
					}
				}
				break;
			}
		}
	}

	/**
	 * Return SysexMessage with replacable values set via data passed as arguments.
	 * 
	 * @param deviceID
	 *            Device ID
	 * @param nameValues
	 *            a array of <code>NameValue</code> value
	 * @return a <code>SysexMessage</code> value
	 * @see NameValue
	 */
	public SysexMessage toSysexMessage(int deviceID, NameValue[] nameValues) {
		return toSysexMessage(toByteArray(deviceID, nameValues));
	}

	/**
	 * Return SysexMessage with replacable values set via data passed as arguments.
	 * <p>
	 * <code>toSysexMessage(int deviceID, int value)</code> is a simple form of this method.
	 * 
	 * @param deviceID
	 *            Device ID
	 * @param nameValue
	 *            a <code>NameValue</code> value
	 * @return a <code>SysexMessage</code> value
	 * @see NameValue
	 * @see #toSysexMessage(int deviceID, int value)
	 */
	public SysexMessage toSysexMessage(int deviceID, NameValue nameValue) {
		NameValue[] nameValues = new NameValue[] { nameValue };
		return toSysexMessage(deviceID, nameValues);
	}

	/**
	 * Return SysexMessage with replacable values set via data passed as arguments.
	 * 
	 * @param deviceID
	 *            Device ID
	 * @param nameValue1
	 *            a <code>NameValue</code> value
	 * @param nameValue2
	 *            a <code>NameValue</code> value
	 * @return a <code>SysexMessage</code> value
	 * @see NameValue
	 */
	public SysexMessage toSysexMessage(int deviceID, NameValue nameValue1, NameValue nameValue2) {
		NameValue[] nameValues = new NameValue[] { nameValue1, nameValue2 };
		return toSysexMessage(deviceID, nameValues);
	}

	/**
	 * A simplified methodology to use when there's only one value to be replaced (so the *patchNum* syntax is
	 * wasteful).
	 */
	public SysexMessage toSysexMessage(int deviceID, int value) {
		return toSysexMessage(toByteArray(deviceID, value));
	}

	/**
	 * A simplified methodology to use when no value to be replaced (so the *patchNum* syntax is wasteful).
	 */
	public SysexMessage toSysexMessage(int deviceID) {
		return toSysexMessage(toByteArray(deviceID, 0));
	}

	/** convert byte array into SysexMessage */
	private SysexMessage toSysexMessage(byte[] b) {
		SysexMessage m = new SysexMessage();
		try {
			// ErrorMsgUtil.reportStatus(HexaUtil.hexDumpOneLine(sysex, 0, -1, 20));
			m.setMessage(sysex, sysex.length);
		} catch (InvalidMidiDataException e) {
			ErrorMsgUtil.reportStatus(e);
		}
		return m;
	}

	/**
	 * Return Sysex string as a byte array with replacable values set via data passed as arguments.
	 * 
	 * @param deviceID
	 *            Device ID
	 * @param nameValues
	 *            a array of <code>NameValue</code> value
	 * @return a <code>byte[]</code> value
	 * @see NameValue
	 * @see #toSysexMessage(int deviceID, NameValue[] nameValues)
	 */
	public byte[] toByteArray(int deviceID, NameValue[] nameValues) {
		// Replace the deviceID number
		if (deviceIdIndex != -1)
			sysex[deviceIdIndex] = (byte) (deviceID - 1);
		
		// TODO microkorg
		if (deviceId16Index != -1)
			sysex[deviceId16Index] = (byte) (deviceID - 1 + addToBankByte);

		// Replace values
		for (Enumeration en = vNameValueIndex.elements(); en.hasMoreElements();) {
			NameValue nameValueIndex = (NameValue) en.nextElement();

			boolean bValueFound = false;
			for (int i = 0; i < nameValues.length; i++) {
				if (nameValueIndex.getName().equalsIgnoreCase(nameValues[i].getName())) {
					sysex[nameValueIndex.getValue()] = (byte) nameValues[i].getValue();
					bValueFound = true;
					break;
				}
			}
			if (!bValueFound) {
				ErrorMsgUtil.reportError("SysexHandler", "No value specified for: " + nameValueIndex.getName());
				return sysex;
			}
		}
		return sysex;
	}

	/**
	 * A simplified methodology to use when there's only one value to be replaced (so the *patchNum* syntax is
	 * wasteful).
	 * 
	 * @see #toSysexMessage(int deviceID, int value)
	 */
	public byte[] toByteArray(int deviceID, int value) {
		// Replace the channel number
		if (deviceIdIndex != -1)
			sysex[deviceIdIndex] = (byte) (deviceID - 1);
		if (deviceId16Index != -1)
			sysex[deviceId16Index] = (byte) (deviceID - 1 + addToBankByte);

		// Replace values
		for (Enumeration en = vNameValueIndex.elements(); en.hasMoreElements();)
			sysex[((NameValue) en.nextElement()).getValue()] = (byte) value;

		return sysex;
	}

	/**
	 * Return a byte array where <code>@@</code>,<code>**</code> etc.. have been replaced by 0. This is equivalent with
	 * <code>toByteArray(0, 0)</code>.
	 * 
	 * @see #toSysexMessage(int, int)
	 */
	public byte[] toByteArray() {
		return toByteArray(0, 0);
	}

	public int getNumberToAddForchannel() {
		return addToBankByte;
	}

	// TODO ssmCurtis - evaluate other solutions for this issue
	public void setAddToBankByte(int addToBankByte) {
		this.addToBankByte = addToBankByte;
	}

	public byte[] getSysex() {
		return sysex;
	}

} // End Class: SysexHandler
