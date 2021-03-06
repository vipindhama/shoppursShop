package com.shoppursshop.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppursshop.R;
import com.shoppursshop.activities.NetworkBaseActivity;
import com.shoppursshop.adapters.OrderAdapter;
import com.shoppursshop.adapters.ShopOfferListAdapter;
import com.shoppursshop.models.MyProduct;
import com.shoppursshop.models.MyProductItem;
import com.shoppursshop.models.OrderItem;
import com.shoppursshop.models.ProductComboDetails;
import com.shoppursshop.models.ProductComboOffer;
import com.shoppursshop.models.ProductDiscountOffer;
import com.shoppursshop.models.ShopOfferItem;
import com.shoppursshop.utilities.ConnectionDetector;
import com.shoppursshop.utilities.Constants;
import com.shoppursshop.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOffersActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView;
    private ShopOfferListAdapter myItemAdapter;
    private List<ShopOfferItem> itemList;
    private TextView textViewError,tv_top_parent, text_second_label;
    private FloatingActionButton fab_new_offer;

    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarTheme();
        init();
    }

    private void init(){
        flag = getIntent().getStringExtra("flag");
        itemList = new ArrayList<>();
        textViewError = findViewById(R.id.text_no_order);
        recyclerView=findViewById(R.id.recycler_view);
        fab_new_offer = findViewById(R.id.fab_new_offer);
        fab_new_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOffersActivity.this, CreateOfferActivity.class));
                //DialogAndToast.showToast("Creating New Offer ", MyOffersActivity.this);
            }
        });

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new ShopOfferListAdapter(this,itemList);
        recyclerView.setAdapter(myItemAdapter);

        tv_top_parent = findViewById(R.id.text_left_label);
        text_second_label = findViewById(R.id.text_second_label);

        tv_top_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOffersActivity.this, SettingActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if (ConnectionDetector.isNetworkAvailable(this)){
            getItemList();
        }else{
            showNoNetwork(true);
        }
    }

    //ems simulation iq


    private void getItemList(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url)+Constants.GET_PRODUCT_OFFER;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"offerList");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

        try {
            if (apiName.equals("offerList")) {
                if (response.getBoolean("status")) {
                    itemList.clear();
                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray freeArray = jsonObject.getJSONArray("freeOfferList");
                    JSONArray comboArray = jsonObject.getJSONArray("comboOfferList");
                    JSONArray priceArray = jsonObject.getJSONArray("priceOfferList");
                    JSONObject dataObject = null;
                    ShopOfferItem offerItem= null;
                    ProductComboOffer productComboOffer = null;
                    ProductComboDetails productComboDetails = null;
                    ProductDiscountOffer productDiscountOffer = null;
                    int len = freeArray.length();
                    for (int i = 0; i < len; i++) {
                        dataObject = freeArray.getJSONObject(i);
                        productDiscountOffer = new ProductDiscountOffer();
                        offerItem = new ShopOfferItem();
                        offerItem.setOfferName(dataObject.getString("offerName"));
                        offerItem.setProductName("Free Product Offer");
                        offerItem.setProductLocalImage(R.drawable.thumb_12);
                        offerItem.setOfferType("free");
                        productDiscountOffer.setId(dataObject.getInt("id"));
                        productDiscountOffer.setOfferName(dataObject.getString("offerName"));
                        productDiscountOffer.setProdBuyId(dataObject.getInt("prodBuyId"));
                        productDiscountOffer.setProdFreeId(dataObject.getInt("prodFreeId"));
                        productDiscountOffer.setProdBuyQty(dataObject.getInt("prodBuyQty"));
                        productDiscountOffer.setProdFreeQty(dataObject.getInt("prodFreeQty"));
                        productDiscountOffer.setStatus(dataObject.getString("status"));
                        productDiscountOffer.setStartDate(dataObject.getString("startDate"));
                        productDiscountOffer.setEndDate(dataObject.getString("endDate"));
                        offerItem.setProductObject(productDiscountOffer);
                        itemList.add(offerItem);
                    }

                    len = comboArray.length();
                    for (int i = 0; i < len; i++) {
                        dataObject = comboArray.getJSONObject(i);
                        offerItem = new ShopOfferItem();
                        offerItem.setOfferName(dataObject.getString("offerName"));
                        offerItem.setProductName("Combo Product Offer");
                        offerItem.setProductLocalImage(R.drawable.thumb_12);
                        offerItem.setOfferType("combo");
                        itemList.add(offerItem);
                    }

                    len = priceArray.length();
                    for (int i = 0; i < len; i++) {
                        dataObject = priceArray.getJSONObject(i);
                        offerItem = new ShopOfferItem();
                        offerItem.setOfferName(dataObject.getString("offerName"));
                        offerItem.setProductName("Product Price Offer");
                        offerItem.setProductLocalImage(R.drawable.thumb_12);
                        offerItem.setOfferType("price");
                        itemList.add(offerItem);
                    }

                    if(len == 0){
                        showNoData(true);
                    }else{
                        showNoData(false);
                        myItemAdapter.notifyDataSetChanged();
                    }

                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNoData(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }
    }

    private void showNoNetwork(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
            textViewError.setText(getResources().getString(R.string.no_internet));
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }
    }

}
