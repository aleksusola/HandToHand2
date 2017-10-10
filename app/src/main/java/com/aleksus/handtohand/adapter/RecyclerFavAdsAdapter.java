package com.aleksus.handtohand.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.aleksus.handtohand.RecyclerAdsItem;
import com.aleksus.handtohand.presentation.InfoAdActivity;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RecyclerFavAdsAdapter extends RecyclerView.Adapter<RecyclerFavAdsAdapter.ViewHolder> {

    private static final String TAG = "MYAPP";

    private List<RecyclerAdsItem> mListItems;
    private Context mContext;
    private String mUserPhone;
    private String mOwnerName;
    private String mOwnerFamily;

    public RecyclerFavAdsAdapter(List<RecyclerAdsItem> listItems, Context mContext) {
        this.mListItems = listItems;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_ads, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final RecyclerAdsItem itemList = mListItems.get(position);
        final BackendlessUser user = Backendless.UserService.CurrentUser();

        Backendless.UserService.findById(itemList.getAuthor(), new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser adsOwner) {
                if (user.getObjectId().equals(adsOwner.getObjectId())) {
                    mOwnerName = "Вы";
                    holder.txtAuthor.setText(mOwnerName);
                } else {
                    mOwnerName = adsOwner.getProperty("firstName").toString();
                    mOwnerFamily = adsOwner.getProperty("secondName").toString();
                    holder.txtAuthor.setText(mOwnerName + " " + mOwnerFamily);
                }
            }

            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });

        final String whereClause = "ads_users[collection].name = '" + itemList.getTitle() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("collection").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(final List<Map> foundCollection) {
                if (!foundCollection.isEmpty()) {
                    holder.txtCollection.setText(foundCollection.get(0).get("type").toString());
                } else {
                    holder.txtCollection.setText("Коллекция: null");
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtPrice.setText("Цена: " + itemList.getPrice());
        Date date = new Date(itemList.getCreated());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        holder.txtCreated.setText("Создан: " + dateFormat.format(date));
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
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.menu_option_fav);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_connect:
                                Toast.makeText(mContext, "Связываемся", Toast.LENGTH_SHORT).show();
                                Backendless.UserService.findById(itemList.getAuthor(), new AsyncCallback<BackendlessUser>() {
                                    public void handleResponse(BackendlessUser adsOwner) {
                                        mUserPhone = adsOwner.getProperty("phone").toString();
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:+7" + mUserPhone));
                                        mContext.startActivity(intent);
                                    }

                                    public void handleFault(BackendlessFault fault) {
                                        Log.e(TAG, "server reported an error - " + fault.getMessage());
                                    }
                                });
                                break;
                            case R.id.mnu_item_full:
                                Toast.makeText(mContext, "Редактирование", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(holder.itemView.getContext(), InfoAdActivity.class);
                                intent.putExtra("title", itemList.getTitle());
                                intent.putExtra("author", itemList.getAuthor());
                                intent.putExtra("collection", holder.txtCollection.getText());
                                intent.putExtra("created", holder.txtCreated.getText());
                                intent.putExtra("mOwnerName", holder.txtAuthor.getText());
                                mContext.startActivity(intent);
                                break;
                            case R.id.mnu_item_remove:
                                Backendless.Data.of(BackendlessUser.class).deleteRelation(user, "favorites", "name = '" + itemList.getTitle() + "'", new AsyncCallback<Integer>() {
                                    @Override
                                    public void handleResponse(Integer response) {
                                        mListItems.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext, "Удалено из избранных", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Log.e(TAG, "server reported an error - " + fault.getMessage());
                                    }
                                });
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
        return mListItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtAuthor;
        TextView txtCollection;
        TextView txtPrice;
        TextView txtOptionDigit;
        final ImageView photoIcon;
        TextView txtCreated;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.textview_title);
            txtAuthor = (TextView) itemView.findViewById(R.id.textview_author);
            txtCollection = (TextView) itemView.findViewById(R.id.textview_collection);
            txtPrice = (TextView) itemView.findViewById(R.id.textview_price);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.textview_option_digit);
            photoIcon = (ImageView) itemView.findViewById(R.id.imageview_photo);
            txtCreated = (TextView) itemView.findViewById(R.id.textview_created);
        }
    }
}

