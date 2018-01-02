package de.dlw.timing.viz.viewmodel.tooltip;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TimingBlockTooltip extends GridPane {

    private final Label call_name = new Label();
    private final Label call_container = new Label();
    private final Label start_time = new Label();
    private final Label end_time = new Label();

    public TimingBlockTooltip() {
        Label name = new Label("Call:");
        Label container = new Label("Comp:");
        Label start = new Label("sTime:");
        Label end = new Label("eTime:");
        name.getStyleClass().add("candlestick-tooltip-label");
        container.getStyleClass().add("candlestick-tooltip-label");
        start.getStyleClass().add("candlestick-tooltip-label");
        end.getStyleClass().add("candlestick-tooltip-label");
        setConstraints(name, 0, 0);
        setConstraints(call_name, 1, 0);
        setConstraints(container, 0, 1);
        setConstraints(call_container, 1, 1);
        setConstraints(start, 0, 2);
        setConstraints(start_time, 1, 2);
        setConstraints(end, 0, 3);
        setConstraints(end_time, 1, 3);
        getChildren().addAll(name, call_name, container, call_container, start, start_time, end, end_time);
    }

    public void update(String name, String container, long startTime, long endTime) {
        call_name.setText(name);
        call_container.setText(container);
        start_time.setText(Long.toString(startTime));
        end_time.setText(Long.toString(endTime));
    }
}