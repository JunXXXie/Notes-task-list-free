package com.example.notes;

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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class History extends AppCompatActivity {
    AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent i = getIntent();
        SharedPreferences sps = getSharedPreferences("spinner",0);
        String spinnerselected = sps.getString("savedspinneropt","Main");

        SharedPreferences sph = getSharedPreferences(spinnerselected+"History",0);
        SharedPreferences.Editor editorh = sph.edit();
        //total amount of deleted notes
        int amount = sph.getInt("historyAmount",0);

        Log.d("hisamount",String.valueOf(amount));
        //Log.d("hisnotes",sph.getString("deletedNote1",""));
        //get previous largest amount
        int preAmount = sph.getInt("hispreAmount",0);
        Log.d("hispreamount",String.valueOf(preAmount));
        if(amount >= 1){
            //preload each deleted notes
            for(int n=3;n<=preAmount*3;n+=3){
                String deletedNotesContent = sph.getString("deletedNote"+n,"");
                if(deletedNotesContent.matches("") == false){
                    //update deleted note id after all deleted note have been loaded
                    int newID = (sph.getInt("hisloadedcount",0)+1)*3;
                    Log.d("hisnewID",String.valueOf(newID));
                    loadDeletedNotes(newID,deletedNotesContent);
                    editorh.putString("deletedNote"+newID, deletedNotesContent);
                    editorh.putInt("hisloadedcount",sph.getInt("hisloadedcount",0)+1);
                    editorh.apply();
                }else{
                    editorh.remove("deletedNote"+n);
                    editorh.putInt("hispreAmount",sph.getInt("hispreAmount",0)-1);
                    editorh.apply();
                }
            }
        }
        //update previous largest amount to current amount
        editorh.putInt("hispreAmount",amount);
        editorh.remove("hisloadedcount");
        editorh.apply();


        //Theme
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        ConstraintLayout mainbg = findViewById(R.id.activitybg);
        String th = theme+"main_bg";
        int mainbgid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable mainbgdrawable = getResources().getDrawable(mainbgid);
        mainbg.setBackgroundDrawable(mainbgdrawable);

        //update textView at top
        //TextView showAmount = findViewById(R.id.showDeletedAmount);
        textViewTop(amount);

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

    public void setTheme(Button button,String drawablename){
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        String th = theme+drawablename;
        int newdrawableid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable newdrawable = getResources().getDrawable(newdrawableid);
        button.setBackgroundDrawable(newdrawable);

    }

    public void textViewTop(int amount){
        TextView showAmount = findViewById(R.id.showDeletedAmount);
        String amountString = String.valueOf(amount);
        showAmount.setText("You have "+amountString+" deleted notes");
        showAmount.setTextSize(21);
        showAmount.setTextColor(-1);
    }

    public void loadDeletedNotes(int id,String content){
        LinearLayout list = findViewById(R.id.delNotesLinearList);

        LinearLayout eachpost = new LinearLayout(this);
        eachpost.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.history_eachpost));
        eachpost.setOrientation(LinearLayout.VERTICAL);
        int paddingDP = (int) convertDpToPixel(18, this);
        eachpost.setPadding(paddingDP,paddingDP,paddingDP,paddingDP);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int marginDP = (int) convertDpToPixel(11, this);
        params.setMargins(0,0,0,marginDP);
        eachpost.setLayoutParams(params);


        //create note View
        TextView deletedNoteView = new TextView(this);
        deletedNoteView.setId(id);
        deletedNoteView.setText(content);
        deletedNoteView.setTextSize(20);
        deletedNoteView.setTextColor(Color.WHITE);

        //Create new linear layout to constrain two buttons
        LinearLayout buttonsList = new LinearLayout(this);
        buttonsList.setOrientation(LinearLayout.HORIZONTAL);
        buttonsList.setGravity(Gravity.RIGHT);
        int btnpaddingDP = (int) convertDpToPixel(10, this);
        buttonsList.setPadding(0,0,0,btnpaddingDP);



        //Create recover btn View
            //recover btn id = noteid +1
        int recoverid = id +1;
        Button recoverBtn = new Button(this);
        setUpRecoverBtn(recoverBtn,recoverid);

        //Create clear btn View
            //clear btn id = noteid +2
        int clearid = id +2;
        Button clearBtn = new Button(this);
        setUpClearBtn(clearBtn,clearid);
        float Ydp = convertDpToPixel((float) 10,this);
        buttonsList.setY(Ydp);
        //list.addView(deletedNoteView);
        //list.addView(buttonsList);
        list.addView(eachpost);
        eachpost.addView(deletedNoteView);
        eachpost.addView(buttonsList);
        buttonsList.addView(recoverBtn);
        buttonsList.addView(clearBtn);
        //update preAmount

    }

    public void setUpRecoverBtn(Button recoverBtn, int recoverid){
        SharedPreferences sps = getSharedPreferences("spinner",0);
        final String spinnerselected = sps.getString("savedspinneropt","Main");
        recoverBtn.setId(recoverid); //+1
        recoverBtn.setText(" Recover");
        recoverBtn.setTextColor(Color.WHITE);
        int imgres = R.drawable.ic_restore;
        int heightDP = (int) convertDpToPixel(35,this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, heightDP);
        int marginrightDP = (int) convertDpToPixel(10,this);
        params.setMargins(0,0,marginrightDP,0);

        recoverBtn.setLayoutParams(params);
        int paddingsides = (int) convertDpToPixel(3, this);
        int paddingleft = (int) convertDpToPixel(5,this);
        int paddingright = (int) convertDpToPixel(10,this);
        recoverBtn.setPadding(paddingleft,paddingsides,paddingright ,paddingsides);
        recoverBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);
        String drawablename = "recover_btn";
        setTheme(recoverBtn,drawablename);
        //recoverBtn.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classicrecover_btn));


        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send note content back to main activity
                    //get the deleted note content
                    int rmbtnid = view.getId();
                    int noteid = rmbtnid-1;
                    TextView note = findViewById(noteid);
                    String content = (String) note.getText();
                    //Add content to the Main Activity note list
                    SharedPreferences sp = getSharedPreferences(spinnerselected,0);
                    SharedPreferences.Editor editor = sp.edit();
                    int num = sp.getInt("numPlans",0);
                    int id1 = sp.getInt("previousAmount",0);
                    //main activity notes amount +1
                    editor.putInt("numPlans",num+1);
                    editor.putInt("previousAmount",id1+1);
                    editor.apply();
                    //add content
                    int id2 = (id1+1)*2;
                    editor.putString("plan"+id2, content);
                    Log.d("hisrecovertest","plan "+id2+", "+content);
                    editor.apply();
                    //(apply at the end)

                    //remove note and buttons's parent from History Activity
                    LinearLayout btnparent = (LinearLayout) view.getParent();
                    LinearLayout btngrantparent = (LinearLayout) btnparent.getParent();
                    LinearLayout list = findViewById(R.id.delNotesLinearList);
                    btnparent.removeAllViews();
                    list.removeView(btngrantparent);

                //deletedAmount -1
                SharedPreferences sph = getSharedPreferences(spinnerselected+"History",0);
                SharedPreferences.Editor editorh = sph.edit();
                int curHisAmount = sph.getInt("historyAmount",0);
                editorh.putInt("historyAmount",curHisAmount-1);
                Log.d("historyAmountwhenrecover",String.valueOf(curHisAmount-1));
                editorh.remove("deletedNote"+noteid);
                //editorh.putInt("hispreAmount",sph.getInt("hispreAmount",0)-1);
                editorh.apply();

                //update textView at top
                textViewTop(curHisAmount-1);

            }
        });
    }

    public void setUpClearBtn(Button clearBtn, int clearid){
        SharedPreferences sps = getSharedPreferences("spinner",0);
        final String spinnerselected = sps.getString("savedspinneropt","Main");
        clearBtn.setId(clearid); // +2
        clearBtn.setText("Delete");
        clearBtn.setTextColor(Color.WHITE);
        //clearBtn.setWidth(30);
        int imgres = R.drawable.ic_delete_forever;
        clearBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);
        int heightDP = (int) convertDpToPixel(35,this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, heightDP);
        //params.setMargins(0,0,0,-93);
        clearBtn.setLayoutParams(params);
        int paddingsides = (int) convertDpToPixel(3, this);
        int paddingleft = (int) convertDpToPixel(5,this);
        int paddingright = (int) convertDpToPixel(10,this);
        clearBtn.setPadding(paddingleft,paddingsides,paddingright,paddingsides);
        String drawablename = "historydelete_btn";
        setTheme(clearBtn,drawablename);
        //clearBtn.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classichistorydelete_btn));
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the deleted note content
                int clrbtnid = view.getId();
                int noteid = clrbtnid-2;
                //deletedAmount -1
                SharedPreferences sph = getSharedPreferences(spinnerselected+"History",0);
                SharedPreferences.Editor editorh = sph.edit();
                int curHisAmount = sph.getInt("historyAmount",0);
                editorh.putInt("historyAmount",curHisAmount-1);
                editorh.remove("deletedNote"+noteid);
                //editorh.putInt("hispreAmount",sph.getInt("hispreAmount",0)-1);
                editorh.apply();
                //remove note and buttons's parent from History Activity
                TextView note = findViewById(noteid);
                LinearLayout btnparent = (LinearLayout) view.getParent();
                LinearLayout btngrantparent = (LinearLayout) btnparent.getParent();
                LinearLayout list = findViewById(R.id.delNotesLinearList);
                btnparent.removeAllViews();
                list.removeView(btngrantparent);
                //update textView at top
                textViewTop(curHisAmount-1);
            }
        });
    }

    public void clearAll(View view){

        //pop up window
        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        editDialog.setTitle("All history will be cleared");
        editDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sps = getSharedPreferences("spinner", 0);
                        String spinnerselected = sps.getString("savedspinneropt","Main");
                        //set deletedNote amount to 0
                        SharedPreferences sph = getSharedPreferences(spinnerselected+"History",0);
                        SharedPreferences.Editor editorh = sph.edit();
                        //delete all saving in sph
                        int tta = sph.getInt("historyAmount",0);
                        for(int k=3; k <= tta*3; k+=3){
                            editorh.remove("deletedNote"+k);
                        }
                        editorh.putInt("historyAmount",0);
                        editorh.apply();
                        //delete all view in History activity
                        LinearLayout hisList = findViewById(R.id.delNotesLinearList);
                        hisList.removeAllViews();
                        //update the textView at top
                        textViewTop(sph.getInt("historyAmount",0));
                    }
                });
        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        editDialog.create().show();
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

}