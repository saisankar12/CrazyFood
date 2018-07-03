package com.crazy.food.app.crazyfood;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    String BASEURLMENU;
    @BindView(R.id.grid_main_recycler) RecyclerView rvmFoodList;
    @BindView(R.id.main_grid_layout)GridLayout gridLayout;
    String bundleKey = "MyBKey";
    ArrayList<MyUserMeal> jsonFoodImages;
    String bundleValue = "Starter";
    String s="",action="";
    private Cursor cursorData;
    private int mIdCol, mTitleCol, mCategoryCol, mAreaCol, mBackgroundCol;
    int scrollpos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BASEURLMENU = "https://www.themealdb.com";
        ButterKnife.bind(this);
        /*gridLayout=findViewById();*/
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(getString(R.string.no_internet_display))
                    .setPositiveButton(getString(R.string.accepted), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            Snackbar.make(gridLayout, getString(R.string.loading), Snackbar.LENGTH_SHORT).show();
            if (savedInstanceState != null) {
                if (savedInstanceState.getString(bundleKey) != "favourites") {
                    bundleValue = savedInstanceState.getString(bundleKey);
                    new FoodData().execute(bundleValue);
                } else if (savedInstanceState.getString(bundleKey) == "favourites") {
                    bundleValue = savedInstanceState.getString(bundleKey);
                    new CursorMealData().execute();
                }
            } else {
                new FoodData().execute(bundleValue);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(bundleKey, bundleValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (bundleValue) {
            case "Starter":
                FoodData foodData = new FoodData();
                foodData.execute(bundleValue);
                break;
            case "Vegetarian":
                FoodData foodData1 = new FoodData();
                foodData1.execute(bundleValue);
                break;
            case "Desert":
                FoodData foodData2 = new FoodData();
                foodData2.execute(bundleValue);
                break;
            case "favourites":
                new CursorMealData().execute();
                break;
            case "Chicken":
                FoodData foodData3 = new FoodData();
                foodData3.execute(bundleValue);
                break;
            case "SeaFood":
                FoodData foodData4 = new FoodData();
                foodData4.execute(bundleValue);
                break;
            default:
                Toast.makeText(this,"Nothing to be displayed.....",Toast.LENGTH_LONG).show();
                break;
        }
    }

    public class FoodData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            jsonFoodImages = new ArrayList<MyUserMeal>();
            HttpUrlResponse responseConnection = new HttpUrlResponse();
            URL url = responseConnection.buildURL(BASEURLMENU, strings[0]);
            String response = null;
            try {
                response = responseConnection.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray(getString(R.string.mealsarray));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject foodDetail = jsonArray.getJSONObject(i);
                    String strMeal = foodDetail.getString(getString(R.string.strMeal));
                    String strMealThumb = foodDetail.getString(getString(R.string.strMealThumb));
                    String idMeal = foodDetail.getString(getString(R.string.idMeal));
                    jsonFoodImages.add(new MyUserMeal(strMeal, strMealThumb, idMeal));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            rvmFoodList.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
            rvmFoodList.setAdapter(new MyFoodList(MainActivity.this,jsonFoodImages));
        }
    }

    public void loading(final String s,final String action){
        Snackbar.make(gridLayout, s, Snackbar.LENGTH_SHORT).setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s= "Loading...";
                final String action = "";
                loading(s,action);
                new FoodData().execute("Starter");

            }
        }).show();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemThatClicked = menuItem.getItemId();
        switch (itemThatClicked) {
            case R.id.menu_starters:
                bundleValue="Starter";
                FoodData foodData = new FoodData();
                foodData.execute(bundleValue);
                break;
            case R.id.menu_vegetarian:
                bundleValue="Vegetarian";
                FoodData foodData1 = new FoodData();
                foodData1.execute(bundleValue);
                break;
            case R.id.menu_desert:
                bundleValue="Desert";
                FoodData foodData2 = new FoodData();
                foodData2.execute(bundleValue);
                break;
            case R.id.menu_favourites:
                bundleValue="favourites";
                new CursorMealData().execute();
                break;
            case R.id.menu_chicken:
                bundleValue="Chicken";
                FoodData foodData3 = new FoodData();
                foodData3.execute(bundleValue);
                break;
            case R.id.menu_seafood:
                bundleValue="SeaFood";
                FoodData foodData4 = new FoodData();
                foodData4.execute(bundleValue);
                break;
            case R.id.menu_nonveg:
                Toast.makeText(this, "Choose any one non-veg item", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this,"Nothing to be displayed.....",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class CursorMealData extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(MyMealContract.FavMealEntry.CONTENT_URI,
                    null, null, null, null);
            return cursor;
        }
        @Override
        protected void onPostExecute(Cursor cursor) {
            ArrayList<MyUserMeal> cursorMealData = new ArrayList<MyUserMeal>();
            super.onPostExecute(cursor);
            cursorData = cursor;
            mIdCol = cursorData.getColumnIndex(MyMealContract.FavMealEntry.COLUMN_MEALID);
            mTitleCol = cursorData.getColumnIndex(MyMealContract.FavMealEntry.COLUMN_TITLE);
            mCategoryCol = cursorData.getColumnIndex(MyMealContract.FavMealEntry.COLUMN_CATEGORY);
            mAreaCol = cursorData.getColumnIndex(MyMealContract.FavMealEntry.COLUMN_AREA);
            mBackgroundCol = cursorData.getColumnIndex(MyMealContract.FavMealEntry.COLUMN_BACKGROUND_IMAGE);
            while (cursorData.moveToNext()) {
                String mId = cursorData.getString(mIdCol);
                String mTitle = cursorData.getString(mTitleCol);
                String mCategory = cursorData.getString(mCategoryCol);
                String mArea = cursorData.getString(mAreaCol);
                String mBackground = cursorData.getString(mBackgroundCol);
                cursorMealData.add(new MyUserMeal(mTitle,mBackground,mId));
            }
            cursorData.close();
            if (cursorMealData.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("No Favourites");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            } else {
                rvmFoodList.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                rvmFoodList.setAdapter(new MyFoodList(MainActivity.this, cursorMealData));
                Toast.makeText(MainActivity.this, "No of favourites:" + cursorData.getCount(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
