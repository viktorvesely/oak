package oak.oakapplication;

import android.content.Intent;
import android.os.Bundle;
import android.location.Location;
import com.facebook.FacebookSdk;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainScreen extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;
    private DatabaseReference mUserRef;
    private ChildEventListener mPostListener;
    private PostArrayAdapter adapter;
    private OakappMain main;
    public static MainScreen selfPointer;
    private ListView mPostsListView;
    private FloatingActionButton fab;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth;
    private static int RC_SIGN_IN = 1;

    private Button mButtonSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        main = ((OakappMain)getApplicationContext());
        if (OakappMain.HasInternetAcces() == false) {
            Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).setAction("Action", null).show();

        }

        //find views

        mButtonSignOut = (Button) findViewById(R.id.b_signOut);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mPostsListView = (ListView) findViewById(R.id.lv_listOfPosts);

        //Init
        selfPointer = this;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mRootRef.child("Posts");
        mUserRef = mRootRef.child("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();
        adapter = new PostArrayAdapter(this, OakappMain.postsToShow);
        mPostsListView.setAdapter(adapter);

        //listeners

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPost = new Intent(selfPointer, PostsActivity.class);
                startActivity(newPost);
            }
        });

        mPostsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent openPost = new Intent(selfPointer, oak.oakapplication.openPost.class);
                openPost.putExtra("id",position);
                startActivity(openPost);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                OakappMain.firebaseUser = firebaseAuth.getCurrentUser();
                if (OakappMain.firebaseUser != null) {
                onSignedInInit();
                }
                else {
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mButtonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(selfPointer);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    private void onSignedInInit() {

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(OakappMain.firebaseUser.getUid())) {
                    OakappMain.getUserByUid(OakappMain.firebaseUser.getUid(), new UserInterface() {
                        @Override
                        public void UserListener(User u) {
                            OakappMain.user = u;
                            ReputationManager.Init(findViewById(android.R.id.content).getRootView());
                            OakappMain.user.lastLogged = System.currentTimeMillis();
                            attachDatabaseReadListener();
                        }
                    });
                }
                else {
                    User u = new User();
                    u.ActivateUser(OakappMain.firebaseUser.getDisplayName(),0,true,false, OakappMain.firebaseUser.getUid());
                    mUserRef.child(OakappMain.firebaseUser.getUid()).setValue(u);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void onSignedOutCleanUp () {
        OakappMain.user.mUsername = "anonymous";
        adapter.clear();
    }

    private void attachDatabaseReadListener() {
        if (mPostListener != null) {
            //is already attached
            return;
        }
        mPostListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post.mActive == true || AdminSettings.showInActivePosts == true && OakappMain.user.mAdmin == true) {
                    //OakappMain.postsToShow.add(post);
                    adapter.add(post);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //on error
            }

        };
        mPostRef.addChildEventListener(mPostListener);
    }

    private void detachDatabaseReadListener() {
        if (mPostListener != null) {
            mPostRef.removeEventListener(mPostListener);
            mPostListener = null;
        }
    }



}
