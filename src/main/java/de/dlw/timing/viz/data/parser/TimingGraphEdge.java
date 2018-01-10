package de.dlw.timing.viz.data.parser;

import org.jgrapht.graph.DefaultWeightedEdge;

public class TimingGraphEdge extends DefaultWeightedEdge {

	private static final long serialVersionUID = 8626563608718722997L;

	private String sourcePortName;
	private String targetPortName;

	public String getSourcePortName() {
		return sourcePortName;
	}

	public String getSource() {
		return (String) super.getSource();
	}

	public String getTarget() {
		return (String) super.getTarget();
	}

	public double getWeight() {
		return super.getWeight();
	}

	public void setSourcePortName(String sourcePortName) {
		this.sourcePortName = sourcePortName;
	}

	public String getTargetPortName() {
		return targetPortName;
	}

	public void setTargetPortName(String targetPortName) {
		this.targetPortName = targetPortName;
	}

	public TimingGraphEdge(String sourcePortName, String targetPortName) {
		super();
		this.sourcePortName = sourcePortName;
		this.targetPortName = targetPortName;
	}

}
