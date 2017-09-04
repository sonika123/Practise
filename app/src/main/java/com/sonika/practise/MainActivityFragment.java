package com.sonika.practise;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sonika.practise.Adapter.ProductAdapter;
import com.sonika.practise.JsonParser.JsonParserA;
import com.sonika.practise.Pojo.ProductObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        perform();
        return v;
    }

    public void perform()
    {
        new ProductAsyncTask().execute();
    }

        class ProductAsyncTask extends AsyncTask<String, String, String> {
            ProgressDialog mprogressDialog;
            RecyclerView bestRecyclerView;
            int flag;
            List<ProductObject> ProductList = new ArrayList<ProductObject>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mprogressDialog = new ProgressDialog(getContext());
                mprogressDialog.setMessage("Please wait");
                mprogressDialog.setCancelable(false);
                mprogressDialog.show();

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> loginHashMap = new HashMap<>();
                JsonParserA jsonParser = new JsonParserA();
                JSONObject jsonObject = jsonParser.performPostCI("http://kinbech.6te.net/ResturantFoods/api/showMenuList", loginHashMap);


                try {
                    if (jsonObject == null) {
                        flag = 1;
                    } else if (jsonObject.getString("status").equals("success")) {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject dataObject = jsonArray.getJSONObject(i);
                            String id = dataObject.getString("id");
                            String description = dataObject.getString("details");
                            String price = dataObject.getString("price");
                            String materials = dataObject.getString("materials");
                            String image = dataObject.getString("image");
                            String name = dataObject.getString("name");

                            ProductObject productObject= new ProductObject(id, name, price, description, image, materials );
                            ProductList.add(productObject);
                            flag = 2;
                        }
                    }

                    else
                    {
                        flag = 3;
                    }

                } catch (JSONException e) {

                }


                return  null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mprogressDialog.dismiss();
                if (flag == 1) {
                    Toast.makeText(getContext(), "Server/Network issue", Toast.LENGTH_SHORT).show();
                } else if (flag == 2) {
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    bestRecyclerView = (RecyclerView) getView().findViewById(R.id.product_list);

                    GridLayoutManager mGrid = new GridLayoutManager(getContext(),2);
                    bestRecyclerView.setLayoutManager(mGrid);
                    bestRecyclerView.setHasFixedSize(true);
                    bestRecyclerView.setNestedScrollingEnabled(false);

                    ProductAdapter mAdapter = new ProductAdapter(getContext(), ProductList );
                    bestRecyclerView.setAdapter(mAdapter);

                } else {
                    Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }


