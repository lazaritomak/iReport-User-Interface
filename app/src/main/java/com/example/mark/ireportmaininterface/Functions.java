package com.example.mark.ireportmaininterface;

/**
 * Created by Mark on 11/1/2014.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;


public class Functions extends AsyncTask<String, Void, String>
{
    private static final String TAG = ReportActivity.class.getSimpleName();
    URLConnection connection = null;

    AlertDialog alertDialog;
    String command;
    Context context;
    public static String link = "http://192.168.15.10/iReportDB/controller.php";//ip address/localhost
    public Functions (Context context)
    {
        this.context = context;
        alertDialog = new AlertDialog.Builder(this.context).create();
    }

    public URLConnection getConnection(String link)//Retrieve and connect to the url link
    {
        URL url = null;
        try//retrieves link from string
        {
            url = new URL(link);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            Log.d(TAG, e.getMessage().toString());
        }
        URLConnection connection = null;
        try//opens the url link provided from the "link" variable
        {
            connection = url.openConnection();
        }
        catch(IOException e)
        {
            e.printStackTrace();

            Log.d(TAG, e.getMessage().toString());
        }
        connection.setDoOutput(true);
        return connection;
    }

    public String getResult(URLConnection connection, String logs){

        String result="";

        OutputStreamWriter wr = null;
        try{
            wr= new OutputStreamWriter(connection.getOutputStream());
        }
        catch(IOException e){
            e.printStackTrace();

            Log.d(TAG, e.getMessage().toString());
        }

        try{
            wr.write(logs);
        }
        catch(IOException e){
            e.printStackTrace();

            Log.d(TAG, e.getMessage().toString());
        }

        try{
            wr.flush();
        }catch(IOException e){
            e.printStackTrace();

            Log.d(TAG, e.getMessage().toString());
        }

        BufferedReader reader = null;

        try{
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        }
        catch(IOException e){
            e.printStackTrace();

            Log.d(TAG, e.getMessage().toString());
        }

        StringBuilder sb = new StringBuilder();
        String line = null;

        try{
            while(((line = reader.readLine())!=null)){
                sb.append(line);
            }
        }
        catch(IOException e){
            e.printStackTrace();

            Log.d(TAG, e.getMessage().toString());
        }

        result = sb.toString();

        return result;
    }
    @Override
    protected String doInBackground(String... strings)
    {
        String result = "";
        try
        {
            command = (String) strings[0];
            if (command == "insertReport")
            {
                connection = getConnection(link);
                String logs = "";
                try
                {
                    logs="&command=" + URLEncoder.encode("insertReport","UTF-8");
                }
                catch(UnsupportedEncodingException e)
                {
                    throw new Exception();
                }
                try
                {
                    logs+="&reportid=" + URLEncoder.encode(generateReportID(),"UTF-8");
                }
                catch(UnsupportedEncodingException e)
                {
                    throw new Exception();
                }
                try
                {
                    logs+="&reportmd=" + URLEncoder.encode("url","UTF-8");
                }
                catch(UnsupportedEncodingException e)
                {
                    throw new Exception();
                }
                try
                {
                    logs+="&reportloc=" + URLEncoder.encode("Maniles","UTF-8");
                }
                catch(UnsupportedEncodingException e)
                {
                    throw new Exception();
                }
                try
                {
                    logs+="&reportcaption=" + URLEncoder.encode(ReportActivity.captionText.getText().toString(),"UTF-8");
                }
                catch(UnsupportedEncodingException e)
                {
                    throw new Exception();
                }
                try
                {
                    logs+="&reportcateg=" + URLEncoder.encode(ReportActivity.catList.getSelectedItem().toString(),"UTF-8");
                }
                catch(UnsupportedEncodingException e)
                {
                    throw new Exception();
                }
                result = getResult(connection, logs);
            }
            return result;
        }
        catch(Exception e)
        {
            return result;
        }
    }
    private String generateReportID()
    {
        String genID = "";
        char[] numArrays = {'1','2','3','4','5','6','7','8','9','0'};
        Random rand = new Random();
        for (int i = 0; i < 5; i++)
        {
            genID += numArrays[rand.nextInt(numArrays.length)];
        }
        return genID;
    }
}
