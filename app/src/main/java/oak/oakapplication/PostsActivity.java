package oak.oakapplication;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Locale;

import static com.google.firebase.crash.FirebaseCrash.log;
import static java.lang.Math.toIntExact;

public class PostsActivity extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private DatabaseReference postRef;
    private Geocoder geocoder;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPostPhotosreference;
    private DatabaseReference feedbackRef;

    private OakappMain main;
    private Button mPostButton;
    private Button mAddImage1;
    private Button mAddImage2;
    private Button mFeedbackButton;
    private Button mFeedbackAdd;
    private EditText mTitle;
    private EditText mPostText;
    private EditText mAddress;
    private Spinner mCastSpinner;
    private Spinner mMediumSpinner;
    private RatingBar mRB_1;
    private RatingBar mRB_2;
    private RatingBar mRB_3;
    private EditText mFeedback_com;
    private TextView mAddressOutput;
    boolean mIsUploading;

    private int mType1=0;
    private int mType2=0;
    private int mType3=0;
    private String feedback_com;

    private ArrayAdapter<String> casti;
    private ArrayAdapter<String> komunikacia;
    private final static int MaxTagsPerPost = 3;
    private Uri mImgUrl1;
    private Uri mImgUrl2;
    private double mLongitude;
    private double mLatitude;

    private int mSelectedImg;

    private static final int RC_PHOTO_PICKER =  2;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        main = ((OakappMain)getApplicationContext());
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPostPhotosreference = mFirebaseStorage.getReference().child("post_photos");
        postRef = mRootRef.child("Posts");

        mImgUrl1 = null;
        mImgUrl2 = null;
        mIsUploading = false;
        mSelectedImg = 0;
        mPostButton = (Button) findViewById(R.id.b_post);
        mPostText = (EditText) findViewById(R.id.et_postText);
        mTitle = (EditText) findViewById(R.id.et_title);
        mAddress = (EditText) findViewById(R.id.et_location);
        mAddImage1 = (Button) findViewById(R.id.b_addImg1);
        mAddImage2 = (Button) findViewById(R.id.b_addImg2);
        mFeedbackButton = (Button) findViewById(R.id.b_addFeedback);
        mAddressOutput = findViewById(R.id.tv_address_output);

        geocoder = new Geocoder(this);


        mAddress.setOnEditorActionListener(    new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ||
                        actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT)  {
                    if (event == null ||!event.isShiftPressed()) {
                        AddressOutput();
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });

        mAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    AddressOutput();
                }
            }
        });

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

        mFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PostsActivity.this);

                View mView = getLayoutInflater().inflate(R.layout.activity_new_feedback, null);
                mBuilder.setTitle("Feedback");

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                mCastSpinner = (Spinner) mView.findViewById(R.id.sp_Cast);
                casti = new ArrayAdapter<String>(PostsActivity.this,android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.mestske_casti));
                casti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mCastSpinner.setAdapter(casti);

                mMediumSpinner = (Spinner) mView.findViewById(R.id.sp_medium);
                komunikacia = new ArrayAdapter<String>(PostsActivity.this,android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.komunikacia));
                komunikacia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mMediumSpinner.setAdapter(komunikacia);

                mFeedback_com = (EditText) mView.findViewById(R.id.et_feeback_com);

                mRB_1 = (RatingBar) mView.findViewById(R.id.rb_type1);
                mRB_2 = (RatingBar) mView.findViewById(R.id.rb_type2);
                mRB_3 = (RatingBar) mView.findViewById(R.id.rb_type3);
                mFeedbackAdd = (Button) mView.findViewById(R.id.b_feedback_ok);

                mRB_1.setRating(mType1);
                mRB_2.setRating(mType2);
                mRB_3.setRating(mType3);

                mRB_1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        mType1 = (int)rating;
                    }
                });
                mRB_2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        mType2 = (int)rating;
                    }
                });
                mRB_3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        mType3 = (int)rating;
                    }
                });

                mFeedbackAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        feedbackRef = FirebaseDatabase.getInstance().getReference().child("Feedback")
                                .child(Integer.toString(mCastSpinner.getSelectedItemPosition()))
                                .child(Integer.toString(mMediumSpinner.getSelectedItemPosition()));
                        dialog.cancel();
                    }
                });

                dialog.show();
            }

        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsUploading) {
                    Snackbar.make(v, getString(R.string.upload_in_progress), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }



                String imgaddr1 = "";
                String imgaddr2 = "";
                if (mImgUrl1 != null) { imgaddr1 = mImgUrl1.toString();}
                if (mImgUrl2 != null) { imgaddr2 = mAddImage2.toString();}
                if (mPostText.getText().toString().isEmpty()) { Snackbar.make(v, getString(R.string.no_text), Snackbar.LENGTH_LONG).setAction("Action", null).show(); }
                if (mTitle.getText().toString().isEmpty()) { Snackbar.make(v, getString(R.string.no_title), Snackbar.LENGTH_LONG).setAction("Action", null).show(); }


                Post post = new Post(mPostText.getText().toString(), mTitle.getText().toString() , OakappMain.firebaseUser.getUid() , imgaddr1, imgaddr2, 0, mLatitude, mLongitude,true);
                post.mKey = postRef.push().getKey();
                OakappMain.user.mOwnPosts.add(post.mKey);
                OakappMain.SaveUserByUid(OakappMain.user);
                OakappMain.SavePostByKey(post);
                Problem problem = new Problem(post.mKey);
                problem.addWorker("INIT", "INIT");
                problem.setJoinAbilitie(true);
                problem.save();

                new HTTPrequest().execute(post);
                if (feedbackRef != null) {

                    feedbackRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild("Ratings")) {
                                Feedback feedback = dataSnapshot.child("Ratings").getValue(Feedback.class);
                                feedback.mN++;
                                feedback.mT_1+=mType1;
                                feedback.mT_2+=mType2;
                                feedback.mT_3+=mType3;
                                feedbackRef.child("Ratings").setValue(feedback);
                            } else {
                                Feedback feedback = new Feedback(1, mType1, mType2, mType3);
                                feedbackRef.child("Ratings").setValue(feedback);
                            };

                            if (!mFeedback_com.getText().toString().isEmpty()) {
                                FeedbackComment fb_com = new FeedbackComment(mFeedback_com.getText().toString(), OakappMain.firebaseUser.getUid());
                                fb_com.mKey = feedbackRef.child("Comments").push().getKey();
                                feedbackRef.child("Comments").child(fb_com.mKey).setValue(fb_com);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
                finish();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            mIsUploading = true;
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
                    mIsUploading = false;
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.upload_success), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }
    }

    private void AddressOutput() {
        // the user is done typing.
        List<Address> addresses = null;
        mLongitude = 0;
        mLatitude = 0;

        try {
            addresses = geocoder.getFromLocationName(mAddress.getText().toString(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses == null || addresses.size() == 0) { mAddressOutput.setText(R.string.address_could_not_been_found); mAddressOutput.setTextColor(Color.RED); }
        else if(addresses.size() > 0) {
            mLatitude= addresses.get(0).getLatitude();
            mLongitude = addresses.get(0).getLongitude();
            mAddressOutput.setText(R.string.address_found);
            mAddressOutput.setTextColor(Color.GREEN);
        }
    }


    public class HTTPrequest extends AsyncTask<Post, Void, String> {

        private Post mCurrentPost;

        protected String doInBackground(Post...p) {

            String accountName = OakappMain.getAccount(getApplicationContext());
            String idToken = null;
            try {
                idToken = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scope);
            } catch (Exception e) {
                log("exception while getting idToken: " + e);
            }

            String notificationKey = null;
            try {
                 notificationKey = OakappMain.createGroup(getString(R.string.sender_id), p[0].mKey, idToken , getString(R.string.api_key));
            } catch (IOException | JSONException | NetworkOnMainThreadException error) {
                Log.e(TAG, "Error while creating Group: " + error.getMessage());
            }

            mCurrentPost = p[0];
            return notificationKey;
        }


        protected void onPostExecute(String result) {
            mCurrentPost.mNotificationKey = result;
            OakappMain.SavePostByKey(mCurrentPost);
        }
    }

    private final String scope = "audience:server:client_id:"
            + "1262xxx48712-9qs6n32447mcj9dirtnkyrejt82saa52.apps.googleusercontent.com";
    private final String TAG = "CreatePost";
}
