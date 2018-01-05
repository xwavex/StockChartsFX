package de.dlw.timing.viz.viewmodel.tooltip;

import javafx.scene.control.Tooltip;

public class ToolTipAlive extends Tooltip {
	private boolean keepAlive = false;

	public ToolTipAlive() {
		setAutoHide(false);
	}

	@Override
	public void hide() {
		if (!keepAlive) {
			super.hide();
		}
	}

	public void doHide() {
		super.hide();
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
}
