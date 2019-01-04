package edu.iit.llu25hawk.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder>
{
    private ArrayList<StockInfo> stockInfoList;
    private MainActivity mainAct;

    public StockAdapter(ArrayList<StockInfo> list, MainActivity ma)
    {
        this.mainAct = ma;
        stockInfoList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_info, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        StockInfo stockInfo = stockInfoList.get(position);
        String companyName = stockInfo.getCompanyName();
        int i = companyName.indexOf("Corp.");
        int j = companyName.indexOf("Corporation");
        if(i > 0)
        {
            String str1 = companyName.substring(0, i);
            String str2 = companyName.substring(i, companyName.length());
            companyName = str1 + "\n" + str2;
        }
        if(j > 0)
        {
            String str1 = companyName.substring(0, j);
            String str2 = companyName.substring(j, companyName.length());
            companyName = str1 + "\n" + str2;
        }

        holder.companyName.setText(companyName);
        holder.stockName.setText(stockInfo.getStockName());
        holder.stockPrice.setText(String.format("%.2f", stockInfo.getStockPrice()));

        if(stockInfo.getChangeRate() < 0)
        {
            holder.stockChange.setText(String.format("▼ "+"%.2f", stockInfo.getPriceChange())
                    + "(" + String.format("%.2f", stockInfo.getChangeRate()) + "%)");
            holder.stockName.setTextColor(Color.parseColor("#FF0000"));
            holder.stockPrice.setTextColor(Color.parseColor("#FF0000"));
            holder.stockChange.setTextColor(Color.parseColor("#FF0000"));
            holder.companyName.setTextColor(Color.parseColor("#FF0000"));
        }
        else if(stockInfo.getChangeRate() > 0)
            {
                holder.stockChange.setText(String.format("▲ "+"%.2f", stockInfo.getPriceChange())
                        + "(+" + String.format("%.2f", stockInfo.getChangeRate()) + "%)");
                holder.stockName.setTextColor(Color.parseColor("#32CD32"));
                holder.stockPrice.setTextColor(Color.parseColor("#32CD32"));
                holder.stockChange.setTextColor(Color.parseColor("#32CD32"));
                holder.companyName.setTextColor(Color.parseColor("#32CD32"));
            }
            else
                {
                    holder.stockChange.setText(String.format("-- "+"%.2f", stockInfo.getPriceChange())
                            + "(" + String.format("%.2f", stockInfo.getChangeRate()) + "%)");
                    holder.stockName.setTextColor(Color.parseColor("#32CD32"));
                    holder.stockPrice.setTextColor(Color.parseColor("#32CD32"));
                    holder.stockChange.setTextColor(Color.parseColor("#32CD32"));
                    holder.companyName.setTextColor(Color.parseColor("#32CD32"));
                }

    }

    @Override
    public int getItemCount() { return stockInfoList.size(); }
}
