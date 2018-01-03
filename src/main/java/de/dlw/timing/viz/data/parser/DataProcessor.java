package de.dlw.timing.viz.data.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.ComponentCallData;
import de.dlw.timing.viz.data.ComponentData;
import de.dlw.timing.viz.data.ComponentPortData;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.TimingData;

public class DataProcessor {
	private List<TimingData> data = new ArrayList<TimingData>();
	private HashMap<String, ComponentData> components = new HashMap<String, ComponentData>();

	public DataProcessor() {
		components = new HashMap<String, ComponentData>();
	}

	public HashMap<String, ComponentData> getComponents() {
		return components;
	}

	public boolean loadDataBatch(List<TimingData> data) {
		if (data != null) {
			this.data = data;
			return true;
		} else {
			return false;
		}
	}

	public List<TimingData> getData() {
		return data;
	}

	public void clearData() {
		// or this.data = null?
		this.data = new ArrayList<TimingData>();
	}

	/**
	 * Shift the timestamps to get a reasonable starting point. Using the
	 * minimal(/first) timestamp as base.
	 *
	 * @param minimalTimestamp
	 */
	public void shiftTimestamps(long minimalTimestamp) {
		shiftTimestamps(minimalTimestamp, this.data);
	}

	public static void shiftTimestamps(long minimalTimestamp, List<TimingData> data) {
		for (TimingData timingData : data) {
			if (timingData instanceof CallEventData) {
				CallEventData tmp = (CallEventData) timingData;
				tmp.setTimestamp(tmp.getTimestamp() - minimalTimestamp);
				tmp.setEndTimestamp(tmp.getEndTimestamp() - minimalTimestamp);
			} else if (timingData instanceof PortEventData) {
				PortEventData tmp = (PortEventData) timingData;
				tmp.setTimestamp(tmp.getTimestamp() - minimalTimestamp);
			} else {
				timingData.setTimestamp(timingData.getTimestamp() - minimalTimestamp);
			}
		}
	}

	/**
	 * Calculate the set of involved components and update the existing set if
	 * necessary.
	 */
	public boolean calculateSetOfComponents() {
		if (this.data == null) {
			return false;
		}
		HashMap<String, ComponentData> comps = new HashMap<String, ComponentData>();

		for (TimingData timingData : data) {
			if (timingData instanceof CallEventData) {
				CallEventData tmp = (CallEventData) timingData;
				String name = tmp.getName();
				String containername = tmp.getContainerName();

				// if component is not yet there, create and add it!
				ComponentData metaComp = null;
				if (!comps.containsKey(containername)) {
					metaComp = new ComponentData(containername);
				} else {
					metaComp = comps.get(containername);
				}

				// if ComponentCallData is not yet there, create and add it!
				ComponentCallData ccd = null;
				if (!metaComp.callData.containsKey(name)) {
					// add component call data to meta component
					ccd = new ComponentCallData(name, containername);
				} else {
					ccd = metaComp.callData.get(name);
				}

				// add call sample
				ccd.addCallEvent(tmp);

				metaComp.callData.put(name, ccd);
				comps.put(containername, metaComp);
			} else if (timingData instanceof PortEventData) {
				PortEventData tmp = (PortEventData) timingData;
				String name = tmp.getName();
				String containername = tmp.getContainerName();

				// if component is not yet there, create and add it!
				ComponentData metaComp = null;
				if (!comps.containsKey(containername)) {
					metaComp = new ComponentData(containername);
				} else {
					metaComp = comps.get(containername);
				}

				// if ComponentPortData is not yet there, create and add it!
				ComponentPortData cpd = null;
				if (!metaComp.portData.containsKey(name)) {
					// add component call data to meta component
					cpd = new ComponentPortData(name, containername);
				} else {
					cpd = metaComp.portData.get(name);
				}

				// add call sample
				cpd.addPortEvent(tmp);

				metaComp.portData.put(name, cpd);
				comps.put(containername, metaComp);
			}
		}

		if (comps.size() > 0) {
			components = comps;
			return true;
		}
		return false;
	}

	/**
	 * Update iteratively to not do everything every time again over all the
	 * huge load of data!
	 *
	 * @param data
	 */
	public void updateWithNewDataTODO(List<TimingData> data) {

	}

	public void printReport() {
		System.out.println("--- Report ---");
		Iterator<Entry<String, ComponentData>> it = components.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ComponentData> pair = it.next();
			System.out.println("# Component " + pair.getValue().getName());
			System.out.println("### calls " + pair.getValue().callData.keySet());
			for (Entry<String, ComponentCallData> entry : pair.getValue().callData.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue().getCallEvents().size());
			}
			System.out.println("### ports " + pair.getValue().portData.keySet());
			for (Entry<String, ComponentPortData> entry : pair.getValue().portData.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue().getPortEvents().size());
			}
			System.out.println("--- end ---");
			System.out.println();
			// it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public void calculateBasicStatistics() {
		// For components
		ArrayList<Long> startTime = new ArrayList<Long>();
		ArrayList<Long> endTime = new ArrayList<Long>();
		ArrayList<Long> duration = new ArrayList<Long>();

		// get data into arrays
		Iterator<Entry<String, ComponentData>> it = components.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ComponentData> pair = it.next();

			for (Entry<String, ComponentCallData> entry : pair.getValue().callData.entrySet()) {

				for (CallEventData ced : entry.getValue().getCallEvents()) {
					startTime.add(ced.getTimestamp());
					endTime.add(ced.getEndTimestamp());
					duration.add(ced.getEndTimestamp() - ced.getTimestamp());
				}

				ComponentCallData tmp = entry.getValue();

				// get mean, variance and std. dev.
				double mean_startTime = mean(startTime);
				double var_startTime = variance(mean_startTime, startTime);
				double std_startTime = stdDev(var_startTime);

				double mean_endTime = mean(endTime);
				double var_endTime = variance(mean_endTime, endTime);
				double std_endTime = stdDev(var_endTime);

				double mean_duration = mean(duration);
				double var_duration = variance(mean_duration, duration);
				double std_duration = stdDev(var_duration);

				tmp.setMeanStartTime(mean_startTime);
				tmp.setMeanEndTime(mean_endTime);
				tmp.setMeanDuration(mean_duration);

				tmp.setVarStartTime(var_startTime);
				tmp.setVarEndTime(var_endTime);
				tmp.setVarDuration(var_duration);

				tmp.setStdStartTime(std_startTime);
				tmp.setStdEndTime(std_endTime);
				tmp.setStdDuration(std_duration);

				entry.setValue(tmp);
				// clear for next iteration
				startTime.clear();
				endTime.clear();
				duration.clear();
			}
		}
	}

	private static double mean(ArrayList<Long> in) {
		long sum_startTime = 0L;
		for (Long s : in) {
			sum_startTime += s;
		}
		return sum_startTime / in.size();
	}

	private static double variance(double mean, ArrayList<Long> in) {
		double temp = 0;
		for (Long a : in) {
			temp += (a - mean) * (a - mean);
		}
		return temp / (in.size() - 1);
	}

	private static double stdDev(double variance) {
		return Math.sqrt(variance);
	}

}
