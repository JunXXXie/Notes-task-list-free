package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the spinner selected item from SharedPreference
        SharedPreferences sps = getSharedPreferences("spinner",0);

        String spinnerselected = sps.getString("savedspinneropt","");
        Spinner spinner = (Spinner) findViewById(R.id.spinner);



        //add preset plans
        LinearLayout lists = (LinearLayout) findViewById(R.id.notesList);
        SharedPreferences sp = getSharedPreferences(spinnerselected,0);
        SharedPreferences.Editor changeSaving = sp.edit();
        //reset SP Boolean"exist"
        if(sp.getInt("numPlans",0)==0){
            changeSaving.putInt("numPlans",0);
            changeSaving.putBoolean("exist",false);
            changeSaving.apply();
        }else{
            changeSaving.putBoolean("exist",true);
            changeSaving.apply();
        }
        //Log.d("currentExist",String.valueOf(sp.getBoolean("exist",false)));
        //Log.d("currentnotesamount",String.valueOf(sp.getInt("numPlans",0)));
        //Log.d("currentOldamount",String.valueOf(sp.getInt("previousAmount",0)));


        //check if any pro note exist
        if(sp.getBoolean("exist",false)==true){
            int amount = sp.getInt("numPlans",0);
            int preAmount = sp.getInt("previousAmount",0);

            Log.d("currentpreamount",String.valueOf(amount));
            for(int i =2 ;  i<= preAmount*2; i+=2){
                //get content from sharePreference
                String input = sp.getString("plan"+i,"");
                //if function: 1. when input not null, set ID
                            // 2. when input is null, check the next input.
                                // 1. if not null, bring up the ID 2. if null, check next(use for loop to record times)
                if(input.matches("") == false){
                        //noteCount++;
                        loadpreNote(i,lists,sp,i);
                }
                else{ //when input is null


                    for(int j=i+2;j<=preAmount*2; j+=2){
                        //check next input
                        String nextInput = sp.getString("plan"+j,"");
                        if(nextInput != ""){
                            loadpreNote(j,lists,sp,i);
                            //remove original id
                            SharedPreferences.Editor saving = sp.edit();
                            saving.remove("plan"+j);
                            saving.apply();
                            j = preAmount*2+1;
                        }
                    }


                }
            }
        }

        //Spinner
        //Get list from SharedPreference

        SharedPreferences.Editor editors = sps.edit();
        //Create Spinner Array
        List<String> spinnerArray =  new ArrayList<String>();
        //get number of spinner
        int spinnerAmount = sps.getInt("spinnerAmount",0);
        if(spinnerAmount > 0){
            for(int s = 1; s <= spinnerAmount;s++){
                spinnerArray.add(sps.getString("spinnerArray"+s,""));
            }
        }else{ //Main page only
            spinnerArray.add("Main");
            editors.putString("spinnerArray"+1,"Main");
            editors.putInt("spinnerAmount",spinnerAmount+1);
            editors.putString("savedspinneropt","Main");
            editors.apply();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        int selection = sps.getInt("spinnerpos",0);
        spinner.setSelection(selection);

        //Theme
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        ConstraintLayout mainbg = findViewById(R.id.activitybg);
        String th = theme+"main_bg";
        int mainbgid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable mainbgdrawable = getResources().getDrawable(mainbgid);
        mainbg.setBackgroundDrawable(mainbgdrawable);



        amountAtTop(sp.getInt("numPlans",0));

        //google admob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



    }

    public void amountAtTop(int amount){
        TextView textView = findViewById(R.id.shownoteamount);
        String amountString = String.valueOf(amount);
        if(amount <= 1){
            textView.setText("Total "+amountString+" task");
        }else {
            textView.setText("Total "+amountString+" tasks");
        }

        textView.setTextColor(-1);
        textView.setTextSize(23);
    }

    public void setTheme(LinearLayout layout,String drawablename){
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        String th = theme+drawablename;
        int newdrawableid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable newdrawable = getResources().getDrawable(newdrawableid);
        layout.setBackgroundDrawable(newdrawable);

        //MainActivity
            //background theme
            //ConstraintLayout mainbg = findViewById(R.id.activitybg);
            //String th = selectedtheme+"main_bg";
            //int mainbgid =  getResources().getIdentifier(th,"drawable",getPackageName());
            //Drawable mainbgdrawable = getResources().getDrawable(mainbgid);
            //mainbg.setBackgroundDrawable(mainbgdrawable);

            //each post background color


        //History Activity
            //each deleted post
            //buttons
        //Manage Activity
            //each board

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_manage:
                //load the manage activity
                Intent i = new Intent(this, Manage.class);
                startActivity(i);

                return true;
            case R.id.menu_theme:
                //Toast toast2 = Toast.makeText(this,"Theme function will be added soon",Toast.LENGTH_LONG);
                //toast2.setGravity(17,0,0);
                //toast2.show();
                //window pop up for theme selection

                //pop up window
                AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
                editDialog.setTitle("Choose a theme: ");
                editDialog.setNegativeButton("Classic", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences themesp = getSharedPreferences("theme",0);
                        SharedPreferences.Editor editortheme = themesp.edit();
                        editortheme.putString("selectedtheme","classic");
                        editortheme.apply();

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                });
                editDialog.setPositiveButton("Dark", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences themesp = getSharedPreferences("theme",0);
                        SharedPreferences.Editor editortheme = themesp.edit();
                        editortheme.putString("selectedtheme","dark");
                        editortheme.apply();

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                });
                editDialog.create().show();

        }
        return super.onOptionsItemSelected(item);
    }

    //Function to load notes in SharePreferences
    public void loadpreNote(int i,LinearLayout lists, SharedPreferences sp, int newID){
        //Layout of each note
        LinearLayout layout_post = new LinearLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_post.setOrientation(LinearLayout.HORIZONTAL);
        int bottommarginDP = (int) convertDpToPixel(34,this);

        params.setMargins(0,0,0,-bottommarginDP);
        layout_post.setLayoutParams(params);
        String drawablename = "each_note";
        setTheme(layout_post,drawablename);
        //layout_post.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classiceach_note));



        //LinearLayout layout_textNbtn = new LinearLayout(this);

        //get content from sharePreference
        String input = sp.getString("plan"+i,"");
        SharedPreferences.Editor saving = sp.edit();
        saving.remove("plan"+i);
        final TextView preNote = new TextView(this);
        preNote.setId(newID);
        Log.d("currentnewID",String.valueOf(newID));

        //int textwidth = lengthEdit(input);
        //int widthdiv = (int) convertDpToPixel((float) 9,this);
        //int times = textwidth/widthdiv;

        preNote.setText(input);
        preNote.setTextColor(-1);
        //int textSizeDP = (int) convertDpToPixel((float) 7,this);
        preNote.setTextSize(21);
        preNote.measure(0,0);
        int prenoteY = (int) convertDpToPixel(5,this);
        preNote.setY(-prenoteY);
        preNote.setClickable(true);
        float preNotewidth = preNote.getMeasuredWidth();
        //Log.d("preNoteWidth",String.valueOf(preNotewidth));
        int preNoteWidthdp = (int) convertPixelsToDP(preNotewidth,this);
        //Log.d("preNoteWidthDP",String.valueOf(preNoteWidthdp));
        int times = preNoteWidthdp/239;

        preNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pass id to the edit activity
                String id = String.valueOf(view.getId());
                //launch history page
                Intent i = new Intent(MainActivity.this,editNote.class);
                //Add putExtra here
                i.putExtra("noteid",id);
                startActivity(i);
            }
        });

        /*
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(times>0){
            params.setMargins(0, 0, 0, 50);
        }else{
            params.setMargins(0, 0, 0, 0);
        }
        preNote.setLayoutParams(params);

         */

        //save newNote to sharePreferencce
        saving.putString("plan"+newID, input);
        saving.apply();

        //set up the finish button ( named: remove)
        final Button remove = new Button(this);
        int postheight60 = (int) convertDpToPixel(22,this);
        int postheight15 = (int) convertDpToPixel((float) 11,this);
        int postheight = times*postheight15+postheight60;

        if(times > 0){
            setUpRemoveBtn(remove,newID,0,postheight,false);

            //layout_post.setPadding(50,50,175, 50);
        }else{
            setUpRemoveBtn(remove,newID,1,postheight,false);

            //layout_post.setPadding(50,50,50, 50);
        }
        int padding1 = (int) convertDpToPixel(20,this);
        int padding2 = (int) convertDpToPixel(60,this);
        layout_post.setPadding(padding1,padding1,padding2, padding1);

        //important Btn
        Button importantbtn = new Button(this);
        setUpImportantBtn(importantbtn,newID, sp,false);


        lists.addView(layout_post);
        layout_post.addView(importantbtn);
        layout_post.addView(preNote);
        //layout_post.addView(remove);

        //lists.addView(preNote);
        lists.addView(remove);

        //check if the note is importanted

        int impAmount = sp.getInt("importantAmount",0);
        Log.d("impAmount",String.valueOf(impAmount));
        if(impAmount > 0){  //try set note as important
            for(int imp =0; imp <= impAmount; imp++){
                int impnoteid = sp.getInt("important"+imp,0);
                if(impnoteid == newID){
                    lists.removeView(layout_post);
                    lists.removeView(remove);
                    setImportantNoteView(newID,sp);
                    Log.d("impcurinfo","newID = "+String.valueOf(newID));
                }
            }
        }


    }

    public void setUpImportantBtn(Button importantbtn, final int newID, final SharedPreferences sp,boolean importanted){

        int imgres = R.drawable.ic_important;
        importantbtn.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);
        importantbtn.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.important_btn));
        int impbtnwidth = (int) convertDpToPixel(20,this);
        int impbtnheight = (int) convertDpToPixel(23,this);

        RelativeLayout.LayoutParams paramsimportant = new RelativeLayout.LayoutParams(impbtnwidth, impbtnheight);
        int impbtnmargin = (int)convertDpToPixel(8,this);
        paramsimportant.setMargins(0,0,impbtnmargin,0);
        importantbtn.setLayoutParams(paramsimportant);
        int impbtnY = (int) convertDpToPixel(2,this);
        int impbtnX = (int) convertDpToPixel(6,this);
        importantbtn.setX(-impbtnX);
        importantbtn.setY(impbtnY);

        if(!importanted){
            importantbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //save content to SharedPreference "important"
                    SharedPreferences.Editor editor = sp.edit();
                    //get important amount
                    int impAmount = sp.getInt("importantAmount",0);
                    int impid = impAmount+1;
                    editor.putInt("important"+impid,newID);
                    editor.putInt("importantAmount",impAmount+1);
                    Log.d("impidset",String.valueOf(impid));
                    //editor.remove("plan"+newID);
                    editor.apply();
                    //Remove View from normal list
                    LinearLayout parent = (LinearLayout) view.getParent();
                    LinearLayout lists = findViewById(R.id.notesList);
                    Button remove = findViewById(newID+1);
                    lists.removeView(parent);
                    lists.removeView(remove);
                    //add note to the top with different color
                    setImportantNoteView(newID,sp);
                }
            });
        }else{
            importantbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sp.edit();
                    //get important amount
                    int impAmount = sp.getInt("importantAmount",0);
                    for(int impid = 0; impid <= impAmount;impid++){
                        int spimpid = sp.getInt("important"+impid,0);
                        if(spimpid == newID){

                            //remove view
                            LinearLayout lists = findViewById(R.id.notesList);
                            LinearLayout postlayout = (LinearLayout) view.getParent();
                            int postremoveid = newID+1;
                            Button postremove = findViewById(postremoveid);
                            lists.removeView(postlayout);
                            lists.removeView(postremove);
                            //add view
                            LinearLayout layout_post = new LinearLayout(MainActivity.this);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            int bottommarginDP = (int) convertDpToPixel(34,MainActivity.this);
                            params.setMargins(0,0,0,-bottommarginDP);
                            layout_post.setLayoutParams(params);
                            String drawablename = "each_note";
                            setTheme(layout_post,drawablename);
                            TextView newNote = new TextView(MainActivity.this);
                            newNote.setId(newID);
                            String newNoteContent = sp.getString("plan"+newID,"");
                            newNote.setText(newNoteContent);
                            newNote.setTextSize(21);
                            newNote.measure(0,0);
                            float preNotewidth = newNote.getMeasuredWidth();
                            //Log.d("preNoteWidth",String.valueOf(preNotewidth));
                            int preNoteWidthdp = (int) convertPixelsToDP(preNotewidth,MainActivity.this);
                            //Log.d("preNoteWidthDP",String.valueOf(preNoteWidthdp));
                            int times = preNoteWidthdp/239;
                            newNote.setTextColor(Color.WHITE);
                            //set up the finish button ( named: remove)
                            final Button remove = new Button(MainActivity.this);
                            int postheight60 = (int) convertDpToPixel(22,MainActivity.this);
                            int postheight15 = (int) convertDpToPixel((float) 11,MainActivity.this);
                            int postheight = times*postheight15+postheight60;
                            if(times > 0){
                                setUpRemoveBtn(remove,newID,0,postheight,true);
                                //layout_post.setPadding(50,50,175, 50);
                            }else{
                                setUpRemoveBtn(remove,newID,1,postheight,true);
                            }
                            int padding1 = (int) convertDpToPixel(20,MainActivity.this);
                            int padding2 = (int) convertDpToPixel(60,MainActivity.this);
                            layout_post.setPadding(padding1,padding1,padding2, padding1);

                            //important Btn
                            Button importantbtn = new Button(MainActivity.this);
                            setUpImportantBtn(importantbtn,newID, sp,false);
                            //int pos = newID-impAmount*2;
                            lists.addView(layout_post);
                            layout_post.addView(importantbtn);
                            layout_post.addView(newNote);
                            //int removepos = pos+1;
                            lists.addView(remove);

                            //remove impnote from sp
                            editor.remove("important"+impid);
                            editor.apply();

                            //the rest impid - 1, and delete the last one

                            for(int restimp = impid+1; restimp <= impAmount;restimp++){
                                //original
                                int originalvalue = sp.getInt("important"+restimp,0);
                                int newimpid = restimp -1;
                                editor.putInt("important"+newimpid,originalvalue);
                                editor.apply();
                            }


                            //delete the last one
                            editor.remove("important"+impAmount);
                            editor.apply();


                        }
                    }

                    //decrease impAmount
                    if(impAmount > 0){
                        editor.putInt("importantAmount",impAmount-1);
                    }else{
                        editor.putInt("importantAmount",0);
                    }
                    editor.apply();

                }
            });
        }

    }

    public void setImportantNoteView(int newID,SharedPreferences sp){
        //Layout of each note
        LinearLayout layout_post = new LinearLayout(MainActivity.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_post.setOrientation(LinearLayout.HORIZONTAL);
        int bottommarginDP = (int) convertDpToPixel(34,MainActivity.this);
        params.setMargins(0,0,0,-bottommarginDP);
        layout_post.setLayoutParams(params);
        String drawablename = "impnote";
        setTheme(layout_post,drawablename);

        LinearLayout lists = findViewById(R.id.notesList);

        TextView impnote = new TextView(MainActivity.this);
        impnote.setId(newID);
        impnote.setText(sp.getString("plan"+newID,""));
        impnote.setTextColor(-1);
        impnote.setTextSize(21);
        impnote.measure(0,0);
        int noteY = (int) convertDpToPixel(5,MainActivity.this);
        impnote.setY(-noteY);
        impnote.setClickable(true); // Toast message: important note cannot be edited
        impnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pass id to the edit activity
                String id = String.valueOf(view.getId());
                //launch history page
                Intent i = new Intent(MainActivity.this,editNote.class);
                //Add putExtra here
                i.putExtra("noteid",id);
                startActivity(i);
            }
        });
        float impnotewidth = impnote.getMeasuredWidth();
        int impNoteWidthDP = (int) convertPixelsToDP(impnotewidth,MainActivity.this);
        int times = impNoteWidthDP/239;
        //set up the finish button ( named: remove)
        Button impremove = new Button(MainActivity.this);
        int postheight60 = (int) convertDpToPixel(22,MainActivity.this);
        int postheight15 = (int) convertDpToPixel((float) 11,MainActivity.this);
        int postheight = times*postheight15+postheight60;

        if(times > 0){
            setUpRemoveBtn(impremove,newID,0,postheight,true);

            //layout_post.setPadding(50,50,175, 50);
        }else{
            setUpRemoveBtn(impremove,newID,1,postheight,true);
            //layout_post.setPadding(50,50,50, 50);
        }
        int padding1 = (int) convertDpToPixel(20,MainActivity.this);
        int padding2 = (int) convertDpToPixel(60,MainActivity.this);
        layout_post.setPadding(padding1,padding1,padding2, padding1);

        //important Btn
        Button importantbtn = new Button(MainActivity.this);
        setUpImportantBtn(importantbtn,newID, sp,true);
        int imgres = R.drawable.ic_important2;
        importantbtn.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);



        lists.addView(layout_post,0);
        layout_post.addView(importantbtn);
        layout_post.addView(impnote);
        lists.addView(impremove,1);

    }

    public void addAndRemove(Integer count){
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        String spinnerselected = spinner.getSelectedItem().toString();
        SharedPreferences sp = getSharedPreferences(spinnerselected,0);
        final SharedPreferences.Editor saving = sp.edit();

        LinearLayout layout_post = new LinearLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int bottommarginDP = (int) convertDpToPixel(34,this);
        params.setMargins(0,0,0,-bottommarginDP);
        layout_post.setLayoutParams(params);
        String drawablename = "each_note";
        setTheme(layout_post,drawablename);

        TextView i = findViewById(R.id.textInput);
        String firinput = i.getText().toString();
        saving.putString("Input",firinput);
        saving.apply();

        //int textwidth = lengthEdit(firinput);
        //Log.d("textl",String.valueOf(textwidth));
        //int widthdiv = (int) convertDpToPixel(9,this);
        //int times = textwidth/widthdiv;
        //Log.d("div",String.valueOf(times));

        //method 2
        String input = firinput;

        //when input is not blank, start adding
        if(input. matches("") == false){
            final TextView newNote = new TextView(this);
            //add newNote with ID of count++
                int countplus = count*2+2;
                newNote.setId(countplus);
                //Log.d("nnid",String.valueOf(newNote.getId()));
                saving.putInt("numPlans",count+1);
                saving.putInt("previousAmount",count+1);
                saving.apply();

            //newNote config
                //set newNote content
                newNote.setText(input);
                //this is position for method 1
                /*
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 30, 0, 0);
                newNote.setLayoutParams(params);

                 */
                //newNote.setY(15);

                //newNote.setMaxWidth(50);
                //int textSizeDP = (int) convertDpToPixel((float) 7.5,this);
                int noteY = (int) convertDpToPixel(5,this);
                newNote.setY(-noteY);
                newNote.setTextSize(21);
                newNote.measure(0,0);
                float preNotewidth = newNote.getMeasuredWidth();
                //Log.d("preNoteWidth",String.valueOf(preNotewidth));
                int preNoteWidthdp = (int) convertPixelsToDP(preNotewidth,this);
                //Log.d("preNoteWidthDP",String.valueOf(preNoteWidthdp));
                int times = preNoteWidthdp/239;
                newNote.setTextColor(Color.WHITE);
                newNote.setClickable(true);
                newNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //pass id to the edit activity
                        String id = String.valueOf(view.getId());
                        //launch history page
                        Intent i = new Intent(MainActivity.this,editNote.class);
                        //Add putExtra here
                        i.putExtra("noteid",id);
                        startActivity(i);
                    }
                });

                final String noteID = String.valueOf(newNote.getId());
                //Log.d("tt",noteID);

            //save newNote to sharePreferencce
            saving.putString("plan"+countplus, input);

            //change "exist" to true
            saving.putBoolean("exist",true);
            saving.apply();

            //set up the finish button ( named: remove)
            final Button remove = new Button(this);
            int postheight60 = (int) convertDpToPixel(22,this);
            int postheight15 = (int) convertDpToPixel((float) 11,this);
            int postheight = times*postheight15+postheight60;
            if(times > 0){
                setUpRemoveBtn(remove,countplus,0,postheight,false);
                //layout_post.setPadding(50,50,175, 50);
            }else{
                setUpRemoveBtn(remove,countplus,1,postheight,false);
            }
            int padding1 = (int) convertDpToPixel(20,this);
            int padding2 = (int) convertDpToPixel(60,this);
            layout_post.setPadding(padding1,padding1,padding2, padding1);
            //important Btn
            Button importantbtn = new Button(this);
            setUpImportantBtn(importantbtn,countplus, sp,false);


            LinearLayout lists = (LinearLayout) findViewById(R.id.notesList);
            lists.addView(layout_post);
            layout_post.addView(importantbtn);
            layout_post.addView(newNote);
            lists.addView(remove);
            i.setText("");
        }

        amountAtTop(sp.getInt("numPlans",0));
    }

    public void add(View v){
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        String spinnerselected = spinner.getSelectedItem().toString();
        //check if there are content in the LinearLayout
        //get sharepreference to check content
        SharedPreferences sp = getSharedPreferences(spinnerselected,0);
        SharedPreferences.Editor saving = sp.edit();
        //saving.putString("work","yes, it works");
        saving.apply();

        //Log.d("work",sp.getString("work",""));
        //Log.d("boolean", String.valueOf(sp.getBoolean("exist",false)));
        if( sp.getBoolean("exist",false) == false){
            saving.putInt("newNum",0);
            saving.putBoolean("exist",true);
            saving.apply();
            addAndRemove(0);
        }else{
            int lines = sp.getInt("numPlans",0);
            //Log.d("else",String.valueOf(lines));
            addAndRemove(lines);
        }
    }

    public void setUpRemoveBtn(Button remove, int countplus, int method, int postheight, final boolean importanted){
        remove.setId(countplus+1);
        int imgres = R.drawable.ic_tick;
        remove.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);
        Log.d("height",String.valueOf(postheight));
        //remove.setText("finish");

        //remove.setLayoutParams(new LinearLayout.LayoutParams(250,100));
        //Get the parent Layout
        //LinearLayout parent = (LinearLayout) findViewById(R.id.notesList);
        //edit layout = remove
        //create para for layout(button remove)
        int widthpix = (int) convertPixelsToDP((float) 335,this);
        int widthdp = (int) convertDpToPixel(45,this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widthdp, widthdp);
        //params.addRule(RelativeLayout.CENTER_VERTICAL);

        //This is position for method 1
        if (method == 1){
            float Ydp = convertDpToPixel((float) 23,this);
            float Xdp = convertDpToPixel((float) 8,this);
            remove.setY(-Ydp);
            remove.setX(-Xdp);
            params.rightMargin=0;
            remove.setLayoutParams(params);
        }else{
            remove.setY(-postheight);
            float Xdp = convertDpToPixel((float) 8,this);
            remove.setX(-Xdp);
            params.rightMargin=0;
            //params.bottomMargin=0;
            remove.setLayoutParams(params);
        }
        remove.setGravity(Gravity.CENTER);
        //remove.setX(0);


        //remove.setPadding(0,0,0,0);
        remove.setTextSize(1,15);
        remove.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.finishbtn));

        //remove.setY(-15);
        remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                removeOnclick(view,importanted);
            }
        });
    }

    public void removeOnclick(View view, boolean importanted){
        //for importanted note
        if(importanted){
            SharedPreferences sps = getSharedPreferences("spinner",0);
            String spinnerselected = sps.getString("savedspinneropt","");
            SharedPreferences sp = getSharedPreferences(spinnerselected,0);
            int impAmount = sp.getInt("importantAmount",0);
            if(impAmount > 0){
                int newImpAmount = impAmount -1;
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("importantAmount",newImpAmount);
                editor.apply();
            }

        }
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        String spinnerselected = spinner.getSelectedItem().toString();
        //Delete button and textView from sharePreference(function)
        //get id, the number at the end of the finish button ID
        // get the finish button ID
        int finishID = view.getId();
        //int length= finishID.length()-1;
        //String deleteid = finishID.substring(6,length);
        int noteid = finishID-1;

        TextView note = findViewById(noteid);
        //Button toRemove = findViewById(noteid+1);
        //remove views from layout
        LinearLayout noteParent = (LinearLayout) note.getParent();
        LinearLayout parent = (LinearLayout) view.getParent();
        //Pass deleted notes to History Page
        String noteContent = (String) note.getText();
        //Log.d("hisNoteContent",noteContent);
        passToHistory(noteContent);

        parent.removeView(noteParent);
        parent.removeView(view);

        //delete content from sp
        SharedPreferences sp = getSharedPreferences(spinnerselected,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("plan"+noteid);
        //decrease amount of layoutLineNum
        int tta = sp.getInt("numPlans",0);
        editor.putInt("numPlans",tta-1);
        editor.apply();

        //reset SP Boolean"exist"
        int currentAmount = sp.getInt("numPlans",0);
        if(currentAmount == 0){
            editor.putBoolean("exist",false);
            editor.apply();
        }

        amountAtTop(sp.getInt("numPlans",0));

    }
    
    public void toHistoryPage(View view){
        //launch history page
        Intent i = new Intent(this,History.class);
        //Add putExtra here
        startActivity(i);
    }

    public void passToHistory(String content){
        SharedPreferences sps = getSharedPreferences("spinner",0);
        String spinnerselected = sps.getString("savedspinneropt","Main");

        SharedPreferences sph = getSharedPreferences(spinnerselected+"History",0);
        SharedPreferences.Editor editorh = sph.edit();

        int deletedamount = sph.getInt("hispreAmount",0);
        Log.d("hispreAmountwhenremove",String.valueOf(deletedamount));

        //deleted note's id increment by 3
        int newid = (deletedamount+1) * 3;
        Intent i = new Intent(this, History.class);
        Log.d("hispasscontent",content);
        //i.putExtra("deletedNote"+newid,content);
        editorh.putString("deletedNote"+newid,content);
        editorh.putInt("historyAmount",sph.getInt("historyAmount",0)+1);
        editorh.putInt("hispreAmount",sph.getInt("hispreAmount",0)+1);
        editorh.apply();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = getIntent();
        overridePendingTransition(0,0);
        finish();
        overridePendingTransition(0,0);
        startActivity(i);
    }

    //For Spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        SharedPreferences sps = getSharedPreferences("spinner",0);
        SharedPreferences.Editor editors = sps.edit();
        int prepos = sps.getInt("spinnerpos",0);
        if(prepos != i){
            Spinner spinner = findViewById(R.id.spinner);
            //save selected option to SharedPreference

            String text = adapterView.getItemAtPosition(i).toString();
            editors.putString("savedspinneropt",text);
            editors.putInt("spinnerpos",i);
            editors.apply();
            //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_LONG).show();
            Intent intent = getIntent();
            finish();

            startActivity(intent);
            overridePendingTransition(0,0);

        }

        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) adapterView.getChildAt(0)).setTextSize(17);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //change the length of String when there is chinese
    public static int lengthEdit(String value){
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    public static float convertPixelsToDP(Float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}