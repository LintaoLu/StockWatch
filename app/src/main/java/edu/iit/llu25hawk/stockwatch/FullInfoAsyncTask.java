package edu.iit.llu25hawk.stockwatch;

/*
    This class received a StockInfo object(only contains stock's symbol and company's name).
    It will download other information from internet and store them into the received object.
    Finally, return the object(now every information has gotten) to mainActivity.
 */
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FullInfoAsyncTask extends AsyncTask<Void, Void, String>
{
    private MainActivity mainActivity;
    private StockInfo stock;
    private static final String DATA_URL_1 = "https://api.iextrading.com/1.0/stock/";
    private static final String DATA_URL_2 = "/quote?displayPercent=true";
    private static final String TAG = "FullInfoAsyncTask";

    public FullInfoAsyncTask(MainActivity ma, StockInfo stock)
    {
        mainActivity = ma;
        this.stock = stock;
        Log.d(TAG, "FullInfoAsyncTask: ");
    }

    @Override
    protected void onPostExecute(String s)
    {
        StockInfo tempStock = parseJason(s);
        Log.d(TAG, "onPostExecute: " + tempStock.getStockName() + tempStock.getCompanyName() + tempStock.changeRate);
        mainActivity.updateData_2(tempStock);
    }

    @Override
    protected String doInBackground(Void... voids) {

        Log.d(TAG, "doInBackground: ---------------------------------------------------");
        String newUrl = DATA_URL_1 + stock.getStockName() + DATA_URL_2;
        Log.d(TAG, "doInBackground: " + newUrl);
        Uri dataUri = Uri.parse(newUrl);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        try
        {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null)
                sb.append(line).append("\n");
            Log.d(TAG, "doInBackground: " + sb);
        }
        catch(Exception e)
        {
            return null;
        }

        return sb.toString();
    }

    private StockInfo parseJason(String s)
    {
        StockInfo tempStock;
        try
        {
            JSONObject jsonObject = new JSONObject(s);
            String latestPrice = jsonObject.getString("latestPrice");
            String change = jsonObject.getString("change");
            String changePercent = jsonObject.getString("changePercent");
            tempStock = new StockInfo(
                    stock.getStockName(), Double.parseDouble(latestPrice),
                    stock.getCompanyName(), Double.parseDouble(changePercent), Double.parseDouble(change));
            return tempStock;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
