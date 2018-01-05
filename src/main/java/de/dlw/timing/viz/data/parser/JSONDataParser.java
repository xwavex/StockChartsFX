package de.dlw.timing.viz.data.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.TimingData;
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
						data.add(new PortEventData(call_name, container_name, call_time, CallPortType.CALL_PORT_READ_NODATA));
						break;
					case "CALL_PORT_READ_OLDDATA":
						data.add(new PortEventData(call_name, container_name, call_time, CallPortType.CALL_PORT_READ_OLDDATA));
						break;
					case "CALL_PORT_READ_NEWDATA":
						data.add(new PortEventData(call_name, container_name, call_time, CallPortType.CALL_PORT_READ_NEWDATA));
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

//						// TODO dummy remove
//						 if (call_duration - call_time < 500000L) {
//						 call_duration = call_duration + (1 + (int)(Math.random() * 5))*1000000L;
//						 }

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

}
