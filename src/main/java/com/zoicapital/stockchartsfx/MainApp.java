package com.zoicapital.stockchartsfx;
/*
Copyright 2014 Zoi Capital, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.TimingData;
import de.dlw.timing.viz.data.parser.DataProcessor;
import de.dlw.timing.viz.data.parser.JSONDataParser;
import de.dlw.timing.viz.data.PortEventData.CallPortType;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// read JSON data
		// read JSON data
		JSONDataParser jdp = new JSONDataParser();
		List<TimingData> data = jdp.parse("/home/dwigand/code/cogimon/CoSimA/framework-extensions/rtt-core-extensions/build/reports.dat");

		// analyze data
		DataProcessor dp = new DataProcessor();
		dp.loadDataBatch(data);
		dp.shiftTimestamps(jdp.minimalTimestamp);
		dp.calculateSetOfComponents();
//		dp.printReport();
		dp.calculateBasicStatistics();

		CandleStickChart candleStickChart = new CandleStickChart("Timing Chart DLW", dp.getComponents());
		Scene scene = new Scene(candleStickChart);
		scene.getStylesheets().add("/styles/CandleStickChartStyles.css");

		stage.setTitle("Timing Chart DLW");
		stage.setScene(scene);
		stage.show();

		// candleStickChart.setXAxisFormatter(new
		// DecimalAxisFormatter("#000.00"));
	}

//	public List<TimingData> buildData() {
//
//		final List<TimingData> data = new ArrayList<>();
//
//		// ib
//		CallEventData ibUpdate = new CallEventData();
//		PortEventData ibPortAccess = new PortEventData();
//
//		ibUpdate = new CallEventData("updateHook()", "ib", 1513785953570310542L, 1513785953570323730L);
//		data.add(ibUpdate);
//		ibPortAccess = new PortEventData("ib", 1513785953670314392L, CallPortType.CALL_PORT_WRITE);
//		data.add(ibPortAccess);
//		ibUpdate = new CallEventData("updateHook()", "ib", 1513785953670304084L, 1513785953670322803L);
//		data.add(ibUpdate);
//		ibPortAccess = new PortEventData("ib", 1513785953770317835L, CallPortType.CALL_PORT_WRITE);
//		data.add(ibPortAccess);
//		ibUpdate = new CallEventData("updateHook()", "ib", 1513785953770312749L, 1513785953770326416L);
//		data.add(ibUpdate);
//		ibPortAccess = new PortEventData("ib", 1513785953870303928L, CallPortType.CALL_PORT_WRITE);
//		data.add(ibPortAccess);
//		ibUpdate = new CallEventData("updateHook()", "ib", 1513785953870299000L, 1513785953870312202L);
//		data.add(ibUpdate);
//		ibPortAccess = new PortEventData("ib", 1513785953970241186L, CallPortType.CALL_PORT_WRITE);
//		data.add(ibPortAccess);
//
//		// ib2
//		CallEventData ib2Update = new CallEventData();
//		PortEventData ib2PortAccess = new PortEventData();
//
////		System.out.println("ib - ib2 = " + (1513785953570310542L - 1513785953572173848L));
////
////		System.out.println("ib = " + (1513785953570323730L - 1513785953570310542L));
////
////		System.out.println("ib2 = " + (1513785953672239432L - 1513785953672232511L));
//
//		ib2Update = new CallEventData("updateHook()", "ib2", 1513785953572173848L, 1513785953572184113L);
//		data.add(ib2Update);
//		ib2PortAccess = new PortEventData("ib2", 1513785953672235242L, CallPortType.CALL_PORT_WRITE);
//		data.add(ib2PortAccess);
//		ib2Update = new CallEventData("updateHook()", "ib2", 1513785953672232511L, 1513785953672239432L);
//		data.add(ib2Update);
//		ib2PortAccess = new PortEventData("ib2", 1513785953772435861L, CallPortType.CALL_PORT_WRITE);
//		data.add(ib2PortAccess);
//		ib2Update = new CallEventData("updateHook()", "ib2", 1513785953772430529L, 1513785953772444672L);
//		data.add(ib2Update);
//		ib2PortAccess = new PortEventData("ib2", 1513785953872169257L, CallPortType.CALL_PORT_WRITE);
//		data.add(ib2PortAccess);
//		ib2Update = new CallEventData("updateHook()", "ib2", 1513785953872165545L, 1513785953872174813L);
//		data.add(ib2Update);
//		ib2PortAccess = new PortEventData("ib2", 1513785953972174661L, CallPortType.CALL_PORT_WRITE);
//		data.add(ib2PortAccess);
//
//		// normalize timestamps
//		long minimalTimestamp = Long.MAX_VALUE;
//		for (TimingData timingData : data) {
//			if (timingData.getTimestamp() < minimalTimestamp) {
//				minimalTimestamp = timingData.getTimestamp();
//			}
//		}
//		for (TimingData timingData : data) {
//			if (timingData instanceof CallEventData) {
//				CallEventData tmp = (CallEventData) timingData;
//				tmp.setTimestamp(tmp.getTimestamp() - minimalTimestamp);
//				tmp.setEndTimestamp(tmp.getEndTimestamp() - minimalTimestamp);
//
//			} else if (timingData instanceof PortEventData) {
//				PortEventData tmp = (PortEventData) timingData;
//				tmp.setTimestamp(tmp.getTimestamp() - minimalTimestamp);
//			} else {
//				timingData.setTimestamp(timingData.getTimestamp() - minimalTimestamp);
//			}
//		}
//
//		return data;
//	}

	protected double getNewValue(double previousValue) {
		int sign;

		if (Math.random() < 0.5) {
			sign = -1;
		} else {
			sign = 1;
		}
		return getRandom() * sign + previousValue;
	}

	protected double getRandom() {
		double newValue = 0;
		newValue = Math.random() * 10;
		return newValue;
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
