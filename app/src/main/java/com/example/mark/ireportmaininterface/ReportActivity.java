package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import java.io.File;
import java.util.ArrayList;

public class ReportActivity extends Activity {

    //Interface objects
    ImageView viewImage;
    Button btnAction;
    Button btnCategory;
    Button btnSubmit;
    public static Spinner catList;
    public static EditText captionText;
    //Gps objects
    public static GPSTracker gps;
    static double latitude;
    static double longitude;
    //Alert objects
    AlertDialog alertDialog;
    //Yes
    boolean[] isSelectedArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //Widget initialization
        final AlertDialog.Builder builder;
        btnAction = (Button)findViewById(R.id.btnSelectPhoto);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnCategory = (Button)findViewById(R.id.selectCategory);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        //catList = (Spinner) findViewById(R.id.selectCategory);
        captionText = (EditText) findViewById(R.id.captionText);
        //GPS initialization
        gps = new GPSTracker(ReportActivity.this);

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
        final CharSequence[] agencyItems = {//add tag stuff here
                "Police Emergency",
                "Medical Services",
                "Traffic Enforcement",
                "Public Works",
                "Waste Management"
        };
        final ArrayList selectedItems = new ArrayList();
        isSelectedArray = new boolean[agencyItems.length];
        int arraylength = isSelectedArray.length;
        for (int i = 0; i < arraylength; i++)//initial all the lists inside agencyItems to be unchecked ( false)
        {
            isSelectedArray[i] = false;
        }
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setMultiChoiceItems(agencyItems, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    //If the user checked the item, add it to the selected items
                    //code when user checked the checkbox
                    selectedItems.add(indexSelected);
                    isSelectedArray[indexSelected] = true;

                } else if (selectedItems.contains(indexSelected)) {
                    //Else, if the item is already in the array, remove it
                    //code when user unchecks the checkbox
                    selectedItems.remove(Integer.valueOf(indexSelected));
                    isSelectedArray[indexSelected] = false;
                }
            }

        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //do nothing
            }
        });
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                builder.show();
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    private void selectAction(){
        final CharSequence[] options = {"Take Photo", "Take Video", "Choose From Gallery", "Sign Out" ,"Cancel"};//Initialize options inside the builder dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setTitle("Select Action!");
        builder.setItems(options, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);//1 is take photo
                }
                else if (options[item].equals("Take Video"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "myvideo.mp4");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 101); //101 is video capture
                }
                else if (options[item].equals("Choose From Gallery"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);//2 selects from gallery
                }
                else if (options[item].equals("Sign Out"))
                {
                    Intent mainmenu = new Intent(ReportActivity.this, LoginMenu.class);
                    startActivity(mainmenu);
                }
                else if (options[item].equals("Cancel"))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void submitReport()
    {
        new Functions(this).execute("getAccountData");
        //Toast.makeText(this, "Inserted", Toast.LENGTH_LONG).show();
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
                    if (temp.getName().equals("temp.jpg"))
                    {
                        f = temp;
                        break;
                    }
                }
                try
                {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    viewImage.setImageBitmap(bitmap);
                    //no need
                    /*String path = android.os.Environment.getExternalStorageDirectory() + File.separator + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try
                    {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }*/
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
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery....***************.....", picturePath + "");
                Toast.makeText(this, picturePath.toString() + "", Toast.LENGTH_LONG).show();
                viewImage.setImageBitmap(thumbnail);
            }
            else if (requestCode == 101)
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
                Bitmap bitmap;
/*                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);*/
                //viewImage.setImageBitmap(bitmap);
                Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath().toString(), MediaStore.Images.Thumbnails.MINI_KIND);
                viewImage.setImageBitmap(bmThumbnail);
            }
        }
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
