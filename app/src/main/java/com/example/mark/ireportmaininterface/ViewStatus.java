package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;


public class ViewStatus extends Activity {

    //Having their own arrays for display purposes
    String[] reportid;
    String[] reportdate;
    String[] reportprogress;
    String[] reportmediacaption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);
        try
        {
            initializeList();
        }
        catch(Exception e)//I should probably remove but errors could occur, so fuck it.
        {
            new AlertDialog.Builder(ViewStatus.this)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .setPositiveButton("Its Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void SimpleAlert(String title, String message, String buttonMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }
    private void initializeList()
    {
        ListView lv;
        String result = "";
        String[] resultArr;
        try
        {
            //Initialize Hashmap and ArrayList
            ArrayList<HashMap<String, String>> feedList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map = new HashMap<String, String>();
            //Tokenizer for separating text with delimiter
            StringTokenizer st;
            //Text for headers
            TextView header = (TextView) findViewById(R.id.textView);
            header.setText("Reports");
            lv = (ListView)findViewById(R.id.listView);
            //get result from server
            result = new Functions(this).execute("viewStatus").get();
            //Split each record with ;
            resultArr = result.split(";");
            //Create arrays for report id, date, and progress
            reportdate = new String[resultArr.length];
            reportprogress = new String[resultArr.length];
            reportmediacaption = new String[resultArr.length];
            //Initialize the results to each of the array
            Log.v("Working", String.valueOf(resultArr.length));
            for (int i = 0; i < resultArr.length; i++)
            {
                st = new StringTokenizer(resultArr[i].toString(), "/");
                reportdate[i] = st.nextToken();
                reportprogress[i] = st.nextToken();
                reportmediacaption[i] = st.nextToken();
                Log.v("Values", reportdate[i] + reportprogress[i] + reportmediacaption[i]);
                map = new HashMap<String, String>();
                map.put("Reports", reportdate[i] + " / " + reportprogress[i]+ " / "+ reportmediacaption[i]);
                feedList.add(map);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(ViewStatus.this , feedList, R.layout.activity_view_status, new String[]{"Reports"}, new int[]
                    {R.id.textView});
            lv.setAdapter(simpleAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ViewStatus.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage(reportid[i].toString());
                    alertDialog.setButton("Oh Yeah", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                }
            });
        }
        catch(ExecutionException e)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(ViewStatus.this).create();
            alertDialog.setMessage("Uh oh");
            alertDialog.setButton(e.getMessage(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alertDialog.show();
        }
        catch (InterruptedException e)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(ViewStatus.this).create();
            alertDialog.setMessage("Uh oh");
            alertDialog.setButton(e.getMessage(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alertDialog.show();
        }
    }
}