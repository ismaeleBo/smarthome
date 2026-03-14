package com.ismaelebonaventura.analytics_service.aggregation;

public class AggregationBucket {

    private double totalConsumptionKwh;
    private double totalCost;
    private int count;

    public void add(double consumptionKwh, double pricePerKwh) {
        this.totalConsumptionKwh += consumptionKwh;
        this.totalCost += consumptionKwh * pricePerKwh;
        this.count++;
    }

    public double getTotalConsumptionKwh() {
        return totalConsumptionKwh;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int getCount() {
        return count;
    }

    public double getAverageConsumptionKwh() {
        return count == 0 ? 0 : totalConsumptionKwh / count;
    }

    public double getAverageCost() {
        return count == 0 ? 0 : totalCost / count;
    }
}