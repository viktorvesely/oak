package oak.oakapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;

public class MyProfile extends Menu {

    private ImageView mProfilePicture;
    private TextView mUsername;
    private TextView mDisplayName;
    private TextView mRank;
    private ProgressBar mReputation;
    private TextView mJudgePower;
    private Button mSavedPosts;
    private User mUser;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private boolean mInit;

    private static final int MAX_REPUTATION = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        if (getIntent().getExtras().getString("uid").equals(OakappMain.THIS_USER)) {
            mUser = OakappMain.user;
            mInit = true;
        }
        else {
            mInit = false;
            OakappMain.getUserByUid(getIntent().getExtras().getString("uid"), new UserInterface() {
                @Override
                public void UserListener(User u) {
                    mUser = u;
                    InitView();
                }
            });
        }


        mProfilePicture = (ImageView) findViewById(R.id.iv_profilePicture);
        mUsername = (TextView) findViewById(R.id.tv_profileUsername);
        mDisplayName = (TextView) findViewById(R.id.tv_profileName);
        mRank = (TextView) findViewById(R.id.tv_profileRank);
        mReputation = (ProgressBar) findViewById(R.id.pb_profileReputation);
        mSavedPosts = (Button) findViewById(R.id.b_savedPosts);
        mJudgePower = (TextView) findViewById(R.id.tv_judgePower);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.lv_drawerlist);
        ListAdapter menu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu));
        mDrawerList.setAdapter(menu);


        if (mInit) {InitView();}

        if (OakappMain.user.mUniqueName.equals(mUser.mUniqueName) == false) {
            mSavedPosts.setVisibility(View.GONE);
        }

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OnMenuItemSelected(position);
                mDrawerLayout.closeDrawers();
            }
        });

    }

    private void InitView() {
        mDisplayName.setText(OakappMain.user.mUsername);
        mUsername.setText(OakappMain.user.mUniqueName);
        mRank.setText(OakappMain.user.Rank());
        mReputation.setMax(100);
        mReputation.setProgress(OakappMain.user.mReputation / MAX_REPUTATION * 100);
        Glide.with(mProfilePicture.getContext()).load(Uri.parse(OakappMain.user.mPhoto)).into(mProfilePicture);

        if(AdminSettings.wereActivated) {mJudgePower.setText(String.valueOf(mUser.mJudgePower));}
    }

    @Override
    public void onBackPressed(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }



}
