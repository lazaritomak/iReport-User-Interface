package com.example.mark.ireportmaininterface;

/**
 * Created by Mark on 11/1/2014.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class Functions extends AsyncTask<String, Void, String>
{
    URLConnection connection = null;

    private ProgressDialog pd;
    String command;
    Context context;
    private static String ipadd = "192.168.15.10";
    public static String ServerAddress = ipadd;
    public static String link = "http://"+ipadd+"/iReportDB/controller.php";//ip address/localhost
    public Functions (Context context)
    {
        this.context = context;
    }
    public URLConnection getConnection(String link)//Retrieve and connect to the url link
    {
        URLConnection connection = null;
        try {
            URL url = null;
            url = new URL(link);
            connection = null;
            Log.v("getConnection", "Connecting");
            connection = url.openConnection();
            Log.v("getConnection", "Connected");
            connection.setDoOutput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public String getResult(URLConnection connection, String logs)
    {
        String result="";
        try {
            OutputStreamWriter wr = null;

            Log.v("Reader1", "Writing");
            wr= new OutputStreamWriter(connection.getOutputStream());
            wr.write(logs);
            wr.flush();

            BufferedReader reader = null;

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while(((line = reader.readLine())!=null))
            {
                sb.append(line);
            }
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean GenerateHttpPostData()
    {
        String TAG = "ReportActivity.java";
        boolean successful;
        String postReceiverUrl = "http://"+ipadd+"/iReportDB/filereceive.php";
        Log.v(TAG, "postURL: " + postReceiverUrl);
        try
        {
            //httpclient
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiverUrl);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //data needed
            NameValuePairsData(nameValuePairs);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpClient.execute(httpPost);
            successful = true;
        }
        catch (Exception e)
        {
            successful = false;
        }
        return successful;
    }

    private void NameValuePairsData(List<NameValuePair> nameValuePairs) {
    //add new data here
        nameValuePairs.add(new BasicNameValuePair("rpt_username", ReportActivity.username));
        nameValuePairs.add(new BasicNameValuePair("rpt_lat", String.valueOf(ReportActivity.latitude)));
        nameValuePairs.add(new BasicNameValuePair("rpt_long", String.valueOf(ReportActivity.longitude)));
        nameValuePairs.add(new BasicNameValuePair("rpt_desc", ReportActivity.captionText.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("rpt_categ", ReportActivity.selectedAgency));
        nameValuePairs.add(new BasicNameValuePair("rpt_image", ReportActivity.image_str));
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        pd.setMessage("Please Wait...");
        pd.show();
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
        pd.dismiss();
    }

    @Override
    protected String doInBackground(String... strings)
    {
        String result = "";
        Log.v("Functions", "doInBackground");
        try
        {
            command = (String) strings[0];
            Log.v("Command", command);
            if (command == "insertReport")//Not acually being used, but preserve in case of failure
            {
                result = InsertReport();
            }
            else if (command == "insertUser")
            {
                result = AddUser();
            }
            else if (command == "getAccountData")
            {
                result = GetAccountData();
            }
            else if (command == "uploadData")
            {
                result = UploadData();
            }
            else if (command == "viewStatus")
            {
                result = ViewStatus();
            }
            return result;
        }
        catch(Exception e)
        {
            return result;
        }
    }

    private String UploadData() {
        String result = "";
        if (GenerateHttpPostData())
        {
            result = "Your Report has been sent to the cops";
        }
        else
        {
            result = "Your report did not send successfully";
        }
        return result;
    }

    private String ViewStatus() throws UnsupportedEncodingException {
        String result = "";
        connection = getConnection(link);
        String logs = "";
        logs = "&command=" + URLEncoder.encode("viewStatus", "UTF-8");
        logs += "&user_name=" + URLEncoder.encode(ReportActivity.username, "UTF-8");
        result = getResult(connection, logs);
        return result;
    }

    private String GetAccountData() throws UnsupportedEncodingException {
        String result = "";
        connection = getConnection(link);
        String logs = "";
        logs = "&command=" + URLEncoder.encode("getAccountData", "UTF-8");
        logs += "&user_name=" + URLEncoder.encode(LoginMenu.txtUsername.getText().toString(), "UTF-8");
        logs += "&user_password="+ URLEncoder.encode(LoginMenu.txtPassword.getText().toString(), "UTF-8");
        result = getResult(connection, logs);
        Log.d("Sql Result", result);
        return result;
    }

    private String AddUser() throws UnsupportedEncodingException {
        String result = "";
        connection = getConnection(link);
        String logs = "";
        logs="&command=" + URLEncoder.encode("insertUser", "UTF-8");
        logs+="&user_email=" + URLEncoder.encode(CreateAccount.txtemailadd.getText().toString(),"UTF-8");
        logs+="&user_name=" + URLEncoder.encode(CreateAccount.txtusername.getText().toString(), "UTF-8");
        logs+="&user_password=" + URLEncoder.encode(CreateAccount.txtpassword.getText().toString(),"UTF-8");
        result = getResult(connection, logs);
        Log.v("Functions", "User Insert Successful");
        return result;
    }

    private String InsertReport() throws UnsupportedEncodingException {
        String result = "";
        connection = getConnection(link);
        String logs = "";
        logs="&command=" + URLEncoder.encode("insertReport", "UTF-8");
        //logs+="&report_id=" + URLEncoder.encode(generateReportID(),"UTF-8");
        logs+="&report_username=" + URLEncoder.encode("user", "UTF-8");
        logs+="$report_lat=" + String.valueOf(ReportActivity.latitude);
        logs+="$report_long=" + String.valueOf(ReportActivity.longitude);
//                logs+="&report_murl=" + URLEncoder.encode("url","UTF-8");
        logs+="&report_capt=" + URLEncoder.encode(ReportActivity.captionText.getText().toString(),"UTF-8");
        Log.v("Functions", "report_capt Successful");

        result = getResult(connection, logs);
        Log.v("Functions", "Report Insert Successful Successful");
        return result;
    }
}
