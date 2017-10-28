package oak.oakapplication;

import android.content.Intent;
import android.os.Bundle;
import android.location.Location;
import com.facebook.FacebookSdk;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainScreen extends AppCompatActivity{

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;
    private DatabaseReference mUserRef;
    private ChildEventListener mPostListener;
    private PostArrayAdapter adapter;
    private OakappMain main;
    public  MainScreen selfPointer;
    private ListView mPostsListView;
    private FloatingActionButton fab;
    private ListView mDrawerList;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth;
    private static int RC_SIGN_IN = 1;


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


        fab = (FloatingActionButton) findViewById(R.id.fab);
        mPostsListView = (ListView) findViewById(R.id.lv_listOfPosts);

        mDrawerList = (ListView) findViewById(R.id.lv_drawerlist);
        ListAdapter menu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu));
        mDrawerList.setAdapter(menu);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //OakappMain.OnMenuItemSelected(position, MainScreen.this);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent intent = new Intent(MainScreen.this, Feedbacky.class);
                        startActivity(intent);
                        break;
                    case 3:
                        Intent mainscreen = new Intent(MainScreen.this, MyProfile.class);
                        mainscreen.putExtra("uid", OakappMain.THIS_USER);
                        startActivity(mainscreen);
                        break;
                    case 4:
                        break;
                    case 5:
                        AuthUI.getInstance().signOut(MainScreen.this);
                        break;

                    default:
                        break;
                }
            }
        });

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

                Intent openPost = new Intent(MainScreen.this, oak.oakapplication.openPost.class);
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
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())) //new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


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

        if (! OakappMain.UserAlreadyExist) {
            Log.i(TAG, "onSignInInit: Loading user from database");
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(OakappMain.firebaseUser.getUid())) {
                        OakappMain.getUserByUid(OakappMain.firebaseUser.getUid(), new UserInterface() {
                            @Override
                            public void UserListener(User u) {
                                OakappMain.user = u;
                                InitUser();
                            }
                        });
                    }/*
                    else {
                        RegisterUser();
                    }*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "onSignInInit reading failed " + databaseError.getMessage());
                }
            });
        }
        else {
            Log.i(TAG, "onSignInInit: user already loaded");
        }
    }

    private void onSignedOutCleanUp () {
        OakappMain.user.mUsername = "anonymous";
        OakappMain.UserAlreadyExist = false;
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
                Log.w(TAG, "Reading Posts from database failed " + databaseError.getMessage());
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


    private void InitUser() {
        if (OakappMain.user.mActive == false) {
            Intent deactivate = new Intent(this, Deactivate.class);
            startActivity(deactivate);
        }

        ReputationManager.Init(findViewById(android.R.id.content).getRootView());


        if (OakappMain.user.mAdmin)
            AdminSettings.Activate();

        if (OakappMain.user.mFCMToken.isEmpty() || OakappMain.user.mFCMToken.equals("NONE")) {
            OakappMain.user.mFCMToken = OakappMain.getToken();
            OakappMain.SaveUserByUid(OakappMain.user);
        }

        OakappMain.UserAlreadyExist = true;
        attachDatabaseReadListener();
    }


    private void RegisterUser() {
        User u = new User();
        OakappMain.user = u;

        if(OakappMain.user.ActivateUser(OakappMain.firebaseUser.getDisplayName(),0,true,false, OakappMain.firebaseUser.getUid()) == false ) {
            Intent deactivate = new Intent(this, Deactivate.class);
            startActivity(deactivate);
        }

        Intent extraRegInfo = new Intent(this, Registration.class);
        startActivity(extraRegInfo);


    }

    private static final String TAG = "MainScreen";


}
