package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
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

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
//XML creation namespaces
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReportActivity extends Activity {

    //session objects
    public static String username;
    public static boolean isLoggedIn;
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
        builder.setMultiChoiceItems(agencyItems, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
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
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        Log.d("Lat", String.valueOf(latitude));
        Log.d("Long", String.valueOf(longitude));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    String picFileName;
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
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), picFileName = generateFileName());
                    //generate file name
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
        //new Functions(this).execute("insertReport");
        new Functions(this).execute("uploadData");
    }

    private void generateHttpPostData()
    {
        String TAG = "ReportActivity.java";
        String postReceiverUrl = "http://192.168.15.10/iReportDB/filereceive.php";
        Log.v(TAG, "postURL: " + postReceiverUrl);

        try
        {
            //basic info
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiverUrl);
            List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("rpt_id", new Functions(this).generateReportID()));
            nameValuePairs.add(new BasicNameValuePair("rpt_username", "user"));
            nameValuePairs.add(new BasicNameValuePair("rpt_lat", String.valueOf(latitude)));
            nameValuePairs.add(new BasicNameValuePair("rpt_long", String.valueOf(longitude)));
            nameValuePairs.add(new BasicNameValuePair("rpt_desc", captionText.getText().toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpClient.execute(httpPost);
            Toast.makeText(this, "Sent to server", Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private String generateFileName()
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
    private void generateXMLReport()
    {
        try //database structure
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //test report elements, does not reflect the real database in the future
            //mandatory/ username, location, caption, media, time(Actually, it's best if the server determines recieve time)
            //optional/
            //reportdata elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("reportdata");
            doc.appendChild(rootElement);

            //username elements
            Element username = doc.createElement("username");
            username.appendChild(doc.createTextNode("testusername"));
            rootElement.appendChild(username);

            //Location ELEMENTS
            //latitude elements
            Element lat = doc.createElement("latitude");
            lat.appendChild(doc.createTextNode(String.valueOf(latitude)));
            rootElement.appendChild(lat);
            //longitude
            Element longi = doc.createElement("longitude");
            longi.appendChild(doc.createTextNode(String.valueOf(longitude)));
            rootElement.appendChild(longi);

            //caption text elements

            Element capt = doc.createElement("caption");
            capt.appendChild(doc.createTextNode(captionText.getText().toString()));
            rootElement.appendChild(capt);

            //tag elements
            String[] tagArr = new String[selectItems.size()];
            tagArr = selectItems.toArray(tagArr);
            Element tags = doc.createElement("tags");
            rootElement.appendChild(tags);
            //int o = selectItems.size();
            for(String s: tagArr) {
                Element tagname = doc.createElement("tagname");
                tagname.appendChild(doc.createTextNode(s));
                tags.appendChild(tagname);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            //save to directory
            StreamResult result = new StreamResult(new File(android.os.Environment.getExternalStorageDirectory(), "upload_data.xml"));
            transformer.transform(source, result);
            Log.d("MESSAGE", result.toString());
        }
        catch (ParserConfigurationException pce)
        {
            pce.printStackTrace();
            Log.d("XML ParserConfigurationException", pce.getMessage());
        }
        catch(TransformerException tfe)
        {
            tfe.printStackTrace();
            Log.d("XML TransformerException", tfe.getMessage());
        }
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
//                    if (temp.getName().equals("temp.jpg"))
                    if (temp.getName().equals(picFileName))
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

