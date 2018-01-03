package de.dlw.timing.viz.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class ComponentCallData extends TimingData {

	private static final long serialVersionUID = -2577594626668914851L;
	protected String containerName;

	protected ArrayList<CallEventData> callEvents;

	// TODO meanStartTime, meanEndTime machen noch keinen Sinn!
	private double meanStartTime, meanEndTime, meanDuration = 0.0;
	private double varStartTime, varEndTime, varDuration = 0.0;
	private double stdStartTime, stdEndTime, stdDuration = 0.0;

	// protected ArrayList<PortEventData> portEvents;

	public double getMeanStartTime() {
		return meanStartTime;
	}

	public ArrayList<CallEventData> getCallEventsInRange_MSec(double min, double max) {
		ArrayList<CallEventData> ret = new ArrayList<CallEventData>();
		for (CallEventData c : callEvents) {
			if ((c.getTimestamp2msecs() >= min) && (c.getTimestamp2msecs() <= max)) {
				ret.add(c);
			}
		}
		return ret;
	}

	public ArrayList<CallEventData> getCallEventsInRange_NSec(long min, long max) {
		return getCallEventsInRange_MSec(min * 1e-6, max * 1e-6);
	}

	public void setMeanStartTime(double meanStartTime) {
		this.meanStartTime = meanStartTime;
	}

	public double getMeanEndTime() {
		return meanEndTime;
	}

	public void setMeanEndTime(double meanEndTime) {
		this.meanEndTime = meanEndTime;
	}

	public double getTimestamp2msecs(boolean average) {
		if (average) {
			return meanStartTime * 1e-6;
		} else if (!callEvents.isEmpty()) {
			// return callEvents.get(callEvents.size() -
			// 1).getTimestamp2msecs();
			return callEvents.get(0).getTimestamp2msecs();
		}
		throw new NullPointerException("callEvents is empty!");
	}

	public long getTimestamp(boolean average) {
		if (average) {
			return (long) meanStartTime;
		} else if (!callEvents.isEmpty()) {
			// return callEvents.get(callEvents.size() - 1).getTimestamp();
			return callEvents.get(0).getTimestamp();
		}
		throw new NullPointerException("callEvents is empty!");
	}

	public double getEndTimestamp2msecs(boolean average) {
		if (average) {
			return meanEndTime * 1e-6;
		} else if (!callEvents.isEmpty()) {
			// return callEvents.get(callEvents.size() -
			// 1).getEndTimestamp2msecs();
			return callEvents.get(0).getEndTimestamp2msecs();
		}
		throw new NullPointerException("callEvents is empty!");
	}

	public long getEndTimestamp(boolean average) {
		if (average) {
			return (long) meanEndTime;
		} else if (!callEvents.isEmpty()) {
			// return callEvents.get(callEvents.size() - 1).getEndTimestamp();
			return callEvents.get(0).getEndTimestamp();
		}
		throw new NullPointerException("callEvents is empty!");
	}

	@Override
	public long getTimestamp() {
		return getTimestamp(false);
	}

	@Override
	public double getTimestamp2msecs() {
		return getTimestamp2msecs(false);
	}

	public double getVarStartTime() {
		return varStartTime;
	}

	public void setVarStartTime(double varStartTime) {
		this.varStartTime = varStartTime;
	}

	public double getVarEndTime() {
		return varEndTime;
	}

	public void setVarEndTime(double varEndTime) {
		this.varEndTime = varEndTime;
	}

	public double getVarDuration() {
		return varDuration;
	}

	public void setVarDuration(double varDuration) {
		this.varDuration = varDuration;
	}

	public double getStdStartTime() {
		return stdStartTime;
	}

	public void setStdStartTime(double stdStartTime) {
		this.stdStartTime = stdStartTime;
	}

	public double getStdEndTime() {
		return stdEndTime;
	}

	public void setStdEndTime(double stdEndTime) {
		this.stdEndTime = stdEndTime;
	}

	public double getStdDuration() {
		return stdDuration;
	}

	public void setStdDuration(double stdDuration) {
		this.stdDuration = stdDuration;
	}

	public ComponentCallData() {
		callEvents = new ArrayList<CallEventData>();
		meanStartTime = 0.0;
		meanEndTime = 0.0;
		meanDuration = 0.0;
		varStartTime = 0.0;
		varEndTime = 0.0;
		varDuration = 0.0;
		stdStartTime = 0.0;
		stdEndTime = 0.0;
		stdDuration = 0.0;
	}

	public ComponentCallData(String name, String containerName) {
		this.name = name;
		this.containerName = containerName;
		this.callEvents = new ArrayList<CallEventData>();
		meanStartTime = 0.0;
		meanEndTime = 0.0;
		meanDuration = 0.0;
		varStartTime = 0.0;
		varEndTime = 0.0;
		varDuration = 0.0;
		stdStartTime = 0.0;
		stdEndTime = 0.0;
		stdDuration = 0.0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addCallEvent(CallEventData ced) {
		callEvents.add(ced);
	}

	public void addCallEvents(Collection<CallEventData> ceds) {
		callEvents.addAll(ceds);
	}

	// public void addPortEvent(PortEventData ped) {
	// portEvents.add(ped);
	// }
	//
	// public void addPortEvents(Collection<PortEventData> peds) {
	// portEvents.addAll(peds);
	// }

	// @Override
	// public String toString() {
	// StringBuilder sb = new StringBuilder();
	// sb.append("Container Name: ").append(containerName);
	// sb.append(" Name: ").append(name);
	// sb.append(" Timestamp: ").append(timestamp);
	// sb.append(" End Timestamp: ").append(endTimestamp);
	// return sb.toString();
	// }

	public ArrayList<CallEventData> getCallEvents() {
		return callEvents;
	}

	// public ArrayList<PortEventData> getPortEvents() {
	// return portEvents;
	// }

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public double getMeanDuration() {
		return meanDuration;
	}

	public void setMeanDuration(double meanDuration) {
		this.meanDuration = meanDuration;
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
