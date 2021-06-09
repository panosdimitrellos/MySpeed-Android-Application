package com.example.myspeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity3 extends AppCompatActivity {

    TextView tv_Data;
    Switch aSwitch;

    SharedPreferences preferences;
    float f3 ;
    SQLiteDatabase db;
    StringBuilder stringBuilder2 = new StringBuilder();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Ορίζουμε την βάση μας για να μπορεί το activity να έχει πρόσβαση σε αυτήν.
        db = openOrCreateDatabase("Saved_Data_For_MySpeed", Context.MODE_PRIVATE,null);

        // Δημιουργούμε αντικείμενο shared preferences και στην συνέχεια
        // μεταφέρουμε απο το MainActivity τα δεδομένα που έχουμε αποθηκεύσει στα preferences με
        // κλειδί "speedlimit".
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        f3 = preferences.getFloat("speedlimit", 10);
        tv_Data = findViewById(R.id.textViewData);

        addAllData();

        aSwitch = findViewById(R.id.switch2);

        // Ενέργεια που χρησιμοποιείτε αν ο διακόπτης switch πατηθεί.
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true){
                    addSelectedData();
                }
                else {
                    addAllData();
                }
            }
        });
    }

    // Με την μέθοδο addAllData() τραβάμε όλα τα δεδομένα που έχπουμε κανει putExtras()
    // στο MainActivity2, με κλειδί "data".
    public void addAllData(){
        Bundle bundle = getIntent().getExtras();
        String data = bundle.getString("data");
        tv_Data.setText(data);
        Toast.makeText(this,"All speed limit exceedances",Toast.LENGTH_LONG).show();
    }

    public void addSelectedData(){
        Toast.makeText(this,"Speed limit exceedances above current limit",Toast.LENGTH_LONG).show();

        // Ζητάμε από την βάση να τραβήξει όλες τις καταγραφές υπέρβασης του ορίου απο τον πίνακα Speed_Limit_Exceedances
        // όπου το speed είναι μεγαλύτερο του f3 δηλαδή του δοσμένου ορίου ταχύτητας.
        Cursor cursor2 = db.rawQuery("SELECT * FROM Speed_Limit_Exceedances WHERE speed>"+f3,null);
        if(cursor2.getCount()>0){
            while (cursor2.moveToNext()){
                stringBuilder2.append("Speed: ").append(cursor2.getString(0)).append("\n");
                stringBuilder2.append("Time: ").append(cursor2.getString(1)).append("\n");
                stringBuilder2.append("Location: ").append(cursor2.getString(2)).append("\n");
                stringBuilder2.append("---------------------------------------------------------\n");
            }
        }

        String strData2 = stringBuilder2.toString();
        tv_Data.setText(strData2);
    }
}