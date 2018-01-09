package de.dlw.timing.viz.viewmodel;

import java.text.DecimalFormat;

import de.dlw.timing.viz.data.ActivitySpecification;
import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.CallSpecification;
import de.dlw.timing.viz.data.ComponentSpecification;
import de.dlw.timing.viz.viewmodel.tooltip.TimingBlockTooltip;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

public class TimingBlock extends Group {

	private final Region specificationWCET = new Region();
	private final Line meanDurationLine = new Line();
	private final Region wmeDuration = new Region();
	private final Region stdRegion = new Region();
	private final Region bar = new Region();
	private String seriesStyleClass;
	private String dataStyleClass;
	private boolean openAboveClose = true;
	private final Tooltip tooltip = new Tooltip();
	private Label lblName;
	private Label lblDuration;
	private DecimalFormat df = new DecimalFormat("#.00");

	public CallEventData cedReference = null;


	public TimingBlock(String seriesStyleClass, String dataStyleClass, CallEventData ced) {
		setAutoSizeChildren(false);
		this.cedReference = ced;
		lblName = new Label(this.cedReference.getName());
		lblName.setAlignment(Pos.CENTER);

		lblDuration = new Label("" + df.format((this.cedReference.getEndTimestamp2msecs()-this.cedReference.getTimestamp2msecs())) + " ms");
		lblDuration.setAlignment(Pos.TOP_CENTER);
		getChildren().addAll(bar, lblName, wmeDuration, specificationWCET, lblDuration, stdRegion, meanDurationLine);
		this.seriesStyleClass = seriesStyleClass;
		this.dataStyleClass = dataStyleClass;
		updateStyleClasses();
		TimingBlockTooltip tmpToolTip = new TimingBlockTooltip();
		tmpToolTip.update(this.cedReference.getName(), this.cedReference.getContainerName(),
				this.cedReference.getTimestamp2msecs(), this.cedReference.getEndTimestamp2msecs());
		tooltip.setGraphic(tmpToolTip);
		Tooltip.install(bar, tooltip);
		Tooltip.install(lblName, tooltip);
	}

	public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass) {
		this.seriesStyleClass = seriesStyleClass;
		this.dataStyleClass = dataStyleClass;
		updateStyleClasses();
	}

	public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth, NumberAxis xAxis,
			boolean use_msecs) {
		openAboveClose = closeOffset > 0;
		updateStyleClasses();

		lblName.toFront();
		lblName.resizeRelocate(closeOffset, highOffset, lowOffset, candleWidth);

		bar.resizeRelocate(closeOffset, highOffset, lowOffset, candleWidth);

		double wmed2msec = cedReference.parentReference.getWorstMeasuredExecutionDuration2msecs();
		double cedST2msec = xAxis.getDisplayPosition(cedReference.getTimestamp2msecs());
		if (wmed2msec > 0.0) {
			double wmedET2msec = xAxis.getDisplayPosition(cedReference.getTimestamp2msecs() + wmed2msec);
			double dXwmed2msec = wmedET2msec - cedST2msec;
			wmeDuration.resizeRelocate(closeOffset, highOffset, dXwmed2msec, candleWidth);
			wmeDuration.toBack();
			wmeDuration.setVisible(true);
		} else {
			wmeDuration.setVisible(false);
		}


		ComponentSpecification csp = cedReference.parentReference.parentComponent.getComponentSpecs();
		if (csp != null) {
			CallSpecification call = csp.calls.get(this.cedReference.getName());
			if (call != null) {
				double s_wcet_2msec = call.wcet * 1e-6;
				if (s_wcet_2msec > 0.0) {
					if (cedReference.parentReference.getWorstMeasuredExecutionDuration() > call.wcet) {
						// wcet violated
						specificationWCET.getStyleClass().setAll("specification","wcet-bar", "violated");
					} else {
						specificationWCET.getStyleClass().setAll("specification","wcet-bar");
					}

					double wcetET2msec = xAxis.getDisplayPosition(cedReference.getTimestamp2msecs() + s_wcet_2msec);
					double endWcet2msec = wcetET2msec - cedST2msec;
					specificationWCET.resizeRelocate(endWcet2msec, highOffset-6, 5, candleWidth+12);
					specificationWCET.toFront();
					specificationWCET.setVisible(true);
				} else {
					specificationWCET.setVisible(false);
				}
			}
//			ActivitySpecification act = csp.activity;
//			if (act != null && act.color != null) {
//				System.out.println("-fx-border-color: rgb("+ act.color.getRed() + ", " + act.color.getGreen() + ", " + act.color.getBlue() + ");");
//				bar.setStyle("-fx-border-color: rgb("+ act.color.getRed() + ", " + act.color.getGreen() + ", " + act.color.getBlue() + ");");
//			}
		}


//		lblDuration.setStyle("-fx-background-color:#FF0000");
		lblDuration.setText("" + df.format((this.cedReference.getEndTimestamp2msecs()-this.cedReference.getTimestamp2msecs())) + " ms");
		lblDuration.resizeRelocate(closeOffset-lowOffset*0.25, highOffset-candleWidth, lowOffset*1.5, candleWidth);


		// System.out.println("Real s : "+ closeOffset + ", e : " + (closeOffset
		// + lowOffset));

//		if (internalExtraInformation != null) {
//			if (!getChildren().contains(stdRegion)) {
//				getChildren().add(stdRegion);
//			}
//
			double mDuration = this.cedReference.parentReference.getMeanDuration() * 1e-6;
			double minStdDuration = this.cedReference.parentReference.getStdDuration() * 1e-6;
			if (mDuration > 0.0 && minStdDuration > 0.0) {
				double stdST2msec = xAxis.getDisplayPosition(cedReference.getTimestamp2msecs() + mDuration - minStdDuration) - cedST2msec;
				double stdET2msec = xAxis.getDisplayPosition(cedReference.getTimestamp2msecs() + mDuration + minStdDuration) - cedST2msec - stdST2msec;
				double meanDuration2msec = xAxis.getDisplayPosition(cedReference.getTimestamp2msecs() + mDuration) - cedST2msec;

				meanDurationLine.setStartX(meanDuration2msec);
				meanDurationLine.setEndX(meanDuration2msec);
				meanDurationLine.setStartY(-2.5);
				meanDurationLine.setEndY(5+2.5);

				stdRegion.resizeRelocate(stdST2msec, 0, stdET2msec, 5);
				stdRegion.toBack();
				meanDurationLine.toFront();
				stdRegion.setVisible(true);
				meanDurationLine.setVisible(true);
			} else {
				stdRegion.setVisible(false);
				meanDurationLine.setVisible(false);
			}

//			double mDuration = xAxis.getDisplayPosition(this.cedReference.parentReference.getMeanDuration() * 1e-6);
//			double minStdDuration = xAxis.getDisplayPosition(this.cedReference.parentReference.getStdDuration() * 1e-6);
//
//			double endPoint_inChartCoord = (closeOffset + lowOffset);
//			// System.out.println("endPoint_inChartCoord = " +
//			// endPoint_inChartCoord);
//			// System.out.println("startPoint_inChartCoord = " + closeOffset);
//			// System.out.println("duration_inChartCoord = " + lowOffset);
//
//			if (use_msecs) {
//				mDuration = xAxis.getDisplayPosition(internalExtraInformation.getMeanDuration() * 1e-6);
//				minStdDuration = xAxis.getDisplayPosition(internalExtraInformation.getStdDuration() * 1e-6);
//			} else {
//				mDuration = xAxis.getDisplayPosition(internalExtraInformation.getMeanDuration());
//				minStdDuration = xAxis.getDisplayPosition(internalExtraInformation.getStdDuration());
//			}
//			//
//			// double dX = minStdDuration * 2;
//			// if (dX < 1) {
//			// tBlock.update(0, -10, 1, 20, xAxis, true);
//			// } else {
//			// tBlock.update(0, -10, dX, 20, xAxis, true);
//			// }
//			// double minDurPoint_inChartCoord = closeOffset + ;
//
//			// System.out.println("length " + minStdDuration * 2);
//
//			stdRegion.resizeRelocate(mDuration - minStdDuration, 15, minStdDuration * 2, 10);
//
//			// System.out.println("mean : " +
//			// internalExtraInformation.getMeanDuration() * 1e-6 + " std : " +
//			// internalExtraInformation.getStdDuration() * 1e-6);
//			// System.out.println("meanG : " + mDuration + " stdG : " +
//			// minStdDuration);
//			// stdRegion.resizeRelocate(-10, -15, 10, 15);
//
//		} else if (getChildren().contains(stdRegion)) {
//			getChildren().remove(stdRegion);
//		}
	}

	public void updateTooltip(String name, String container, double startTimeMS, double endTimeMS) {
		TimingBlockTooltip tooltipContent = (TimingBlockTooltip) tooltip.getGraphic();
		tooltipContent.update(name, container, startTimeMS, endTimeMS);
	}

	private void updateStyleClasses() {
		getStyleClass().setAll("candlestick-candle");

		bar.getStyleClass().setAll("candlestick-bar");

		wmeDuration.getStyleClass().setAll("wmed-bar");

		stdRegion.getStyleClass().setAll("stdregion-bar");
		meanDurationLine.getStyleClass().setAll("mean-duration-line");

		ComponentSpecification csp = cedReference.parentReference.parentComponent.getComponentSpecs();
		if (csp != null) {
			ActivitySpecification act = csp.activity;
			if (act != null && act.color != null) {
				bar.setStyle("-fx-border-color: " + act.color.toString().replaceFirst("0x", "#"));
			} else {
				bar.setStyle("-fx-border-color: #000000");
			}
		} else {
			bar.setStyle("-fx-border-color: #000000");
		}
	}
}
