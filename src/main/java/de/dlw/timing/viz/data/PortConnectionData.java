package de.dlw.timing.viz.data;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class PortConnectionData extends TimingData {

	private static final long serialVersionUID = -5444442834027725625L;

	public PortEventData source;
	public PortEventData target;

	public PortConnectionData(PortEventData source, PortEventData target) {
		this.source = source;
		this.target = target;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("source: ").append(source);
        sb.append("target: ").append(target);
        return sb.toString();
    }

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp = source.getTimestamp();
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		temp = target.getTimestamp();
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		result = PRIME * result + source.hashCode();
		result = PRIME * result + target.hashCode();
		return result;
	}

	@Override
	public long getTimestamp() {
		return source.getTimestamp();
	}

	public PortEventData getSource() {
		return source;
	}

	public void setSource(PortEventData source) {
		this.source = source;
	}

	public PortEventData getTarget() {
		return target;
	}

	public void setTarget(PortEventData target) {
		this.target = target;
	}

	@Override
	public double getTimestamp2msecs() {
		return source.getTimestamp2msecs();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PortConnectionData other = (PortConnectionData) obj;
		if (source != other.source) {
			return false;
		}
		if (target != other.target) {
			return false;
		}
		return true;
	}

}
