package com.quintus.labs.datingapp.Main;

import static com.quintus.labs.datingapp.Login.Login.emailLogin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import com.quintus.labs.datingapp.Utils.PulsatorLayout;
import com.quintus.labs.datingapp.Utils.TopNavigationViewHelper;
import com.quintus.labs.datingapp.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity{
    public static MainActivity instance;
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 1;
    private MutableLiveData<List<Cards>> cardsData;
    final private int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    ListView listView;
    List<Cards> rowItems = new ArrayList<>();
    FrameLayout cardFrame, moreFrame;
    private Context mContext = MainActivity.this;
    private NotificationHelper mNotificationHelper;
    private Cards cards_data[];
    private PhotoAdapter arrayAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef,matchRef;
    private String email1;
    private boolean checkIs = false;
    public  String keyID = "";
    private List<String> keyList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTopNavigationView();
        cardsData = new MutableLiveData<>();
        cardFrame = findViewById(R.id.card_frame);
        moreFrame = findViewById(R.id.more_frame);
        // start pulsator
        if(instance == null){ // if there is no instance available... create new one
            instance = this;
        }
        PulsatorLayout mPulsator = findViewById(R.id.pulsator);
        mPulsator.start();
        mNotificationHelper = new NotificationHelper(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();// get instance of database
        myRef = mFirebaseDatabase.getReference("User");// get reference to "User" node
        matchRef = mFirebaseDatabase.getReference("Match");// get reference to "Match" node
        checkRowItem(rowItems);

        //query for getting data from database and adding to rowItems
        Query query = myRef.orderByChild("email").equalTo(emailLogin);// get all data from database
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    keyID = singleSnapshot.getKey();// get key of user from database
                    Log.d("testAPI", "onDataChange: keyID: " + keyID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkRowItem(List<Cards> rowItems) {
        if (rowItems.isEmpty()) {
            Log.d("testAPI", "checkRowItem: rowItems is empty");
            moreFrame.setVisibility(View.VISIBLE);
            cardFrame.setVisibility(View.GONE);
        }else{
            Log.d("testAPI", "checkRowItem: rowItems is not empty");
            moreFrame.setVisibility(View.GONE);
            cardFrame.setVisibility(View.VISIBLE);
        }
    }

    private void updateLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        updateLocation();
                    } else {
                        Toast.makeText(MainActivity.this, "Location Permission Denied. You have to give permission inorder to know the user range ", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateSwipeCard() {
        final SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        try {
            arrayAdapter = new PhotoAdapter(mContext, R.layout.item,rowItems);
            flingContainer.setAdapter(arrayAdapter);
        }catch (Exception e){
            Log.d("testAPI", "updateSwipeCard: "+e.getMessage());
        }
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                checkRowItem(rowItems);
                String userId = obj.getUserId();
                matchRef.child(keyID).child(userId).child("connection").setValue("Dislike");// set value to database when user swipe left
            }
            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                checkRowItem(rowItems);
                String userId = obj.getUserId();
                matchRef.child(keyID).child(userId).child("connection").setValue("Like");// set value to database when user swipe right and check if user is matched
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here


            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void sendNotification() {
        NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(mContext.getString(R.string.app_name), mContext.getString(R.string.match_notification));

        mNotificationHelper.getManager().notify(1, nb.build());
    }
    private int getAge(int year, int month, int day) {
        Calendar dateOfBirth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dateOfBirth.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }


    public void DislikeBtn(View v) {
        if (rowItems.size() != 0) {
            Cards card_item = rowItems.get(0);
            String userId = card_item.getUserId();
            card_item.setUserId(userId);
            matchRef.child(keyID).child(userId).child("connection").setValue("Dislike");// set value to database when user swipe left and check if user is matched
            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();
            Intent btnClick = new Intent(mContext, BtnDislikeActivity.class);
            btnClick.putExtra("url", card_item.getProfileImageUrl());
            startActivity(btnClick);

        }
    }

    public void LikeBtn(View v) {
        if (rowItems.size() != 0) {
            Cards card_item =rowItems.get(0);
            String userId = card_item.getUserId();
            matchRef.child(keyID).child(userId).child("connection").setValue("Like");// set value to database when user swipe right and check if user is matched
            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();
            Intent btnClick = new Intent(mContext, BtnLikeActivity.class);
            btnClick.putExtra("url", card_item.getProfileImageUrl());
            startActivity(btnClick);
        }
    }


    /**
     * setup top tool bar
     */
    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationViewEx tvEx = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
    //check if user is matched with other user
    private void updateV(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String ns = ds.child("dateOfBirth").getValue().toString();
                    String[] arr = ns.split("-");
                    String email = ds.child("email").getValue().toString();
                    String key1 = ds.getKey();
                    if(!email.equals(emailLogin)) {//check if user is matched with other user
                        matchRef.child(keyID).child(key1).addValueEventListener(new ValueEventListener() {//check if user is matched with other user
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){//check if user is matched with other user and if not exist then add to database
                                    Cards item = new Cards(
                                            ds.getKey(),
                                            ds.child("username").getValue().toString(),
                                            ds.child("profileImageUrl").getValue().toString(),
                                            getAge(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]))
                                    );
                                    rowItems.add(item);//add to database if user is matched with other user
                                }
                                checkRowItem(rowItems);
                                updateSwipeCard();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateV();
    }
}
