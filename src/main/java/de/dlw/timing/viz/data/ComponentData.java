package de.dlw.timing.viz.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class ComponentData implements Serializable {

	private static final long serialVersionUID = 8450889466016270934L;

	protected String name;

	public HashMap<String, ComponentCallData> callData;

	public HashMap<String, ComponentPortData> portData;

	public ComponentSpecification componentSpecs = null;

	public ComponentData() {
		this.callData = new HashMap<String, ComponentCallData>();
		this.portData = new HashMap<String, ComponentPortData>();
	}

	public void setComponentSpecs(ComponentSpecification componentSpecs) {
		this.componentSpecs = componentSpecs;
	}

	public ComponentSpecification getComponentSpecs() {
		return this.componentSpecs;
	}

	public ComponentData(String name) {
		this.name = name;
		this.callData = new HashMap<String, ComponentCallData>();
		this.portData = new HashMap<String, ComponentPortData>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addComponentCallData(ComponentCallData ccd) {
		callData.put(ccd.getName(), ccd);
	}

	public void addComponentPortData(ComponentPortData cpd) {
		portData.put(cpd.getName(), cpd);
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
