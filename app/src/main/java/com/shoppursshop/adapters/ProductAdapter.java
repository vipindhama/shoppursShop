package com.shoppursshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppursshop.R;
import com.shoppursshop.activities.AddProductActivity;
import com.shoppursshop.activities.ProductDetailActivity;
import com.shoppursshop.activities.ProductListActivity;
import com.shoppursshop.activities.ScannarActivity;
import com.shoppursshop.activities.SubCatListActivity;
import com.shoppursshop.interfaces.MyItemTouchListener;
import com.shoppursshop.models.Category;
import com.shoppursshop.models.HomeListItem;
import com.shoppursshop.models.MyHeader;
import com.shoppursshop.models.MyItem;
import com.shoppursshop.models.MyProduct;
import com.shoppursshop.models.SubCategory;
import com.shoppursshop.models.CatListItem;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> itemList;
    private Context context;
    private String type;

    private MyItemTouchListener myItemTouchListener;

    public void setMyItemTouchListener(MyItemTouchListener myItemTouchListener) {
        this.myItemTouchListener = myItemTouchListener;
    }

    private ConstraintSet constraintSet = new ConstraintSet();

    public ProductAdapter(Context context, List<Object> itemList,String type) {
        this.itemList = itemList;
        this.context=context;
        this.type = type;

    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textDesc;
        private RecyclerView recyclerView;

        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_date_range);
            textDesc=itemView.findViewById(R.id.text_desc);
            recyclerView=itemView.findViewById(R.id.recycler_view);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public class MyHomeHeader1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader;
        private Button btnSeeAll;
        private RecyclerView recyclerView;

        public MyHomeHeader1ViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_title);
            btnSeeAll=itemView.findViewById(R.id.btn_see_all);
            recyclerView=itemView.findViewById(R.id.recycler_view);

            btnSeeAll.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == btnSeeAll){
                CatListItem myItem = (CatListItem) itemList.get(getAdapterPosition());
                Intent intent = new Intent(context,SubCatListActivity.class);
                intent.putExtra("catName",myItem.getTitle());
                context.startActivity(intent);
            }
        }
    }


    public class MyListType31ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
       // private ConstraintLayout constraintLayout;
        private View rootView;
        private long downTime,upTime;

        public MyListType31ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            imageView=itemView.findViewById(R.id.image_view);
            //constraintLayout=itemView.findViewById(R.id.parentContsraint);
            rootView.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("Adapter","onPressDown");
                    zoomAnimation(true,rootView);
                    downTime = new Date().getTime();
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    zoomAnimation(false,rootView);
                    upTime = new Date().getTime();
                    long diff = upTime - downTime;
                    if(getAdapterPosition() == 0){

                    }else{
                        Category myItem = (Category) itemList.get(getAdapterPosition());
                        Intent intent = new Intent(context,SubCatListActivity.class);
                        intent.putExtra("catName",myItem.getName());
                        context.startActivity(intent);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.i("Adapter","onPressCancel");
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    public class MyListScanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        private RelativeLayout relativeLayoutScan,relativeLayoutAddManually;
        // private ConstraintLayout constraintLayout;
        private View rootView;

        public MyListScanViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            imageView=itemView.findViewById(R.id.image_view);
            relativeLayoutScan=itemView.findViewById(R.id.relative_scan);
            relativeLayoutAddManually=itemView.findViewById(R.id.relative_add_manually);
            //constraintLayout=itemView.findViewById(R.id.parentContsraint);
            rootView.setOnTouchListener(this);
            relativeLayoutScan.setOnClickListener(this);
            relativeLayoutAddManually.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == relativeLayoutAddManually){
                Intent intent = new Intent(context,AddProductActivity.class);
                intent.putExtra("flag","manual");
                context.startActivity(intent);
            }else if(view == relativeLayoutScan){
                Intent intent = new Intent(context,ScannarActivity.class);
                intent.putExtra("flag","scan");
                intent.putExtra("isProduct",true);
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("Adapter","onPressDown");
                    zoomAnimation(true,rootView);
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    zoomAnimation(false,rootView);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.i("Adapter","onPressCancel");
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    public class MyListType3ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        private ConstraintLayout constraintLayout;
        private View rootView;

        public MyListType3ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            imageView=itemView.findViewById(R.id.image_view);
            constraintLayout=itemView.findViewById(R.id.parentContsraint);
            rootView.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("Adapter","onPressDown");
                    zoomAnimation(true,rootView);
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    zoomAnimation(false,rootView);
                    SubCategory myItem = (SubCategory) itemList.get(getAdapterPosition());
                    Intent intent = new Intent(context,ProductListActivity.class);
                    intent.putExtra("subCatName",myItem.getName());
                    context.startActivity(intent);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.i("Adapter","onPressCancel");
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    public class MySubHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textDesc;


        public MySubHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_date_range);
            textDesc=itemView.findViewById(R.id.text_desc);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public class MyProductListType1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textCatName,textName,textMrp,textSellingPrice,textDesc;
        private ImageView imageView;
        private View rootView;

        public MyProductListType1ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textCatName=itemView.findViewById(R.id.text_cat_name);
            textName=itemView.findViewById(R.id.text_name);
            textMrp=itemView.findViewById(R.id.text_mrp);
            textSellingPrice=itemView.findViewById(R.id.text_selling_price);
            textDesc=itemView.findViewById(R.id.text_desc);
            imageView=itemView.findViewById(R.id.image_view);
            rootView.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("Adapter","onPressDown");
                    zoomAnimation(true,rootView);
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    MyProduct item = (MyProduct) itemList.get(getAdapterPosition());
                    Intent intent = new Intent(context,ProductDetailActivity.class);
                    intent.putExtra("id",item.getId());
                    intent.putExtra("subCatName",item.getSubCatName());
                    intent.putExtra("productName",item.getName());
                    intent.putExtra("productDesc",item.getDesc());
                    intent.putExtra("productCode",item.getCode());
                    intent.putExtra("productDesc",item.getDesc());
                    intent.putExtra("productLocalImage",item.getLocalImage());
                    context.startActivity(intent);
                    zoomAnimation(false,rootView);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.i("Adapter","onPressCancel");
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textDesc;
        private CircleImageView image;


        public MyViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_header);
            textDesc=itemView.findViewById(R.id.text_desc);
            image=itemView.findViewById(R.id.image_pic);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public class MyHomeHeader2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader;
        private Button buttonAdd;

        public MyHomeHeader2ViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_title);
            buttonAdd=itemView.findViewById(R.id.btn_add);
            buttonAdd.setText("Add Product");
        }

        @Override
        public void onClick(View view) {
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.product_list_header_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.list_item_type_3_1_layout, parent, false);
                viewHolder = new MyListType31ViewHolder(v1);
                break;
            case 2:
                View v2 = inflater.inflate(R.layout.header_list_item_type_1_layout, parent, false);
                viewHolder = new MyHomeHeader1ViewHolder(v2);
                break;
            case 3:
                View v3 = inflater.inflate(R.layout.list_item_type_3_layout, parent, false);
                viewHolder = new MyListType3ViewHolder(v3);
                break;
            case 4:
                View v4 = inflater.inflate(R.layout.list_item_scan_layout, parent, false);
                viewHolder = new MyListScanViewHolder(v4);
                break;
            case 5:
                View v5 = inflater.inflate(R.layout.home_list_header_item_layout, parent, false);
                viewHolder = new MySubHomeHeaderViewHolder(v5);
                break;
            case 6:
                View v6 = inflater.inflate(R.layout.list_item_type_3_layout, parent, false);
                viewHolder = new MyListType3ViewHolder(v6);
                break;
            case 7:
                View v7 = inflater.inflate(R.layout.home_list_header_item_layout, parent, false);
                viewHolder = new MySubHomeHeaderViewHolder(v7);
                break;
            case 8:
                View v8 = inflater.inflate(R.layout.product_list_item_layout, parent, false);
                viewHolder = new MyProductListType1ViewHolder(v8);
                break;
            case 9:
                View v9 = inflater.inflate(R.layout.header_item_type_2_layout, parent, false);
                viewHolder = new MyHomeHeader2ViewHolder(v9);
                break;
            default:
                View v = inflater.inflate(R.layout.list_item_layout, parent, false);
                viewHolder = new MyViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(type.equals("catList")){
            Object item = itemList.get(position);
            if(item instanceof CatListItem){
                CatListItem myItem = (CatListItem) item;
                if(myItem.getType() == 0){
                    return 0;
                }else{
                    return 2;
                }

            }else if(item instanceof Category){
                if(position == 0){
                    return 4;
                }else{
                    return 1;
                }

            }else{
                return 3;
            }
        }else if(type.equals("subCatList")){
            Object item = itemList.get(position);
            if(position == 0){
                return 5;
            }else{
                return 6;
            }
        }else if(type.equals("productList")){
            Object item = itemList.get(position);
            if(position == 0){
                return 7;
            }else if(position == 1){
                return 9;
            }else{
                return 8;
            }
        } else{
            return 10;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            MyItem item = (MyItem) itemList.get(position);
            MyViewHolder myViewHolder = (MyViewHolder)holder;
            myViewHolder.textHeader.setText(item.getHeader());
            myViewHolder.textDesc.setText(item.getDesc());
        }else if(holder instanceof MyHomeHeaderViewHolder){

            CatListItem item = (CatListItem) itemList.get(position);
            MyHomeHeaderViewHolder myViewHolder = (MyHomeHeaderViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());
            myViewHolder.textDesc.setText(item.getDesc());

            myViewHolder.recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
            myViewHolder.recyclerView.setLayoutManager(layoutManager);
            myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            ProductAdapter myItemAdapter = new ProductAdapter(context,item.getItemList(),"catList");
            myViewHolder.recyclerView.setAdapter(myItemAdapter);

          //  StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
          //  layoutParams.setFullSpan(true);


        }else if(holder instanceof MyHomeHeader1ViewHolder){

            CatListItem item = (CatListItem) itemList.get(position);
            MyHomeHeader1ViewHolder myViewHolder = (MyHomeHeader1ViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());

            myViewHolder.recyclerView.setHasFixedSize(true);
            //RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            myViewHolder.recyclerView.setLayoutManager(staggeredGridLayoutManager);
            myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            ProductAdapter myItemAdapter = new ProductAdapter(context,item.getItemList(),"catList");
            myViewHolder.recyclerView.setAdapter(myItemAdapter);

          //  StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
         //   layoutParams.setFullSpan(true);


        }else if(holder instanceof MyListType3ViewHolder){

            SubCategory item = (SubCategory) itemList.get(position);
            MyListType3ViewHolder myViewHolder = (MyListType3ViewHolder)holder;
            myViewHolder.textTitle.setText(item.getName());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(true);

            Glide.with(context)
                    .load(item.getLocalImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);

            String ratio = String.format("%d:%d", (int)item.getWidth(),(int)item.getHeight());

            constraintSet.clone(myViewHolder.constraintLayout);
            constraintSet.setDimensionRatio(myViewHolder.imageView.getId(), ratio);
            constraintSet.applyTo(myViewHolder.constraintLayout);

        }else if(holder instanceof MyListType31ViewHolder){
            Category item = (Category) itemList.get(position);
            MyListType31ViewHolder myViewHolder = (MyListType31ViewHolder)holder;
            myViewHolder.textTitle.setText(item.getName());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            //requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(true);

            Glide.with(context)
                    .load(item.getLocalImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);
        }else if(holder instanceof MySubHomeHeaderViewHolder){

            HomeListItem item = (HomeListItem) itemList.get(position);
            MySubHomeHeaderViewHolder myViewHolder = (MySubHomeHeaderViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());
            myViewHolder.textDesc.setText(item.getDesc());

            if(!type.equals("productList")){
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }


        }else if(holder instanceof MyProductListType1ViewHolder){
            MyProduct item = (MyProduct) itemList.get(position);
            MyProductListType1ViewHolder myViewHolder = (MyProductListType1ViewHolder)holder;

            myViewHolder.textCatName.setText(item.getSubCatName());
            myViewHolder.textName.setText(item.getName()+", "+item.getCode());
            //myViewHolder.textAmount.setText("Rs. "+String.format("%.02f",item.getMrp()));
            myViewHolder.textMrp.setText("MRP: Rs"+item.getMrp());
            myViewHolder.textSellingPrice.setText("Selling Price: Rs"+item.getSellingPrice());
            myViewHolder.textDesc.setText(item.getDesc());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            requestOptions.centerCrop();
            requestOptions.skipMemoryCache(true);

            Glide.with(context)
                    .load(item.getLocalImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);


        }else if(holder instanceof MyHomeHeader2ViewHolder){

            MyHeader item = (MyHeader) itemList.get(position);
            MyHomeHeader2ViewHolder myViewHolder = (MyHomeHeader2ViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());


        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void zoomAnimation(boolean zoomOut,View view){
        if(zoomOut){
            Animation aniSlide = AnimationUtils.loadAnimation(context,R.anim.zoom_out);
            aniSlide.setFillAfter(true);
            view.startAnimation(aniSlide);
        }else{
            Animation aniSlide = AnimationUtils.loadAnimation(context,R.anim.zoom_in);
            aniSlide.setFillAfter(true);
            view.startAnimation(aniSlide);
        }
    }
}
