package de.dlw.timing.viz.data;

import java.util.ArrayList;
import java.util.HashMap;

public class ComponentSpecification {
	public HashMap<String, PortSpecification> ports;
	public HashMap<String, CallSpecification> calls;
	public ActivitySpecification activity = null;

	public ComponentSpecification(String name, String className) {
		ports = new HashMap<String, PortSpecification>();
		calls = new HashMap<String, CallSpecification>();
	}

	public ComponentSpecification(String name, String className, ActivitySpecification activity) {
		ports = new HashMap<String, PortSpecification>();
		calls = new HashMap<String, CallSpecification>();
		this.activity = activity;
	}

	public ComponentSpecification(String name, String className, double period, int priority, int core,
			String scheduler) {
		ports = new HashMap<String, PortSpecification>();
		calls = new HashMap<String, CallSpecification>();
		this.activity = new ActivitySpecification(period, priority, core, scheduler);
	}

	public ActivitySpecification createActivity(double period, int priority, int core, String scheduler) {
		return new ActivitySpecification(period, priority, core, scheduler);
	}

	public void overrideActivity(double period, int priority, int core, String scheduler) {
		if (activity != null) {
			activity.period = period;
			activity.priority = priority;
			activity.core = core;
			activity.scheduler = scheduler;
		} else {
			activity = new ActivitySpecification(period, priority, core, scheduler);
		}
	}

	public void setActivity(ActivitySpecification act) {
		if (act != null) {
			activity = act;
		}
	}

	public void addPort(String name, String type, String data) {
		ports.put(name, new PortSpecification(name, type, data));
	}

	public PortSpecification createPort(String name, String type, String data) {
		return new PortSpecification(name, type, data);
	}

	public void removePort(PortSpecification p) {
		ports.remove(p);
	}

	public void addCall(String name, Long wcet) {
		calls.put(name, new CallSpecification(name, wcet));
	}

	public CallSpecification createCall(String name, Long wcet) {
		return new CallSpecification(name, wcet);
	}

	public void removeCall(CallSpecification c) {
		calls.remove(c);
	}
}
