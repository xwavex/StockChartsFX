package de.dlw.timing.viz.viewmodel;

import de.dlw.timing.viz.data.CallEventData;
import de.dlw.timing.viz.data.ComponentCallData;
import de.dlw.timing.viz.viewmodel.tooltip.TimingBlockTooltip;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.Axis;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;

public class TimingBlock extends Group {

//        private final Line highLowLine = new Line();
        private final Region bar = new Region();
        private String seriesStyleClass;
        private String dataStyleClass;
        private boolean openAboveClose = true;
        private final Tooltip tooltip = new Tooltip();
        private Label lblName;

        private final Region stdRegion = new Region();

        private ComponentCallData internalExtraInformation = null;

        public TimingBlock(String seriesStyleClass, String dataStyleClass, String name, String container, long startTime, long endTime) {
            setAutoSizeChildren(false);
            lblName = new Label(name);
            getChildren().addAll(bar, lblName);
            this.seriesStyleClass = seriesStyleClass;
            this.dataStyleClass = dataStyleClass;
            updateStyleClasses();
            TimingBlockTooltip tmpToolTip = new TimingBlockTooltip();
            tmpToolTip.update(name, container, startTime, endTime);
            tooltip.setGraphic(tmpToolTip);
            Tooltip.install(bar, tooltip);
            Tooltip.install(lblName, tooltip);
        }

        public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass) {
            this.seriesStyleClass = seriesStyleClass;
            this.dataStyleClass = dataStyleClass;
            updateStyleClasses();
        }

        public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth, Axis<Number> xAxis, boolean use_msecs) {
            openAboveClose = closeOffset > 0;
            updateStyleClasses();
//            highLowLine.setStartY(highOffset);
//            highLowLine.setEndY(lowOffset);
//            if (candleWidth == -1) {
//                candleWidth = bar.prefWidth(-1);
//            }
//            if (openAboveClose) {
//                bar.resizeRelocate(-candleWidth / 2, 0, candleWidth, closeOffset);
//            } else {
//                bar.resizeRelocate(-candleWidth / 2, closeOffset, candleWidth, closeOffset * -1);
//            }

            lblName.toFront();
            lblName.resizeRelocate(closeOffset, highOffset, lowOffset, candleWidth);
            lblName.setAlignment(Pos.CENTER);

            bar.resizeRelocate(closeOffset, highOffset, lowOffset, candleWidth);


//            System.out.println("Real s : "+ closeOffset + ", e : " + (closeOffset + lowOffset));

            if (internalExtraInformation != null) {
        		if (!getChildren().contains(stdRegion)) {
            		getChildren().add(stdRegion);
            	}

            	stdRegion.toFront();

            	double mDuration = 0.0;
            	double minStdDuration = 0.0;

            	double endPoint_inChartCoord = (closeOffset + lowOffset);
//            	System.out.println("endPoint_inChartCoord = " + endPoint_inChartCoord);
//            	System.out.println("startPoint_inChartCoord = " + closeOffset);
//            	System.out.println("duration_inChartCoord = " + lowOffset);

            	if (use_msecs) {
            		mDuration = xAxis.getDisplayPosition(internalExtraInformation.getMeanDuration() * 1e-6);
            		minStdDuration = xAxis.getDisplayPosition(internalExtraInformation.getStdDuration() * 1e-6);
            	} else {
            		mDuration = xAxis.getDisplayPosition(internalExtraInformation.getMeanDuration());
            		minStdDuration = xAxis.getDisplayPosition(internalExtraInformation.getStdDuration());
            	}
//
//            	double dX = minStdDuration * 2;
//				if (dX < 1) {
//					tBlock.update(0, -10, 1, 20, xAxis, true);
//				} else {
//					tBlock.update(0, -10, dX, 20, xAxis, true);
//				}
//            	double minDurPoint_inChartCoord = closeOffset + ;

//            	System.out.println("length " + minStdDuration * 2);

            	stdRegion.resizeRelocate(mDuration-minStdDuration, 15, minStdDuration * 2, 10);

//            	System.out.println("mean : " + internalExtraInformation.getMeanDuration() * 1e-6 + " std : " + internalExtraInformation.getStdDuration() * 1e-6);
//            	System.out.println("meanG : " + mDuration + " stdG : " + minStdDuration);
//            	stdRegion.resizeRelocate(-10, -15, 10, 15);

            } else if (getChildren().contains(stdRegion)) {
            	getChildren().remove(stdRegion);
            }
        }

        public void updateTooltip(String name, String container, long startTime, long endTime) {
        	TimingBlockTooltip tooltipContent = (TimingBlockTooltip) tooltip.getGraphic();
            tooltipContent.update(name, container, startTime, endTime);
        }

        private void updateStyleClasses() {
            getStyleClass().setAll("candlestick-candle", seriesStyleClass, dataStyleClass);
//            highLowLine.getStyleClass().setAll("candlestick-line", seriesStyleClass, dataStyleClass,
//                    openAboveClose ? "open-above-close" : "close-above-open");
            bar.getStyleClass().setAll("candlestick-bar", seriesStyleClass, dataStyleClass,
                    openAboveClose ? "open-above-close" : "close-above-open");

            stdRegion.getStyleClass().setAll("stdregion-bar");
        }

		public ComponentCallData getInternalExtraInformation() {
			return internalExtraInformation;
		}

		public void setInternalExtraInformation(ComponentCallData internalExtraInformation) {
			this.internalExtraInformation = internalExtraInformation;
		}
}
