package hk.edu.cpce.gp4701.admissions;

public class QuotaStat {
    private final String programmeName;
    private final Byte level;
    private final int seatLimit;
    private final long occupied;
    private final long remaining;

    public QuotaStat(String programmeName, Byte level, int seatLimit, long occupied) {
        this.programmeName = programmeName;
        this.level = level;
        this.seatLimit = seatLimit;
        this.occupied = occupied;
        this.remaining = Math.max(0, seatLimit - occupied);
    }

    public String getProgrammeName() {
        return programmeName;
    }

    public Byte getLevel() {
        return level;
    }

    public int getSeatLimit() {
        return seatLimit;
    }

    public long getOccupied() {
        return occupied;
    }

    public long getRemaining() {
        return remaining;
    }
}