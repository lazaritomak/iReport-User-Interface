package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;


public class LoginMenu extends Activity {


    public static TextView ipAddDisp;
    public static EditText txtUsername;
    public static EditText txtPassword;
    Button btnLogin;
    Button btnRegister;
    public static String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        ipAddDisp = (TextView) findViewById(R.id.infoDisplay);
        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        btnRegister = (Button) findViewById(R.id.register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Account Data
                try
                {
                    message = new Functions(LoginMenu.this).execute("getAccountData").get();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                } catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                //boolean yes = true;
                Log.d("FUNCTIONS USER", Functions.userresult);

                //do not to campre null textbox to null message. put them in different condition
                if (txtUsername.length() == 0 || txtPassword.length() == 0)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginMenu.this).create();
                    alertDialog.setTitle("Login Error");
                    alertDialog.setMessage("Enter Username and Password");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                }
                else if (txtUsername.getText().toString().equals(message))
                {
                    Intent nextstep = new Intent(LoginMenu.this, ReportActivity.class);
                    startActivity(nextstep);
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginMenu.this).create();
                    alertDialog.setTitle("Login Error");
                    alertDialog.setMessage("Invalid Username/Password");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
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

        //gets ip address
        ipAddDisp.setText(getIpAddress());
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
        ip += "DO NOT FORGET TO CHANGE HOST IP LINK";
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
