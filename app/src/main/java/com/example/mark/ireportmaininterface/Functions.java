package com.example.mark.ireportmaininterface;

/**
 * Created by Mark on 11/1/2014.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Functions extends AsyncTask<String, Void, String>
{
    URLConnection connection = null;

    AlertDialog alertDialog;
    String command;
    public static String userresult = "";
    Context context;
    Button btnCategory;
    //DONT FORGET TO CHANGE SERVER IP AHUEHUEHUE
    static String ipadd = "192.168.100.7";
    public static String link = "http://"+ipadd+"/iReportDB/controller.php";//ip address/localhost
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
        { e.printStackTrace();
            Log.v("getConnection", e.getMessage().toString());
        }
        connection.setDoOutput(true);
        return connection;
    }

    public String getResult(URLConnection connection, String logs){

        String result="";

        OutputStreamWriter wr = null;
        try
        {wr= new OutputStreamWriter(connection.getOutputStream());}
        catch(IOException e)
        {Log.v("getResult", e.getMessage().toString()); }

        try
        {wr.write(logs); }
        catch(IOException e)
        {Log.v("getResult", e.getMessage().toString()); }

        try
        {wr.flush();}
        catch(IOException e)
        { Log.v("getResult", e.getMessage().toString()); }

        BufferedReader reader = null;

        try
        { reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));}
        catch(IOException e)
        { Log.v("getResult", e.getMessage().toString());}

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

    public void generateHttpPostData()
    {
        String TAG = "ReportActivity.java";
        String postReceiverUrl = "http://"+ipadd+"/iReportDB/filereceive.php";
        Log.v(TAG, "postURL: " + postReceiverUrl);
        try
        {
            //httpclient
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiverUrl);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("rpt_id",generateReportID()));
            nameValuePairs.add(new BasicNameValuePair("rpt_username", ReportActivity.username));
            nameValuePairs.add(new BasicNameValuePair("rpt_lat", String.valueOf(ReportActivity.latitude)));
            nameValuePairs.add(new BasicNameValuePair("rpt_long", String.valueOf(ReportActivity.longitude)));
            nameValuePairs.add(new BasicNameValuePair("rpt_desc", ReportActivity.captionText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("rpt_image", ReportActivity.image_str));
//            Toast.makeText(context, ReportActivity.image_str, Toast.LENGTH_LONG).show();
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpClient.execute(httpPost);
        }
        catch (IOException e)
        {
        }
    }
    public void showMessage(Context context, String title, String message, String OkButtonString)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(OkButtonString, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
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
            if (command == "insertReport")
            {
                connection = getConnection(link);
                String logs = "";
                logs="&command=" + URLEncoder.encode("insertReport","UTF-8");
                logs+="&report_id=" + URLEncoder.encode(generateReportID(),"UTF-8");
                logs+="&report_username=" + URLEncoder.encode("user", "UTF-8");
                logs+="$report_lat=" + String.valueOf(ReportActivity.latitude);
                logs+="$report_long=" + String.valueOf(ReportActivity.longitude);
//                logs+="&report_murl=" + URLEncoder.encode("url","UTF-8");
//                logs+="&report_loc=" + URLEncoder.encode(String.valueOf(ReportActivity.latitude + " " + ReportActivity.longitude),"UTF-8");
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
                result = getResult(connection, logs);
                Log.d("Sql Result", result);
            }
            else if (command == "uploadData")
            {
                generateHttpPostData();
                result = "Your Report has been sent to the cops";
            }
            else if (command == "viewStatus")
            {
                connection = getConnection(link);
                String logs = "";
                logs = "&command=" + URLEncoder.encode("viewStatus", "UTF-8");
                logs += "&user_name=" + URLEncoder.encode(ReportActivity.username, "UTF-8");
                result = getResult(connection, logs);
            }
            return result;
        }
        catch(Exception e)
        {
            //Log.v("Functions", e.getMessage());
            return result;
        }
    }
    public String generateReportID()//temp lng to
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
