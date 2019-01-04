package edu.iit.llu25hawk.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/*
    Download information form internet and store them into AllStockDatabase.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, String>
{
    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    private static final String TAG = "DownloadAsyncTask";

    public DownloadAsyncTask(MainActivity ma)
    {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String s)
    {
        Log.d(TAG, "onPostExecute: " + s);
        HashMap<String, String> temp = parseJason(s);
        mainActivity.updateData_1(temp);
    }

    @Override
    protected String doInBackground(Void... voids)
    {
        Log.d(TAG, "doInBackground: ----------------------------------------------");
        Uri dataUri = Uri.parse(DATA_URL);
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
        }
        catch(Exception e)
        {
            return null;
        }

        return sb.toString();
    }

    private HashMap<String, String> parseJason(String s)
    {
        HashMap<String, String> hm = new HashMap<>();

        try
        {
            JSONArray jObjMain = new JSONArray(s);
            for(int i = 0; i < jObjMain.length(); i++)
            {
                JSONObject jStockInfo = (JSONObject) jObjMain.get(i);
                String symbol = jStockInfo.getString("symbol");
                String company = jStockInfo.getString("name");
                Log.d(TAG, "parseJason: " + symbol + " " + company + "\n");
                hm.put(symbol, company);
            }
            return hm;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
