package de.dlw.timing.viz.viewmodel;

import de.dlw.timing.viz.viewmodel.tooltip.TimingBlockTooltip;
import javafx.geometry.Pos;
import javafx.scene.Group;
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

        public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth) {
            openAboveClose = closeOffset > 0;
            updateStyleClasses();
//            highLowLine.setStartY(highOffset);
//            highLowLine.setEndY(lowOffset);
            if (candleWidth == -1) {
                candleWidth = bar.prefWidth(-1);
            }
            if (openAboveClose) {
                bar.resizeRelocate(-candleWidth / 2, 0, candleWidth, closeOffset);
            } else {
                bar.resizeRelocate(-candleWidth / 2, closeOffset, candleWidth, closeOffset * -1);
            }

            lblName.toFront();
            lblName.resizeRelocate(closeOffset, highOffset, lowOffset, candleWidth);
            lblName.setAlignment(Pos.CENTER);

            bar.resizeRelocate(closeOffset, highOffset, lowOffset, candleWidth);
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
        }
}
