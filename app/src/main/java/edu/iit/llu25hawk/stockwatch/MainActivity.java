package edu.iit.llu25hawk.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener

{
    private ArrayList<StockInfo> stockInfoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private StockAdapter mAdapter;
    // RecyclerView's position.
    private int position;
    private static final String STOCK_WEB = "http://www.marketwatch.com/investing/stock/";


    MainActivity ma;
    private static final String TAG = "MainActivity";

    HashMap<String, String> allStockInfo = new HashMap<>();
    UserStockDatabase user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ma = this;

        swiper = findViewById(R.id.swiper_view);
        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new StockAdapter(stockInfoList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        user = new UserStockDatabase(this);
        ArrayList<StockInfo> temp = user.loadStockInfo();
        if(doNetCheck())
        {
            for(int i = 0; i < temp.size(); i++)
            {
                getFullInfo(temp.get(i));
            }
        }
        else
            {
                showWarning("No network connection","Please connect to the internet!");
                stockInfoList.addAll(temp);
                Collections.sort(stockInfoList);
            }

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateStockInfo();
            }
        });

        new DownloadAsyncTask(this).execute();
    }

    //Methods about RecyclerView. Remove item form arrayList.
    private void removeItem(int position)
    {
        stockInfoList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    //Open a website form outside by using Android browser.
    @Override
    public void onClick(View v)
    {
        position = recyclerView.getChildAdapterPosition(v);
        String symbol = stockInfoList.get(position).getStockName();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(STOCK_WEB + symbol));
        startActivity(intent);
    }

    //On long click delete item form arrayList(RecyclerView)
    //We also need delete information form database.
    @Override
    public boolean onLongClick(View v) {
        position = recyclerView.getChildAdapterPosition(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.removeItem(stockInfoList.get(position).getStockName());
                removeItem(position);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setIcon(R.drawable.warning);

        builder.setMessage("Delete stock symbol '" + stockInfoList.get(position).getStockName()+"'");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    //UI setting. Show "add" button on top right.
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    //Once user click "add" button, there must be a dialog which allows user
    //enter a stock name and then search it form AllStockDatabase.
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.add_menu)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ma);
            final EditText et = new EditText(this);
            et.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);
            //builder.setIcon(R.drawable.icon2);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String symbol = et.getText().toString().trim();
                    if(!symbol.equals(""))
                    {
                        if(doNetCheck())
                        {
                            updateStockInfo();
                            ArrayList<String> temp = likeSearch(symbol);
                            if(temp.size() != 0)
                                showList(temp);
                            else
                                showWarning("'" + symbol + "' Search Failed!","No stock found!");
                        }
                        else
                            {
                                showWarning("No network connection",
                                        "Stock cannot be displayed without a network connection!");
                                ArrayList<StockInfo> temp = user.loadStockInfo();
                                stockInfoList.clear();
                                stockInfoList.addAll(temp);
                                Collections.sort(stockInfoList);
                                mAdapter.notifyDataSetChanged();
                            }
                    }else
                    {
                        showWarning("Search Failed!","No value!");
                    }
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ma, "canceled", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setMessage("Please enter a stock name:");
            builder.setTitle("Single Input");

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
        else
            {
                return super.onOptionsItemSelected(item);
            }
    }

    //Likely search from HashMap
    private ArrayList<String> likeSearch(String name)
    {
        ArrayList<String> result = new ArrayList<>();
        for(String key : allStockInfo.keySet())
        {
            if(key.contains(name))
                result.add(key);
        }
        return result;
    }

    //Warning dialog, can be used in other places.
    private void showWarning(String title, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.warning);

        builder.setMessage(msg);
        builder.setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showList(ArrayList<String> temp)
    {
        final ArrayList<String> sArray = new ArrayList<>();
        sArray.addAll(temp);
        Collections.sort(sArray);
        String[] tArray = new String[sArray.size()];
        for(int i = 0; i < sArray.size(); i++)
            tArray[i] = sArray.get(i) + "\n" + allStockInfo.get(sArray.get(i))+"\n";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        //builder.setIcon();
        builder.setItems(tArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String symbol = sArray.get(which);
                String company = allStockInfo.get(symbol);
                for(StockInfo s : stockInfoList)
                {
                    if(s.getStockName().equals(symbol))
                    {
                        showWarning("Duplicate Stock!",
                                "Stock symbol '"+ symbol + "' is already displayed!");
                        return;
                    }
                }
                user.addItem(symbol, company);
                getFullInfo(new StockInfo(symbol, company));
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ma, "CANCELED", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getFullInfo(StockInfo info)
    {
        new FullInfoAsyncTask(ma, info).execute();
    }

    public void updateData_1(HashMap<String, String> list)
    {
        allStockInfo = list;
    }

    public void updateData_2(StockInfo temp)
    {
        Log.d(TAG, "updateData_2: --------------------------------------------------");
        Log.d(TAG, "updateData_2: " + temp.getStockName() + temp.getCompanyName() + temp.getChangeRate());
        stockInfoList.add(temp);
        Collections.sort(stockInfoList);
        mAdapter.notifyDataSetChanged();
    }

    private void updateStockInfo()
    {
        ArrayList<StockInfo> temp = user.loadStockInfo();
        stockInfoList.clear();
        if(doNetCheck())
        {
            for(int i = 0; i < temp.size(); i++)
            {
                getFullInfo(temp.get(i));
            }
        }
        else
            {
                showWarning("No network connection",
                        "Stock cannot be update without a network connection!");
                ArrayList<StockInfo> temp1 = user.loadStockInfo();
                stockInfoList.clear();
                stockInfoList.addAll(temp1);
                Collections.sort(stockInfoList);
                mAdapter.notifyDataSetChanged();
            }

        swiper.setRefreshing(false);
    }

    private boolean doNetCheck()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null)
        {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    @Override
    protected void onDestroy() {
        user.shutDown();
        super.onDestroy();
    }
}
