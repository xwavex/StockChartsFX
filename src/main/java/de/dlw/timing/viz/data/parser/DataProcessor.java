package de.dlw.timing.viz.data.parser;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import de.dlw.timing.viz.data.ActivitySpecification;
import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.ComponentCallData;
import de.dlw.timing.viz.data.ComponentData;
import de.dlw.timing.viz.data.ComponentPortData;
import de.dlw.timing.viz.data.PortConnectionData;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.TimingData;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class DataProcessor {
	/**
	 * SPECIFICATION TODO
	 */
	private long wcetDummy = 100000L;

	public Graph<String, TimingGraphEdge> graph = null;

	public ArrayList<ActivitySpecification> activities;

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
		activities = new ArrayList<ActivitySpecification>();
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
			cpd = new ComponentPortData(portName, componentName,
					ComponentPortData.testForInputPort(sample.getCallType().toString()));
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

		// if (!triggerFullRecalculation) {
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
			ccd = new ComponentCallData(functionName, componentName, componentData);
		} else {
			ccd = componentData.callData.get(functionName);
		}

		ccd.addCallEvent(sample);

		componentData.callData.put(functionName, ccd);
		components.put(componentName, componentData);

		if (!addToSeries) {
			return;
		}

		// 2. Calculate basic statistics.
		ccd.updateStatistics(sample);
		calculateBasicStatistics(ccd);

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
				if (isWithinTimestampBounds(sample.getTimestamp2msecs(), sample.getEndTimestamp2msecs(),
						currentBounds.x, currentBounds.y)) {
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
							if (!isWithinTimestampBounds(tmpCed.getTimestamp2msecs(), currentBounds.x,
									currentBounds.y)) {
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

	public void triggerRecalculation() {
		HashMap<ComponentPortData, ArrayList<PortEventData>> outputPortDataInRange = new HashMap<ComponentPortData, ArrayList<PortEventData>>();
		HashMap<ComponentPortData, ArrayList<PortEventData>> inputPortDataInRange = new HashMap<ComponentPortData, ArrayList<PortEventData>>();
		// 3. Find minimal bounds.
		currentBounds = findViewRangeOfMinimalSet();
		System.out.println("Bounds " + currentBounds.x + " <=> " + currentBounds.y);
		// 4. Add the active view objects for the sample's ComponentCallData.
		ObservableList<XYChart.Data<Number, String>> dataSeries = dataSeriesReference.get(0).getData();
		// 3. Find minimal bounds.
		// Point2D.Double bounds = findViewRangeOfMinimalSet();
		// FULL update every view sample.
		// loop over all components.
		int iComp = 0;
		for (ComponentData cd : this.components.values()) {

			// loop over all functions (e.g., updateHook(), etc.).
			for (Entry<String, ComponentCallData> entry_s_ccd : cd.callData.entrySet()) {
				ComponentCallData tmp = entry_s_ccd.getValue();

				// if (!tmp.getContainerName().equals(cd.getName())) {
				// System.err.println("In " + cd.getName() + " found
				// ComponentCallData with name " + tmp.getContainerName());
				// }

				System.out.println("Processing Hook: " + tmp.getName());

				// SORTING CallEventData
				System.out.println("Sorting callEvents");
				Collections.sort(tmp.callEvents, new Comparator<CallEventData>() {
					@Override
					public int compare(CallEventData o1, CallEventData o2) {
						return Long.compare(o1.getTimestamp(), o2.getTimestamp());
					}
				});
				System.out.println("Finished callEvents");

				calculateBasicStatistics(tmp);

				System.out.println("WMET in nsec: " + tmp.getWorstMeasuredExecutionDuration());
				System.out.println("WMET in msec: " + tmp.getWorstMeasuredExecutionDuration2msecs());

				tmp.getActiveChartData().clear();

				ArrayList<CallEventData> aaa = tmp.getCallEventsInRange_MSec(currentBounds.x, currentBounds.y);
				System.out.println("in bounds size " + aaa.size());
				// loop over samples in bounds and ...
				for (CallEventData sampleCED : aaa) {

					// if (!sampleCED.getContainerName().equals(cd.getName())) {
					// System.err.println("In " + cd.getName() + " found
					// CallEventData with name " +
					// sampleCED.getContainerName());
					// }

					sampleCED.parentReference = tmp;
					XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(sampleCED.getTimestamp2msecs(),
							sampleCED.getContainerName(), sampleCED);
					tmp.addActiveChartData(xy);
					dataSeries.add(xy);
				}
			}

			// loop over all ports.
			for (Entry<String, ComponentPortData> entry_s_ccd : cd.portData.entrySet()) {
				ComponentPortData tmp = entry_s_ccd.getValue();

				// if (!tmp.getContainerName().equals(cd.getName())) {
				// System.err.println("In " + cd.getName() + " found
				// ComponentPortData with name " + tmp.getContainerName());
				// }

				// SORTING PortEventData
//				System.out.println("Sorting portEvents");
				Collections.sort(tmp.portEvents, new Comparator<PortEventData>() {
					@Override
					public int compare(PortEventData o1, PortEventData o2) {
						return Long.compare(o1.getTimestamp(), o2.getTimestamp());
					}
				});
//				System.out.println("Finished portEvents");

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
				ArrayList<PortEventData> tmpPortData = tmp.getPortEventsInRange_MSec(currentBounds.x, currentBounds.y);
				if (!tmp.isInputType()) {
					outputPortDataInRange.put(tmp, tmpPortData);
				} else {
					inputPortDataInRange.put(tmp, tmpPortData);
				}

				for (PortEventData samplePED : tmpPortData) {
					// if (!samplePED.getContainerName().equals(cd.getName())) {
					// System.err.println("In0 " + cd.getName() + " found
					// PortEventData with name "
					// + samplePED.getContainerName());
					// }
					// if
					// (!samplePED.getContainerName().equals(tmp.getContainerName()))
					// {
					// System.err.println("In1 " + tmp.getContainerName() + "
					// found PortEventData with name "
					// + samplePED.getContainerName());
					// }
					// if (!samplePED.getName().equals(tmp.getName())) {
					// System.err.println("In2 " + tmp.getName() + " found
					// PortEventData with name "
					// + samplePED.getContainerName());
					// }

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
			iComp++;
			System.out.println("Finished Processing Component " + iComp + " of " + this.components.size());
		}

//		for (Entry<ComponentPortData, ArrayList<PortEventData>> inRagePortEntry : inputPortDataInRange.entrySet()) {
//			ComponentPortData tmp = inRagePortEntry.getKey();
//			System.out.println("##### CHECK " + tmp.getContainerName() + "." + tmp.getName() + " | size = "
//					+ inRagePortEntry.getValue().size());
//			for (PortEventData ped : inRagePortEntry.getValue()) {
//				if (ped.getName().equals(tmp.getName())) {
//					System.out.println("### CHECK " + ped.getContainerName() + "." + ped.getName());
//				}
//				if (ped.getContainerName().equals(tmp.getContainerName())) {
//					System.out.println("### CHECK " + ped.getContainerName() + "." + ped.getName());
//				}
//			}
//		}
//		System.exit(1);

		for (Entry<ComponentPortData, ArrayList<PortEventData>> inRagePortEntry : outputPortDataInRange.entrySet()) {
			// check for all output ports
			ComponentPortData tmp = inRagePortEntry.getKey();
			if (!tmp.isInputType()) {
				// find connected input port
				String outputCompName = tmp.getContainerName();
				String outputPortName = tmp.getName();

				Iterator<TimingGraphEdge> tgeIt = graph.outgoingEdgesOf(outputCompName).stream()
						.filter(e -> e.getSourcePortName().equals(outputPortName)).iterator();
				while (tgeIt.hasNext()) {
					TimingGraphEdge edge = tgeIt.next();

					String inputCompName = edge.getTarget();
					String inputPortName = edge.getTargetPortName();

					ComponentPortData candidateInputComp = inputPortDataInRange.keySet().stream()
							.filter(c -> c.getContainerName().equals(inputCompName) && c.getName().equals(inputPortName)).findFirst().orElse(null);

					if (candidateInputComp == null) {
						// System.err.println("Candidate for " +
						// inputCompName + " is null!");
					} else {
						// System.out.println("CHECK " + outputCompName + "." +
						// outputPortName + " -> " + inputCompName
						// + "." + inputPortName + " : " +
						// candidateInputComp.getContainerName() + "."
						// + candidateInputComp.getName());

						for (PortEventData ped : inRagePortEntry.getValue()) {
							long timestamp = ped.getTimestamp();
							// find next time stamp
							// System.out.println("FIND ports for " +
							// candidateInputComp.getContainerName() +
							// "." + candidateInputComp.getName());
							for (PortEventData inputEventData : inputPortDataInRange.get(candidateInputComp)) {
								if (inputEventData.getTimestamp() > timestamp) {
									// found input (real target)
									// port event
//									System.out.println("EVENT WTIH " + ped.getContainerName() + " -> "
//											+ inputEventData.getContainerName());
									PortConnectionData pcd = new PortConnectionData(ped, inputEventData);
									XYChart.Data<Number, String> xy = new XYChart.Data<Number, String>(
											ped.getTimestamp2msecs(), outputCompName, pcd);
									// tmp.addActiveChartData(xy);
									// // TODO perhaps in the
									// future!
									dataSeries.add(xy);
									break;
								}
							}
						}

					}

				}
			}
		}

		System.out.println("Finished Processing Port Connections of " + iComp + " of " + this.components.size());
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
				if (entry_s_ccd.getKey().equals("stopHook()")) {
					continue;
				}
				ComponentCallData tmp = entry_s_ccd.getValue();
				// System.out.println("For " + cd.getName() + " check hook " +
				// tmp.getName());
				// use the first sample to determine the bounds.
				double start = tmp.getCallEvents().get(0).getTimestamp2msecs();
				if (start < minTimestamp) {
					minTimestamp = start;
				}
				double end = tmp.getCallEvents().get(0).getEndTimestamp2msecs();
				if (end > maxTimestamp) {
					maxTimestamp = end;
				}
				// System.out.println("Consider " +
				// tmp.getCallEvents().get(0).getTimestamp() + " && " +
				// tmp.getCallEvents().get(0).getEndTimestamp());
			}
		}
		return new Point2D.Double(minTimestamp, maxTimestamp);
	}

	private void calculateBasicStatistics(ComponentCallData ccd) {
		ArrayList<Long> duration = new ArrayList<Long>();
		long wmect = 0L;

		long debug_start = 0L;

		for (CallEventData ced : ccd.getCallEvents()) {
			long dur = ced.getEndTimestamp() - ced.getTimestamp();
			duration.add(dur);
		}

		// TODO do this differently and save the duration array and the old size
		// inside ccd.

		double mean_duration = mean(duration);
		double var_duration = variance(mean_duration, duration);
		double std_duration = stdDev(var_duration);

		ccd.setMeanDuration(mean_duration);
		ccd.setVarDuration(var_duration);
		ccd.setStdDuration(std_duration);

		for (CallEventData ced : ccd.getCallEvents()) {
			long dur = ced.getEndTimestamp() - ced.getTimestamp();
			if (dur > wmect && dur <= (mean_duration * 2)) { // TODO HACK!
				wmect = dur;
				debug_start = ced.getTimestamp();
			}
		}

		ccd.setWorstMeasuredExecutionDuration(wmect);

		System.out.println("wmect         = " + wmect);
		System.out.println("debug_start n = " + debug_start);
		System.out.println("debug_start m = " + debug_start * 1e-6);
		System.out.println("mean_duration = " + mean_duration);
		System.out.println("var_duration  = " + var_duration);
		System.out.println("std_duration  = " + std_duration);
	}

	public static String hsvToRgb(float hue, float saturation, float value) {

		int h = (int) (hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0:
			return rgbToString(value, t, p);
		case 1:
			return rgbToString(q, value, p);
		case 2:
			return rgbToString(p, value, t);
		case 3:
			return rgbToString(p, q, value);
		case 4:
			return rgbToString(t, p, value);
		case 5:
			return rgbToString(value, p, q);
		default:
			throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", "
					+ saturation + ", " + value);
		}
	}

	public static String rgbToString(float r, float g, float b) {
		String rs = Integer.toHexString((int) (r * 256));
		String gs = Integer.toHexString((int) (g * 256));
		String bs = Integer.toHexString((int) (b * 256));
		return rs + gs + bs;
	}

}
