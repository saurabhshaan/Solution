package bharat.otouch.www.solution;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView mRVFishPrice;
    private Adapter mAdapter;
    SharedPreferences shared;
    String email,replay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_activity);
        Intent intent=getIntent();

        shared=getApplicationContext().getSharedPreferences("MyPref",1);
        email=shared.getString("email",null);
        //Make call to AsyncTask
        new AsyncFetch().execute(email);
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        // ProgressDialog pdLoading = new ProgressDialog(Main2Activity.this);
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            //   pdLoading.setMessage("\tLoading...");
            // pdLoading.setCancelable(false);
            //pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            Log.d("TAG", "hp befor url call");
            String url1 = "http://172.28.172.2/problemsolver/jsonreplay.php";
            Log.d("TAG", "hp after url call");
            String email= params[0];
            try {

                Log.d("TAG", "open url connection");
                URL url = new URL(url1);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(20 * 1000);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                //encode data before send it
                //no space permiteted in equals sign
                String data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") ;

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream input = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                input.close();
                httpURLConnection.disconnect();
                // Pass data to onPostExecute method
                return result.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            List<UserData1> data=new ArrayList<>();
            //this method will be running on UI thread

            //   pdLoading.dismiss();
            //   List<DataFish> data=new ArrayList<>();

            //  pdLoading.dismiss();
            Log.d("TAG",result);
            try {
                Log.d("TAG",result);
               JSONArray jArray = new JSONArray(result);

                // Extract data from json and store into ArrayList as class objects
                for(int i=0;i<jArray.length();i++){

                    JSONObject json_data = jArray.getJSONObject(i);
                    UserData1 ob=new UserData1();

                    ob.ExecutiveReplay=json_data.getString("replaydata");
                    ob.Email= json_data.getString("Email");
                    Log.d("Tag",json_data.getString("replaydata")+"  "+json_data.getString("Email"));

                    data.add(ob);
                }

                // Setup and Handover data to recyclerview
                mRVFishPrice = (RecyclerView)findViewById(R.id.fishPriceList);
                mAdapter = new Adapter(RecyclerViewActivity.this, data);
                mRVFishPrice.setAdapter(mAdapter);
                mRVFishPrice.setLayoutManager(new LinearLayoutManager(RecyclerViewActivity.this));

            } catch (JSONException e) {
                Toast.makeText(RecyclerViewActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }

        }

    }
}