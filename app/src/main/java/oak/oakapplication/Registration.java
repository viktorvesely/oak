package oak.oakapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Registration extends AppCompatActivity {

    private Button mPickImg;
    private Button mContinue;
    private EditText mUsername;
    private ImageView mProfilePicture;
    private Uri mPhoto;
    private FirebaseRemoteConfig mConfigs;
    private StorageReference mPostPhotosreference;
    private DatabaseReference mUserRef;

    private long mMinUsernameLength;
    private boolean duplicate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        duplicate = false;
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mConfigs = FirebaseRemoteConfig.getInstance();
        mMinUsernameLength = mConfigs.getLong("username_min_length");
        mPostPhotosreference = FirebaseStorage.getInstance().getReference().child("profile_photos");
        mPickImg = (Button) findViewById(R.id.b_registration_pickImg);
        mContinue = (Button) findViewById(R.id.b_continue);
        mUsername = (EditText) findViewById(R.id.et_username);
        mProfilePicture = (ImageView) findViewById(R.id.iv_profilePic);


        mContinue.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (mPhoto == null) {
                                                 mPhoto = Uri.parse(OakappMain.NO_PICTURE);
                                             }
                                             OakappMain.user.mPhoto = mPhoto;
                                             if (mUsername.getText().length() >= mMinUsernameLength)
                                                 Snackbar.make(v, getString(R.string.short_username), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                             else {
                                                 CheckForExistingUser();
                                                 //TODO: sleep
                                                 if (duplicate) {
                                                     Snackbar.make(findViewById(android.R.id.content).getRootView(), getString(R.string.duplicate_name), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                                 }
                                                 else {
                                                     OakappMain.user.mPhoto = mPhoto;
                                                     OakappMain.user.mUniqueName = mUsername.getText().toString();
                                                     finish();
                                                 }
                                             }
                                         }
        });

        mPickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action)), RC_PHOTO_PICKER);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            StorageReference photoReference = mPostPhotosreference.child(selectedImage.getLastPathSegment());
            photoReference.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get check mark
                    mPhoto = taskSnapshot.getDownloadUrl();
                }
            });
        }
    }

    private void CheckForExistingUser()
    {
        Query filter = mUserRef.orderByChild("mUniqueName").equalTo(mUsername.getText().toString());

        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                duplicate = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;
}
