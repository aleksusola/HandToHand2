package com.aleksus.handtohand.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aleksus.handtohand.R;
import com.aleksus.handtohand.RecyclerMyAdsAdapter;
import com.aleksus.handtohand.RecyclerMyAdsItem;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyAdsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyAds;
    private RecyclerMyAdsAdapter adapterMy;
    private List<RecyclerMyAdsItem> listItemsMy;
    private String parentObjectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myads);

        recyclerViewMyAds = (RecyclerView) findViewById(R.id.recyclerViewMyAds);
        recyclerViewMyAds.setHasFixedSize(true);
        recyclerViewMyAds.setLayoutManager(new LinearLayoutManager(this));

        BackendlessUser AdsOwner = Backendless.UserService.CurrentUser();
        parentObjectId = AdsOwner.getObjectId();
        final String whereClause = "ownerId = '" + parentObjectId + "'";
        final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( whereClause );
        Backendless.Data.of( "ads_users" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
            @Override
            public void handleResponse(final List<Map> foundMyAds ) {
                Backendless.Data.of( "ads_users" ).getObjectCount( queryBuilder, new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse( Integer adsCnt ) {
                        listItemsMy = new ArrayList<>();
                        for (int i = 0; i<adsCnt; i++) {
                            listItemsMy.add(new RecyclerMyAdsItem(foundMyAds.get(i).get("name").toString(), foundMyAds.get(i).get( "description" ).toString(), foundMyAds.get(i).get("collection").toString() , "Цена: " + foundMyAds.get(i).get("price"), foundMyAds.get(i).get("ads_icon").toString() ));
                        }
                        adapterMy = new RecyclerMyAdsAdapter(listItemsMy, MyAdsActivity.this);
                        recyclerViewMyAds.setAdapter(adapterMy);
                    }
                    @Override
                    public void handleFault( BackendlessFault backendlessFault ) {
                        Log.e( "MYAPP", " error - " + backendlessFault.getMessage() );
                    }
                } );
            }
            @Override
            public void handleFault( BackendlessFault fault ) {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        });
    }
}
