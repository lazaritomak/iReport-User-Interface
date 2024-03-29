package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_LONG;


public class LoginMenu extends Activity {


    public static TextView ipAddDisp;
    public static EditText txtUsername;
    public static EditText txtPassword;
    Button btnLogin;
    Button btnRegister;
    static String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        ipAddDisp = (TextView) findViewById(R.id.infoDisplay);
        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);

        ipAddDisp.setText(Functions.ServerAddress);

        SharedPreferences mySession = getSharedPreferences(ReportActivity.PREFS_NAME, 0);
        if (mySession.getBoolean("sessionState", false) == true)
        {
//            ReportActivity.username = mySession.getString("sessionUser", null);
            Intent nextstep = new Intent(LoginMenu.this, ReportActivity.class);
            startActivity(nextstep);
        }
        btnLogin = (Button) findViewById(R.id.login);
        btnRegister = (Button) findViewById(R.id.register);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Account Data
                try
                {
                    message = new Functions(LoginMenu.this).execute("getAccountData").get();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    SimpleAlert("Error", "Cannot retrieve account", "OK");
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                    SimpleAlert("Error", "Cannot retrieve account", "OK");
                }
                //boolean yes = true;
                //if (!yes) //testing
                if (!(txtUsername.getText().toString().equals(message)))
                {
                    SimpleAlert("Login Error", "Invalid Username/Password", "OK");
                }
                else if (txtUsername.length() == 0 || txtPassword.length() == 0)
                {
                    SimpleAlert("Login Error", "Enter Username and Password", "OK");
                }
                else if ((txtUsername.getText().toString().equals(message)))//if successful
                {
                    SignIn();
                }
                else//if some unknown error occurred because fuck this shit
                {
                    SimpleAlert("Login Error", "An unknown error occurred, Please try again", "OK");
                }
                Log.d("END", "END OF LINE LOGIN");
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regstep = new Intent(LoginMenu.this, CreateAccount.class);
                startActivity(regstep);
            }
        });
    }

    private void SignIn() {
        ReportActivity.username = txtUsername.getText().toString();
        SharedPreferences mySession = getSharedPreferences(ReportActivity.PREFS_NAME, 0);
        SharedPreferences.Editor sessionEditor = mySession.edit();
        sessionEditor.putBoolean("sessionState", true);
        sessionEditor.putString("sessionUser", txtUsername.getText().toString());
        sessionEditor.commit();
        Intent nextStep = new Intent(this, ReportActivity.class);
        startActivity(nextStep);
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
    private void ToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private String getIpAddress(){
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while(enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    String ipAddress = "";
                    if(inetAddress.isLoopbackAddress())
                    {
                        ipAddress = "LoopbackAddress: ";
                    }
                    else if(inetAddress.isSiteLocalAddress())
                    {
                        ipAddress = "SiteLocalAddress: ";
                    }
                    else if(inetAddress.isLinkLocalAddress())
                    {
                        ipAddress = "LinkLocalAddress: ";
                    }
                    else if(inetAddress.isMulticastAddress())
                    {
                        ipAddress = "MulticastAddress: ";
                    }
                    ip += ipAddress + inetAddress.getHostAddress() + "\n";
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        ip += "DO NOT FORGET TO CHANGE HOST IP LINK\nGITHUB PROJECT";
        return ip;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
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
}
