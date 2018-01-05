package de.dlw.timing.viz.viewmodel.tooltip;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TimingPortTooltip extends GridPane {

    private final Label port_name = new Label();
    private final Label port_time = new Label();
    private final Label port_type = new Label();

    public TimingPortTooltip() {
        Label name = new Label("Port:");
        Label type = new Label("Type:");
        Label time = new Label("Time:");
        name.getStyleClass().add("candlestick-tooltip-label");
        type.getStyleClass().add("candlestick-tooltip-label");
        time.getStyleClass().add("candlestick-tooltip-label");
        setConstraints(name, 0, 0);
        setConstraints(port_name, 1, 0);
        setConstraints(type, 0, 1);
        setConstraints(port_type, 1, 1);
        setConstraints(time, 0, 2);
        setConstraints(port_time, 1, 2);
        getChildren().addAll(name, port_name, type, port_type, time, port_time);
    }

    public void update(String name, String type, double time) {
        port_name.setText(name);
        port_type.setText(type);
        port_time.setText(Double.toString(time) + " ms");
    }
}