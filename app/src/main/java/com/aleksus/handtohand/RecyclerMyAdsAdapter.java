package com.aleksus.handtohand;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksus.handtohand.presentation.EditActivity;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

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

        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display option menu

                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.menu_option_myads);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_change:
                                Toast.makeText(mContext, "Редактирование", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(holder.itemView.getContext(),  EditActivity.class);
                                intent.putExtra("title", itemList.getTitle());
                                mContext.startActivity(intent);

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
        public TextView txtCollection;
        public TextView txtPrice;
        public TextView txtOptionDigit;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtCollection = (TextView) itemView.findViewById(R.id.txtCollection);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
        }
    }

    interface OnClickItemListener {
        void OnClickItemListener();
    }

}