package com.rtree.core.rtree;

public final class Statistics {

    private final long count;
    private final double sumX;
    private final double sumX2;

    private Statistics(long count, double sumX, double sumX2) {
        this.count = count;
        this.sumX = sumX;
        this.sumX2 = sumX2;
    }

    public static Statistics create() {
        return new Statistics(0, 0, 0);
    }

    public Statistics add(Number number) {
        return new Statistics(count + 1, sumX + number.doubleValue(), sumX2 + number.doubleValue() * number.doubleValue());
    }

    public long count() {
        return count;
    }

    private double sum() {
        return sumX;
    }

    public double sumSquares() {
        return sumX2;
    }

    private double mean() {
        return sumX / count;
    }

    private double sd() {
        return Math.sqrt(sumX2 / count - Math.pow(mean(), 2));
    }

    @Override
    public String toString() {
        return "Statistics [count=" +
                count +
                ", sum=" +
                sum() +
                ", mean=" +
                mean() +
                ", sd=" +
                sd() +
                "]";
    }
}
