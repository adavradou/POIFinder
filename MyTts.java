package com.example.unipitouristapp;


import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;

import java.util.Locale;

public class MyTts {
    private TextToSpeech tts;
    private TextToSpeech.OnInitListener initListener =
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status==TextToSpeech.SUCCESS)
                        tts.setLanguage(Locale.ENGLISH);
                }
            };
    public MyTts(Context context){
        tts = new TextToSpeech(context,initListener);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speak(String message){
        tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);
    }
}