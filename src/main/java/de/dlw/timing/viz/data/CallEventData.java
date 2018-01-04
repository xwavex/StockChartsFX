package de.dlw.timing.viz.data;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class CallEventData extends TimingData {
	private static final long serialVersionUID = -7474169739641660644L;

	public ComponentCallData parentReference = null;

	protected String containerName;
	protected long endTimestamp;

    public CallEventData() {

    }

    public CallEventData(String name, String containerName, long timestamp, long endTimestamp) {
		this.name = name;
		this.timestamp = timestamp;
		this.containerName = containerName;
		this.endTimestamp = endTimestamp;
    }

//    /**
//     * Updates the last price, adjusting the high and low
//     * @param close The last price
//     */
//    public void update( double close ) {
//        if( close > high ) {
//            high = close;
//        }
//
//        if( close < low ) {
//            low = close;
//        }
//        this.close = close;
//    }

//    protected BigDecimal format( double price ) {
//        return BigDecimal.ZERO;
//    }


	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Container Name: ").append(containerName);
        sb.append(" Name: ").append(name);
        sb.append(" Timestamp: ").append(timestamp);
        sb.append(" End Timestamp: ").append(endTimestamp);
        return sb.toString();
    }

    public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public double getEndTimestamp2msecs() {
		return endTimestamp * 1e-6;
	}

	@Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        long temp = timestamp;
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        for (int i = 0; i < name.length(); i++) {
        	result = PRIME * result + (int) (name.charAt(i) ^ (name.charAt(i) >>> 32));
        }
        for (int i = 0; i < containerName.length(); i++) {
        	result = PRIME * result + (int) (containerName.charAt(i) ^ (containerName.charAt(i) >>> 32));
        }
        result = PRIME * result + (int) (endTimestamp ^ (endTimestamp >>> 32));
        return result;
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
        final CallEventData other = (CallEventData) obj;

        if (timestamp != other.timestamp) {
            return false;
        }
        if (!name.equals(other.name)) {
            return false;
        }
        if (!containerName.equals(other.containerName)) {
            return false;
        }
        if (endTimestamp != other.endTimestamp) {
            return false;
        }
        return true;
    }

}
