package com.shoppursshop.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppursshop.R;
import com.shoppursshop.adapters.MyItemAdapter;
import com.shoppursshop.adapters.OrderAdapter;
import com.shoppursshop.models.HomeListItem;
import com.shoppursshop.models.OrderItem;
import com.shoppursshop.utilities.ConnectionDetector;
import com.shoppursshop.utilities.Constants;
import com.shoppursshop.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView,recyclerViewPreOrders;
    private OrderAdapter myItemAdapter,preItemAdapter;
    private List<Object> itemList,preItemList;
    private TextView textViewError,textViewNoTodayOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView textViewPreOrderLabel;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private Button btnNewOrder,btnLoadMore;
    private FloatingActionButton fabNewOrder;

    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemList = new ArrayList<>();
        preItemList = new ArrayList<>();
        HomeListItem myItem = new HomeListItem();
       // myItem.setTitle("Sunday 16 December, 2018");
        myItem.setTitle(Utility.getTimeStamp("EEE dd MMMM, yyyy"));
        myItem.setDesc("Today Orders");
        myItem.setType(0);
        itemList.add(myItem);

      /*  OrderItem orderItem = new OrderItem();
        orderItem.setType(1);
        orderItem.setCustomerName("Sonam Kapoor");
        orderItem.setMobile("9718181697");
        orderItem.setAmount(2000);
        orderItem.setDeliveryType("Cash On Delivery");
        orderItem.setLocalImage(R.drawable.thumb_11);
        itemList.add(orderItem);

        orderItem = new OrderItem();
        orderItem.setType(1);
        orderItem.setCustomerName("Katie Perry");
        orderItem.setMobile("9718181698");
        orderItem.setAmount(24000);
        orderItem.setDeliveryType("Pick Up");
        orderItem.setLocalImage(R.drawable.thumb_12);
        itemList.add(orderItem);

        orderItem = new OrderItem();
        orderItem.setType(1);
        orderItem.setCustomerName("Mohit Kumar");
        orderItem.setMobile("9718181699");
        orderItem.setAmount(34000);
        orderItem.setDeliveryType("In Store");
        orderItem.setLocalImage(R.drawable.thumb_13);
        itemList.add(orderItem);

        orderItem = new OrderItem();
        orderItem.setType(1);
        orderItem.setCustomerName("Sachin Kumar");
        orderItem.setMobile("9718181610");
        orderItem.setAmount(4000);
        orderItem.setDeliveryType("In Store");
        orderItem.setLocalImage(R.drawable.thumb_11);
        itemList.add(orderItem);

        orderItem = new OrderItem();
        orderItem.setType(1);
        orderItem.setCustomerName("Amit Kumar");
        orderItem.setMobile("9718181611");
        orderItem.setAmount(10000);
        orderItem.setDeliveryType("Cash On Delivery");
        orderItem.setLocalImage(R.drawable.thumb_12);
        itemList.add(orderItem);*/

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        textViewNoTodayOrders = findViewById(R.id.text_no_today_order);
        textViewPreOrderLabel = findViewById(R.id.text_pre_order_label);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new OrderAdapter(this,itemList,"orderList");
        recyclerView.setAdapter(myItemAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewPreOrders=findViewById(R.id.recycler_view_pre_orders);
        recyclerViewPreOrders.setHasFixedSize(true);
        RecyclerView.LayoutManager preLayoutManager=new LinearLayoutManager(this);
        recyclerViewPreOrders.setLayoutManager(preLayoutManager);
        recyclerViewPreOrders.setItemAnimator(new DefaultItemAnimator());
        preItemAdapter=new OrderAdapter(this,preItemList,"orderList");
        recyclerViewPreOrders.setAdapter(preItemAdapter);
        recyclerViewPreOrders.setNestedScrollingEnabled(false);

        fabNewOrder = findViewById(R.id.fab_new_order);
        fabNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnNewOrder = findViewById(R.id.btn_new_order);
        btnNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnLoadMore = findViewById(R.id.btn_load_more);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = offset + limit;
                getItemList();
            }
        });

        initFooter(this,0);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                if (ConnectionDetector.isNetworkAvailable(MainActivity.this)){
                    showNoNetwork(false);
                    offset = 0;
                    resetItemList();
                    getItemList();
                }else{
                    showNoNetwork(true);
                }

            }
        });

        limit = 5;
        offset = 0;

        if (ConnectionDetector.isNetworkAvailable(this)){
            getItemList();
        }else{
            showNoNetwork(true);
        }


    }

    private void getItemList(){
        Map<String,String> params=new HashMap<>();
        params.put("limit", ""+limit);
        params.put("offset",""+offset);
        params.put("code",sharedPreferences.getString(Constants.SHOP_CODE,""));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url)+Constants.GET_PENDING_ORDERS;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"orders");
    }

    @Override
    public void onResume(){
        super.onResume();

        editor.putInt("orderPosition",-1);
        editor.putString("type","");
        editor.putString("orderStatus","");
        editor.commit();

        int position = sharedPreferences.getInt("orderPosition",-1);

        Log.i(TAG,"position "+position);
        if(position > -1){
            String type = sharedPreferences.getString("type","");
            String status = sharedPreferences.getString("orderStatus","");
            Log.i(TAG,"type "+type);
            Log.i(TAG,"status "+status);
            if(type.equals("today")){
                ((OrderItem)itemList.get(position)).setStatus(status);
                myItemAdapter.notifyItemChanged(position);
            }else{
                ((OrderItem)preItemList.get(position)).setStatus(status);
                preItemAdapter.notifyItemChanged(position);
            }

            editor.putInt("orderPosition",-1);
            editor.putString("type","");
            editor.putString("orderStatus","");
            editor.commit();
        }

    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

        try {
            if (apiName.equals("orders")) {
                if (response.getBoolean("status")) {
                    JSONArray dataArray = response.getJSONArray("result");
                    JSONObject jsonObject = null;
                    int len = dataArray.length();
                    OrderItem orderItem= null;

                    for (int i = 0; i < len; i++) {
                        jsonObject = dataArray.getJSONObject(i);
                        orderItem = new OrderItem();
                        orderItem.setType(1);
                        orderItem.setId(jsonObject.getString("orderId"));
                        orderItem.setDateTime(jsonObject.getString("orderDate"));
                        orderItem.setCustomerName(jsonObject.getString("custName"));
                        orderItem.setCustCode(jsonObject.getString("custCode"));
                        orderItem.setMobile(jsonObject.getString("mobileNo"));
                        orderItem.setAmount(Float.parseFloat(jsonObject.getString("toalAmount")));
                        orderItem.setDeliveryType(jsonObject.getString("orderDeliveryMode"));
                        orderItem.setDeliveryAddress(jsonObject.getString("deliveryAddress"));
                        orderItem.setOrderImage(jsonObject.getString("orderImage"));
                        orderItem.setStatus(jsonObject.getString("orderStatus"));
                        orderItem.setOrderPayStatus(jsonObject.getString("oderPaymentStatus"));
                        orderItem.setLocalImage(R.drawable.thumb_12);

                        Log.i(TAG,"order date "+Utility.getTimeStamp("yyyy-MM-dd")+
                                " "+orderItem.getDateTime().split(" ")[0]);

                        if(Utility.getTimeStamp("yyyy-MM-dd").equals(orderItem.getDateTime().split(" ")[0]))
                        itemList.add(orderItem);
                        else
                        preItemList.add(orderItem);
                    }

                    if(len == 0){
                        showNoData(false);
                    }else{
                        showNoData(false);
                        myItemAdapter.notifyDataSetChanged();
                    }

                    if(len == limit){
                        Log.i(TAG,"Load more is visible");
                        btnLoadMore.setVisibility(View.VISIBLE);
                    }else {
                        btnLoadMore.setVisibility(View.GONE);
                    }

                    if(itemList.size() == 1){
                        //recyclerView.setVisibility(View.GONE);
                        textViewNoTodayOrders.setVisibility(View.VISIBLE);
                    }else{
                       // recyclerView.setVisibility(View.VISIBLE);
                        textViewNoTodayOrders.setVisibility(View.GONE);
                    }

                    if(preItemList.size() == 0){
                        textViewPreOrderLabel.setVisibility(View.GONE);
                        recyclerViewPreOrders.setVisibility(View.GONE);
                    }else{
                        textViewPreOrderLabel.setVisibility(View.VISIBLE);
                        recyclerViewPreOrders.setVisibility(View.VISIBLE);
                        preItemAdapter.notifyDataSetChanged();
                    }

                    Log.i(TAG,"itemList "+itemList.size()+" pre item list "+preItemList.size());
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void resetItemList(){
        itemList.clear();
        preItemList.clear();
        HomeListItem myItem = new HomeListItem();
        // myItem.setTitle("Sunday 16 December, 2018");
        myItem.setTitle(Utility.getTimeStamp("EEEE dd MMMM, yyyy"));
        myItem.setDesc("Today Orders");
        myItem.setType(0);
        itemList.add(myItem);
    }

    private void runLayoutAnimation() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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
            swipeRefreshLayout.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
            textViewError.setText(getResources().getString(R.string.no_internet));
        }else{
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }
    }
}
