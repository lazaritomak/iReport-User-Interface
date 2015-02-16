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


public class CreateAccount extends Activity {

    Button btnSubmit;
    Button btnBack;
    public static EditText txtusername;
    public static EditText txtpassword;
    public static EditText txtconfirmpass;
    public static EditText txtemailadd;
    public static TextView errorlabel;
    TextView viewError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnBack = (Button) findViewById(R.id.btnBack);
        txtusername = (EditText)findViewById(R.id.username);
        txtpassword = (EditText)findViewById(R.id.password);
        txtconfirmpass = (EditText)findViewById(R.id.confirmpass);
        txtemailadd = (EditText)findViewById(R.id.emailadd);
        errorlabel = (TextView) findViewById(R.id.lblError);
        viewError = (TextView)findViewById(R.id.lblError);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!IsAllFieldsValid())
                {
                    ShowMessageDialog("incomplete_fields");
                }
                else if (!IsPasswordsSame())
                {
                    ShowMessageDialog("diff_pass");
                }
                else
                {
                    ShowMessageDialog("success");
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainmenustep = new Intent(CreateAccount.this, LoginMenu.class);
                startActivity(mainmenustep);
            }
        });
    }

    private boolean IsAllFieldsValid() //Checks if all fields are entered valid yes
    {
        boolean isvalid = false;
        if (txtusername.getText().toString().trim().length() == 0
                || txtpassword.getText().toString().trim().length() == 0
                || txtconfirmpass.getText().toString().trim().length() == 0
                ||txtemailadd.getText().toString().trim().length() == 0)
        {
            isvalid = false;
        }
        else
        {
            isvalid = true;
        }
        return isvalid;
    }
    private boolean IsPasswordsSame()
    {
        boolean isvalid = false;
        Log.v("Non same error" , txtpassword.getText().toString());
        Log.v("Non same error" , txtconfirmpass.getText().toString());
        if (txtpassword.getText().toString().equals(txtconfirmpass.getText().toString()))
        {
            isvalid = true;
        }
        else
        {
            isvalid = false;
        }
        return isvalid;
    }
    private void ShowMessageDialog(String infoerror)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        if (infoerror == "incomplete_fields")
        {
            alertDialog.setTitle("Incomplete Fields");
            alertDialog.setMessage("Please fill out the required fields");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    //Do nothing
                }
            });
        }
        else if (infoerror == "diff_pass")
        {
            alertDialog.setTitle("Non-same Passwords");
            alertDialog.setMessage("Your passwords are not the same, please recheck");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    //D
                }
            });
        }
        else if (infoerror == "success")
        {
            new Functions(this).execute("insertUser");
            alertDialog.setTitle("Success");
            alertDialog.setMessage("Registration Successful, you can now log in");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
//                    btnBack.callOnClick();
                }
            });
        }
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_account, menu);
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
