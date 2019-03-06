package com.shoppursshop.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.shoppursshop.R;
import com.shoppursshop.activities.RegisterActivity;
import com.shoppursshop.interfaces.OnFragmentInteraction;
import com.shoppursshop.models.MyUser;
import com.shoppursshop.utilities.ConnectionDetector;
import com.shoppursshop.utilities.Constants;
import com.shoppursshop.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link BankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BankFragment extends NetworkBaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView;
    private EditText editTextShopName,editTextBankName,editTextIfscCode,editTextAccountNo,editTextBranchAddress;
    private String shopName,bankName,accountNo,ifscCode,branchAddress;
    private Button btnSubmit,btnBack;
    private MyUser myUser;

    private OnFragmentInteraction mListener;

    public BankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BankFragment newInstance(String param1, String param2) {
        BankFragment fragment = new BankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_bank, container, false);
        init();

       /* editor.putString(Constants.MOBILE_NO,"9718181501");
        editor.putString(Constants.DB_NAME,"shoppurs_9718181501");
        editor.putString(Constants.DB_USER_NAME,"shuppurs_master");
        editor.putString(Constants.DB_PASSWORD,"$hop@2018#");
        editor.commit();*/
        return rootView;
    }

    private void init(){
        btnSubmit=(Button)rootView.findViewById(R.id.btn_submit);
        btnBack=(Button)rootView.findViewById(R.id.btn_back);

        editTextShopName = rootView.findViewById(R.id.edit_business_name);
        editTextAccountNo = rootView.findViewById(R.id.edit_bank_account);
        editTextBankName = rootView.findViewById(R.id.edit_bank_name);
        editTextIfscCode = rootView.findViewById(R.id.edit_bank_ifsc);
        editTextBranchAddress = rootView.findViewById(R.id.edit_bank_address);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUser = new MyUser();
               // mListener.onFragmentInteraction(myUser,RegisterActivity.BANK);
                 attemptUpdateBankDetails();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void attemptUpdateBankDetails(){
        shopName = editTextShopName.getText().toString();
        bankName = editTextBankName.getText().toString();
        accountNo = editTextAccountNo.getText().toString();
        ifscCode = editTextIfscCode.getText().toString();
        branchAddress = editTextBranchAddress.getText().toString();

        boolean cancel = false;
        View focus = null;

        if(TextUtils.isEmpty(branchAddress)){
            cancel = true;
            focus = editTextBranchAddress;
            editTextBranchAddress.setError("Please enter branch address");
        }

        if(TextUtils.isEmpty(ifscCode)){
            cancel = true;
            focus = editTextIfscCode;
            editTextIfscCode.setError("Please enter IFSC code");
        }

        if(TextUtils.isEmpty(accountNo)){
            cancel = true;
            focus = editTextAccountNo;
            editTextAccountNo.setError("Please enter account number");
        }

        if(TextUtils.isEmpty(bankName)){
            cancel = true;
            focus = editTextBankName;
            editTextBankName.setError("Please enter bank name");
        }

        if(TextUtils.isEmpty(shopName)){
            cancel = true;
            focus = editTextShopName;
            editTextShopName.setError("Please enter Shop name");
        }


        if(cancel){
            focus.requestFocus();
            return;
        }else{
            if(ConnectionDetector.isNetworkAvailable(getActivity())) {
               // progressDialog.setMessage(getResources().getString(R.string.creating_account));
                Map<String,String> params=new HashMap<>();

                params.put("mobile",sharedPreferences.getString(Constants.MOBILE_NO,""));
                params.put("bussinessName",shopName);
                params.put("bankName",bankName);
                params.put("acctNo",accountNo);
                params.put("ifscCode",ifscCode);
                params.put("branchAddress",branchAddress);
               // params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
               // params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
               // params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));

                String url=getResources().getString(R.string.url)+"/api/updateBankDetails";
                showProgress(true);
                jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updateBankDetails");
                   /* editor.putString(Constants.FULL_NAME,fullName);
                    editor.putString(Constants.EMAIL,email);
                    editor.putString(Constants.MOBILE_NO,mobile);
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.commit();*/

            }else {
                DialogAndToast.showDialog(getResources().getString(R.string.no_internet),getActivity());
            }
        }
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

        try {
            if(apiName.equals("updateBankDetails")){
                if(response.getBoolean("status")){
                    editor.putBoolean(Constants.IS_BANK_DETAIL_ADDED,true);
                    editor.commit();
                    mListener.onFragmentInteraction(myUser,RegisterActivity.BANK);
                }else{
                   DialogAndToast.showDialog(response.getString("message"),getActivity());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteraction) {
            mListener = (OnFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
