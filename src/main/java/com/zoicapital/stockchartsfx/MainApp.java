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

import java.util.List;

import de.dlw.timing.viz.data.TimingData;
import de.dlw.timing.viz.data.parser.DataProcessor;
import de.dlw.timing.viz.data.parser.JSONDataParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// read JSON data
		JSONDataParser jdp = new JSONDataParser();
		// List<TimingData> data =
		// jdp.parse("/home/dwigand/code/cogimon/CoSimA/framework-extensions/rtt-core-extensions/build/reports.dat");
		// List<TimingData> data =
		// jdp.parse("/home/dwigand/code/misc/java/javafx/StockChartsFX/reports.dat");
		// List<TimingData> data =
		// jdp.parse("/home/dwigand/code/misc/java/javafx/StockChartsFX/reportsWOtimesync.dat");
		// List<TimingData> data =
		// jdp.parse("/home/dwigand/code/misc/java/javafx/StockChartsFX/reportsCapacity40_2.dat");


//		List<TimingData> data = jdp.parse("/home/dwigand/code/misc/java/javafx/StockChartsFX/rtReport8.dat");

		List<TimingData> data = jdp.parse("/home/dwigand/code/misc/java/javafx/StockChartsFX/coman2.dat");

		// analyze data
		DataProcessor dp = new DataProcessor();
//		dp.graph = jdp.parseDotGraph("/home/dwigand/code/misc/java/javafx/StockChartsFX/orograph_minimal.dot");

		dp.graph = jdp.parseDotGraph("/home/dwigand/code/misc/java/javafx/StockChartsFX/coman1_graph_minimal.dot");

		// DataProcessor.shiftTimestamps(jdp.minimalTimestamp, data);
		// dp.setComponents(DataProcessor.calculateSetOfComponents(data));

		CandleStickChart candleStickChart = new CandleStickChart("Timing Chart DLW", dp);
		// enable the data processor to work on the data series

		// dp.dummyTestAddData();
		System.out.println("READING finished = " + data.size());
		int i = 0;
		for (TimingData timingData : data) {
			dp.processTimingDataSample(timingData, false);
			i++;
			if (i % 10000 == 0) {
				System.out.println("Processed " + i + " of " + data.size());
			}
		}
		System.out.println("PROCESSING finished");
		// dp.printReport();
		dp.triggerRecalculation();
		jdp.parseAndAddRTSpecification("/home/dwigand/code/misc/java/javafx/StockChartsFX/RTSpecificationTest.json", dp);
		System.out.println("PROCESSING parseAndAddRTSpecification() finished");

		Scene scene = new Scene(candleStickChart);
		scene.getStylesheets().add("/styles/CandleStickChartStyles.css");

		stage.setTitle("Timing Chart DLW");
		stage.setScene(scene);
		stage.show();

		// candleStickChart.setXAxisFormatter(new
		// DecimalAxisFormatter("#000.00"));
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
		// HashMap<String, ComponentCallData> h = new HashMap<String,
		// ComponentCallData>();
		// h.put("a", new ComponentCallData("myname", "containername"));
		//
		// System.out.println(h.get("a").getContainerName());
		//
		// ComponentCallData c = h.get("a");
		//
		// c.setContainerName("blaaa");
		//
		// System.out.println(h.get("a").getContainerName());

		launch(args);
	}

}
