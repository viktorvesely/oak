package oak.oakapplication;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;

public class Feedbacky extends AppCompatActivity {

    public static Feedbacky selfPointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacky);

        final String[] casti = getResources().getStringArray(R.array.mestske_casti);
        final Intent openFeedback = new Intent(this, openFeedback.class);

        ListAdapter feedbAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, casti);
        ListView zoznam = (ListView) findViewById(R.id.lv_mest_casti);
        zoznam.setAdapter(feedbAdapter);

        zoznam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openFeedback.putExtra("cast", Integer.toString(position));
                startActivity(openFeedback);
            }
        });

        ListView mDrawerList = (ListView) findViewById(R.id.lv_drawerlist);
        ListAdapter menu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu));
        mDrawerList.setAdapter(menu);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //OakappMain.OnMenuItemSelected(position, Feedbacky.this);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent feedbacky = new Intent(Feedbacky.this, Feedbacky.class);
                        startActivity(feedbacky);
                        break;
                    case 3:
                        Intent intent = new Intent(Feedbacky.this, MyProfile.class);
                        intent.putExtra("uid", OakappMain.THIS_USER);
                        startActivity(intent);
                        break;
                    case 4:
                        break;
                    case 5:
                        AuthUI.getInstance().signOut(Feedbacky.this);
                        break;

                    default:
                        break;
                }
            }
        });

    }
}