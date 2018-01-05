package de.dlw.timing.viz.data.parser;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.ComponentCallData;
import de.dlw.timing.viz.data.ComponentData;
import de.dlw.timing.viz.data.ComponentPortData;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.TimingData;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class DataProcessor {
	/**
	 * SPECIFICATION TODO
	 */
	private long wcetDummy = 3000000L;

	protected Point2D.Double currentBounds;
	/**
	 * Component model.
	 */
	private HashMap<String, ComponentData> components = new HashMap<String, ComponentData>();

	public void setComponents(HashMap<String, ComponentData> components) {
		this.components = components;
	}

	protected ObservableList<XYChart.Series<Number, String>> dataSeriesReference = null;

	public DataProcessor() {
		components = new HashMap<String, ComponentData>();
		currentBounds = new Point2D.Double(0, 0);
	}

	public HashMap<String, ComponentData> getComponents() {
		return components;
	}

	public void setDataSeriesReference(ObservableList<XYChart.Series<Number, String>> dataSeriesReference) {
		this.dataSeriesReference = dataSeriesReference;
	}

	public void processTimingDataSample(TimingData sample, boolean addToSeries) {
		if (sample instanceof CallEventData) {
			processCallEventData((CallEventData) sample, addToSeries);
		} else if (sample instanceof PortEventData) {
			processPortEventData((PortEventData) sample, addToSeries);
		}
	}

	public void processPortEventData(PortEventData sample, boolean addToSeries) {

		// 1. Update Component Map.
		String portName = sample.getName();
		String componentName = sample.getContainerName();

		ComponentData componentData = null;
		if (components == null) {
			components = new HashMap<String, ComponentData>();
		}

		if (components.containsKey(componentName)) {
			componentData = components.get(componentName);
		} else {
			componentData = new ComponentData(componentName);
		}

		ComponentPortData cpd = null;
		if (!componentData.portData.containsKey(portName)) {
			cpd = new ComponentPortData(portName, componentName);
		} else {
			cpd = componentData.portData.get(portName);
		}

		cpd.addPortEvent(sample);

		// 2. Calculate basic statistics.
		// calculateBasicStatistics(ccd); TODO

		componentData.portData.put(portName, cpd);
		components.put(componentName, componentData);

		if (!addToSeries) {
			return;
		}

		// 4. Add the active view objects for the sample's ComponentPortData.
		ObservableList<XYChart.Data<Number, String>> dataSeries = dataSeriesReference.get(0).getData();

//		if (!triggerFullRecalculation) {
			if (cpd.getActiveChartData().isEmpty()) {
				// calculate and add all view elements.
				for (PortEventData ped : cpd.getPortEventsInRange_MSec(currentBounds.x, currentBounds.y)) {
					ped.parentReference = cpd;
					XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(ped.getTimestamp2msecs(),
							ped.getContainerName(), ped);
					cpd.addActiveChartData(xy);
					dataSeries.add(xy);
				}
			} else {
				// only process the new event and check if it is within bounds.

				if (isWithinTimestampBounds(sample.getTimestamp2msecs(), currentBounds.x, currentBounds.y)) {
					sample.parentReference = cpd;
					XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(sample.getTimestamp2msecs(),
							sample.getContainerName(), sample);
					cpd.addActiveChartData(xy);
					dataSeries.add(xy);
				}
			}
	}

	public void processCallEventData(CallEventData sample, boolean addToSeries) {

		// 1. Update Component Map.
		String functionName = sample.getName();
		String componentName = sample.getContainerName();

		ComponentData componentData = null;
		if (components == null) {
			components = new HashMap<String, ComponentData>();
		}

		if (components.containsKey(componentName)) {
			componentData = components.get(componentName);
		} else {
			componentData = new ComponentData(componentName);
		}

		ComponentCallData ccd = null;
		if (!componentData.callData.containsKey(functionName)) {
			ccd = new ComponentCallData(functionName, componentName);
		} else {
			ccd = componentData.callData.get(functionName);
		}

		ccd.addCallEvent(sample);

		// 2. Calculate basic statistics.
		ccd.updateStatistics(sample);
		calculateBasicStatistics(ccd);
		// 2B. SPECIFICATIONS TODO
		ccd.wcet = wcetDummy;


		componentData.callData.put(functionName, ccd);
		components.put(componentName, componentData);

		if (!addToSeries) {
			return;
		}

		// 3. Find minimal bounds.
		Point2D.Double bounds = findViewRangeOfMinimalSet();

		boolean triggerFullRecalculation = false;
		if (bounds.x != currentBounds.x || bounds.y != currentBounds.y) {
			currentBounds = bounds;
			triggerFullRecalculation = true;
		}

		// 4. Add the active view objects for the sample's ComponentCallData.
		ObservableList<XYChart.Data<Number, String>> dataSeries = dataSeriesReference.get(0).getData();

		if (!triggerFullRecalculation) {
			if (ccd.getActiveChartData().isEmpty()) {
				// calculate and add all view elements.
				for (CallEventData ced : ccd.getCallEventsInRange_MSec(currentBounds.x, currentBounds.y)) {
					ced.parentReference = ccd;
					XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(ced.getTimestamp2msecs(),
							ced.getContainerName(), ced);
					ccd.addActiveChartData(xy);
					dataSeries.add(xy);
				}
			} else {
				// only process the new event and check if it is within bounds.
				if (isWithinTimestampBounds(sample.getTimestamp2msecs(), sample.getEndTimestamp2msecs(), currentBounds.x,
						currentBounds.y)) {
					sample.parentReference = ccd;
					XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(sample.getTimestamp2msecs(),
							sample.getContainerName(), sample);
					ccd.addActiveChartData(xy);
					dataSeries.add(xy);
				}
			}
		} else {
			// FULL update every view sample.
			// loop over all components.
			for (ComponentData cd : this.components.values()) {
				// loop over all functions (e.g., updateHook(), etc.).
				for (Entry<String, ComponentCallData> entry_s_ccd : cd.callData.entrySet()) {
					ComponentCallData tmp = entry_s_ccd.getValue();
					// iterate over all view samples.
					ListIterator<XYChart.Data<Number, String>> iter = tmp.getActiveChartData().listIterator();
					while (iter.hasNext()) {
						XYChart.Data<Number, String> viewSample = iter.next();
						Object extra = viewSample.getExtraValue();
						if (extra != null && extra instanceof CallEventData) {
							CallEventData tmpCed = (CallEventData) extra;
							// remove view sample that is not in range anymore.
							if (!isWithinTimestampBounds(tmpCed.getTimestamp2msecs(), tmpCed.getEndTimestamp2msecs(),
									currentBounds.x, currentBounds.y)) {
								dataSeries.remove(viewSample);
								iter.remove();
								continue;
							}
						}
					}
					// loop over samples in bounds and ...
					for (CallEventData sampleCED : tmp.getCallEventsInRange_MSec(currentBounds.x, currentBounds.y)) {
						// ... check if already represented as view sample.
						boolean addSampleAsView = true;
						for (XYChart.Data<Number, String> sampleViewCED : tmp.getActiveChartData()) {
							if (sampleViewCED.getExtraValue().equals(sampleCED)) {
								// already a view, skipping!
								addSampleAsView = false;
								break;
							}
						}
						if (addSampleAsView) {
							// if not yet contained, add it.
							sampleCED.parentReference = tmp;
							XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(
									sampleCED.getTimestamp2msecs(), sampleCED.getContainerName(), sampleCED);
							tmp.addActiveChartData(xy);
							dataSeries.add(xy);
						}
					}
				}

				// loop over all ports.
				for (Entry<String, ComponentPortData> entry_s_ccd : cd.portData.entrySet()) {
					ComponentPortData tmp = entry_s_ccd.getValue();
					// iterate over all view samples.
					ListIterator<XYChart.Data<Number, String>> iter = tmp.getActiveChartData().listIterator();
					while (iter.hasNext()) {
						XYChart.Data<Number, String> viewSample = iter.next();
						Object extra = viewSample.getExtraValue();
						if (extra != null && extra instanceof PortEventData) {
							PortEventData tmpCed = (PortEventData) extra;
							// remove view sample that is not in range anymore.
							if (!isWithinTimestampBounds(tmpCed.getTimestamp2msecs(), currentBounds.x, currentBounds.y)) {
								dataSeries.remove(viewSample);
								iter.remove();
								continue;
							}
						}
					}
					// loop over samples in bounds and ...
					for (PortEventData samplePED : tmp.getPortEventsInRange_MSec(currentBounds.x, currentBounds.y)) {
						// ... check if already represented as view sample.
						boolean addSampleAsView = true;
						for (XYChart.Data<Number, String> sampleViewPED : tmp.getActiveChartData()) {
							if (sampleViewPED.getExtraValue().equals(samplePED)) {
								// already a view, skipping!
								addSampleAsView = false;
								break;
							}
						}
						if (addSampleAsView) {
							// if not yet contained, add it.
							samplePED.parentReference = tmp;
							XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(
									samplePED.getTimestamp2msecs(), samplePED.getContainerName(), samplePED);
							tmp.addActiveChartData(xy);
							dataSeries.add(xy);
						}
					}
				}
			}
		}
	}

	private boolean isWithinTimestampBounds(double min, double max, double bmin, double bmax) {
		return (min >= bmin && max <= bmax);
	}

	private boolean isWithinTimestampBounds(double t, double bmin, double bmax) {
		return (t >= bmin && t <= bmax);
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

	public void dummyTestAddData() {
		ComponentCallData c = new ComponentCallData("nnn", "dlw");
		CallEventData ccc = new CallEventData("nnn", "dlw", 0L, 4000000L);
		c.addCallEvent(ccc);
		dataSeriesReference.get(0).getData().add(new XYChart.Data<>(0L, "dlw", c));
	}

	// /**
	// * Calculate the set of involved components and update the existing set if
	// * necessary.
	// */
	// public static HashMap<String, ComponentData>
	// calculateSetOfComponents(List<TimingData> data) {
	// if (data == null) {
	// return null;
	// }
	// HashMap<String, ComponentData> comps = new HashMap<String,
	// ComponentData>();
	//
	// for (TimingData timingData : data) {
	// if (timingData instanceof CallEventData) {
	// CallEventData tmp = (CallEventData) timingData;
	// String name = tmp.getName();
	// String containername = tmp.getContainerName();
	//
	// // if component is not yet there, create and add it!
	// ComponentData metaComp = null;
	// if (!comps.containsKey(containername)) {
	// metaComp = new ComponentData(containername);
	// } else {
	// metaComp = comps.get(containername);
	// }
	//
	// // if ComponentCallData is not yet there, create and add it!
	// ComponentCallData ccd = null;
	// if (!metaComp.callData.containsKey(name)) {
	// // add component call data to meta component
	// ccd = new ComponentCallData(name, containername);
	// } else {
	// ccd = metaComp.callData.get(name);
	// }
	//
	// // add call sample
	// ccd.addCallEvent(tmp);
	//
	// metaComp.callData.put(name, ccd);
	// comps.put(containername, metaComp);
	// } else if (timingData instanceof PortEventData) {
	// PortEventData tmp = (PortEventData) timingData;
	// String name = tmp.getName();
	// String containername = tmp.getContainerName();
	//
	// // if component is not yet there, create and add it!
	// ComponentData metaComp = null;
	// if (!comps.containsKey(containername)) {
	// metaComp = new ComponentData(containername);
	// } else {
	// metaComp = comps.get(containername);
	// }
	//
	// // if ComponentPortData is not yet there, create and add it!
	// ComponentPortData cpd = null;
	// if (!metaComp.portData.containsKey(name)) {
	// // add component call data to meta component
	// cpd = new ComponentPortData(name, containername);
	// } else {
	// cpd = metaComp.portData.get(name);
	// }
	//
	// // add call sample
	// cpd.addPortEvent(tmp);
	//
	// metaComp.portData.put(name, cpd);
	// comps.put(containername, metaComp);
	// }
	// }
	//
	// if (comps.size() > 0) {
	// return comps;
	// }
	// return null;
	// }

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

	// public void calculateBasicStatistics() {
	// // For components
	// ArrayList<Long> startTime = new ArrayList<Long>();
	// ArrayList<Long> endTime = new ArrayList<Long>();
	// ArrayList<Long> duration = new ArrayList<Long>();
	//
	// // get data into arrays
	// Iterator<Entry<String, ComponentData>> it =
	// components.entrySet().iterator();
	// while (it.hasNext()) {
	// Map.Entry<String, ComponentData> pair = it.next();
	//
	// for (Entry<String, ComponentCallData> entry :
	// pair.getValue().callData.entrySet()) {
	//
	// for (CallEventData ced : entry.getValue().getCallEvents()) {
	// startTime.add(ced.getTimestamp());
	// endTime.add(ced.getEndTimestamp());
	// duration.add(ced.getEndTimestamp() - ced.getTimestamp());
	// }
	//
	// ComponentCallData tmp = entry.getValue();
	//
	// // get mean, variance and std. dev.
	//// double mean_startTime = mean(startTime);
	//// double var_startTime = variance(mean_startTime, startTime);
	//// double std_startTime = stdDev(var_startTime);
	//
	//// double mean_endTime = mean(endTime);
	//// double var_endTime = variance(mean_endTime, endTime);
	//// double std_endTime = stdDev(var_endTime);
	//
	// double mean_duration = mean(duration);
	// double var_duration = variance(mean_duration, duration);
	// double std_duration = stdDev(var_duration);
	//
	// // tmp.setMeanStartTime(mean_startTime);
	// // tmp.setMeanEndTime(mean_endTime);
	// tmp.setMeanDuration(mean_duration);
	//
	// // tmp.setVarStartTime(var_startTime);
	// // tmp.setVarEndTime(var_endTime);
	// tmp.setVarDuration(var_duration);
	//
	// // tmp.setStdStartTime(std_startTime);
	// // tmp.setStdEndTime(std_endTime);
	// tmp.setStdDuration(std_duration);
	//
	// entry.setValue(tmp);
	// // clear for next iteration
	// startTime.clear();
	// endTime.clear();
	// duration.clear();
	// }
	// }
	// }

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

	/**
	 * Loop over all components and all functions associated to these
	 * components, to find all samples within specific minimal bounds.
	 *
	 * @return x = minTimestamp, y = maxTimestamp
	 */
	public Point2D.Double findViewRangeOfMinimalSet() {
		double minTimestamp = Double.MAX_VALUE;
		double maxTimestamp = 0.0;
		// loop over all components.
		for (ComponentData cd : this.components.values()) {
			// loop over all functions (e.g., updateHook(), etc.).
			for (Entry<String, ComponentCallData> entry_s_ccd : cd.callData.entrySet()) {
				ComponentCallData tmp = entry_s_ccd.getValue();
				// use the first sample to determine the bounds.
				double start = tmp.getCallEvents().get(0).getTimestamp2msecs();
				if (start < minTimestamp) {
					minTimestamp = start;
				}
				double end = tmp.getCallEvents().get(0).getEndTimestamp2msecs();
				if (end > maxTimestamp) {
					maxTimestamp = end;
				}
			}
		}
		return new Point2D.Double(minTimestamp, maxTimestamp);
	}

	private void calculateBasicStatistics(ComponentCallData ccd) {
		ArrayList<Long> duration = new ArrayList<Long>();
		for (CallEventData ced : ccd.getCallEvents()) {
			duration.add(ced.getEndTimestamp() - ced.getTimestamp());
		}
		// TODO do this differently and save the duration array and the old size
		// inside ccd.

		double mean_duration = mean(duration);
		double var_duration = variance(mean_duration, duration);
		double std_duration = stdDev(var_duration);

		ccd.setMeanDuration(mean_duration);
		ccd.setVarDuration(var_duration);
		ccd.setStdDuration(std_duration);
	}

}
