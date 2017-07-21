package com.aleksus.handtohand;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksus.handtohand.presentation.EditActivity;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class RecyclerMyAdsAdapter extends RecyclerView.Adapter<RecyclerMyAdsAdapter.ViewHolder> {

    private List<RecyclerMyAdsItem> listItemsMy;
    private Context mContext;

    public RecyclerMyAdsAdapter(List<RecyclerMyAdsItem> listItemsMy, Context mContext) {
        this.listItemsMy = listItemsMy;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_my_ads, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final RecyclerMyAdsItem itemList = listItemsMy.get(position);
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtDesc.setText(itemList.getDesc());
        holder.txtPrice.setText(itemList.getPrice());
        String whereClause = "ads_users[collection].name = '"+ itemList.getTitle() +"'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( whereClause );
        Backendless.Data.of( "collection" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
            @Override
            public void handleResponse(final List<Map> foundCollection ) {
                holder.txtCollection.setText("Коллекция: " + foundCollection.get(0).get("type").toString());
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        });
        Picasso.with(mContext).load(itemList.getPhoto()).into(holder.photoIcon);
        holder.txtHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.txtDesc.setVisibility(View.GONE);
                holder.txtHide.setVisibility(View.GONE);
            }
        });
        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.menu_option_myads);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_change:
                                Toast.makeText(mContext, "Редактирование", Toast.LENGTH_LONG).show();
                                holder.photoIcon.buildDrawingCache();
                                Bitmap image = holder.photoIcon.getDrawingCache();
                                Bundle extras = new Bundle();
                                extras.putParcelable("imagebitmap", image);
                                Intent intent = new Intent(holder.itemView.getContext(),  EditActivity.class);
                                intent.putExtra("title", itemList.getTitle());
                                intent.putExtras(extras);
                                mContext.startActivity(intent);
                                break;
                            case R.id.mnu_item_full:
                                Toast.makeText(mContext, "Подробнее", Toast.LENGTH_LONG).show();
                                holder.txtDesc.setVisibility(View.VISIBLE);
                                holder.txtHide.setVisibility(View.VISIBLE);
                                break;
                            case R.id.mnu_item_delete:
                                //Delete item
                                listItemsMy.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Скрываем", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemsMy.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtTitle;
        public TextView txtDesc;
        public TextView txtCollection;
        public TextView txtPrice;
        public TextView txtOptionDigit;
        public ImageView photoIcon;
        public TextView txtHide;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
            txtCollection = (TextView) itemView.findViewById(R.id.txtCollection);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            photoIcon = (ImageView) itemView.findViewById(R.id.photoIcon);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            txtHide = (TextView) itemView.findViewById(R.id.txtHide);
        }
    }

    interface OnClickItemListener {
        void OnClickItemListener();
    }

}