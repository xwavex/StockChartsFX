package de.dlw.timing.viz.data;

import java.io.Serializable;

/**
 *
 * @author Dennis Leroy Wigand
 */
public class TimingData implements Serializable {

    public static long serialVersionUID = 1L;

//    public static final double NULL = -9D;

//    public enum LENGTH_UNIT {
//
//        TICK, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
//    };

//    protected GregorianCalendar dateTime;

    protected String name;
    protected long timestamp;

    public TimingData() {

    }

    public TimingData(String name, long timestamp) {
    	this.name = name;
    	this.timestamp = timestamp;
    }

//    public TimingData(GregorianCalendar dateTime, double open, double high, double low, double close, long volume, long openInterest) {
//        this(dateTime, open, high, low, close, volume);
//        this.openInterest = openInterest;
//    }//constructor()

//    public GregorianCalendar getDateTime() {
//        return dateTime;
//    }
//
//    public void setDateTime(GregorianCalendar dateTime) {
//        this.dateTime = dateTime;
//    }

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

	public double getTimestamp2msecs() {
		return timestamp * 1e-6;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name);
        sb.append(" Timestamp: ").append(timestamp);
        return sb.toString();
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
//        temp = Double.doubleToLongBits(openInterest);
//        result = PRIME * result + (int) (temp ^ (temp >>> 32));
//        result = PRIME * result + ((dateTime == null) ? 0 : dateTime.hashCode());
//        result = PRIME * result + (int) (volume ^ (volume >>> 32));
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
        final TimingData other = (TimingData) obj;

        if (timestamp != other.timestamp) {
            return false;
        }
        if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public static double nsecs2msecs(long nsecs) {
    	return ((double)nsecs) * 1e-6;
    }
}
