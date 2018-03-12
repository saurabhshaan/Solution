package bharat.otouch.www.solution.SharedprefrenceEmail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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


import bharat.otouch.www.solution.RecyclerViewActivity;

/**
 * Created by shaan on 16/07/2017.
 */

public class BackgroundTaskShared extends AsyncTask<String,Void,String> {
    Context ctx;
    SharedPreferences share;
    String Customer_email;
   String replay,email;

    public BackgroundTaskShared(Context ctx) {
        this.ctx = ctx;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected String doInBackground(String... params) {
        String reg_url = "http://172.28.172.2/problemsolver/jsonreplay.php";
        Log.d("TAG", "hello1");

        String method = params[0];
        Log.d("TAG", "hello2");
        if (method.equals("shared"))
            Log.d("TAG", "hello3");
        {
            Log.d("TAG", "hello4");
            Customer_email = params[1];
            Log.d("TAG", Customer_email);
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                Log.d("TAG", "open url connnection ");
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                Log.d("TAG", "buffered writer");
                //encode data before send it
                //no space permiteted in equals sign
                Log.d("TAG", "hello5");
                String OpnAcc =
                        URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(Customer_email, "UTF-8");

                Log.d("TAG", "data parameter set ");
                bufferedWriter.write(OpnAcc);
                bufferedWriter.flush();
                bufferedWriter.close();
                Log.d("TAG", "buffer writer close");
                os.close();


                // Read data sent from server
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
    }
// //get response from server


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("TAG1", result);
        JSONArray jArray = null;
        Log.d("TAG","JSONArray"+jArray);
        List<UserData> data=new ArrayList<>();

        try {
            jArray = new JSONArray(result);
            Toast.makeText(ctx, "json result" + jArray, Toast.LENGTH_SHORT).show();
        Log.d("TAG","json"+result);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Extract data from json and store into ArrayList as class objects
        for (int i = 0; i < jArray.length(); i++) {

            try {
                JSONObject json_data = jArray.getJSONObject(i);
                UserData userData=new UserData();

                replay=json_data.getString("replaydata");
                email= json_data.getString("Email");
                Log.d("Tag",json_data.getString("replaydata")+"  "+json_data.getString("Email"));



            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(ctx, RecyclerViewActivity.class);
            intent.putExtra("replay",replay);
            Log.d("TAG2",replay);
            intent.putExtra("email",email);
            Log.d("TAG#",email);
            ctx.startActivity(intent);



        }
    }

     class UserData {
        public String ExecutiveReplay;
        public String Email;
    }
}