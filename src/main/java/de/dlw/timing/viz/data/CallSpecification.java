package de.dlw.timing.viz.data;

public class CallSpecification {
	public String name;
	public Long wcet = 0L;

	public CallSpecification(String name, Long wcet) {
		this.name = name;
		this.wcet = wcet;
	}
}
