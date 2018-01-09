package de.dlw.timing.viz.data;

import javafx.scene.paint.Color;

public class ActivitySpecification {
	public double period;
	public long priority;
	public long core;
	public String scheduler;
	public Color color = null;

	public ActivitySpecification(double period, long priority, long core, String scheduler) {
		this.period = period;
		this.priority = priority;
		this.core = core;
		this.scheduler = scheduler;
	}
}
