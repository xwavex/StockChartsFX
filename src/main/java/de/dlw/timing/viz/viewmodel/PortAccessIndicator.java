package de.dlw.timing.viz.viewmodel;

import de.dlw.timing.viz.data.PortEventData;
import de.dlw.timing.viz.data.PortEventData.CallPortType;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

public class PortAccessIndicator extends Group {

	private final Line highLowLine = new Line();
	private final Region bar = new Region();
	private String seriesStyleClass;
	private String dataStyleClass;
	private boolean inputport = true;
	protected PortEventData portEventData;
	// private final Tooltip tooltip = new Tooltip();

	public PortAccessIndicator(String seriesStyleClass, String dataStyleClass, PortEventData pedRef) {
		setAutoSizeChildren(false);
		getChildren().addAll(bar, highLowLine);
		this.seriesStyleClass = seriesStyleClass;
		this.dataStyleClass = dataStyleClass;
		this.portEventData = pedRef;
		updateStyleClasses();
		// tooltip.setGraphic(new TooltipContent());
		// Tooltip.install(bar, tooltip);
	}

	public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass, PortEventData pedRef) {
		this.seriesStyleClass = seriesStyleClass;
		this.dataStyleClass = dataStyleClass;
		this.portEventData = pedRef;
		updateStyleClasses();
	}

	public void update() {
		// public void update(double highOffset, double lowOffset) {
		bar.resizeRelocate(0, 5, 15, 15);
		if (this.inputport) {
			highLowLine.setStartY(-20);
			highLowLine.setEndY(0);
		} else {
			highLowLine.setStartY(0);
			highLowLine.setEndY(20);
		}

	}

	// public void updateTooltip(double open, double close, double high, double
	// low) {
	// TooltipContent tooltipContent = (TooltipContent) tooltip.getGraphic();
	// tooltipContent.update(open, close, high, low);
	// }

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
