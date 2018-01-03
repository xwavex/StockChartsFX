package de.dlw.timing.viz.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class ComponentPortData extends TimingData {

	private static final long serialVersionUID = -3213534612701712913L;
	protected String containerName;

	protected ArrayList<PortEventData> portEvents;

	// TODO add average!

	public ComponentPortData() {
		portEvents = new ArrayList<PortEventData>();
	}

	public ComponentPortData(String name, String containerName) {
		this.name = name;
		this.containerName = containerName;
		this.portEvents = new ArrayList<PortEventData>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addPortEvent(PortEventData ped) {
		portEvents.add(ped);
	}

	public void addPortEvents(Collection<PortEventData> peds) {
		portEvents.addAll(peds);
	}

	public double getTimestamp2msecs(boolean average) {
//		if (average) {
//			return meanStartTime * 1e-6;
		//} else
		if (!portEvents.isEmpty()) {
			return portEvents.get(portEvents.size() - 1).getTimestamp2msecs();
		}
		throw new NullPointerException("portEvents is empty!");
	}

	public long getTimestamp(boolean average) {
//		if (average) {
//			return (long) meanStartTime;
		//} else
		if (!portEvents.isEmpty()) {
			return portEvents.get(portEvents.size() - 1).getTimestamp();
		}
		throw new NullPointerException("portEvents is empty!");
	}

	@Override
	public long getTimestamp() {
		return getTimestamp(false);
	}

	@Override
	public double getTimestamp2msecs() {
		return getTimestamp2msecs(false);
	}

	// @Override
	// public String toString() {
	// StringBuilder sb = new StringBuilder();
	// sb.append("Container Name: ").append(containerName);
	// sb.append(" Name: ").append(name);
	// sb.append(" Timestamp: ").append(timestamp);
	// sb.append(" End Timestamp: ").append(endTimestamp);
	// return sb.toString();
	// }

	public ArrayList<PortEventData> getPortEvents() {
		return portEvents;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	// public long getEndTimestamp() {
	// return endTimestamp;
	// }
	//
	// public void setEndTimestamp(long endTimestamp) {
	// this.endTimestamp = endTimestamp;
	// }
	//
	// public double getEndTimestamp2msecs() {
	// return endTimestamp * 1e-6;
	// }

	// @Override
	// public int hashCode() {
	// final int PRIME = 31;
	// int result = 1;
	// long temp = timestamp;
	// result = PRIME * result + (int) (temp ^ (temp >>> 32));
	// for (int i = 0; i < name.length(); i++) {
	// result = PRIME * result + (int) (name.charAt(i) ^ (name.charAt(i) >>>
	// 32));
	// }
	// for (int i = 0; i < containerName.length(); i++) {
	// result = PRIME * result + (int) (containerName.charAt(i) ^
	// (containerName.charAt(i) >>> 32));
	// }
	// result = PRIME * result + (int) (endTimestamp ^ (endTimestamp >>> 32));
	// return result;
	// }

	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj) {
	// return true;
	// }
	// if (obj == null) {
	// return false;
	// }
	// if (getClass() != obj.getClass()) {
	// return false;
	// }
	// final ComponentData other = (ComponentData) obj;
	//
	// if (timestamp != other.timestamp) {
	// return false;
	// }
	// if (!name.equals(other.name)) {
	// return false;
	// }
	// if (!containerName.equals(other.containerName)) {
	// return false;
	// }
	// if (endTimestamp != other.endTimestamp) {
	// return false;
	// }
	// return true;
	// }

}
