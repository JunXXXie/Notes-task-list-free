package com.example.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Manage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        //get the amount of boards
        SharedPreferences sps = getSharedPreferences("spinner",0);
        SharedPreferences.Editor editors = sps.edit();
        int boardAmount = sps.getInt("spinnerAmount",0);

        //Load each board name
        //get previous largest amount
        int preAmount = sps.getInt("spinnerpreAmount",1);
        if(boardAmount >= 1){
            //preload each board
            for(int s=1;s<=preAmount;s++){
                String boardName = sps.getString("spinnerArray"+s,"");
                if (boardName != ""){
                    int newID = (sps.getInt("loadedcount",0)+1);
                    loadBoard(newID,boardName);
                    editors.putString("spinnerArray"+newID,boardName);
                    editors.putInt("loadedcount",newID);
                    editors.apply();
                }else{
                    editors.remove("spinnerArray"+s);
                    editors.apply();
                }
            }
        }

        //update previous largest amount to current amount
        editors.putInt("spinnerpreAmount",boardAmount);
        editors.remove("loadedcount");
        editors.apply();


        //Theme
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        ConstraintLayout mainbg = findViewById(R.id.activitybg);
        String th = theme+"main_bg";
        int mainbgid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable mainbgdrawable = getResources().getDrawable(mainbgid);
        mainbg.setBackgroundDrawable(mainbgdrawable);

        //Crate TextView at top
        textViewTop(boardAmount);


        //Test
        SharedPreferences spb = getSharedPreferences("shopping",0);
        SharedPreferences sph = getSharedPreferences("shopping"+"History",0);
        //1. spinner list,sps
        Log.d("deleteBoardspinnerPre",sps.getString("spinnerArray"+1,""));
        //2. Note list,spb
        Log.d("deleteboardNotePre",spb.getString("plan"+2,""));
        //3. History list, sph
        Log.d("deleteBoardhisPre",sph.getString("deletedNote"+3,""));


    }

    public void setThemebtn(Button button,String drawablename){
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        String th = theme+drawablename;
        int newdrawableid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable newdrawable = getResources().getDrawable(newdrawableid);
        button.setBackgroundDrawable(newdrawable);
    }

    public void setTheme(LinearLayout layout,String drawablename){
        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        String th = theme+drawablename;
        int newdrawableid =  getResources().getIdentifier(th,"drawable",getPackageName());
        Drawable newdrawable = getResources().getDrawable(newdrawableid);
        layout.setBackgroundDrawable(newdrawable);
    }

    public void textViewTop(int amount){
        TextView showAmount = findViewById(R.id.manage_title);
        String amountString = String.valueOf(amount);
        showAmount.setText("You have "+amountString+" boards");
        showAmount.setTextSize(28);
        showAmount.setTextColor(Color.WHITE);
        showAmount.setTypeface(null, Typeface.BOLD);
    }

    public void loadBoard(int id, String name){
        LinearLayout list = findViewById(R.id.manage_linearlayout);
        TextView boardView = new TextView(this);

        LinearLayout eachboard = new LinearLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int marginbottomDP = (int) convertDpToPixel(12,this);
        params.setMargins(0,0,0,marginbottomDP);
        eachboard.setLayoutParams(params);
        int paddingleftDP = (int) convertDpToPixel(23,this);
        int paddingUpDownDP = (int) convertDpToPixel(13,this);
        eachboard.setPadding(paddingleftDP,paddingUpDownDP,paddingleftDP,paddingUpDownDP);
        String drawablename = "eachboard";
        setTheme(eachboard,drawablename);
        //eachboard.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classiceachboard));
        eachboard.setOrientation(LinearLayout.VERTICAL);

        boardView.setId(id*3);
        boardView.setText(name);
        boardView.setTextSize(22);

        SharedPreferences themesp = getSharedPreferences("theme",0);
        String theme = themesp.getString("selectedtheme","classic");
        if(theme.matches("dark") == true){
            boardView.setTextColor(Color.WHITE);
        }else{
            boardView.setTextColor(Color.DKGRAY);
        }

        boardView.setTypeface(null, Typeface.BOLD);
        //Create new linear layout to constrain two buttons
        LinearLayout buttonsList = new LinearLayout(this);
        buttonsList.setOrientation(LinearLayout.HORIZONTAL);
        buttonsList.setGravity(Gravity.RIGHT);

        //Create rename btn, id = boardID +1
        int renameid = (id*3)+1;
        Button renameBtn = new Button(this);
        setUpButton(" rename", renameBtn,renameid);
        //Create delete btn, id = boardID +2
        int deleteid = (id*3)+2;
        Button deleteBtn = new Button(this);
        setUpButton("delete",deleteBtn,deleteid);

        list.addView(eachboard);
        eachboard.addView(boardView);
        eachboard.addView(buttonsList);
        //list.addView(boardView);
        //list.addView(buttonsList);
        buttonsList.addView(renameBtn);
        buttonsList.addView(deleteBtn);
    }

    public void setUpButton(String type, Button button, int id){
        if(type == " rename"){ //for rename button
            button.setId(id);
            button.setText(type);
            int imgres = R.drawable.ic_rename;
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);
            button.setTextColor(Color.WHITE);
            int heightDP = (int) convertDpToPixel(35,this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, heightDP);
            int marginrightDP = (int) convertDpToPixel(10,this);
            params.setMargins(0,0,marginrightDP,0);

            button.setLayoutParams(params);
            int paddingsides = (int) convertDpToPixel(3, this);
            int paddingleft = (int) convertDpToPixel(5,this);
            int paddingright = (int) convertDpToPixel(10,this);
            button.setPadding(paddingleft,paddingsides,paddingright ,paddingsides);
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);

            SharedPreferences themesp = getSharedPreferences("theme",0);
            String theme = themesp.getString("selectedtheme","classic");
            if(theme.matches("dark") == true){
                String drawablename = "recover_btn";
                setThemebtn(button,drawablename);
                //button.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classicmanagerename));
            }else{
                button.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classicmanagerename));
            }


            //rename OnclickListener: popup window with input bar
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //get the board TextView id
                    final int renamebtnid = view.getId();
                    final int boardNameid = renamebtnid - 1;
                    //get board name
                    TextView board = findViewById(boardNameid);
                    String boardName = board.getText().toString();
                    Log.d("renameName",boardName);
                    if(boardName.matches("Main") == true){
                        Toast.makeText(getApplicationContext(),"This board cannot be renamed",Toast.LENGTH_LONG).show();
                    }else{
                        //pop up window
                        AlertDialog.Builder editDialog = new AlertDialog.Builder(Manage.this);
                        editDialog.setTitle("New Name:");
                        //editDialog.setIcon();
                        final EditText inputView = new EditText(Manage.this);
                        editDialog.setView(inputView);
                        editDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //rename board: change the board name in SharedPreference
                                String input = inputView.getText().toString();
                                //Log.d("renamefinalinput",input);
                                //get the board TextView id
                                if(input.length() > 0){
                                    int boardViewid = renamebtnid - 1;
                                    int boardspid = boardViewid/3;
                                    //get board name
                                    TextView board = findViewById(boardViewid);
                                    String boardName = board.getText().toString();
                                    Log.d("tname",boardName);


                                    //check if name exits(function)
                                    boolean check = checkBoardNameExistence(input);
                                    Log.d("renameCheck",String.valueOf(check));
                                    if(check == true){
                                        //exist, cannot use the name
                                        Toast.makeText(getApplicationContext(),"This name is used.",Toast.LENGTH_LONG).show();
                                    }else{
                                        //not exist, can use
                                        //new SP
                                        SharedPreferences spn = getSharedPreferences(input,0);
                                        SharedPreferences.Editor editorn = spn.edit();

                                        SharedPreferences spnh = getSharedPreferences(input+"History",0);
                                        SharedPreferences.Editor editornh = spnh.edit();

                                        SharedPreferences spb = getSharedPreferences("spinner",0);
                                        SharedPreferences.Editor editorb = spb.edit();

                                        SharedPreferences spo = getSharedPreferences(boardName,0);
                                        SharedPreferences.Editor editoro = spo.edit();

                                        SharedPreferences spoh = getSharedPreferences(boardName+"History",0);
                                        SharedPreferences.Editor editoroh = spoh.edit();

                                        editorn.putInt("numPlans",spo.getInt("numPlans",0));
                                        editorn.putBoolean("exist",spo.getBoolean("exist",false));
                                        editorn.putInt("previousAmount",spo.getInt("previousAmount",0));
                                        editorn.putString("Input",spo.getString("Input",""));
                                        editorn.putInt("newNum",spo.getInt("newNum",0));
                                        editorn.apply();
                                        int pA = spo.getInt("previousAmount",0);
                                        for(int p = 2; p <= pA*2; p+=2){
                                            editorn.putString("plan"+p,spo.getString("plan"+p,""));
                                            editorn.apply();
                                        }

                                        editornh.putInt("historyAmount",spoh.getInt("historyAmount",0));
                                        editornh.putInt("hispreAmount",spoh.getInt("hispreAmount",0));
                                        editornh.putInt("hisloadedcount",0);
                                        editornh.apply();
                                        int hA = spoh.getInt("hispreAmount",0);
                                        for(int h = 3; h <= hA*3; h+=3){
                                            editornh.putString("deletedNote"+h,spoh.getString("deletedNote"+h,""));
                                            editornh.apply();
                                        }
                                        editorb.remove("spinnerArray"+boardspid);
                                        editorb.putString("spinnerArray"+boardspid,input);
                                        editorb.apply();

                                        editoro.clear();
                                        editoro.apply();
                                        editoroh.clear();
                                        editoroh.apply();

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                        overridePendingTransition(0,0);
                                    }
                                }else {
                                    Toast.makeText(Manage.this,"New name cannot be blank",Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        editDialog.create().show();

                        //refresh page to load the new name in activity
                    }



                }
            });

        }else{ // for delete button
            button.setId(id);
            button.setText(type);
            int imgres = R.drawable.ic_delete_forever;
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(imgres,0,0,0);
            int heightDP = (int) convertDpToPixel(35,this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, heightDP);
            //params.setMargins(0,0,0,-93);
            int marginrightDP = (int) convertDpToPixel(10,this);
            params.setMargins(0,0,marginrightDP,0);
            button.setTextColor(Color.WHITE);
            button.setLayoutParams(params);
            int paddingsides = (int) convertDpToPixel(3, this);
            int paddingleft = (int) convertDpToPixel(5,this);
            int paddingright = (int) convertDpToPixel(10,this);
            button.setPadding(paddingleft,paddingsides,paddingright,paddingsides);

            SharedPreferences themesp = getSharedPreferences("theme",0);
            String theme = themesp.getString("selectedtheme","classic");
            if(theme.matches("dark") == true){
                String drawablename = "historydelete_btn";
                setThemebtn(button,drawablename);
                //button.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classicmanagerename));
            }else{
                button.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.classicmanagedelete));
            }


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //delete OnClickListener: pop up window to confirm deleting board
                    // if yes, 1. remove SharedPreference of the selected board
                    // 2. remove board from spinner
                    // 3. remove all related view of the board from manage activity
                    //4. delete board history(boardname + "History")
                    //if no, close pop up window

                    //for delete
                        //get the delete btn id
                        int deletebtnid = view.getId();
                        //board id = deletebtnid - 2
                        int boardid = deletebtnid -2;
                        //get board name
                        TextView board = findViewById(boardid);
                        String boardName = board.getText().toString();

                        if(boardName.matches("Main") == true){
                            Toast toast = Toast.makeText(getApplicationContext(),"Cannot delete this board",Toast.LENGTH_LONG);
                            toast.show();

                        }else{
                            //pop up window
                            AlertDialog.Builder editDialog = new AlertDialog.Builder(Manage.this);
                            editDialog.setTitle("Board "+boardName + " will be deleted.");

                            editDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //board id in SharedPreference
                                    int deletebtnid = view.getId();
                                    int boardid = deletebtnid -2;
                                    int boardspid = boardid/3;
                                    TextView board = findViewById(boardid);
                                    String boardName = board.getText().toString();
                                    Log.d("deleteBoardName",boardName);
                                    //remove board from SharedPreference
                                    SharedPreferences spb = getSharedPreferences(boardName,0);
                                    SharedPreferences.Editor editorb = spb.edit();
                                    editorb.clear();
                                    editorb.apply();
                                    //remove board's history
                                    SharedPreferences sph = getSharedPreferences(boardName+"History",0);
                                    SharedPreferences.Editor editorh = sph.edit();
                                    editorh.clear();
                                    editorh.apply();
                                    //remove board from spinner
                                    SharedPreferences sps = getSharedPreferences("spinner",0);
                                    SharedPreferences.Editor editors = sps.edit();
                                    editors.putString("savedspinneropt","Main");
                                    editors.putString("savedspinneropt","Main");
                                    editors.putInt("spinnerpos",0);
                                    editors.remove("spinnerArray"+boardspid);
                                    editors.putInt("spinnerAmount",sps.getInt("spinnerAmount",0)-1);
                                    editors.apply();
                                    //remove all related view from manage activity
                                    //get delete btn's parent(include rename btn)
                                    LinearLayout btnsparent = (LinearLayout) view.getParent();
                                    // get the linearlayout View
                                    LinearLayout list = findViewById(R.id.manage_linearlayout);
                                    TextView boardView = findViewById(boardid);
                                    list.removeView(btnsparent);
                                    list.removeView(boardView);
                                    //Test
                                    //1. spinner list,sps
                                    Log.d("deleteBoardspinner",sps.getString("spinnerArray"+boardid,""));
                                    //2. Note list,spb
                                    Log.d("deleteboardNote",spb.getString("plan"+1,""));
                                    //3. History list, sph
                                    Log.d("deleteBoardhis",sph.getString("deletedNote"+1,""));
                                    Toast.makeText(getApplicationContext(),boardName+" has been deleted.",Toast.LENGTH_LONG).show();
                                    //refresh activity
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(0,0);
                                }
                            });
                            editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            editDialog.create().show();
                        }
                }
            });

        }
    }

    public void CreateNewBoard(View view){
        AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        editDialog.setTitle("New Board Name:");
        //editDialog.setIcon();
        final EditText inputView = new EditText(this);

        editDialog.setView(inputView);

        editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_LONG).show();
            }
        });

        editDialog.setPositiveButton("Create New Board", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String input = inputView.getText().toString();
                Log.d("boardinput",input);
                if(input.length() > 0){
                    //check if name exits(function)
                    boolean check = checkBoardNameExistence(input);
                    Log.d("renameCheck",String.valueOf(check));
                    if(check == true){
                        //exist, cannot use the name
                        Toast.makeText(getApplicationContext(),"This name is used.",Toast.LENGTH_LONG).show();
                    }else {
                        SharedPreferences sps = getSharedPreferences("spinner", 0);
                        SharedPreferences.Editor editors = sps.edit();
                        //String spinnerselected = sps.getString("savedspinneropt","Main");
                        int spinnerAmount = sps.getInt("spinnerAmount", 0);
                        int newSpinnerAmount = spinnerAmount + 1;
                        //add the name to spinner list
                        editors.putString("spinnerArray" + newSpinnerAmount, input);
                        //spinner amount +1
                        editors.putInt("spinnerAmount", spinnerAmount + 1);
                        editors.putInt("spinnerpreAmount", sps.getInt("spinnerpreAmount", 0) + 1);
                        editors.apply();
                        Toast.makeText(getApplicationContext(), "New Board " + input + " created", Toast.LENGTH_LONG).show();
                        Intent intent = getIntent();
                        finish();

                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please name your new board.",Toast.LENGTH_LONG).show();
                }
            }
        });

        editDialog.create().show();
    }

    public boolean checkBoardNameExistence(String newName){
        SharedPreferences spb = getSharedPreferences("spinner",0);
        int amount = spb.getInt("spinnerAmount",0);
        Log.d("renameAmount",String.valueOf(amount));
        boolean result = false;
        for(int i = 1; i <= amount; i++){
            String existedName = spb.getString("spinnerArray"+i,"");
            Log.d("renameExisted",existedName);
            Log.d("renamenew",newName);
            if(existedName.matches(newName) == true){
                result = true;
                break;
            }else{
                continue;
            }
        }
        return result;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}