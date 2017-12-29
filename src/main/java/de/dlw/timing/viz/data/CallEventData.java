package de.dlw.timing.viz.data;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class CallEventData extends TimingData {
//	{"call_name":"updateHook()","container_name":"ib","call_time":1513785953570310542,"call_duration":1513785953570323730,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib","call_time":1513785953670314392,"call_duration":0,"call_type":"CALL_PORT_WRITE"}
//
//	{"call_name":"updateHook()","container_name":"ib","call_time":1513785953670304084,"call_duration":1513785953670322803,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib","call_time":1513785953770317835,"call_duration":0,"call_type":"CALL_PORT_WRITE"}
//
//	{"call_name":"updateHook()","container_name":"ib","call_time":1513785953770312749,"call_duration":1513785953770326416,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib","call_time":1513785953870303928,"call_duration":0,"call_type":"CALL_PORT_WRITE"}
//
//	{"call_name":"updateHook()","container_name":"ib","call_time":1513785953870299000,"call_duration":1513785953870312202,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib","call_time":1513785953970241186,"call_duration":0,"call_type":"CALL_PORT_WRITE"}
//
//	{"call_name":"updateHook()","container_name":"ib2","call_time":1513785953572173848,"call_duration":1513785953572184113,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib2","call_time":1513785953672235242,"call_duration":0,"call_type":"CALL_PORT_WRITE"}
//
//	{"call_name":"updateHook()","container_name":"ib2","call_time":1513785953672232511,"call_duration":1513785953672239432,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib2","call_time":1513785953772435861,"call_duration":0,"call_type":"CALL_PORT_WRITE"}
//
//	{"call_name":"updateHook()","container_name":"ib2","call_time":1513785953772430529,"call_duration":1513785953772444672,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib2","call_time":1513785953872169257,"call_duration":0,"call_type":"CALL_PORT_WRITE"}
//
//	{"call_name":"updateHook()","container_name":"ib2","call_time":1513785953872165545,"call_duration":1513785953872174813,"call_type":"CALL_START_WITH_DURATION"}
//
//	{"call_name":"port_access","container_name":"ib2","call_time":1513785953972174661,"call_duration":0,"call_type":"CALL_PORT_WRITE"}

//    public static final double NULL = -9D;

//    public enum LENGTH_UNIT {
//
//        TICK, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
//    };

	private static final long serialVersionUID = -7474169739641660644L;


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


    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

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
