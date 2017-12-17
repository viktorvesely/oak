package oak.oakapplication;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.android.gms.auth.GoogleAuthUtil;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.google.firebase.crash.FirebaseCrash.log;

public class AdminFunctions extends AppCompatActivity {

    Button forceGroup;
    Button connectToDennik;
    EditText groupId;
    TextView notificationId;
    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_functions);


        forceGroup = (Button) findViewById(R.id.b_createGroup);
        groupId = (EditText) findViewById(R.id.et_groupId);
        notificationId = (TextView) findViewById(R.id.tv_notificationKey);
        connectToDennik = (Button) findViewById(R.id.b_connectToDennik);
        mResponse = (TextView) findViewById(R.id.tv_response);


        connectToDennik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HTTPRequest request = new HTTPRequest();
                Void temp = null;
                request.execute(temp);
            }
        });

        forceGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatGroupRequest asyncTask = new CreatGroupRequest();
                asyncTask.execute(groupId.getText().toString());

            }
        });



    }


    public class HTTPRequest extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void ... v) {
            URL url = null;
            try {
                url = new URL("https://dennikn.sk/");
            }
            catch (MalformedURLException e) {
                Log.e(TAG, "Invalid url: " + e.getMessage());
            }
            HttpURLConnection con = null;

            try {
                if (url != null)
                    con = (HttpURLConnection) url.openConnection();
            }
            catch (IOException e) {
                Log.e(TAG, "Error while openning connection: " + e.getMessage());
            }
            if (con == null)
                return "NOOOOOOOOOOOOOOOOOOOOOOOOOOOOONE";

            con.setDoOutput(true);

            InputStream is;
            String responseString = "";

            try {
                is = con.getInputStream();
                responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
                is.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Error while openning connection: " + e.getMessage());
            }

            return responseString;




        }

        @Override
        protected void onPostExecute(String result) {
            mResponse.setText(result);
        }
    }


    public class CreatGroupRequest extends AsyncTask<String, Void, String> {


        protected String doInBackground(String...s) {

            String accountName = OakappMain.getAccount(getApplicationContext());
            String idToken = null;
            try {
                idToken = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scope);
            } catch (Exception e) {
                log("exception while getting idToken: " + e);
            }

            String notificationKey = null;
            try {
                notificationKey = OakappMain.createGroup(getString(R.string.sender_id), s[0], idToken , getString(R.string.api_key));
            } catch (IOException | JSONException | NetworkOnMainThreadException error) {
                Log.e(TAG, "Error while creating Group: " + error.getMessage());
            }

            return notificationKey;
        }


        protected void onPostExecute(String result) {
            notificationId.setText(result);
        }
    }


    private final String TAG = "AdminFunctions";
    private final String scope = "audience:server:client_id:"
            + "1262xxx48712-9qs6n32447mcj9dirtnkyrejt82saa52.apps.googleusercontent.com";




}
