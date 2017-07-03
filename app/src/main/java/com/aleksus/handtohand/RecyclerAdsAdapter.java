package com.aleksus.handtohand;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;

public class RecyclerAdsAdapter extends RecyclerView.Adapter<RecyclerAdsAdapter.ViewHolder> {

    private List<RecyclerAdsItem> listItems;
    private Context mContext;
    public String userPhone;
    public String userName;

    public RecyclerAdsAdapter(List<RecyclerAdsItem> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_ads, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final RecyclerAdsItem itemList = listItems.get(position);
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtCollection.setText(itemList.getCollection());
        holder.txtPrice.setText(itemList.getPrice());

        Backendless.UserService.findById(itemList.getAuthor().toString(), new AsyncCallback<BackendlessUser>() {
            public void handleResponse( BackendlessUser adsOwner ) {
                userName = adsOwner.getProperty( "name" ).toString();;
            }

            public void handleFault( BackendlessFault fault ) {
                // login failed, to get the error code call fault.getCode()
            }
        });
        holder.txtAuthor.setText(userName);
        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display option menu

                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.menu_option);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_connect:
                                Toast.makeText(mContext, "Связываемся", Toast.LENGTH_LONG).show();
                                Backendless.UserService.findById(itemList.getAuthor().toString(), new AsyncCallback<BackendlessUser>() {
                                    public void handleResponse( BackendlessUser adsOwner ) {
                                        userPhone = adsOwner.getProperty( "phone" ).toString();
                                        userName = adsOwner.getProperty( "name" ).toString();
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:+7"+ userPhone));
                                        mContext.startActivity(intent);
                                    }

                                    public void handleFault( BackendlessFault fault ) {
                                        // login failed, to get the error code call fault.getCode()
                                    }
                                });
                                break;
                            case R.id.mnu_item_delete:
                                //Delete item
                                listItems.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_LONG).show();
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
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtTitle;
        public TextView txtAuthor;
        public TextView txtCollection;
        public TextView txtPrice;
        public TextView txtOptionDigit;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
            txtCollection = (TextView) itemView.findViewById(R.id.txtCollection);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
        }
    }
}

