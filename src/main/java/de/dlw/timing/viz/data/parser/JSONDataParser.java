package de.dlw.timing.viz.data.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.dlw.timing.viz.data.ActivitySpecification;
import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.ComponentData;
import de.dlw.timing.viz.data.ComponentSpecification;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.TimingData;
import javafx.scene.paint.Color;
import de.dlw.timing.viz.data.PortEventData.CallPortType;

public class JSONDataParser {
	public long minimalTimestamp = Long.MAX_VALUE;

	public JSONDataParser() {

	}

	public List<TimingData> parse(String jsonFile) throws FileNotFoundException, IOException, ParseException {
		List<TimingData> data = new ArrayList<>();
		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(jsonFile));

		JSONObject jsonObject = (JSONObject) obj;

		JSONArray entries = (JSONArray) jsonObject.get("root");
		Iterator<JSONObject> iterator = entries.iterator();

		while (iterator.hasNext()) {
			JSONObject dataObj = (JSONObject) iterator.next();

			String call_type = (String) dataObj.get("call_type");
			String call_name = (String) dataObj.get("call_name");
			String container_name = (String) dataObj.get("container_name");
			Long call_time = (Long) dataObj.get("call_time");

			if (call_time == null) {
				System.err.println("Could not parse " + dataObj + " (call_time is empty)");
				continue;
			}

			if (call_type != null && !call_type.isEmpty() && call_name != null && !call_name.isEmpty()
					&& container_name != null && !container_name.isEmpty()) {
				if (call_type.contains("PORT")) {
					if (call_time > 0 && call_time < minimalTimestamp) {
						minimalTimestamp = call_time;
					}
					switch (call_type) {
					case "CALL_PORT_READ_NODATA":
						data.add(new PortEventData(call_name, container_name, call_time,
								CallPortType.CALL_PORT_READ_NODATA));
						break;
					case "CALL_PORT_READ_OLDDATA":
						data.add(new PortEventData(call_name, container_name, call_time,
								CallPortType.CALL_PORT_READ_OLDDATA));
						break;
					case "CALL_PORT_READ_NEWDATA":
						data.add(new PortEventData(call_name, container_name, call_time,
								CallPortType.CALL_PORT_READ_NEWDATA));
						break;
					case "CALL_PORT_WRITE":
						data.add(new PortEventData(call_name, container_name, call_time, CallPortType.CALL_PORT_WRITE));
						break;
					default:
						System.err.println("Could not parse " + dataObj + " (call_type is unknown)");
						break;
					}
				} else {
					// if (callType.equals("CALL_START_WITH_DURATION")) { //
					// TODO
					// ////////////////////////////////////////////////////////////////////////////////////////////
					Long call_duration = (Long) dataObj.get("call_duration");
					if (call_duration != null) {
						if (call_time > 0 && call_time < minimalTimestamp) {
							minimalTimestamp = call_time;
						}

						// // TODO dummy remove
						// if (call_duration - call_time < 500000L) {
						// call_duration = call_duration + (1 +
						// (int)(Math.random() * 5))*1000000L;
						// }

						data.add(new CallEventData(call_name, container_name, call_time, call_duration));
					} else {
						System.err.println("Could not parse " + dataObj + " (call_duration is empty)");
					}
					// }
				}
			} else {
				// System.err.println("Could not parse " + dataObj + " (some
				// details are missing)");
			}
		}
		return data;
	}

	public Graph<String, TimingGraphEdge> parseDotGraph(String dotFile)
			throws FileNotFoundException, IOException, ParseException, ImportException {
		Graph<String, TimingGraphEdge> graph = new DirectedPseudograph<>(TimingGraphEdge.class);
		GraphImporter<String, TimingGraphEdge> importer = createImporter();

		importer.importGraph(graph, new BufferedReader(new FileReader(dotFile)));
		return graph;
	}

	public void parseAndAddRTSpecification(String jsonFile, DataProcessor dp)
			throws FileNotFoundException, IOException, ParseException, ImportException {
		ArrayList<ActivitySpecification> global_activities = new ArrayList<ActivitySpecification>();

		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(jsonFile));

		JSONObject jsonObject = (JSONObject) ((JSONObject) obj).get("rtspecs");

		JSONArray componentEntries = (JSONArray) jsonObject.get("components");
		Iterator<JSONObject> componentEntriesIterator = componentEntries.iterator();

		while (componentEntriesIterator.hasNext()) {
			JSONObject dataObj = (JSONObject) componentEntriesIterator.next();
			// ####### COMPONENT (start) #######
			String name = (String) dataObj.get("name");
			String class_name = (String) dataObj.get("class");

			ComponentData candidateComponent = dp.getComponents().get(name);
			if (candidateComponent == null) {
				// ignore missing component
				continue;
			}
			ComponentSpecification candidateCS = new ComponentSpecification(name, class_name);

			// ####### ACTIVITY (start) #######
			JSONObject activity = (JSONObject) dataObj.get("activity");
			Double activity_period = (Double) activity.get("period");
			System.out.println(activity_period);
			Long activity_priority = (Long) activity.get("priority");
			System.out.println(activity_priority);
			Long activity_core = (Long) activity.get("core");
			String activity_scheduler = (String) activity.get("scheduler");
			// check if new activity
			ActivitySpecification found_activity = null;
			for (ActivitySpecification act : global_activities) {
				if (act.period == activity_period && act.priority == activity_priority && act.core == activity_core
						&& act.scheduler.equals(activity_scheduler)) {
					found_activity = act;
				}
			}
			if (found_activity != null) {
				// use activity
				candidateCS.setActivity(found_activity);
			} else {
				// create new one
				ActivitySpecification actSp = new ActivitySpecification(activity_period, activity_priority,
						activity_core, activity_scheduler);
				candidateCS.setActivity(actSp);
				global_activities.add(actSp);
			}
			// ####### ACTIVITY (end) #######

			JSONArray ports = (JSONArray) dataObj.get("ports");
			Iterator<JSONObject> portEntriesIterator = ports.iterator();
			while (portEntriesIterator.hasNext()) {
				JSONObject portObj = (JSONObject) portEntriesIterator.next();
				String port_name = (String) portObj.get("name");
				String port_type = (String) portObj.get("type");
				String port_data = (String) portObj.get("data");
				candidateCS.addPort(port_name, port_type, port_data);
			}

			JSONArray calls = (JSONArray) dataObj.get("calls");
			Iterator<JSONObject> callEntriesIterator = calls.iterator();
			while (callEntriesIterator.hasNext()) {
				JSONObject callObj = (JSONObject) callEntriesIterator.next();
				String call_name = (String) callObj.get("name");
				Long call_wcet = (Long) callObj.get("wcet");
				candidateCS.addCall(call_name, call_wcet);
			}

			candidateComponent.setComponentSpecs(candidateCS);
			// ####### COMPONENT (end) #######
		}

		dp.activities = global_activities;

		// Color[] colors = generateRandomColorHSV(global_activities.size());
		for (int i = 0; i < global_activities.size(); i++) {
			// Color c = colors[i];
			global_activities.get(i).color = KELLY_COLORS[i];
		}

		// JSONArray controlPathEntries = (JSONArray)
		// jsonObject.get("controlpaths");
		// Iterator<JSONObject> controlPathEntriesIterator =
		// controlPathEntries.iterator();
		// while (controlPathEntriesIterator.hasNext()) {
		// JSONObject ctrlPathObj = (JSONObject)
		// controlPathEntriesIterator.next();
		//
		// JSONArray ctrlpath_nodes = (JSONArray) ctrlPathObj.get("nodes");
		// Iterator<JSONObject> ctrlpath_nodesIterator =
		// ctrlpath_nodes.iterator();
		// while (ctrlpath_nodesIterator.hasNext()) {
		// JSONObject ctrlPathNodesObj = (JSONObject)
		// ctrlpath_nodesIterator.next();
		// String ctrlPathNode_name = (String)
		// ctrlPathNodesObj.get("component");
		// if (ctrlPathNodesObj.containsKey("port")) {
		// String ctrlPathNodePort_name = (String) ctrlPathNodesObj.get("port");
		// }
		// // TODO
		// }
		//
		// Long ctrlpath_wcft = (Long) ctrlPathObj.get("wcft");
		// // TODO
		// }
	}

	/**
	 * Create the importer
	 */
	public static GraphImporter<String, TimingGraphEdge> createImporter() {
		/*
		 * Create vertex provider.
		 *
		 * The importer reads vertices and calls a vertex provider to create
		 * them. The provider receives as input the unique id of each vertex and
		 * any additional attributes from the input stream.
		 */
		VertexProvider<String> vertexProvider = (id, attributes) -> {
			String cv = new String(id);
			//
			// // read color from attributes map
			// if (attributes.containsKey("color")) {
			// String color = attributes.get("color").getValue();
			// switch (color) {
			// case "black":
			// cv.setColor(Color.BLACK);
			// break;
			// case "white":
			// cv.setColor(Color.WHITE);
			// break;
			// default:
			// // ignore not supported color
			// }
			// }

			return cv;
		};

		/*
		 * Create edge provider.
		 *
		 * The importer reads edges from the input stream and calls an edge
		 * provider to create them. The provider receives as input the source
		 * and target vertex of the edge, an edge label (which can be null) and
		 * a set of edge attributes all read from the input stream.
		 */
		EdgeProvider<String, TimingGraphEdge> edgeProvider = (from, to, label, attributes) -> new TimingGraphEdge(
				attributes.get("source").getValue(), attributes.get("target").getValue());

		/*
		 * Create the graph importer with a vertex and an edge provider.
		 */
		DOTImporter<String, TimingGraphEdge> importer = new DOTImporter<>(vertexProvider, edgeProvider);

		return importer;
	}

	// public static Color[] generateRandomColorHSV(int colorsNumber) {
	// Color[] colors = new Color[colorsNumber];
	// for (int i = 0, j = 0; i < 360; i += 360 / colorsNumber, j++) {
	// // hsv[0] is Hue [0 .. 360) hsv[1] is Saturation [0...1] hsv[2] is
	// // Value [0...1]
	// float[] hsv = new float[3];
	// hsv[0] = i;
	// hsv[1] = (float) Math.random(); // Some restrictions here?
	// hsv[2] = (float) Math.random();
	//
	// colors[j] = generateRandomColor(hsvToRgb(hsv[0], hsv[1], hsv[2]));
	// }
	// return colors;
	// }
	//
	// public static Color hsvToRgb(float h, float s, float v) {
	// float r = 0f;
	// float g = 0f;
	// float b = 0f;
	//
	// float i = (float) Math.floor(h * 6);
	// float f = h * 6 - i;
	// float p = v * (1 - s);
	// float q = v * (1 - f * s);
	// float t = v * (1 - (1 - f) * s);
	//
	// switch ((int) (i % 6)) {
	// case 0:
	// r = v;
	// g = t;
	// b = p;
	// break;
	// case 1:
	// r = q;
	// g = v;
	// b = p;
	// break;
	// case 2:
	// r = p;
	// g = v;
	// b = t;
	// break;
	// case 3:
	// r = p;
	// g = q;
	// b = v;
	// break;
	// case 4:
	// r = t;
	// g = p;
	// b = v;
	// break;
	// case 5:
	// r = v;
	// g = p;
	// b = q;
	// break;
	// }
	// return new Color((int)(r * 255), (int)(g * 255), (int)(b * 255));
	// }
	//
	// final static Random mRandom = new Random(System.currentTimeMillis());
	//
	// public static Color generateRandomColor(Color mix) {
	// Random random = new Random();
	// int red = random.nextInt(256);
	// int green = random.nextInt(256);
	// int blue = random.nextInt(256);
	//
	// // mix the color
	// if (mix != null) {
	// red = (red + mix.getRed()) / 2;
	// green = (green + mix.getGreen()) / 2;
	// blue = (blue + mix.getBlue()) / 2;
	// }
	//
	// Color color = new Color(red, green, blue);
	// return color;
	// }

	// Don't forget to import javafx.scene.paint.Color;

	private static final Color[] KELLY_COLORS = { Color.web("0xFFB300"), // Vivid
																			// Yellow
			Color.web("0x803E75"), // Strong Purple
			Color.web("0xFF6800"), // Vivid Orange
			Color.web("0xA6BDD7"), // Very Light Blue
			Color.web("0xC10020"), // Vivid Red
			Color.web("0xCEA262"), // Grayish Yellow
			Color.web("0x817066"), // Medium Gray

			Color.web("0x007D34"), // Vivid Green
			Color.web("0xF6768E"), // Strong Purplish Pink
			Color.web("0x00538A"), // Strong Blue
			Color.web("0xFF7A5C"), // Strong Yellowish Pink
			Color.web("0x53377A"), // Strong Violet
			Color.web("0xFF8E00"), // Vivid Orange Yellow
			Color.web("0xB32851"), // Strong Purplish Red
			Color.web("0xF4C800"), // Vivid Greenish Yellow
			Color.web("0x7F180D"), // Strong Reddish Brown
			Color.web("0x93AA00"), // Vivid Yellowish Green
			Color.web("0x593315"), // Deep Yellowish Brown
			Color.web("0xF13A13"), // Vivid Reddish Orange
			Color.web("0x232C16"), // Dark Olive Green
	};

}
