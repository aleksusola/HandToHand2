package com.aleksus.handtohand;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.aleksus.handtohand.R.id.photoIcon;
import static com.aleksus.handtohand.R.id.right;
import static com.aleksus.handtohand.R.id.txtDesc;
import static weborb.util.ThreadContext.context;

public class RecyclerAdsAdapter extends RecyclerView.Adapter<RecyclerAdsAdapter.ViewHolder> {

    private List<RecyclerAdsItem> listItems;
    private Context mContext;
    public String userPhone;
    public String ownerName;

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
        final BackendlessUser user = Backendless.UserService.CurrentUser();

        Backendless.UserService.findById(itemList.getAuthor(), new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser adsOwner ) {
                if (user.getObjectId().equals(adsOwner.getObjectId())) {
                    ownerName = "Вы";
                    holder.txtAuthor.setText(ownerName);
                } else {
                    ownerName = adsOwner.getProperty( "login" ).toString();;
                    holder.txtAuthor.setText(ownerName);
                }
            }

            public void handleFault( BackendlessFault fault ) {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        });

        String whereClause = "ads_users[collection].name = '"+ itemList.getTitle() +"'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( whereClause );
        Backendless.Data.of( "collection" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
            @Override
            public void handleResponse(final List<Map> foundCollection ) {
                if (!foundCollection.isEmpty()) {
                    holder.txtCollection.setText("Коллекция: " + foundCollection.get(0).get("type").toString());
                } else {
                    holder.txtCollection.setText("Коллекция: null");
                }
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        });
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtDesc.setText(itemList.getDesc());
        holder.txtPrice.setText(itemList.getPrice());
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
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:+7"+ userPhone));
                                        mContext.startActivity(intent);
                                    }

                                    public void handleFault( BackendlessFault fault ) {
                                        Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                                    }
                                });
                                break;
                            case R.id.mnu_item_full:
                                Toast.makeText(mContext, "Подробнее", Toast.LENGTH_LONG).show();
                                holder.txtDesc.setVisibility(View.VISIBLE);
                                holder.txtHide.setVisibility(View.VISIBLE);
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
        public TextView txtDesc;
        public TextView txtAuthor;
        public TextView txtCollection;
        public TextView txtPrice;
        public TextView txtOptionDigit;
        public ImageView photoIcon;
        public TextView txtHide;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
            txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
            txtCollection = (TextView) itemView.findViewById(R.id.txtCollection);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            photoIcon = (ImageView) itemView.findViewById(R.id.photoIcon);
            txtHide = (TextView) itemView.findViewById(R.id.txtHide);
        }
    }
}

