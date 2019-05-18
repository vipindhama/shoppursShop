package com.shoppursshop.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppursshop.R;
import com.shoppursshop.adapters.InvoiceItemAdapter;
import com.shoppursshop.models.Invoice;
import com.shoppursshop.models.InvoiceDetail;
import com.shoppursshop.models.InvoiceItem;
import com.shoppursshop.utilities.ConnectionDetector;
import com.shoppursshop.utilities.Constants;
import com.shoppursshop.utilities.EnglishNumberToWords;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView;
    private List<InvoiceItem> itemList;
    private InvoiceItemAdapter itemAdapter;

    private TextView tvShopName,tvShopAddress,tvShopEmail,tvShopPhone,tvShopGSTIN,tvInvoiceNo,tvDate,tvCustomerName,
                      tvSubTotAmt,tvGrossTotAmt,tvTotSgst,tvTotCgst,tvTotIgst,tvShortExcess,tvNetPayableAmt,tvNetPayableWords,
                      tvCollectionMode,tvCollectionAmt,tvPaidAmt,tvBalAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemAdapter=new InvoiceItemAdapter(this,itemList);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setNestedScrollingEnabled(false);

      //  getItemList();

        if(ConnectionDetector.isNetworkAvailable(this))
        getInvoice();

    }

    private void init(){
        tvShopName = findViewById(R.id.text_shop_name);
        tvShopAddress = findViewById(R.id.text_shop_address);
        tvShopEmail = findViewById(R.id.text_shop_email);
        tvShopPhone = findViewById(R.id.text_shop_phone);
        tvShopGSTIN = findViewById(R.id.text_shop_gst);
        tvInvoiceNo = findViewById(R.id.text_invoice_no);
        tvDate = findViewById(R.id.text_date);
        tvCustomerName = findViewById(R.id.text_customer_name);
        tvSubTotAmt = findViewById(R.id.text_sub_total_amount);
        tvGrossTotAmt = findViewById(R.id.text_gross_total_amount);
        tvTotSgst = findViewById(R.id.text_sgst);
        tvTotCgst = findViewById(R.id.text_cgst);
        tvTotIgst = findViewById(R.id.text_igst);
        tvShortExcess = findViewById(R.id.text_short_excess);
        tvNetPayableAmt = findViewById(R.id.text_net_payable_amount);
        tvNetPayableWords = findViewById(R.id.text_net_payable_amount_words);
        tvCollectionMode = findViewById(R.id.text_collection);
        tvCollectionAmt = findViewById(R.id.text_collection_amount);
        tvPaidAmt = findViewById(R.id.text_paid_amount);
        tvBalAmt = findViewById(R.id.text_balance);
    }

    private void getInvoice(){
        Map<String,String> params=new HashMap<>();
        params.put("id",getIntent().getStringExtra("orderId"));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url)+Constants.GET_INVOICE;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"orders");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

        try {
            if (apiName.equals("orders")) {
                if (response.getBoolean("status")) {
                   // JSONArray dataArray = response.getJSONArray("result");
                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray invoiceDetailsArray = jsonObject.getJSONArray("invoiceDetailList");
                    tvShopName.setText(jsonObject.getString("invShopName"));
                    tvShopAddress.setText(jsonObject.getString("invShopAddress"));
                    tvShopPhone.setText("Ph: "+jsonObject.getString("invShopMobile"));
                    tvShopEmail.setText("Email: "+jsonObject.getString("invShopEmail"));
                    tvShopGSTIN.setText("GSTIN: "+jsonObject.getString("invShopGSTIn"));
                    tvDate.setText(jsonObject.getString("invDate"));
                    tvCustomerName.setText(jsonObject.getString("invCustName"));
                    tvInvoiceNo.setText("Invoice No: "+jsonObject.getString("invNo"));
                    float subTotal = Float.parseFloat(""+(jsonObject.getDouble("invTotAmount") - jsonObject.getDouble("invTotTaxAmount")));
                    tvSubTotAmt.setText(String.format("%.02f",subTotal));
                    tvGrossTotAmt.setText(String.format("%.02f",subTotal));
                    tvTotIgst.setText(String.format("%.02f",Float.parseFloat(jsonObject.getString("invTotIGST"))));
                    tvShortExcess.setText(String.format("%.02f",Float.parseFloat(jsonObject.getString("invTotDisAmount"))));
                    float netPayable = (float) Math.round(jsonObject.getDouble("invTotNetPayable"));
                    tvNetPayableAmt.setText(String.format("%.02f",netPayable));
                    tvCollectionMode.setText(jsonObject.getString("invPaymentMode"));
                    tvCollectionAmt.setText(String.format("%.02f",netPayable));
                    tvPaidAmt.setText(String.format("%.02f",netPayable));
                    tvNetPayableWords.setText(EnglishNumberToWords.convert((int)netPayable)+" rupees");

                    int len = invoiceDetailsArray.length();
                  //  InvoiceDetail invoiceDetail= null;
                    for (int i = 0; i < len; i++) {
                        jsonObject = invoiceDetailsArray.getJSONObject(i);
                        //invoiceDetail = new InvoiceDetail();

                        InvoiceItem item = new InvoiceItem();
                        item.setItemName(jsonObject.getString("invDProdName"));
                        item.setHsn(jsonObject.getString("invDHsnCode"));
                        item.setQty(jsonObject.getInt("invDQty"));
                        item.setGst(jsonObject.getInt("invDIGST"));
                        item.setRate(Float.parseFloat(jsonObject.getString("invDSp")));
                        itemList.add(item);
                    }

                    itemAdapter.notifyDataSetChanged();

                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getItemList(){
        InvoiceItem item = new InvoiceItem();
        item.setItemName("Ghee Pongal");
        item.setHsn("9963");
        item.setQty(1);
        item.setGst(5);
        item.setRate(50);
        itemList.add(item);

        item = new InvoiceItem();
        item.setItemName("Coffee");
        item.setHsn("9963");
        item.setQty(1);
        item.setGst(5);
        item.setRate(25);
        itemList.add(item);

        itemAdapter.notifyDataSetChanged();
    }

}