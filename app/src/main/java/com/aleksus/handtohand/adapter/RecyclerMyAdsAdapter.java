package com.aleksus.handtohand.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.aleksus.handtohand.R;
import com.aleksus.handtohand.RecyclerMyAdsItem;
import com.aleksus.handtohand.presentation.EditActivity;
import com.aleksus.handtohand.presentation.InfoAdActivity;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RecyclerMyAdsAdapter extends RecyclerView.Adapter<RecyclerMyAdsAdapter.ViewHolder> {

    private List<RecyclerMyAdsItem> listItemsMy;
    private Context mContext;
    private AlertDialog.Builder sure;

    private static final String TAG = "MYAPP";

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

        final String message = "Вы действительно хотите удалить объявление ";
        final String buttonYes = "Да";
        final String buttonNo = "Нет";

        final RecyclerMyAdsItem itemList = listItemsMy.get(position);
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtPrice.setText("Цена: " + itemList.getPrice());
        Date date = new Date(itemList.getCreated());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        holder.txtCreated.setText("Создан: " + dateFormat.format(date));
        String whereClause = "ads_users[collection].name = '" + itemList.getTitle() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("collection").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(final List<Map> foundCollection) {
                holder.txtCollection.setText(foundCollection.get(0).get("type").toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("MYAPP", "server reported an error - " + fault.getMessage());
            }
        });
        Glide
                .with(mContext)
                .load(itemList.getPhoto())
                .placeholder(R.mipmap.ic_record_voice_over_black)
                .error(R.drawable.ic_error)
                .override(200, 200)
                .crossFade(100)
                .into(holder.photoIcon);
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
                                Toast.makeText(mContext, "Редактирование", Toast.LENGTH_SHORT).show();
                                Intent intentEdit = new Intent(holder.itemView.getContext(), EditActivity.class);
                                intentEdit.putExtra("title", itemList.getTitle());
                                intentEdit.putExtra("collection", holder.txtCollection.getText());
                                mContext.startActivity(intentEdit);
                                break;
                            case R.id.mnu_item_full:
                                Toast.makeText(mContext, "Подробно", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(holder.itemView.getContext(), InfoAdActivity.class);
                                intent.putExtra("title", itemList.getTitle());
                                intent.putExtra("price", itemList.getPrice());
                                intent.putExtra("author", "Вы");
                                intent.putExtra("collection", holder.txtCollection.getText());
                                intent.putExtra("created", holder.txtCreated.getText());
                                intent.putExtra("desc", itemList.getDesc());
                                intent.putExtra("image", itemList.getPhoto());
                                mContext.startActivity(intent);
                                break;
                            case R.id.mnu_item_hide:
                                sure = new AlertDialog.Builder(mContext);
                                sure.setMessage(message + itemList.getTitle());
                                sure.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        String whereClause = "name = '" + itemList.getTitle() + "'";
                                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                        queryBuilder.setWhereClause(whereClause);
                                        Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                                            @Override
                                            public void handleResponse(List<Map> delete) {
                                                Backendless.Persistence.of("ads_users").remove(delete.get(0), new AsyncCallback<Long>() {
                                                    @Override
                                                    public void handleResponse(Long response) {
                                                        listItemsMy.remove(position);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(mContext, "Объявление удалено", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void handleFault(BackendlessFault fault) {
                                                        Log.e(TAG, "server reported an error - " + fault.getMessage());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                Log.e(TAG, "server reported an error - " + fault.getMessage());
                                            }
                                        });
                                    }
                                });
                                sure.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        Toast.makeText(mContext, "Отменено", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                sure.setCancelable(false);
                                sure.show();
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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtCollection;
        TextView txtPrice;
        TextView txtOptionDigit;
        ImageView photoIcon;
        TextView txtCreated;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtCollection = (TextView) itemView.findViewById(R.id.txtCollection);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            photoIcon = (ImageView) itemView.findViewById(R.id.photoIcon);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            txtCreated = (TextView) itemView.findViewById(R.id.txtCreated);
        }
    }

}