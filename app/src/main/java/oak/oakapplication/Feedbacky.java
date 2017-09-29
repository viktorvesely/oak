package oak.oakapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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

                openFeedback.putExtra("cast", casti[position]);
                startActivity(openFeedback);
            }
        });


    }
}