package oak.oakapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.core.Context;
import com.firebase.ui.auth.AuthUI;

/**
 * Created by rohal on 17.12.2017.
 */

public class Menu extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        }

        private class MenuOptions {
            public static final int HOME = 0;
            public static final int FEEDBACKS = 1;
            public static final int PROFILE = 2;
            public static final int SETTINGS = 3;
            public static final int SIGNOUT = 4;
            public static final int ADMIN = 5;
            public static final int TIPS = 6;
            public static final int PROBLEMSFEED = 7;

        }

        public void OnMenuItemSelected(int position){
            switch (position) {
                case MenuOptions.HOME:
                    closeOptionsMenu();
                    break;

                case MenuOptions.ADMIN:
                    Intent admin = new Intent(getApplicationContext(), AdminFunctions.class);
                    startActivity(admin);
                    break;

                case MenuOptions.SETTINGS:
                    Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(settings);
                    break;

                case MenuOptions.TIPS:
                    Intent tips = new Intent(getApplicationContext(), Tips.class);
                    startActivity(tips);
                    break;

                case MenuOptions.FEEDBACKS:
                    Intent feedbacks = new Intent(getApplicationContext(), Feedbacky.class);
                    startActivity(feedbacks);
                    break;
                case MenuOptions.PROFILE:
                    Intent profile = new Intent(getApplicationContext(), MyProfile.class);
                    profile.putExtra("uid", OakappMain.THIS_USER);
                    startActivity(profile);
                    break;
                case MenuOptions.SIGNOUT:
                    AuthUI.getInstance().signOut(this);
                    break;

                case MenuOptions.PROBLEMSFEED:
                    startActivity(new Intent(getApplicationContext(), ProblemsFeed.class));
                    break;

                default:
                    break;
            }
        }


}