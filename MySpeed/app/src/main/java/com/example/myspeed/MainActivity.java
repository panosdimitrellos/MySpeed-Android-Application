package com.example.myspeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView introtext;
    TextView definedSpeedLimit;
    EditText editText;
    SharedPreferences preferences;
    Button ssb ;

    private static final int REC_RESULT = 653;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ssb = findViewById(R.id.showSpeedButton);
        introtext = findViewById(R.id.introText);
        definedSpeedLimit = findViewById(R.id.definedSpeedLimit);
        editText = findViewById(R.id.editTextTextPersonName);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Αν δεν έχει οριστεί κάποιο speed limit απο το χρήστη, τότε αυτό παίρνει την default τιμή 10.
        float f1 = preferences.getFloat("speedlimit", 10);
        String string1 = String.valueOf(f1) ;
        definedSpeedLimit.setText(string1);

    }

    // Μέθοδος που χρεισιμοποιούμε για να γίνει μια ενέργεια οταν πραγματοποιηθεί το speech recognition.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // Αν πει ο χρήστης "show my speed" τότε εκτελείται η ενέργεια του κουμπιού "Show my speed".
            if (matches.contains("show my speed"))
                ssb.performClick();
        }
    }

    // Μέθοδος αναγώρισης φωνής που ενεργοιείται με το πάτημα του κουμπιού "Use voice command"
    public void recognizeVoice(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say something!");
        startActivityForResult(intent, REC_RESULT);
    }

    // Η λειτουργία του κουμπιού "Show my speed".
    public void showSpeed(View view){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    // Η λειτουργία του κουμπιού "Change".
    public void changeLimit(View  view){
        // Δημιουργούμε έναν editor που θα μας επιτρέψει να νέα δεδομένα στα shared preferences.
        SharedPreferences.Editor editor = preferences.edit();
        String value= editText.getText().toString();
        // Μετατρέπουμε το input του χρήστη σε float.
        float finalValue = Float.valueOf(value);
        // Βάζουμε στα shared preferences το όριο ταχύτητας που ορίζει ο χρήστης.
        editor.putFloat("speedlimit", finalValue);
        editor.apply();
        Toast.makeText(this, "Limit changed!", Toast.LENGTH_LONG).show();

        // Εμφανίζουμε τα καινούργια δεδομένα απο τα shared preferences που έχει ορίσει ο χρήστης.
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        float f2 = preferences.getFloat("speedlimit", 10);
        String string2 = String.valueOf(f2) ;
        definedSpeedLimit.setText(string2);
    }
}