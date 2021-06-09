package com.example.myspeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements LocationListener {

    TextView speedlimit;
    TextView tv_speed;
    Button ssh;
    SharedPreferences preferences;

    LocationManager locationManager;
    // Μεταβλητές για να αποθηκεύσουμε τα δεδομένα για την βάση δεδομένων.
    String strCurrentLocation;
    String strDateTime;
    String strPassedLimitSpeed;

    SQLiteDatabase db;
    StringBuilder stringBuilder = new StringBuilder();

    CTextToSpeech cTextToSpeech;
    private static final int REC_RESULT = 643;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        speedlimit = findViewById(R.id.textView13);
        tv_speed = findViewById(R.id.tv_speed);
        ssh = findViewById(R.id.buttonShowHistory);

        // Δημιουργούμε αντικείμενο shared preferences και στην συνέχεια
        // μεταφέρουμε απο το MainActivity τα δεδομένα που έχουμε αποθηκεύσει στα preferences με
        // κλειδί "speedlimit".
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        float f3 = preferences.getFloat("speedlimit", 10);
        String string3 = String.valueOf(f3) ;
        speedlimit.setText(string3);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps();
        this.UpdateSpeed(null);

        // Δημιουργούμε την Βάση δεδομένων με όνομα "Saved_Data_For_MySpeed".
        db = openOrCreateDatabase("Saved_Data_For_MySpeed", Context.MODE_PRIVATE,null);
        // Στην συνέχεια δημιουργούμε τον πίνακα "Speed_Limit_Exceedances"
        // για να αποθηκεύσουμε τα δεδομένα "speed" , "time", "location".
        db.execSQL("CREATE TABLE IF NOT EXISTS Speed_Limit_Exceedances(speed TEXT,time TEXT,location TEXT)");

        cTextToSpeech= new CTextToSpeech(this);

    }


    private void UpdateSpeed(CLocation location) {
        float nCurrentSpeed = 0;

        // Γίνεται έλεγχος αν αλλάζει η τοποθεσία του χρήστη.
        if(location!= null)
        {
            // Στην μεταβλητή nCurrentSpeed αποθηκεύουμε το ταχύτητα του χρήστη την πιο πρόσφατη
            // χρονική στιγμή που η τοποθεσία του έχει αλλάξει .
            nCurrentSpeed= location.getSpeed();
            strPassedLimitSpeed=String.valueOf(nCurrentSpeed);
            try
            {
                // Παίρνουμε την αποθηκευμένη τιμη f3 από τα preferences.
                preferences = PreferenceManager.getDefaultSharedPreferences(this);
                float f3 = preferences.getFloat("speedlimit", 10);
                // Γίνεται ο έλεγχος αν η ταχύτητα του χρήστη είναι μεγαλύτερη απο το όριο ταχύτητας.
                if(nCurrentSpeed>f3)
                {
                    SpeedLimitExceeded();
                    //Αν ναι, αλλάζει το χρώμα του Activity.
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }
            }
            catch (Exception e) {
                showMessage("Error","Something went wrong!");
            }
        }

        // Φτιάχνουμε το format με το οποίο θα εμφανίζεται η ταχύτητα .
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f",nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", " 0 ");

        tv_speed.setText(strCurrentSpeed);
    }

    // Μέθοδος που καλείται για να πάρουμε την τοποθεσία του χρήστη.
    public void gps(){
        // Γίνεται έλεγχος για τα κατάλληλα permissions .
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 234);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // Παίρνουμε το timestamp κάθε φορά που καταγράφεται η τοποθεσία.
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMΜ-yyy hh:mm:ss a");
        strDateTime = simpleDateFormat.format(calendar.getTime());
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Ορίζουμε το γεωγραφικό μήκος και πλάτος.
        double x = location.getLatitude();
        double y = location.getLongitude();
        strCurrentLocation = String.valueOf(x)+" , "+String.valueOf(y);


        // Εέλεγχος αν η τοποθεσία αλλάξει να ανανεωθεί η ταχύτητα.
        if(location != null){
            CLocation myLocation = new CLocation(location);
            this.UpdateSpeed(myLocation);
        }
    }

    // Μέθοδος που εμφανίζει μήνυμα προειδοποίησης αν υπερβει ο χρήστης το όριο ταχύτητας.
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
        // Μέσω της μεθόδου speak() και της TextToSpeech μηχανής εκφωνούμε το μήνυμα.
        cTextToSpeech.speak(message);
    }


    public void SpeedLimitExceeded(){
        showMessage("Warning","Slow Down! You have passed the speed limit.");
        Toast.makeText(this, strDateTime+"\n"+strCurrentLocation+"\n"+strPassedLimitSpeed, Toast.LENGTH_LONG).show();

        // Βάζουμε στην βάση δεδομένων τις πληροφορίες για την στιγμή που το όριο ταχύτητας υπερβήθηκε.
        db.execSQL("INSERT INTO Speed_Limit_Exceedances VALUES('"+strPassedLimitSpeed+"','"+strDateTime+"','"+strCurrentLocation+"')");
        Toast.makeText(this,"Status saved!",Toast.LENGTH_LONG).show();
    }

    public void showHistory(View view){
        // Ζητάμε από την βάση να τραβήξει όλες τις καταγραφές υπέρβασης του ορίου ταχύτητας.
        Cursor cursor = db.rawQuery("SELECT * FROM Speed_Limit_Exceedances",null);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                stringBuilder.append("Speed: ").append(cursor.getString(0)).append("\n");
                stringBuilder.append("Time: ").append(cursor.getString(1)).append("\n");
                stringBuilder.append("Location: ").append(cursor.getString(2)).append("\n");
                stringBuilder.append("---------------------------------------------------------\n");
            }
        }

        // Μέσω του stringBuilder μευταρέπουμε τα δεδομένα σε μορφή string και με την putExtras
        // μεταφέρουμε τα δεδομένα στο MainActivity3.
        String strData = stringBuilder.toString();
        Intent intent = new Intent(this, MainActivity3.class);
        intent.putExtra("data", strData);
        startActivity(intent);

    }

    // Μέθοδος που χρεισιμοποιούμε για να γίνει μια ενέργεια οταν πραγματοποιηθεί το speech
    // recognition.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // Αν δώσουμε την φωνητική εντολή θα πραγαμτοποιθεί η ενέργεια του κουμπιού "Show speed history"
            if (matches.contains("Show me my speed history"))
                ssh.performClick();
        }
    }

    // Μέθοδος αναγώρισης φωνής που ενεργοιείται με το πάτημα του κουμπιού "Use voice command"
    public void recognizeVoice(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say something!");
        startActivityForResult(intent, REC_RESULT);
    }

}
