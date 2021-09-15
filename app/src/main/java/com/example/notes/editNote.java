package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class editNote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent i = getIntent();
        String extrainfo = i.getStringExtra("noteid");
        Log.d("noteid2",extrainfo);
        //get note content
        SharedPreferences sps = getSharedPreferences("spinner",0);
        String spinnerselected = sps.getString("savedspinneropt","");
        SharedPreferences sp = getSharedPreferences(spinnerselected,0);
        SharedPreferences.Editor editor = sp.edit();
        String noteContent = sp.getString("plan"+extrainfo,"");
        editor.putString("editid",extrainfo);
        editor.apply();
        //Log.d("noteContent",noteContent);

        EditText inputText = findViewById(R.id.input);
        inputText.setText(noteContent);

        //Theme
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        ConstraintLayout mainbg = findViewById(R.id.editActivity_bg);
        String th = theme+"main_bg";
        int mainbgid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable mainbgdrawable = getResources().getDrawable(mainbgid);
        mainbg.setBackgroundDrawable(mainbgdrawable);

    }

    public void save(View view){
        SharedPreferences sps = getSharedPreferences("spinner",0);
        String spinnerselected = sps.getString("savedspinneropt","");
        SharedPreferences sp = getSharedPreferences(spinnerselected,0);
        SharedPreferences.Editor editor = sp.edit();
        String id = sp.getString("editid","");
        EditText inputText = findViewById(R.id.input);
        String newcontent = inputText.getText().toString();
        editor.putString("plan"+id,newcontent);
        editor.apply();
        Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();

    }

    public void cancel(View view){
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }
}