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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.ComponentCallData;
import de.dlw.timing.viz.data.ComponentData;
import de.dlw.timing.viz.data.ComponentPortData;
import de.dlw.timing.viz.data.PortConnectionData;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.TimingData;
import de.dlw.timing.viz.data.parser.DataProcessor;
import de.dlw.timing.viz.viewmodel.PortAccessIndicator;
import de.dlw.timing.viz.viewmodel.PortConnection;
import de.dlw.timing.viz.viewmodel.TimingBlock;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

/**
 * A candlestick chart is a style of bar-chart used primarily to describe price
 * movements of a security, derivative, or currency over time.
 *
 * The Data Y value is used for the opening price and then the close, high and
 * low values are stored in the Data's extra value property using a
 * CandleStickExtraValues object.
 *
 *
 */
public class CandleStickChart extends XYChart<Number, String> {
	private boolean average = false;

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	protected static final Logger logger = Logger.getLogger(CandleStickChart.class.getName());
	protected int maxBarsToDisplay;
	protected ObservableList<XYChart.Series<Number, String>> dataSeries;
	// protected TimingData lastBar;
	protected CategoryAxis yAxis;
	protected NumberAxis xAxis;
	private double oldMouseX;
	protected DataProcessor dataProcessor;
	private double currentTickUnit = -1;

	// ComponentCallData c; // TODO test
	// CallEventData ccc; // TODO test

	// protected HashMap<String, ComponentData> componentData;

	public CandleStickChart(String title, DataProcessor dataProcessor) {
		this(title, Integer.MAX_VALUE, dataProcessor);
	}

	/**
	 *
	 * @param title
	 *            The chart title
	 * @param bars
	 *            The bars to display in the chart
	 * @param maxBarsToDisplay
	 *            The maximum number of bars to display in the chart.
	 */
	public CandleStickChart(String title, int maxBarsToDisplay, DataProcessor dataProcessor) {
		this(title, new NumberAxis(), new CategoryAxis(), maxBarsToDisplay, dataProcessor);
	}

	/**
	 * Construct a new CandleStickChart with the given axis.
	 *
	 * @param title
	 *            The chart title
	 * @param xAxis
	 *            The x axis to use
	 * @param yAxis
	 *            The y axis to use
	 * @param bars
	 *            The bars to display on the chart
	 * @param maxBarsToDisplay
	 *            The maximum number of bars to display on the chart.
	 */
	public CandleStickChart(String title, NumberAxis xAxis, CategoryAxis yAxis, int maxBarsToDisplay,
			DataProcessor dataProcessor) {
		super(xAxis, yAxis);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.maxBarsToDisplay = maxBarsToDisplay;
		this.dataProcessor = dataProcessor;

		xAxis.autoRangingProperty().set(true);
		xAxis.forceZeroInRangeProperty().setValue(Boolean.FALSE);
		setTitle(title);
		setAnimated(true);
		getStylesheets().add(getClass().getResource("/styles/CandleStickChartStyles.css").toExternalForm());
		xAxis.setAnimated(true);
		yAxis.setAnimated(true);
		verticalGridLinesVisibleProperty().set(false);

		XYChart.Series<Number, String> series = new XYChart.Series<>();
		dataSeries = FXCollections.observableArrayList(series);
		this.dataProcessor.setDataSeriesReference(dataSeries);
		setData(dataSeries);

		final double SCALE_DELTA = 1.1;
		this.setOnScroll(new EventHandler<ScrollEvent>() {
			public void handle(ScrollEvent event) {
				event.consume();

				if (event.getDeltaY() == 0) {
					return;
				}

				if (xAxis.isAutoRanging()) {
					currentTickUnit = xAxis.getTickUnit();
					xAxis.autoRangingProperty().set(false);
				}

				// // TODO test adding new one
				// ComponentCallData c = new ComponentCallData("nnn", "dlw");
				// c.addCallEvent(new CallEventData("nnn", "dlw", 0L,
				// 4000000000L));
				// series.getData().add(new XYChart.Data<>(0L, "dlw", c));
				// // check if CCD exists otherwise create and do the same for
				// component data!
				// ccc.setTimestamp(ccc.getTimestamp() + 30000000);
				// ccc.setEndTimestamp(ccc.getEndTimestamp() + 30000000);
				// requestChartLayout();

				double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

				if (event.getDeltaY() > 0) {
					if (event.isShiftDown()) {
						double newL = xAxis.getLowerBound() + xAxis.getTickUnit() * 0.01;
						double newU = xAxis.getUpperBound() - xAxis.getTickUnit() * 0.01;
						if (newL < newU) {
							xAxis.setLowerBound(newL);
							xAxis.setUpperBound(newU);
						}
					} else {
						double newL = xAxis.getLowerBound() + xAxis.getTickUnit();
						double newU = xAxis.getUpperBound() - xAxis.getTickUnit();
						if (newL < newU) {
							xAxis.setLowerBound(newL);
							xAxis.setUpperBound(newU);
						}
					}
				} else {
					if (event.isShiftDown()) {
						double newL = xAxis.getLowerBound() - xAxis.getTickUnit() * 0.01;
						double newU = xAxis.getUpperBound() + xAxis.getTickUnit() * 0.01;
						xAxis.setLowerBound(newL);
						xAxis.setUpperBound(newU);
					} else {
						double newL = xAxis.getLowerBound() - xAxis.getTickUnit();
						double newU = xAxis.getUpperBound() + xAxis.getTickUnit();
						xAxis.setLowerBound(newL);
						xAxis.setUpperBound(newU);
					}
				}
			}
		});

		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					ObservableList<String> list = yAxis.getCategories();
					list.clear();
					// sc KUKA
//					list.add("seperator");
//					list.add("transition");
//					list.add("jointposcontroller");
//					list.add("caux");
//					list.add("fkin");
//					list.add("combiner");
//					list.add("robot_gazebo2");
//					list.add("robot_gazebo1");

					//sc coman
					list.add("com");
					list.add("test");
					list.add("base");
					list.add("robot_gazebo");


					//[fkin, jointposcontroller, combiner, seperator, robot_gazebo1, transition, robot_gazebo2, caux]
//					System.out.println(list);
					yAxis.setCategories(list);

					xAxis.autoRangingProperty().set(true);
				}
				oldMouseX = event.getSceneX();
			}
		});

		this.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				event.consume();
				xAxis.autoRangingProperty().set(false);

				double oldX = xAxis.sceneToLocal(oldMouseX, 0).getX();
				double newX = xAxis.sceneToLocal(event.getSceneX(), 0).getX();

				double oldXvalue = xAxis.getValueForDisplay(oldX).doubleValue();
				double newXvalue = xAxis.getValueForDisplay(newX).doubleValue();
				double dM = newXvalue - oldXvalue;

				xAxis.setLowerBound(xAxis.getLowerBound() - dM);
				xAxis.setUpperBound(xAxis.getUpperBound() - dM);

				oldMouseX = event.getSceneX();

			}
		});
	}

	public ObservableList<XYChart.Series<Number, String>> getDataSeries() {
		return dataSeries;
	}

	/**
	 * Defines a formatter to use when formatting the y-axis values.
	 *
	 * @param formatter
	 *            The formatter to use when formatting the y-axis values.
	 */
	public void setXAxisFormatter(DecimalAxisFormatter formatter) {
		xAxis.setTickLabelFormatter(formatter);
	}

	// /**
	// * Appends a new bar on to the end of the chart.
	// * @param bar The bar to append to the chart
	// */
	// public void addBar(BarData bar) {
	//
	// if (dataSeries.get(0).getData().size() >= maxBarsToDisplay) {
	// dataSeries.get(0).getData().remove(0);
	// }
	//
	// int datalength = dataSeries.get(0).getData().size();
	// dataSeries.get(0).getData().get(datalength - 1).setYValue(bar.getOpen());
	// dataSeries.get(0).getData().get(datalength - 1).setExtraValue(bar);
	// String label = sdf.format(bar.getDateTime().getTime());
	//// logger.log(Level.INFO, "Adding bar with actual time: {0}",
	// bar.getDateTime().getTime());
	//// logger.log(Level.INFO, "Adding bar with formated time: {0}", label);
	//
	// lastBar = new BarData(bar.getDateTime(), bar.getClose(), bar.getClose(),
	// bar.getClose(), bar.getClose(), 0);
	// Data<String, Number> data = new XYChart.Data<>(label, lastBar.getOpen(),
	// lastBar);
	// dataSeries.get(0).getData().add(data);
	//
	//
	//
	// }

	/**
	 * Update the "Last" price of the most recent bar
	 *
	 * @param price
	 *            The Last price of the most recent bar.
	 */
	public void updateLast(double price) {
		logger.log(Level.INFO, "Updating!!!! updateLast()");
		// if (lastBar != null) {
		// lastBar.update(price);
		// logger.log(Level.INFO, "Updating last bar with date/time: {0}",
		// lastBar.getDateTime().getTime());
		//
		// int datalength = dataSeries.get(0).getData().size();
		// dataSeries.get(0).getData().get(datalength -
		// 1).setYValue(lastBar.getOpen());
		//
		// dataSeries.get(0).getData().get(datalength -
		// 1).setExtraValue(lastBar);
		// logger.log(Level.INFO, "Updating last bar with formatteddate/time:
		// {0}", dataSeries.get(0).getData().get(datalength - 1).getXValue());
		// }
	}

	protected List<TimingData> getSubList(List<TimingData> bars, int maxBars) {
		List<TimingData> sublist;
		if (bars.size() > maxBars) {
			return bars.subList(bars.size() - 1 - maxBars, bars.size() - 1);
		} else {
			return bars;
		}
	}

	// -------------- METHODS
	// ------------------------------------------------------------------------------------------
	/**
	 * Called to update and layout the content for the plot
	 */
	@Override
	protected void layoutPlotChildren() {
		// we have nothing to layout if no data is present
		if (getData() == null) {
			return;
		}

		double blockHeight = 20;

		// update candle positions
		for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
			Series<Number, String> series = getData().get(seriesIndex);
			Iterator<Data<Number, String>> iter = getDisplayedDataIterator(series);
			Path seriesPath = null;
			if (series.getNode() instanceof Path) {
				seriesPath = (Path) series.getNode();
				seriesPath.getElements().clear();
			}
			while (iter.hasNext()) {
				Data<Number, String> item = iter.next();
				double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
				double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));

				// double lengthh = getYAxis().getTickLength();

				Node itemNode = item.getNode();
				TimingData bar = (TimingData) item.getExtraValue();
				if (itemNode instanceof TimingBlock && item.getYValue() != null) {
					TimingBlock tBlock = (TimingBlock) itemNode;
					// update candle
					// candle.update(close - y, high - y, low - y, candleWidth);
					// double startX =
					// getXAxis().getDisplayPosition(((CallEventData)item.getExtraValue()).getTimestamp2msecs());
					double endX = getXAxis()
							.getDisplayPosition(((CallEventData) item.getExtraValue()).getEndTimestamp2msecs());
					// System.out.println("s = " +
					// ((CallEventData)item.getExtraValue()).getTimestamp());
					// System.out.println("e = " +
					// ((CallEventData)item.getExtraValue()).getEndTimestamp());
					// System.out.println("sX = " + startX);

					// System.out.println("start = " + ((ComponentCallData)
					// item.getExtraValue()).getTimestamp2msecs(average));
					// System.out.println("end = " + ((ComponentCallData)
					// item.getExtraValue()).getEndTimestamp2msecs(average));
					// System.out.println("dur = " + (((ComponentCallData)
					// item.getExtraValue()).getEndTimestamp2msecs(average)
					// - ((ComponentCallData)
					// item.getExtraValue()).getTimestamp2msecs(average)));

					double dX = endX - x;
					// System.out.println("X = " + x);
					// System.out.println("endX = " + endX);
					// System.out.println("dX = " + dX);
					if (dX < 1) {
						tBlock.update(0, -blockHeight, 1, blockHeight, xAxis, true);
					} else {
						tBlock.update(0, -blockHeight, dX, blockHeight, xAxis, true);
					}
					// update tooltip content
					// tBlock.updateTooltip(bar.getOpen(), bar.getClose(),
					// bar.getHigh(), bar.getLow());

					// position the candle
					tBlock.setLayoutX(x);
					tBlock.setLayoutY(y);

					tBlock.toBack();
				} else if (itemNode instanceof PortAccessIndicator && item.getYValue() != null) {
					PortAccessIndicator pAI = (PortAccessIndicator) itemNode;
					pAI.update(blockHeight, xAxis);
					pAI.toFront();
					pAI.setLayoutX(x);
					pAI.setLayoutY(y);

				} else if (itemNode instanceof PortConnection && item.getYValue() != null) {
					PortConnection pAI = (PortConnection) itemNode;
					pAI.update(blockHeight, xAxis, yAxis);
					pAI.toFront();
					pAI.setLayoutX(x);
					pAI.setLayoutY(y);
				}

			}
		}
	}

	@Override
	protected void dataItemChanged(Data<Number, String> item) {
	}

	@Override
	protected void dataItemAdded(Series<Number, String> series, int itemIndex, Data<Number, String> item) {
		Node candle = createTimingElement(getData().indexOf(series), item, itemIndex);
		if (shouldAnimate()) {
			candle.setOpacity(0);
			getPlotChildren().add(candle);
			// fade in new candle
			FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
			ft.setToValue(1);
			ft.play();
		} else {
			getPlotChildren().add(candle);
		}
		// always draw average line on top
		if (series.getNode() != null) {
			series.getNode().toFront();
		}
	}

	@Override
	protected void dataItemRemoved(Data<Number, String> item, Series<Number, String> series) {
		final Node candle = item.getNode();
		if (shouldAnimate()) {
			// fade out old candle
			FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
			ft.setToValue(0);
			ft.setOnFinished((ActionEvent actionEvent) -> {
				getPlotChildren().remove(candle);
			});
			ft.play();
		} else {
			getPlotChildren().remove(candle);
		}
	}

	@Override
	protected void seriesAdded(Series<Number, String> series, int seriesIndex) {
		// handle any data already in series
		for (int j = 0; j < series.getData().size(); j++) {
			Data item = series.getData().get(j);
			Node candle = createTimingElement(seriesIndex, item, j);
			if (shouldAnimate()) {
				candle.setOpacity(0);
				getPlotChildren().add(candle);
				// fade in new candle
				FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
				ft.setToValue(1);
				ft.play();
			} else {
				getPlotChildren().add(candle);
			}
		}
		// create series path
		Path seriesPath = new Path();
		seriesPath.getStyleClass().setAll("candlestick-average-line", "series" + seriesIndex);
		series.setNode(seriesPath);
		getPlotChildren().add(seriesPath);
	}

	@Override
	protected void seriesRemoved(Series<Number, String> series) {
		// remove all candle nodes
		for (XYChart.Data<Number, String> d : series.getData()) {
			final Node candle = d.getNode();
			if (shouldAnimate()) {
				// fade out old candle
				FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
				ft.setToValue(0);
				ft.setOnFinished((ActionEvent actionEvent) -> {
					getPlotChildren().remove(candle);
				});
				ft.play();
			} else {
				getPlotChildren().remove(candle);
			}
		}
	}

	/**
	 * Create a new Candle node to represent a single data item
	 *
	 * @param seriesIndex
	 *            The index of the series the data item is in
	 * @param item
	 *            The data item to create node for
	 * @param itemIndex
	 *            The index of the data item in the series
	 * @return New candle node to represent the give data item
	 */
	private Node createTimingElement(int seriesIndex, final Data item, int itemIndex) {
		Node node = item.getNode();
		// check if candle has already been created
		if (node instanceof TimingBlock) {
			((TimingBlock) node).setSeriesAndDataStyleClasses("series" + seriesIndex, "data" + itemIndex);
			return node;
		}

		if (item.getExtraValue() instanceof CallEventData) {
			CallEventData tmp = (CallEventData) item.getExtraValue();
			node = new TimingBlock("series" + seriesIndex, "data" + itemIndex, tmp);
			item.setNode(node);
		} else if (item.getExtraValue() instanceof PortEventData) {
			PortEventData tmp = (PortEventData) item.getExtraValue();
			node = new PortAccessIndicator(tmp);
			item.setNode(node);
		} else if (item.getExtraValue() instanceof PortConnectionData) {
			PortConnectionData tmp = (PortConnectionData) item.getExtraValue();
			// add me as reference.
			if (tmp.target != null && tmp.target.portConnectionDataRefs != null
					&& !tmp.target.portConnectionDataRefs.contains(tmp)) {
				tmp.target.portConnectionDataRefs.add(tmp);
				// sort list
				Collections.sort(tmp.target.portConnectionDataRefs, new Comparator<PortConnectionData>() {
					@Override
					public int compare(PortConnectionData o1, PortConnectionData o2) {
						return Long.compare(o1.getTimestamp(), o2.getTimestamp());
					}
				});
			}
			node = new PortConnection(tmp);
			item.setNode(node);
		}
		return node;
	}

	/**
	 * This is called when the range has been invalidated and we need to update
	 * it. If the axis are auto ranging then we compile a list of all data that
	 * the given axis has to plot and call invalidateRange() on the axis passing
	 * it that data.
	 */
	@Override
	protected void updateAxisRange() {
		// For candle stick chart we need to override this method as we need to
		// let the axis know that they need to be able
		// to cover the whole area occupied by the high to low range not just
		// its center data value
		final Axis<Number> xa = getXAxis();
		final Axis<String> ya = getYAxis();
		List<Number> xData = null;
		List<String> yData = null;
		if (ya.isAutoRanging()) {
			yData = new ArrayList<>();
		}
		if (xa.isAutoRanging()) {
			xData = new ArrayList<>();
		}
		if (yData != null || xData != null) {
			for (Series<Number, String> series : getData()) {
				for (Data<Number, String> data : series.getData()) {
					if (yData != null) {
						yData.add(data.getYValue());
					}
					if (xData != null) {
						Object extras = data.getExtraValue();
						if (extras != null) {
							if (extras instanceof CallEventData) {
								xData.add(((CallEventData) extras).getTimestamp2msecs());
								xData.add(((CallEventData) extras).getEndTimestamp2msecs());
							} else if (extras instanceof ComponentPortData) {
								xData.add(((ComponentPortData) extras).getTimestamp2msecs(average));
							}
						} else {
							xData.add(data.getXValue());
						}
					}
				}
			}
			if (yData != null) {
				ya.invalidateRange(yData);
			}
			if (xData != null) {
				xa.invalidateRange(xData);
			}
		}
	}

	// private String dataStyleClass;

	protected static CandleStickChart chart;

}
