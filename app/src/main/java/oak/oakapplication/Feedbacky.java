package oak.oakapplication;

import android.content.Intent;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;

public class Feedbacky extends Menu {

    public static Feedbacky selfPointer;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView zoznam;
    private ListAdapter feedbAdaprer;
    private String[] casti;

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.lv_drawerlist);
        ListAdapter menu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu));
        mDrawerList.setAdapter(menu);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OnMenuItemSelected(position);

            }
        });

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