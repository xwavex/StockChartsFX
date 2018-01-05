package de.dlw.timing.viz.viewmodel;

import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.PortEventData.CallPortType;
import de.dlw.timing.viz.viewmodel.tooltip.TimingPortTooltip;
import de.dlw.timing.viz.viewmodel.tooltip.ToolTipAlive;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

public class PortAccessIndicator extends Group {

	private final Line highLowLine = new Line();
	private boolean inputport = true;

	protected PortEventData portEventData;

	private double maxStrokeWidth = 5.0;
	private double minStrokeWidth = 0.8;
	private double maxResolution = 10.0; // ms

	private double currentblockheight = 0.0;

	private final ToolTipAlive tooltip = new ToolTipAlive();
	private TimingPortTooltip tooltipContent;

	private TimingPortTooltip additionalInformation;

	public PortAccessIndicator(PortEventData pedRef) {
		setAutoSizeChildren(false);
		additionalInformation = new TimingPortTooltip();
		additionalInformation.setVisible(false);
		additionalInformation.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				additionalInformation.setVisible(false);
			}
		});

		getChildren().addAll(highLowLine, additionalInformation);
		this.portEventData = pedRef;
		updateStyleClasses();

		tooltipContent = new TimingPortTooltip();
		tooltip.setGraphic(tooltipContent);
		updateTooltip();
		Tooltip.install(highLowLine, tooltip);


		highLowLine.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (additionalInformation.isVisible()) {
					additionalInformation.setVisible(false);
				} else {
					updateTooltip();
					additionalInformation.resizeRelocate(0, currentblockheight, 250, 60);
					additionalInformation.setVisible(true);
				}
			}
		});
	}

	public void setSeriesAndDataStyleClasses(PortEventData pedRef) {
		this.portEventData = pedRef;
		updateStyleClasses();
	}

	public void update(double blockheight, NumberAxis xAxis) {
		currentblockheight = blockheight;
		if (this.inputport) {
			highLowLine.setStartY(-currentblockheight * 1.5);
			highLowLine.setEndY(-currentblockheight * 0.5);
		} else {
			highLowLine.setStartY(-currentblockheight * 0.5);
			highLowLine.setEndY(currentblockheight * 0.5);
		}

		// 10 ms = 100% max scale
		double strokeWidth = maxStrokeWidth * (maxResolution / (xAxis.getUpperBound() - xAxis.getLowerBound()));
		if (strokeWidth < minStrokeWidth) {
			strokeWidth = minStrokeWidth;
		} else if (strokeWidth > maxStrokeWidth) {
			strokeWidth = maxStrokeWidth;
		}
		highLowLine.setStrokeWidth(strokeWidth);
	}

	public void updateTooltip() {
		TimingPortTooltip tooltipContent = (TimingPortTooltip) tooltip.getGraphic();
		tooltipContent.update(portEventData.getName(), portEventData.getCallType().toString(),
				portEventData.getTimestamp2msecs());
		if (additionalInformation != null) {
			additionalInformation.update(portEventData.getName(), portEventData.getCallType().toString(),
					portEventData.getTimestamp2msecs());
		}
	}

	private void updateStyleClasses() {
		// TODO color for data
		switch (this.portEventData.getCallType()) {
		case CALL_PORT_READ_NODATA:
			highLowLine.getStyleClass().setAll("port", "port-input", "port-nodata");
			this.inputport = true;
			break;
		case CALL_PORT_READ_OLDDATA:
			highLowLine.getStyleClass().setAll("port", "port-input", "port-olddata");
			this.inputport = true;
			break;
		case CALL_PORT_READ_NEWDATA:
			highLowLine.getStyleClass().setAll("port", "port-input", "port-newdata");
			this.inputport = true;
			break;
		case CALL_PORT_WRITE:
			highLowLine.getStyleClass().setAll("port", "port-output");
			this.inputport = false;
			break;
		default:
			// Should never happen!
			highLowLine.getStyleClass().setAll("port", "port-output");
			break;
		}
	}
}
