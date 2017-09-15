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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacky);

        String[] casti = {"Staré Mesto", "Ružinov", "Vrakuňa", "Podunajské Biskupice",
                "Nové Mesto", "Rača", "Vajnory", "Karlova Ves", "Dúbravka", "Lamač", "Devín",
                "Devínska Nová Ves", "Záhorská Bystrica", "Petržalka", "Jarovce", "Rusovce",
                "Čunovo", "Magistrát hlavného mesta", "Rôzne"};
        ListAdapter feedbAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, casti);
        ListView zoznam = (ListView) findViewById(R.id.lv_mest_casti);
        zoznam.setAdapter(feedbAdapter);

    }
}