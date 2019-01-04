package edu.iit.llu25hawk.stockwatch;

import java.io.Serializable;

public class StockInfo implements Serializable, Comparable<StockInfo>
{
    private String stockName;
    public double stockPrice;
    private String companyName;
    public double changeRate;
    public double priceChange;

    public StockInfo(String stockName, double stockPrice,
                     String companyName, double changeRate, double priceChange)
    {
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.companyName = companyName;
        this.changeRate = changeRate;
        this.priceChange = priceChange;
    }

    public StockInfo(String stockName, String companyName)
    {
        this.stockName = stockName;
        this.stockPrice = 0;
        this.companyName = companyName;
        this.changeRate = 0;
        this.priceChange = 0;
    }

    public String getStockName() {
        return stockName;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getChangeRate() {
        return changeRate;
    }

    public double getPriceChange() {
        return priceChange;
    }

    @Override
    public int compareTo(StockInfo o)
    {
        return getStockName().compareTo(o.getStockName());
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof StockInfo)) return false;

        StockInfo stockInfo = (StockInfo) o;

        return getStockName() != null ?
                getStockName().equals(stockInfo.getStockName()) : stockInfo.stockName == null;
    }

    @Override
    public int hashCode()
    {
        return getStockName() != null ? getStockName().hashCode() : 0;
    }
}
