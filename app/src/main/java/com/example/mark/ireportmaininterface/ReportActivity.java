package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
//custom imports
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
//gps

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
//XML creation namespaces
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReportActivity extends Activity {

    //session objects
    public static final String PREFS_NAME = "PrefsFile";
    public static String username;
    public static String image_str = "";
    //Interface objects
    ImageView viewImage;
    Button btnAction;
    Button btnCategory;
    Button btnSubmit;
    public static Spinner catList;
    public static EditText captionText;
    //Gps objects
    public static GPSTracker gps;
    public static double latitude;
    public static double longitude;
    //Alert objects
    AlertDialog alertDialog;
    //Yes
    boolean[] isSelectedArray;
    ArrayList<String> selectItems;

    int sI = -1;
    public static String selectedAgency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //Widget initialization
        btnAction = (Button)findViewById(R.id.btnSelectPhoto);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnCategory = (Button)findViewById(R.id.selectCategory);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        //catList = (Spinner) findViewById(R.id.selectCategory);
        captionText = (EditText) findViewById(R.id.captionText);
        //GPS initialization
        gps = new GPSTracker(ReportActivity.this);
        //item selection

        SharedPreferences mySession = getSharedPreferences(ReportActivity.PREFS_NAME, 0);
        Toast.makeText(this, "Welcome, " + mySession.getString("sessionUser", null), Toast.LENGTH_SHORT).show();
        username = mySession.getString("sessionUser", null);

        if (!gps.canGetLocation())
        {
/*            if (!gps.isGPSEnabled)
            {
                Toast.makeText(this, "Your GPS is disabled, please enable your GPS service", Toast.LENGTH_LONG).show();
            }
            if (!gps.isNetworkEnabled)
            {
                Toast.makeText(this, "Your Network is disabled, your GPS cannot work without any network", Toast.LENGTH_LONG).show();
            }*/
            gps.showSettingsAlert();
        }

        //Event Listeners
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAction();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReport();
            }
        });

        final AlertDialog.Builder builder = SetCategory();
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        Log.d("Lat", String.valueOf(latitude));
        Log.d("Long", String.valueOf(longitude));
    }

    private AlertDialog.Builder SetCategory() {
        final AlertDialog.Builder builder;
        final CharSequence[] agencyItems = {//add tag stuff here
                "Crime",
                "Health",
                "Fire",
                "Traffic",
                "Infrastructure",
                "Waste"
        };
        //final ArrayList selectedItems = new ArrayList();
        final ArrayList<String> selectedItems = new ArrayList<String>();
        selectItems = new ArrayList<String>();
        isSelectedArray = new boolean[agencyItems.length];
        int arraylength = isSelectedArray.length;
        for (int i = 0; i < arraylength; i++)//initial all the lists inside agencyItems to be unchecked ( false)
        {
            isSelectedArray[i] = false;
        }
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
/*        builder.setMultiChoiceItems(agencyItems, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() { // Multi choice, preserve tihs.
            @Override
            public void onClick(DialogInterface dialogInterface, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    //If the user checked the item, add it to the selected items
                    //code when user checked the checkbox
                    //selectedItems.add(indexSelected);
                    selectedItems.add(String.valueOf(agencyItems[indexSelected]));
                    selectItems.add(String.valueOf(agencyItems[indexSelected]));
                    isSelectedArray[indexSelected] = true;

                } else if (selectedItems.contains(indexSelected)) {
                    //Else, if the item is already in the array, remove it
                    //code when user unchecks the checkbox
                    selectedItems.remove(Integer.valueOf(indexSelected));
                    isSelectedArray[indexSelected] = false;
                }
            }
        });*/
        builder.setSingleChoiceItems(agencyItems, sI, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sI = i;
                Toast.makeText(ReportActivity.this, String.valueOf(sI), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Toast.makeText(ReportActivity.this, String.valueOf(agencyItems[sI]), Toast.LENGTH_SHORT).show();//TEST
                    selectedAgency = String.valueOf(agencyItems[sI]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return builder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    String picFileName;

    private void selectAction(){
        final CharSequence[] options = {"Take Photo", /*"Take Video",*/ "View Report Status","Choose From Gallery", /*"Sign Out" ,*/ "Cancel"};//Initialize options inside the builder dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setTitle("Select Action!");
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    GetCameraPhoto();
                }
                else if (options[item].equals("View Report Status"))//Report Status Viewing
                {
                    ViewReportStatus();
                }
/*                else if (options[item].equals("Take Video")) //No encoding, my god
                {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "myvideo.mp4");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 101); //101 is video capture
                }*/
                else if (options[item].equals("Choose From Gallery"))
                {
                    GetGalleryPhoto();
                }
                else if (options[item].equals("Sign Out"))
                {
                    //Load session states and current user who signed in previously, when not signed out.
                    SignOut();
                }
                else if (options[item].equals("Cancel"))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void ViewReportStatus() {
        Intent mainMenu = new Intent(this, ViewStatus.class);
        startActivity(mainMenu);
    }

    private void GetGalleryPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);//2 selects from gallery
    }

    private void GetCameraPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        File f = new File(Environment.getExternalStorageDirectory(), picFileName = generateFileName());
        Toast.makeText(this, f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, 1);//1 is take photo
    }

    private void submitReport()
    {
        //new Functions(this).execute("insertReport");
        if (!gps.isNetworkEnabled || !gps.isGPSEnabled)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS / Network not enabled");
            builder.setMessage("Your Phone cannot detect your GPS Location, Please include the exact location of the area of the incident");
            builder.setPositiveButton("Already Included", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendReport();
                }
            });
            builder.setNegativeButton("Not yet Included", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    gps.showSettingsAlert();
                }
            });
            builder.show();
        }
        else if (image_str.length() <= 0)
        {
            SimpleAlert("No media provided", "Please provide a picture for proof", "OK");
        }
        else if (captionText.length() <= 0)
        {
            SimpleAlert("No Description Provided", "Please provide a description of the incident and kindly include the location", "OK");
        }
        else
        {
            if (sI < 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No category selected");
                builder.setMessage("Your report will be defaultly set as Crime. Do you want to set the category?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder builder = SetCategory();
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                builder.setNegativeButton("No, send it now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendReport();
                    }
                });
                builder.show();
            }
            else
            {
                sendReport();
            }
        }
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

    private void sendReport()
    {
        try//success
        {
            SimpleAlert("Success", new Functions(ReportActivity.this).execute("uploadData").get(), "OK");
        }
        //Exceptions
        catch (InterruptedException e)
        {
            SimpleAlert("Unsuccessful", "Your Report did not send successfull", "Damn");
        }
        catch (ExecutionException e)
        {
            SimpleAlert("Unsuccessful", "Your report did not send successfully", "Damn");
        }
    }
    private String generateFileName()//generate file name with random shit
    {
        String genFileName = "";
        String stringFileName = "abcdefghijklmnopqrstuvwxyz123456789";
        char[] charFileName = stringFileName.toCharArray();
        Random rand = new Random();
        for (int i = 0 ;i < 10; i++)
        {
            genFileName += charFileName[rand.nextInt(charFileName.length)];
        }
        return genFileName + ".jpg";
    }
    private void SignOut()
    {
        //Load session states and current user who signed in previously, when not signed out.
        SharedPreferences mySession = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor sessionEditor = mySession.edit();
        sessionEditor.putBoolean("sessionState", false);
        sessionEditor.putString("sessionUser", "");
        sessionEditor.commit();
        //Go to main
        Intent mainMenu = new Intent(ReportActivity.this, LoginMenu.class);
        startActivity(mainMenu);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1)
            {
                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp: f.listFiles())
                {
                    if (temp.getName().equals(picFileName))//finds the filename as the intent saved
                    {
                        f = temp;
                        break;
                    }
                }
                Toast.makeText(this, f.getAbsoluteFile().toString(), Toast.LENGTH_LONG).show();
                try
                {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //image compression
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] byte_arr = stream.toByteArray();
                    image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                    //view image
                    viewImage.setImageBitmap(bitmap);
                    //File out put stream
                    //forces to create a new image file using raw bytes , haha xD
                    FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                    fos.write(byte_arr);
                    fos.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (requestCode == 2)
            {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                //start
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                //start
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] byte_arr = stream.toByteArray();
                image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                //end
                Log.w("path of image from gallery....***************.....", picturePath + "");
                Toast.makeText(this, picturePath.toString() + "", Toast.LENGTH_LONG).show();
                viewImage.setImageBitmap(thumbnail);
            }
/*            else if (requestCode == 101)
            {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp: f.listFiles())
                {
                    if (temp.getName().equals("myvideo.mp4"))
                    {
                        f = temp;
                        break;
                    }
                }
                //Toast.makeText(this, f.getAbsolutePath().toString(), Toast.LENGTH_LONG).show(); //Directory test
//                Bitmap bitmap;
*//*                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);*//*
                //viewImage.setImageBitmap(bitmap);
                Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath().toString(), MediaStore.Images.Thumbnails.MINI_KIND);
                viewImage.setImageBitmap(bmThumbnail);
            }*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                gps.openGPSSettings();
                return true;
            case R.id.sign_out:
                SignOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

