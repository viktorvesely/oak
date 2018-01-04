package oak.oakapplication;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProblemsFeed extends AppCompatActivity {

    private RecyclerView mProblems;
    private ArrayList<Problem> problems;
    private ProblemsAdapter adapter;
    private TextView output;
    private SwipeRefreshLayout mRefresh;
    private ArrayList<ListenerInfo> listeners;
    private  boolean mErrorWhileLoading;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problems_feed);

        listeners = new ArrayList<>();
        adapter = null;
        problems = new ArrayList<>();
        mProblems = findViewById(R.id.rv_problems);
        output = findViewById(R.id.tv_problem_feed_error);
        mRefresh = findViewById(R.id.sr_refresh_problems);

        mErrorWhileLoading = false;

        if (OakappMain.user.mActiveProblems.size() == 0) {
            Snackbar.make(findViewById(android.R.id.content) ,R.string.problems_feed_tv_no_problems, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        else {
            LoadProblems();
        }

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProblems.setVisibility(View.VISIBLE);
                output.setVisibility(View.GONE);
                stopAndDeleteListeners();
                LoadProblems();
            }
        });

    }

    private void LoadProblems() {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    mErrorWhileLoading = true;
                    if (problems.size() == 0) {
                        Snackbar.make(findViewById(android.R.id.content) ,R.string.problems_feed_tv_error, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                    else {
                        adapter = new ProblemsAdapter(ProblemsFeed.this, problems);
                        mProblems.setAdapter(adapter);
                        Snackbar.make(findViewById(android.R.id.content) ,R.string.problems_feed_tv_not_all_items_were_loaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        }, 1000 * SECONDSTOLOAD);

        for(int i = 0, size = OakappMain.user.mActiveProblems.size(); i < size; ++i){
            String filter = OakappMain.user.mActiveProblems.get(i);
            Query query = FirebaseDatabase.getInstance().getReference().child("Problems").orderByChild("mParent").equalTo(filter);

             listeners.add(new ListenerInfo(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    problems.add(dataSnapshot.getValue(Problem.class));
                    if (mRefresh.isRefreshing()) {mRefresh.setRefreshing(false);}
                    if (problems.size() == OakappMain.user.mActiveProblems.size() && !mErrorWhileLoading) {
                        adapter = new ProblemsAdapter(ProblemsFeed.this, problems);
                        mProblems.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }, query));

            query.addListenerForSingleValueEvent(listeners.get(listeners.size() -1).mListener);

        }

    }

    private void stopAndDeleteListeners() {
        if (listeners == null || listeners.size() == 0) { return; }
        for (int i = 0, size = listeners.size(); i < size; ++i) {
            ListenerInfo info = listeners.get(i);
            info.mQuery.removeEventListener(info.mListener);
        }
        listeners = new ArrayList<>();
    }

    private static final int SECONDSTOLOAD = 10;

    private class ListenerInfo {
        ListenerInfo(ValueEventListener listener, Query query) {
            mListener = listener;
            mQuery = query;
        }
        ValueEventListener mListener;
        Query mQuery;
    }
}



