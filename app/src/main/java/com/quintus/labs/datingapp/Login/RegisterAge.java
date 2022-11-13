package com.quintus.labs.datingapp.Login;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quintus.labs.datingapp.Main.MainActivity;
import com.quintus.labs.datingapp.R;
import com.quintus.labs.datingapp.Utils.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class RegisterAge extends AppCompatActivity {

    String password,username,email,sex,preferSex;
    User user;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
    private DatePicker ageSelectionPicker;
    private Button ageContinueButton;
    // age limit attribute
    private int ageLimit = 18;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_age);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email  = intent.getStringExtra("email");
        sex = intent.getStringExtra("sex");
        preferSex = intent.getStringExtra("preferSex");

        ageSelectionPicker = findViewById(R.id.ageSelectionPicker);


        ageContinueButton = findViewById(R.id.ageContinueButton);

        ageContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHobbiesEntryPage();
            }
        });


    }

    public void openHobbiesEntryPage() {
        int age = getAge(ageSelectionPicker.getYear(), ageSelectionPicker.getMonth(), ageSelectionPicker.getDayOfMonth());

        // trên 18 tuổi thì mới đc dùng
        if (age > ageLimit) {
            //  converting date to string
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, ageSelectionPicker.getYear());
            cal.set(Calendar.MONTH, ageSelectionPicker.getMonth());
            cal.set(Calendar.DAY_OF_MONTH, ageSelectionPicker.getDayOfMonth());
            Date dateOfBirth = cal.getTime();
            String strDateOfBirth = dateFormatter.format(dateOfBirth);

            //  set the dateOfBirthAttribute.

            User  user1 = new User();
            user1.setUsername(username);
            user1.setEmail(email);
            user1.setDateOfBirth(strDateOfBirth);
            user1.setSex(sex);
            user1.setPreferSex(preferSex);
            user1.setProfileImageUrl(getIntent().getStringExtra("defaultPhoto"));
            myRef.push().setValue(user1);// push data to firebase database
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Tuổi của người dùng phải lớn hơn " + ageLimit + " !!!", Toast.LENGTH_SHORT).show();
        }

    }

    // method to get the current age of the user.
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
}
