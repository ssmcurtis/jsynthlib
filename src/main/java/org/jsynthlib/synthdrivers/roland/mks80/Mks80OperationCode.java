package org.jsynthlib.synthdrivers.roland.mks80;

public enum Mks80OperationCode {
	IPR((byte) 0x36),
	PGR((byte) 0x34),
	APR((byte) 0x35),
	WSF((byte) 0x40),
	RQF((byte) 0x41),
	DAT((byte) 0x42),
	ACK((byte) 0x43),
	EOF((byte) 0x45),
	ERR((byte) 0x4E),
	RJC((byte) 0x4F); 
	
	private final byte operationCode;

	Mks80OperationCode(byte operationCode) {
		this.operationCode = operationCode;
	}

	public static Mks80OperationCode getOpCode(byte[] data) {
		if (data.length < 2) {
			return null;
		}

		byte oc = data[1];

		for (Mks80OperationCode ct : Mks80OperationCode.values()) {
			if (ct.getOperationCode() == oc) {
				return ct;
			}
		}
		return null;
	}

	public byte getOperationCode() {
		return operationCode;
	}

}
