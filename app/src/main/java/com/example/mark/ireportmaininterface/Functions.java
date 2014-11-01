package com.example.mark.ireportmaininterface;

/**
 * Created by Mark on 11/1/2014.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class Functions extends AsyncTask<String, Void, String>
{
    URLConnection connection = null;

    String command;
    Context context;
    public static String link = "http://localhost/iReportDB/controller.php";//ip address/localhost
    public Functions (Context context)
    {
        this.context = context;
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
        }
        URLConnection connection = null;
        try//opens the url link provided from the "link" variable
        {
            connection = url.openConnection();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        connection.setDoOutput(true);
        return connection;
    }
    @Override
    protected String doInBackground(String... strings)
    {
        String result = "";

        try {
            command = (String) strings[0];
            if (command == "testInsert") {
                connection = getConnection(link);
                String logs = "";
                logs = "&command" + URLEncoder.encode(command, "UTF-8");
            }
            return null;
        }
        catch (Exception e)
        {
            return result;
        }
    }
}
