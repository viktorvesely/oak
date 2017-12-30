package oak.oakapplication;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainScreen extends Menu{

    private DatabaseReference mRootRef;
    private DatabaseReference mPostRef;
    private DatabaseReference mUserRef;
    private ChildEventListener mPostListener;
    private PostArrayAdapter adapter;
    private ListView mPostsListView;
    private FloatingActionButton fab;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private RadioGroup mCategories;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mFirebaseAuth;
    private static int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HasInternet toGarbage = new HasInternet();
        toGarbage.execute();

        //find views


        fab = (FloatingActionButton) findViewById(R.id.fab);
        mPostsListView = (ListView) findViewById(R.id.lv_listOfPosts);
        mCategories = findViewById(R.id.rb_categories);

        //Init
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPostRef = mRootRef.child("Posts");
        mUserRef = mRootRef.child("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCategories.check(R.id.rb_problems);
        adapter = new PostArrayAdapter(this, OakappMain.postsToShow, RB_SOLVED);
        mPostsListView.setAdapter(adapter);

        //listeners

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPost = new Intent(getApplicationContext(), PostsActivity.class);
                startActivity(newPost);
            }
        });

        mCategories.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_problems:
                        adapter = new PostArrayAdapter(getBaseContext(), OakappMain.postsToShow, RB_SOLVED);
                        break;
                    case R.id.rb_solved:
                        adapter = new PostArrayAdapter(getBaseContext(), OakappMain.postsToShow, RB_PROBLEMS);
                        break;
                }
                mPostsListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
                                    .setTheme(R.style.SignIn)
                                    .setLogo(R.drawable.signin_logo)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.lv_drawerlist);
        ListAdapter menu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu));
        mDrawerList.setAdapter(menu);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OnMenuItemSelected(position);
                mDrawerLayout.closeDrawers();

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

    @Override
    public void onBackPressed(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
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
                    }
                    else {
                        RegisterUser();
                    }
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


    public class HasInternet extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void ... v) {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) { return false; }
        }


        protected void onPostExecute(Boolean result) {
            if (! result) {
                Snackbar.make(findViewById(android.R.id.content).getRootView(), getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }
    public static final int RB_PROBLEMS = 0;
    public static final int RB_SOLVED  = 1;
}
