package oak.oakapplication;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.toIntExact;

public class PostsActivity extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private DatabaseReference postRef;
    private Geocoder geocoder;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPostPhotosreference;

    private OakappMain main;
    private Button mPostButton;
    private Button mAddImage1;
    private Button mAddImage2;
    private EditText mTitle;
    private EditText mPostText;
    private EditText mTags;
    private EditText mAddress;
    private String[] mArraySpinner;
    private Spinner mCategories;


    private final static int MaxTagsPerPost = 3;
    private final String[] categories = {"Problemy","V procese", "Vyriesene"};
    private Uri mImgUrl1;
    private Uri mImgUrl2;

    private int mSelectedImg;

    private static final int RC_PHOTO_PICKER =  2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        main = ((OakappMain)getApplicationContext());
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPostPhotosreference = mFirebaseStorage.getReference().child("post_photos");
        postRef = mRootRef.child("Posts");

        mImgUrl1 = null;
        mImgUrl2 = null;
        mSelectedImg = 0;
        mPostButton = (Button) findViewById(R.id.b_post);
        mPostText = (EditText) findViewById(R.id.et_postText);
        mTitle = (EditText) findViewById(R.id.et_title);
        mAddress = (EditText) findViewById(R.id.et_location);
        mCategories = (Spinner) findViewById(R.id.sp_categories);
        mAddImage1 = (Button) findViewById(R.id.b_addImg1);
        mAddImage2 = (Button) findViewById(R.id.b_addImg2);
        mTags = (EditText) findViewById(R.id.et_tags);

        mArraySpinner = categories;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, mArraySpinner);
        mCategories.setAdapter(adapter);

        geocoder = new Geocoder(this);


        mAddImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedImg = 1;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action)), RC_PHOTO_PICKER);
            }
        });

        mAddImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedImg = 2;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action)), RC_PHOTO_PICKER);
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> addresses = null;
                double longitude = 0;
                double latitude = 0;

                String tags = mTags.getText().toString();
                int numberOfTags = 1;

                if (tags.isEmpty()) { Snackbar.make(v, getString(R.string.min_tags), Snackbar.LENGTH_LONG).setAction("Action", null).show(); }

                for (int i = 0; i < tags.length(); ++i)
                {
                    if (tags.charAt(i) == ','){
                        numberOfTags++;
                    }
                }

                if (numberOfTags > MaxTagsPerPost) { Snackbar.make(v, getString(R.string.max_tags), Snackbar.LENGTH_LONG).setAction("Action", null).show(); }
                String imgaddr1 = "";
                String imgaddr2 = "";
                if (mImgUrl1 != null) { imgaddr1 = mImgUrl1.toString();}
                if (mImgUrl2 != null) { imgaddr2 = mAddImage2.toString();}
                if (mPostText.getText().toString().isEmpty()) { Snackbar.make(v, getString(R.string.no_text), Snackbar.LENGTH_LONG).setAction("Action", null).show(); }
                if (mTitle.getText().toString().isEmpty()) { Snackbar.make(v, getString(R.string.no_title), Snackbar.LENGTH_LONG).setAction("Action", null).show(); }

                try {
                    addresses = geocoder.getFromLocationName(mAddress.getText().toString(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addresses == null) { Snackbar.make(v, getString(R.string.no_address_found), Snackbar.LENGTH_LONG).setAction("Action", null).show();}
                else if(addresses.size() > 0) {
                        latitude= addresses.get(0).getLatitude();
                        longitude= addresses.get(0).getLongitude();
                }



                Post post = new Post(mPostText.getText().toString(), mTitle.getText().toString() , OakappMain.firebaseUser.getUid() , imgaddr1, imgaddr2, mTags.getText().toString(),mCategories.getSelectedItemId(), latitude, longitude,true);
                post.mKey = postRef.push().getKey();
                OakappMain.user.mOwnPosts.add(post.mKey);
                OakappMain.SaveUserByUid(OakappMain.user);
                OakappMain.SavePostByKey(post);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            StorageReference photoReference = mPostPhotosreference.child(selectedImage.getLastPathSegment());
            photoReference.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    switch (mSelectedImg) {
                        case 1:
                            mImgUrl1 = taskSnapshot.getDownloadUrl();
                            break;
                        case 2:
                            mImgUrl2 = taskSnapshot.getDownloadUrl();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
}
