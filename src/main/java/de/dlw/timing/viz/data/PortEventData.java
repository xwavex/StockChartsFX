package de.dlw.timing.viz.data;

import java.util.ArrayList;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class PortEventData extends TimingData {
	public ComponentPortData parentReference = null;

	public ArrayList<PortConnectionData> portConnectionDataRefs;

	public enum CallPortType {
		CALL_UNIVERSAL, CALL_START, CALL_END, CALL_INSTANTANEOUS, CALL_PORT_WRITE, CALL_PORT_READ_NODATA, CALL_PORT_READ_NEWDATA, CALL_PORT_READ_OLDDATA, CALL_START_WITH_DURATION
	};

	private static final long serialVersionUID = -7037072453278571319L;
	protected String containerName;
	protected CallPortType callType;

	public CallPortType getCallType() {
		return callType;
	}

	public void setCallType(CallPortType callType) {
		this.callType = callType;
	}

	public PortEventData() {
		portConnectionDataRefs = new ArrayList<PortConnectionData>();
	}

	public PortEventData(String name, String containerName, long timestamp, CallPortType callType) {
		this.name = name;
		this.timestamp = timestamp;
		this.containerName = containerName;
		this.callType = callType;
		this.portConnectionDataRefs = new ArrayList<PortConnectionData>();
	}

	public String getName() {
		return name;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Container Name: ").append(containerName);
		sb.append(" Name: ").append(name);
		sb.append(" Timestamp: ").append(timestamp);
		sb.append(" Call Type: ").append(callType);
		return sb.toString();
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp = timestamp;
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		for (int i = 0; i < name.length(); i++) {
			result = PRIME * result + (int) (name.charAt(i) ^ (name.charAt(i) >>> 32));
		}
		for (int i = 0; i < containerName.length(); i++) {
			result = PRIME * result + (int) (containerName.charAt(i) ^ (containerName.charAt(i) >>> 32));
		}
		result = PRIME * result + (int) (callType.ordinal() ^ (callType.ordinal() >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PortEventData other = (PortEventData) obj;

		if (timestamp != other.timestamp) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		if (!containerName.equals(other.containerName)) {
			return false;
		}
		if (callType != other.callType) {
			return false;
		}
		return true;
	}

}
