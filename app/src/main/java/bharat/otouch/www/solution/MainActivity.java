package bharat.otouch.www.solution;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bharat.otouch.www.solution.SharedprefrenceEmail.BackgroundTaskShared;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //this part include objects (Main UI)
    private EditText mName,mEmail,mMobile,mQuery;
    private ImageView mImage;
    private Button mSubmit,mCamera;
    //UI object part closed

    //String declaration
    String name, mobile,query;
    String encodedImage, imgDecodableString;
    private static int RESULT_LOAD_IMG = 1;
    //sharedPref
    SharedPreferences shared;
    String email;
    String method;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Object Call
        mName = (EditText) findViewById(R.id.name);
        mEmail =(EditText) findViewById(R.id.email);
        mMobile = (EditText) findViewById(R.id.phone);
        mQuery=(EditText)findViewById(R.id.multilinetext);
        mImage=(ImageView)findViewById(R.id.openimage);
        mCamera=(Button)findViewById(R.id.camera);
        mSubmit=(Button)findViewById(R.id.submit);
        //Object Call closed
        mSubmit.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.submit) {
            register();
        }

    }
    public void register() {
        //validation
        if (mName.getText().toString().length() == 0) {
            mName.setError("Name can not be blanked");
            return;
        } else if (mEmail.getText().toString().length() == 0) {
            mEmail.setError("Fill email");
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {//Validation for Invalid Email Address
            mEmail.setError("Invalid Email");
            return;
        }else if (mMobile.getText().toString().length() == 0) {
            mMobile.setError("Fill mobile number");
        } else if(mQuery.getText().toString().length() ==0){
            mQuery.setError("Invalid Query");
            return;
        }
        else {
            Toast.makeText(this, "All Fields Validated", Toast.LENGTH_SHORT).show();
        }
//validation END
        name = mName.getText().toString().trim();
        mobile = mMobile.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        query = mQuery.getText().toString().trim();
        Toast.makeText(this, "result" + name + " " + mobile + " " + email+""+ query, Toast.LENGTH_SHORT).show();
        if (isOnline()) {
            String method = "register";
            Toast.makeText(this, "connection is ok", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "bt start");
            BackgroundTask backgroundTask = new BackgroundTask(MainActivity.this);
            backgroundTask.execute(method, name, mobile, email, query, encodedImage);
            Log.d("TAG", "bt end");

        } else {
            Toast.makeText(MainActivity.this, "Connection is Offline", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public void takePicture(View View) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//MediaStore is type of dtabse whwere image and video storeed.
      /*  imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Test.jpg");//directory path and file name two argument in file
        Toast.makeText(LabourRegistration.this, "Picture Clicked" + imageFile, Toast.LENGTH_SHORT).show();

        //generate path Uri
        Uri value = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, value);//Extraoutput show path for saving file
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//define image quality 0 for low and 1 for high quality
        */
        startActivityForResult(intent, 0);
    }

    public void browseImage(View v) {

// Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra("crop", "true");
        galleryIntent.putExtra("outputX", 200);
        galleryIntent.putExtra("outputY", 260);
        galleryIntent.putExtra("aspectX", 1);
        galleryIntent.putExtra("aspectY", 1);
        galleryIntent.putExtra("scale", true);
        galleryIntent.putExtra("return-data", true);
// Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);


    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 0) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Toast.makeText(this, "Picture saved at " + imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                        Toast.makeText(MainActivity.this, "ImageSet", Toast.LENGTH_SHORT).show();
                        mImage.setImageBitmap(thumbnail);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        if (thumbnail != null) {
                            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object//0 for low quality
                        }
                        byte[] b = baos.toByteArray();
                        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        Toast.makeText(MainActivity.this, "Wait for moment ....", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "Activity.RESULT_CANCELLED", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;


                }

            }//onActivityCamera-END
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                if (cursor != null) {
                    cursor.moveToFirst();
                }

                int columnIndex = 0;
                if (cursor != null) {
                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                }
                if (cursor != null) {
                    imgDecodableString = cursor.getString(columnIndex);
                }
                if (cursor != null) {
                    cursor.close();
                }
                // Set the Image in ImageView after decoding the String
                mImage.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                //imageUploadSTART

                Bitmap bm = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object//0 for low quality
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                Toast.makeText(MainActivity.this, "ImageSet", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Wait for moment ....", Toast.LENGTH_SHORT).show();
                Log.d("error", "images" + encodedImage);
                //close
            }
        } catch (Exception e) {
            Toast.makeText(this, "Problem Detected!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.inbox_menu_btn) {
            shareprefrencemethod();
           Intent intent=new Intent(this,RecyclerViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void shareprefrencemethod(){
     email = mEmail.getText().toString().trim();
        if (isOnline()) {
            shared=getApplicationContext().getSharedPreferences("MyPref",1);
            email=shared.getString("email",null);
            Toast.makeText(this, "email"+email, Toast.LENGTH_SHORT).show();
            method = "shared";
            Toast.makeText(this, "connection is ok", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "btshared start");
           // RecyclerViewActivity recyclerViewActivity = new RecyclerViewActivity();
            //recyclerViewActivity.
            Log.d("TAG", "btshared end");

        } else {
            Toast.makeText(MainActivity.this, "Connection is Offline", Toast.LENGTH_SHORT).show();
        }
    }
}