package de.dlw.timing.viz.viewmodel;

import de.dlw.timing.viz.data.PortConnectionData;
import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.PortEventData.CallPortType;
import de.dlw.timing.viz.viewmodel.tooltip.TimingPortTooltip;
import de.dlw.timing.viz.viewmodel.tooltip.ToolTipAlive;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

public class PortConnection extends Group {

	private final Line highLowLine = new Line();

	protected PortConnectionData portConnectionData;

	private double maxStrokeWidth = 2.0;
	private double minStrokeWidth = 1.0;
	private double maxResolution = 1.0; // ms

	private double currentblockheight = 0.0;

	// private final ToolTipAlive tooltip = new ToolTipAlive();
	// private TimingPortTooltip tooltipContent;

	// private TimingPortTooltip additionalInformation;

	public PortConnection(PortConnectionData pcdRef) {
		setAutoSizeChildren(false);
		// additionalInformation = new TimingPortTooltip();
		// additionalInformation.setVisible(false);
		// additionalInformation.setOnMouseClicked(new
		// EventHandler<MouseEvent>() {
		// public void handle(MouseEvent event) {
		// additionalInformation.setVisible(false);
		// }
		// });

		getChildren().addAll(highLowLine);
		this.portConnectionData = pcdRef;
		updateStyleClasses();

		// tooltipContent = new TimingPortTooltip();
		// tooltip.setGraphic(tooltipContent);
		// updateTooltip();
		// Tooltip.install(highLowLine, tooltip);
	}

	public void setSeriesAndDataStyleClasses(PortConnectionData pcdRef) {
		this.portConnectionData = pcdRef;
		updateStyleClasses();
	}

	public void update(double blockheight, NumberAxis xAxis, CategoryAxis yAxis) {
		currentblockheight = blockheight;

		// check if bold line or not.
		if (this.portConnectionData.getTarget().portConnectionDataRefs.indexOf(
				this.portConnectionData) != (this.portConnectionData.getTarget().portConnectionDataRefs.size() - 1)) {
			highLowLine.getStyleClass().setAll("port-connection", "normal");
		} else {
			highLowLine.getStyleClass().setAll("port-connection", "last");
		}

		double startMeX = xAxis.getDisplayPosition(portConnectionData.getSource().getTimestamp2msecs());
		double startMeY = yAxis.getDisplayPosition(portConnectionData.getSource().getContainerName());

		// if (portConnectionData.getSource().getContainerName().equals("fkin"))
		// {
		// for (int i = 0; i <
		// portConnectionData.getTarget().portConnectionDataRefs.size(); i++) {
		// System.err.println(portConnectionData.getTarget().portConnectionDataRefs.get(i).getTimestamp());
		// }
		// }
		// System.out.println("Lining " +
		// portConnectionData.getSource().getContainerName() + "." +
		// portConnectionData.getSource().getName() + " with " +
		// portConnectionData.getTarget().getContainerName() + "." +
		// portConnectionData.getTarget().getName());
		highLowLine.setStartX(0.0);

		// highLowLine.setEndX(0.0);
		// yAxis.getDisplayPosition(portConnectionData.getTarget().getContainerName())

		highLowLine.setEndX(xAxis.getDisplayPosition(portConnectionData.getTarget().getTimestamp2msecs()) - startMeX);
		highLowLine.setEndY(yAxis.getDisplayPosition(portConnectionData.getTarget().getContainerName()) - startMeY
				- currentblockheight * 1.5);

		highLowLine.setStartY(currentblockheight * 0.5);
		// } else {
		// highLowLine.setStartY(0.0);
		// highLowLine.setStartX(0.0);
		// highLowLine.setEndX(0.0);
		// highLowLine.setEndY(0.0);
		// }

		// 10 ms = 100% max scale
		double strokeWidth = maxStrokeWidth * (maxResolution / (xAxis.getUpperBound() - xAxis.getLowerBound()));
		if (strokeWidth < minStrokeWidth) {
			strokeWidth = minStrokeWidth;
		} else if (strokeWidth > maxStrokeWidth) {
			strokeWidth = maxStrokeWidth;
		}
		highLowLine.setStrokeWidth(strokeWidth);

		highLowLine.toFront();
	}

	// public void updateTooltip() {
	// TimingPortTooltip tooltipContent = (TimingPortTooltip)
	// tooltip.getGraphic();
	// tooltipContent.update(portEventData.getName(),
	// portEventData.getCallType().toString(),
	// portEventData.getTimestamp2msecs());
	// if (additionalInformation != null) {
	// additionalInformation.update(portEventData.getName(),
	// portEventData.getCallType().toString(),
	// portEventData.getTimestamp2msecs());
	// }
	// }

	private void updateStyleClasses() {

	}
}
