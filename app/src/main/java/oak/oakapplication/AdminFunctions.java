package oak.oakapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.GoogleAuthUtil;

import org.json.JSONException;

import java.io.IOException;

import static com.google.firebase.crash.FirebaseCrash.log;

public class AdminFunctions extends AppCompatActivity {

    Button forceGroup;
    EditText groupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_functions);


        forceGroup = (Button) findViewById(R.id.b_createGroup);
        groupId = (EditText) findViewById(R.id.et_groupId);



        forceGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String accountName = OakappMain.getAccount(getApplicationContext());

                String idToken = null;
                try {
                    idToken = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scope);
                } catch (Exception e) {
                    log("exception while getting idToken: " + e);
                }

                try {
                    OakappMain.createGroup(getString(R.string.sender_id), groupId.getText().toString(), idToken, getString(R.string.api_key));
                }
                catch (IOException |JSONException error){
                    Log.e(TAG, "Error while creating Group: " + error.getMessage());
                }
            }
        });

    }

    private final String TAG = "AdminFunctions";
    private final String scope = "audience:server:client_id:"
            + "1262xxx48712-9qs6n32447mcj9dirtnkyrejt82saa52.apps.googleusercontent.com";


}
