package edu.iit.llu25hawk.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder
{
    public TextView stockName;
    public TextView companyName;
    public TextView stockPrice;
    public TextView stockChange;

    public MyViewHolder(View view)
    {
        super(view);
        stockName = view.findViewById(R.id.stock_name);
        companyName = view.findViewById(R.id.company_name);
        stockPrice = view.findViewById(R.id.stock_price);
        stockChange = view.findViewById(R.id.stock_change);
    }
}
