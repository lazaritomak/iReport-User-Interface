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
import android.widget.Button;
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
    URLConnection connection = null;

    AlertDialog alertDialog;
    String command;
    Context context;
    Button btnCategory;
    //DONT FORGET TO CHANGE SERVER IP AHUEHUEHUE
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
            Log.v("getConnection", e.getMessage().toString());
        }
        URLConnection connection = null;
        try//opens the url link provided from the "link" variable
        {
            connection = url.openConnection();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.v("getConnection", e.getMessage().toString());
        }
        connection.setDoOutput(true);
        return connection;
    }

    public String getResult(URLConnection connection, String logs){

        String result="";

        OutputStreamWriter wr = null;
        try
        {
            wr= new OutputStreamWriter(connection.getOutputStream());
        }
        catch(IOException e)
        {
            Log.v("getResult", e.getMessage().toString());
        }
        try
        {
            wr.write(logs);
        }
        catch(IOException e)
        {
            Log.v("getResult", e.getMessage().toString());
        }
        try
        {
            wr.flush();
        }catch(IOException e)
        {
            Log.v("getResult", e.getMessage().toString());
        }

        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        }
        catch(IOException e)
        {
            Log.v("getResult", e.getMessage().toString());
        }

        StringBuilder sb = new StringBuilder();
        String line = null;

        try
        {
            while(((line = reader.readLine())!=null))
            {
                sb.append(line);
            }
        }
        catch(IOException e)
        {
            Log.v("getResult", e.getMessage().toString());
        }
        result = sb.toString();

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (command == "insertReport")
        {
            ReportActivity.captionText.setText(result);
        }
        else if (command == "insertUser")
        {
            CreateAccount.errorlabel.setText(result);
        }
        else if (command == "getAccountData")
        {
          LoginMenu.message = result;
            //LoginMenu.ipAddDisp.setText(result);
        }
    }

    @Override
    protected String doInBackground(String... strings)
    {
        String result = "";
        Log.v("Functions", "doInBackground");
        try
        {
            command = (String) strings[0];
            Log.v("Functions", "Entered background method");
            if (command == "insertReport")
            {
                connection = getConnection(link);
                String logs = "";
                logs="&command=" + URLEncoder.encode("insertReport","UTF-8");
                logs+="&report_id=" + URLEncoder.encode(generateReportID(),"UTF-8");
                logs+="&report_username=" + URLEncoder.encode("user", "UTF-8");
                logs+="&report_murl=" + URLEncoder.encode("url","UTF-8");
                logs+="&report_loc=" + URLEncoder.encode("Maniles","UTF-8");
                logs+="&report_capt=" + URLEncoder.encode(ReportActivity.captionText.getText().toString(),"UTF-8");
                Log.v("Functions", "report_capt Successful");

                result = getResult(connection, logs);
                Log.v("Functions", "Report Insert Successful Successful");
            }
            else if (command == "insertUser")
            {
                connection = getConnection(link);
                String logs = "";
                logs="&command=" + URLEncoder.encode("insertUser","UTF-8");
                logs+="&user_email=" + URLEncoder.encode(CreateAccount.txtemailadd.getText().toString(),"UTF-8");
                logs+="&user_name=" + URLEncoder.encode(CreateAccount.txtusername.getText().toString(), "UTF-8");
                logs+="&user_password=" + URLEncoder.encode(CreateAccount.txtpassword.getText().toString(),"UTF-8");
                result = getResult(connection, logs);
                Log.v("Functions", "User Insert Successful");
            }
            else if (command == "getAccountData")
            {
                connection = getConnection(link);
                String logs = "";
                logs = "&command=" + URLEncoder.encode("getAccountData", "UTF-8");
                logs += "&user_name=" + URLEncoder.encode(LoginMenu.txtUsername.getText().toString(), "UTF-8");
                logs += "&user_password="+ URLEncoder.encode(LoginMenu.txtPassword.getText().toString(), "UTF-8");
                Log.d("logs results",logs);
                result = getResult(connection, logs);
                //Check for null results, null = invaild account;
                Log.v("Sql Result", result);
                if (result.isEmpty())
                {
                    LoginMenu.message = null;
                }
                else
                {
                    LoginMenu.message = result;
                }
                Log.v("login Menu Result", result);
                Log.v("Functions", "User Retrieve Successful");
            }
            return result;
        }
        catch(Exception e)
        {
            //Log.v("Functions", e.getMessage());
            return result;
        }
    }
    private String generateReportID()//temp lng to
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
