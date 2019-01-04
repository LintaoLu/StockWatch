package edu.iit.llu25hawk.stockwatch;

 /*
    This Database store information of user added stock.
    It should support add, delete and load operation.
    The same as a Json file, it store information only for future review.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class UserStockDatabase extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserStockDB";
    private static final String TABLE_NAME = "UserStockTable";

    private static final String STOCK_SYMBOL = "StockSymbol";
    private static final String COMPANY_NAME = "CompanyName";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    STOCK_SYMBOL + " TEXT not null unique, " +
                    COMPANY_NAME + " TEXT not null)";

    private SQLiteDatabase database;
    private MainActivity mainActivity;

    public UserStockDatabase(MainActivity context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mainActivity = context;
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public ArrayList<StockInfo> loadStockInfo()
    {
        ArrayList<StockInfo> stockInfos = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{ STOCK_SYMBOL, COMPANY_NAME},
                null,
                null,
                null,
                null,
                null);

        if(cursor != null)
        {
            cursor.moveToFirst();

            for(int i = 0; i < cursor.getCount(); i++)
            {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                StockInfo s = new StockInfo(
                        symbol,0,company,0,0);
                stockInfos.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stockInfos;
    }

    public void addItem(String symbol, String company)
    {
        ContentValues values = new ContentValues();

        values.put(STOCK_SYMBOL, symbol);
        values.put(COMPANY_NAME, company);

        database.insert(TABLE_NAME, null, values);
    }

    public void removeItem(String name)
    {
        database.delete(TABLE_NAME, STOCK_SYMBOL + " = ?", new String[]{name});
    }

    public void shutDown() { database.close(); }

}