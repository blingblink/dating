package com.quintus.labs.datingapp.Matched;

import static com.quintus.labs.datingapp.Login.Login.emailLogin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.quintus.labs.datingapp.R;
import com.quintus.labs.datingapp.Utils.TopNavigationViewHelper;
import com.quintus.labs.datingapp.Utils.User;

import java.util.ArrayList;
import java.util.List;



public class Matched_Activity extends AppCompatActivity {

    private static final String TAG = "Matched_Activity";
    private static final int ACTIVITY_NUM = 2;
    List<Users> matchList = new ArrayList<>();
    List<User> copyList = new ArrayList<>();
    private Context mContext = Matched_Activity.this;
    private String userId, userSex;
    private double latitude = 37.349642;
    private double longtitude = -121.938987;
    private EditText search;
    private List<Users> usersList = new ArrayList<>();
    private RecyclerView recyclerView, mRecyclerView;
    private ActiveUserAdapter adapter;
    private MatchUserAdapter mAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef,matchRef;
    private String keyID;
    private List<String> keyList = new ArrayList<>();
    private List<String> keyList2 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched);

        setupTopNavigationView();
        searchFunc();
        recyclerView = findViewById(R.id.recyclerView);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("User");
        matchRef = mFirebaseDatabase.getReference("Match");
        Query query = myRef.orderByChild("email").equalTo(emailLogin);//get the user's email from login activity
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    keyID = singleSnapshot.getKey();//get the user's key id
                    Log.d("testAPI", "onDataChange: keyID: " + keyID);
                }
                matchRef.child(keyID).addValueEventListener(new ValueEventListener() {//get the user's match list
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if(dataSnapshot.child("connection").getValue().equals("Like")){//get the user's match list  who like the user
                                keyList.add(dataSnapshot.getKey());//get the user's match list  who like the user's key id
                            }
                            Log.d("testAPI", "onDataChange: keyList: " + keyList);
                        }
                        for(int i = 0; i < keyList.size(); i++){
                            int finalI = i;
                            matchRef.child(keyList.get(i)).addValueEventListener(new ValueEventListener() {//get the user's match list  who like the user's match list
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if(dataSnapshot.child("connection").getValue().equals("Like") && dataSnapshot.getKey().equals(keyID)){ //get the user's match list  who like the user's match list who like the user back
                                            keyList2.add(keyList.get(finalI));//get the user's match list  who like the user's match list who like the user back's key id
                                        }
                                        Log.d("testAPI", "onDataChange: keyList2: " + keyList2);
                                    }
                                    for(int i = 0; i < keyList2.size(); i++){//get the user's match list  who like the user's match list who like the user back's information
                                        myRef.child(keyList2.get(i)).addValueEventListener(new ValueEventListener() {//get the user's match list  who like the user's match list who like the user back's information
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String name = snapshot.child("username").getValue().toString();
                                                String profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                                                String profession = snapshot.child("email").getValue().toString();

                                                Users users = new Users();
                                                users.setName(name);
                                                users.setProfileImageUrl(profileImageUrl);
                                                users.setUserId(profession);
                                                matchList.add(users);
                                                //xóa trùng lặp
                                                for (int i = 0; i < matchList.size(); i++) {
                                                    for (int j = i + 1; j < matchList.size(); j++) {
                                                        if (matchList.get(i).getName().equals(matchList.get(j).getName())) {
                                                            matchList.remove(j);
                                                            j--;
                                                        }
                                                    }
                                                }
                                                Log.d(TAG, "onDataChange: " + matchList);
                                                mAdapter = new MatchUserAdapter(matchList, mContext);
                                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                                recyclerView.setLayoutManager(mLayoutManager);
                                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                                recyclerView.setAdapter(mAdapter);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new ActiveUserAdapter(usersList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }



    private void searchFunc() {
       /* search = findViewById(R.id.searchBar);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText();
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText();
            }
        });*/
    }

    /* private void searchText() {
         String text = search.getText().toString().toLowerCase(Locale.getDefault());
         if (text.length() != 0) {
             if (matchList.size() != 0) {
                 matchList.clear();
                 for (User user : copyList) {
                     if (user.getUsername().toLowerCase(Locale.getDefault()).contains(text)) {
                         matchList.add(user);
                     }
                 }
             }
         } else {
             matchList.clear();
             matchList.addAll(copyList);
         }

         mAdapter.notifyDataSetChanged();
     }

     private boolean checkDup(User user) {
         if (matchList.size() != 0) {
             for (User u : matchList) {
                 if (u.getUsername() == user.getUsername()) {
                     return true;
                 }
             }
         }

         return false;
     }

     private void checkClickedItem(int position) {

         User user = matchList.get(position);
         //calculate distance
         Intent intent = new Intent(this, ProfileCheckinMatched.class);
         intent.putExtra("classUser", user);

         startActivity(intent);
     }
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


}
