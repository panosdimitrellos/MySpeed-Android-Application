package com.example.myspeed;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

// Κλάση για την μετατροπή κειμένου σε ηχητικό μήνυμα.
public class CTextToSpeech {
    private TextToSpeech tts;
    private TextToSpeech.OnInitListener initListener=
            new TextToSpeech.OnInitListener() {
                @Override
                // Ορίζουμε την γλώσσα που θέλουμε να αναγνωρίζεται απο το τεχνική TextToSpeech.
                public void onInit(int i) {
                    tts.setLanguage(Locale.forLanguageTag("EN"));
                }
            };
    public CTextToSpeech(Context context) {
        tts = new TextToSpeech(context,initListener);
    }
    // Μέθοδος για την λειτουργία εκφώνησης με βάση το μήνυμα μας.
    public void speak(String message){
        tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);
    }
}
