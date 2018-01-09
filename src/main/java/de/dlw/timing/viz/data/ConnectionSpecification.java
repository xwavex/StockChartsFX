package de.dlw.timing.viz.data;

public class ConnectionSpecification {
	private String name;
	private String sourceComponent;
	private String targetComponent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSourceComponent() {
		return sourceComponent;
	}

	public void setSourceComponent(String sourceComponent) {
		this.sourceComponent = sourceComponent;
	}

	public String getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(String targetComponent) {
		this.targetComponent = targetComponent;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}

	private String sourcePort;
	private String targetPort;

	public ConnectionSpecification(String name, String sourceComponent, String sourcePort, String targetComponent,
			String targetPort) {
		this.name = name;
		this.sourceComponent = sourceComponent;
		this.sourcePort = sourcePort;
		this.targetComponent = targetComponent;
		this.targetPort = targetPort;
	}

	public ConnectionSpecification(String sourceComponent, String sourcePort, String targetComponent,
			String targetPort) {
		this.sourceComponent = sourceComponent;
		this.sourcePort = sourcePort;
		this.targetComponent = targetComponent;
		this.targetPort = targetPort;
	}

}
