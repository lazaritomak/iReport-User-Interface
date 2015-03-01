package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
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

        //Controls initialization
        InitializeControls();
        //GPS initialization
        gps = new GPSTracker(ReportActivity.this);
        //item selection

        SharedPreferences mySession = getSharedPreferences(ReportActivity.PREFS_NAME, 0);
        ToastMessage("Welcome, " + mySession.getString("sessionUser", null));//Show Toast message welcoming the user
        username = mySession.getString("sessionUser", null);

        if (!gps.canGetLocation())
        {
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

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        Log.d("Lat", String.valueOf(latitude));
        Log.d("Long", String.valueOf(longitude));
    }

    private void InitializeControls() {
        btnAction = (Button)findViewById(R.id.btnSelectPhoto);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        //catList = (Spinner) findViewById(R.id.selectCategory);
        captionText = (EditText) findViewById(R.id.captionText);
    }
    final String[] agencyItems = {//add tag stuff here
            "Crime",
            "Health",
            "Fire",
            "Traffic",
            "Infrastructure",
            "Waste"
    };
    private void ShowCategory() {//This one shows the category with alert dialog
        final AlertDialog.Builder builder;
//        final ArrayList<String> selectedItems = new ArrayList<String>();
        selectItems = new ArrayList<String>();
//        isSelectedArray = new boolean[agencyItems.length];
//        int arraylength = isSelectedArray.length;
//        for (int i = 0; i < arraylength; i++)//initialize all the lists inside agencyItems to be unchecked ( false)
//        {
//            isSelectedArray[i] = false;
//        }
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        //SetMultiChoiceItems(builder, agencyItems, selectedItems);
        builder.setSingleChoiceItems(agencyItems, sI, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sI = i;
                ToastMessage(String.valueOf(sI));
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try
                {
                    selectedAgency = String.valueOf(agencyItems[sI]);
                    sendReport();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                ToastMessage("You have cancelled the submission");
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void SetMultiChoiceItems(AlertDialog.Builder builder, final CharSequence[] agencyItems, final ArrayList<String> selectedItems) {
        builder.setMultiChoiceItems(agencyItems, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() { // Multi choice, preserve tihs.
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
        });
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
        final CharSequence[] options = {"Take Photo", /*"Take Video",*/ "Choose From Gallery","View Report Status", /*"Sign Out" ,*/ "Cancel"};//Initialize options inside the builder dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setTitle("Select Action!");
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    GetCameraPhoto();
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
                else if (options[item].equals("View Report Status"))//Report Status Viewing
                {
                    ViewReportStatus();
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

    private void GetCameraPhoto() {//get photo from camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        File f = new File(Environment.getExternalStorageDirectory(), picFileName = generateFileName());
        ToastMessage(f.getAbsolutePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, 1);//1 is take photo
    }

    private void submitReport()
    {
        //new Functions(this).execute("insertReport");
        if (!gps.isNetworkEnabled || !gps.isGPSEnabled)
        {
            ShowGPSAlert();
        }
        else if (captionText.length() <= 0)
        {
            SimpleAlert("No Description Provided", "Please provide a description of the incident and kindly include the location");
        }
        else
        {
            if (image_str.length() <= 0)
            {
                ShowEmptyImageAlert();
            }
            else
            {
                ShowCategory();
            }
        }
    }
    private void ShowNullCategoryAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No category selected");
        builder.setMessage("Your report will be automatically set as Crime. Do you want to set the category?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ShowCategory();
            }
        });
        builder.setNegativeButton("No, send it now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedAgency = agencyItems[0];
                sendReport();
            }
        });
        builder.show();
    }

    private void ShowEmptyImageAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No image provided");
        builder.setMessage("Please take a picture of the incident or upload one from your gallery?");
        builder.setPositiveButton("Take Picture", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GetCameraPhoto();
            }
        });
        builder.setNegativeButton("Upload From Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GetGalleryPhoto();
            }
        });
        builder.show();
    }

    private void ShowGPSAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS not enabled");
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

    private void SimpleAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
    private void sendReport()
    {
        try//success
        {
            SimpleAlert("Success", new Functions(ReportActivity.this).execute("uploadData").get());
        }
        //Exceptions
        catch (InterruptedException e)
        {
            SimpleAlert("Unsuccessful", "Your Report did not send successfull");
        }
        catch (ExecutionException e)
        {
            SimpleAlert("Unsuccessful", "Your report did not send successfully");
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
                ToastMessage(f.getAbsoluteFile().toString());
                try
                {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //image compression
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byte_arr = stream.toByteArray();
                    image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                    //view image
                    viewImage.setImageBitmap(bitmap);
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
                ToastMessage(picturePath.toString() + "");
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

