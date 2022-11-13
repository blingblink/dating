package com.quintus.labs.datingapp.Login;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.quintus.labs.datingapp.Profile.EditProfileActivity;
import com.quintus.labs.datingapp.R;
import com.quintus.labs.datingapp.Utils.GPS;
import com.quintus.labs.datingapp.Utils.User;



public class RegisterBasicInfo extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Context mContext;
    private String email, username, password;
    private EditText  mUsername;

    private Button btnRegister;
    private String append = "";

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerbasic_info);
        mContext = RegisterBasicInfo.this;
        Log.d(TAG, "onCreate: started");
        initWidgets();
        init();
    }

    private void init() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mUsername.getText().toString();
                email = getIntent().getStringExtra("email");
                if (checkInputs(username)) {
                    //find geo location
                    //find geo location
                    Intent intent = new Intent(RegisterBasicInfo.this, RegisterGender.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkInputs(String username) {
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (username.equals("")) {
            Toast.makeText(mContext, "All fields must be filed out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: initializing widgets");

        mUsername = findViewById(R.id.input_username);
        btnRegister = findViewById(R.id.btn_register);

        mContext = RegisterBasicInfo.this;

    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));

    }


}

