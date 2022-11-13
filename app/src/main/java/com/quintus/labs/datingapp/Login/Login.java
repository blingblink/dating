package com.quintus.labs.datingapp.Login;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quintus.labs.datingapp.Main.MainActivity;
import com.quintus.labs.datingapp.R;


import java.lang.reflect.Array;
import java.util.ArrayList;


public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static String emailLogin = "email";
    private ImageView mGoogleSignIn;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private Context mContext;
    private ArrayList<String> mEmails = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = Login.this;
        mGoogleSignIn = findViewById(R.id.google);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //check if user is already signed in and if so, go to main activity
        //login with google
        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();//get account info from google
                CheckEmail(account.getEmail());//check if email is already in database
                Log.d("testAPI", "onActivityResult: " + account.getEmail());
            } catch (Exception e) {
                Log.w("testAPI", "Google sign in failed : "+ e.getMessage());
            }
        }
    }

    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking string if null.");

        return string.equals("");
    }

    //----------------------------------------Firebase----------------------------------------




    @Override
    public void onBackPressed() {

    }

    private void CheckEmail(String email1){//check if email is already in database
        FirebaseDatabase database = FirebaseDatabase.getInstance();//get database
        DatabaseReference myRef = database.getReference("User");//get reference to user
        myRef.addValueEventListener(new ValueEventListener() {//add value event listener
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mEmails.add(ds.child("email").getValue().toString());
                }
                Intent intent;
                if(mEmails.contains(email1)){//if email is in database go to main activity
                    intent = new Intent(mContext, MainActivity.class);//go to main activity
                    emailLogin = email1;
                } else {//if email is not in database go to register activity
                    intent = new Intent(mContext, RegisterBasicInfo.class);//go to register activity
                    intent.putExtra("email", email1);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
