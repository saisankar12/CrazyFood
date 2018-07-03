package com.crazy.food.app.crazyfood;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecondFoodDetails extends AppCompatActivity {

    ContentValues mealcontentValues;
    String BASEURLMENU;
    ArrayList<String> foodDetails;
    String sid;
    @BindView(R.id.second_backgraph) ImageView background_image;
    @BindView(R.id.id_second_title) TextView title;
    @BindView(R.id.id_second_tv_playvideo) TextView playvideo_tv;
    @BindView(R.id.id_second_category) TextView category;
    @BindView(R.id.id_second_area) TextView area;
    @BindView(R.id.id_second_instructions) TextView tv_instructions;
    @BindView(R.id.id_second_ingredients) TextView tv_ingredients;

    @BindView(R.id.second_fav_img) ImageView favImage;
    boolean check = false;
    ArrayList<MyUserLookup> jsonUserLookup;
    MyUserLookup myUserLookup;
    boolean widcheck=false;
    String widInstructions,widTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_food_details);
        BASEURLMENU = "https://www.themealdb.com";
        ButterKnife.bind(this);
        /*background_image=findViewById(R.id.second_backgraph);*/
        /*favImage=findViewById(R.id.second_fav_img);*/
        /*title=findViewById(R.id.id_second_title);
        playvideo_tv=findViewById(R.id.id_second_tv_playvideo);
        category=findViewById(R.id.id_second_category);
        area=findViewById(R.id.id_second_area);
        tv_instructions=findViewById(R.id.id_second_instructions);
        tv_ingredients=findViewById(R.id.id_second_ingredients);*/
        foodDetails = getIntent().getStringArrayListExtra("jTitle");
        sid = foodDetails.get(2);
        Picasso.with(this).load(foodDetails.get(0)).placeholder(R.mipmap.ic_launcher_round).into(background_image);
        title.setText(foodDetails.get(1));
        Uri urisel = Uri.parse(MyMealContract.FavMealEntry.CONTENT_URI + "/*");
        Cursor selcur = getContentResolver().query(urisel, null, sid, null, null);
        if (selcur.getCount() > 0) {
            check = true;
            favImage.setImageResource(R.mipmap.coloredfavimage);
        } else {
            check = false;
            favImage.setImageResource(R.mipmap.favimage);
        }
        new FoodData(this).execute("");
    }

    public void addFavListImg(View view) {
        if (!check) {
            check = true;
            mealcontentValues = new ContentValues();
            mealcontentValues.put(MyMealContract.FavMealEntry.COLUMN_MEALID, foodDetails.get(2));
            mealcontentValues.put(MyMealContract.FavMealEntry.COLUMN_BACKGROUND_IMAGE, foodDetails.get(0));
            mealcontentValues.put(MyMealContract.FavMealEntry.COLUMN_TITLE, foodDetails.get(1));
            mealcontentValues.put(MyMealContract.FavMealEntry.COLUMN_CATEGORY, myUserLookup.getStrCategory());
            mealcontentValues.put(MyMealContract.FavMealEntry.COLUMN_AREA,myUserLookup.getStrArea());
            Uri uri = getContentResolver().insert(Uri.parse(MyMealContract.FavMealEntry.CONTENT_URI + ""), mealcontentValues);
            if (uri!=null){
                favImage.setImageResource(R.mipmap.coloredfavimage);
                new MealDbHelper(this).showFavoriteMeals();
                Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
            }
        }else {
            check = false;
            getContentResolver().delete(MyMealContract.FavMealEntry.CONTENT_URI, MyMealContract.FavMealEntry.COLUMN_MEALID + " =? ", new String[]{foodDetails.get(2)});
            favImage.setImageResource(R.mipmap.favimage);
            Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show();
        }
    }

    public class FoodData extends AsyncTask<String, Void, String> {
        Context context;
        public FoodData(Context c) {
            this.context=c;
        }

        @Override
        protected String doInBackground(String... strings) {
            jsonUserLookup = new ArrayList<MyUserLookup>();
            HttpUrlResponse responseConnection = new HttpUrlResponse();
            URL url = responseConnection.buildLookupUrl(BASEURLMENU, sid);
            String response = null;
            try {
                response = responseConnection.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.optJSONArray("meals");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject foodSpecific = jsonArray.optJSONObject(i);
                    String idMeal=foodSpecific.optString("idMeal");
                    String strMeal=foodSpecific.optString("strMeal");
                    String strCategory=foodSpecific.optString("strCategory");
                    String strArea=foodSpecific.optString("strArea");
                    String strInstructions=foodSpecific.optString("strInstructions");
                    String strMealThumb=foodSpecific.optString("strMealThumb");
                    String strYoutube=foodSpecific.optString("strYoutube");
                    myUserLookup = new MyUserLookup(idMeal,strMeal,strCategory,strArea,strInstructions,strMealThumb,strYoutube);
                    jsonUserLookup.add(myUserLookup);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for (int i=0;i<jsonUserLookup.size();i++){
                category.setText(jsonUserLookup.get(i).getStrCategory());
                area.setText(jsonUserLookup.get(i).getStrArea());
                tv_instructions.setText(jsonUserLookup.get(i).getStrInstructions());
                tv_ingredients.setText(jsonUserLookup.get(i).getStrIngredient1());
                playvideo_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri =  Uri.parse(myUserLookup.getStrYoutube());
                        String s = uri.toString();
                        String[] parts = s.split("=");
                        Intent i = new Intent(context,YoutubeActivity.class);
                        i.putExtra("youtubeparse",parts[1]);
                        startActivity(i);
                    }
                });
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemThatClicked = menuItem.getItemId();
        switch (itemThatClicked) {
            case R.id.id_seccond_menu_widget:
                    widTitle=foodDetails.get(1);
                    widInstructions = myUserLookup.getStrInstructions();
                    MealWidget mealWidget = new MealWidget();
                    mealWidget.mealWidtitle="Title:"+"\t"+widTitle+"\nInstructions:"+"\n"+widInstructions;
                    Toast.makeText(this, "Added to Widget", Toast.LENGTH_SHORT).show();
                    break;
            default:
                Toast.makeText(this, "Nothing to be displayed.....", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(menuItem);

    }
}
