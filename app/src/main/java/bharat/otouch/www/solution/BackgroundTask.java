package bharat.otouch.www.solution;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by shaan on 16/07/2017.
 */

public class BackgroundTask extends AsyncTask<String,Void,String> {
    Context ctx;
    SharedPreferences share;
    String Customer_email;
    public BackgroundTask(Context ctx)
    {
        this.ctx=ctx;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected String doInBackground(String... params) {
        String reg_url="http://172.28.172.2/problemsolver/problem_db.php";
        Log.d("TAG", "step1");

        String method=params[0];
        if (method.equals("register"))
        {
            String Customer_name=params[1];
            String Customer_mobile=params[2];
             Customer_email=params[3];
            String Customer_query=params[4];
            String Customer_image=params[5];
            Log.d("TAG", Customer_name+" "+Customer_mobile+" "+Customer_email+""+Customer_query+""+Customer_image);
            try {
                URL url=new URL(reg_url);
                HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                Log.d("TAG", "open url connnection ");
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                Log.d("TAG","buffered writer");
                //encode data before send it
                //no space permiteted in equals sign
                String OpnAcc= URLEncoder.encode("Name","UTF-8")+"="+ URLEncoder.encode(Customer_name,"UTF-8")+"&"+
                        URLEncoder.encode("Mobile","UTF-8")+"="+ URLEncoder.encode(Customer_mobile,"UTF-8")+"&"+
                        URLEncoder.encode("Email","UTF-8")+"="+ URLEncoder.encode(Customer_email,"UTF-8")+"&"+
                        URLEncoder.encode("Query","UTF-8")+"="+ URLEncoder.encode(Customer_query,"UTF-8");
                   //    +"&"+ URLEncoder.encode("Image","UTF-8")+"="+ URLEncoder.encode(Customer_image,"UTF-8");

                Log.d("TAG", "data parameter set ");
                bufferedWriter.write(OpnAcc);
                bufferedWriter.flush();
                bufferedWriter.close();
                Log.d("TAG", "buffer writer close");
                os.close();
                //get response from server
                InputStream is=httpURLConnection.getInputStream();
                Log.d("TAG", "debug");
                is.close();
                return "Login Success...Welcome";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("TAG", "loged in");
        share=ctx.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=share.edit();
        editor.putString("email",Customer_email);
        Log.d("TAG","Email Sift");
        editor.commit();

        Toast.makeText(ctx, "Welcome", Toast.LENGTH_SHORT).show();
    }


}
