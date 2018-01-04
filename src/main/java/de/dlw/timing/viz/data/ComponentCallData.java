package de.dlw.timing.viz.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.chart.XYChart;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class ComponentCallData extends TimingData {

	private static final long serialVersionUID = -2577594626668914851L;
	protected String containerName;

	protected ArrayList<CallEventData> callEvents;
	protected ArrayList<XYChart.Data<Number, String>> activeChartData;

	public ArrayList<XYChart.Data<Number, String>> getActiveChartData() {
		return activeChartData;
	}

	public void addActiveChartData(XYChart.Data<Number, String> viewSample) {
		this.activeChartData.add(viewSample);
	}

	public void setActiveChartData(ArrayList<XYChart.Data<Number, String>> activeChartData) {
		if (activeChartData == null) {
			return;
		}
		this.activeChartData = activeChartData;
	}

	private double meanDuration = 0.0;
	private double varDuration = 0.0;
	private double stdDuration = 0.0;

	// protected ArrayList<PortEventData> portEvents;

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


	public double getTimestamp2msecs(boolean average) {
		return callEvents.get(0).getTimestamp2msecs();
	}

	public long getTimestamp(boolean average) {
		return callEvents.get(0).getTimestamp();
	}

	public double getEndTimestamp2msecs(boolean average) {
		return callEvents.get(0).getEndTimestamp2msecs();
	}

	public long getEndTimestamp(boolean average) {
		return callEvents.get(0).getEndTimestamp();
	}

	@Override
	public long getTimestamp() {
		return getTimestamp(false);
	}

	@Override
	public double getTimestamp2msecs() {
		return getTimestamp2msecs(false);
	}

	public double getVarDuration() {
		return varDuration;
	}

	public void setVarDuration(double varDuration) {
		this.varDuration = varDuration;
	}

	public double getStdDuration() {
		return stdDuration;
	}

	public void setStdDuration(double stdDuration) {
		this.stdDuration = stdDuration;
	}

	public ComponentCallData() {
		activeChartData = new ArrayList<XYChart.Data<Number, String>>();
		callEvents = new ArrayList<CallEventData>();
		meanDuration = 0.0;
		varDuration = 0.0;
		stdDuration = 0.0;
	}

	public ComponentCallData(String name, String containerName) {
		this.name = name;
		this.containerName = containerName;
		activeChartData = new ArrayList<XYChart.Data<Number, String>>();
		this.callEvents = new ArrayList<CallEventData>();
		meanDuration = 0.0;
		varDuration = 0.0;
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
