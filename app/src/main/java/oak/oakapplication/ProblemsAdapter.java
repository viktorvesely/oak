package oak.oakapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viktor on 1/3/2018.
 */

public class ProblemsAdapter extends RecyclerView.Adapter<ProblemsAdapter.ProblemViewHolder>{

    private ArrayList<Problem> mProblems;
    private Context mContext;

    ProblemsAdapter(Context context, ArrayList<Problem> problems) {
        mProblems = problems;
        mContext = context;
    }

    @Override
    public ProblemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View problemView = LayoutInflater.from(mContext).inflate(R.layout.problem_item, null);
        return  new ProblemViewHolder(problemView);
    }

    @Override
    public void onBindViewHolder(ProblemViewHolder holder, int position) {
        Problem problem = mProblems.get(position);
        holder.activate(problem);
    }

    @Override
    public int getItemCount() {
        return mProblems.size();
    }

    void add(Problem p) {
        mProblems.add(p);
    }

    public class ProblemViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ListView mParticipants;
        private Button mSolved;
        private Button mLeave;
        private Context mContext;
        private List<String> names;
        private List<String> ids;
        private View.OnClickListener mLeaveListener;
        private View.OnClickListener mSolvedListener;

        ProblemViewHolder(View item) {
            super(item);
            mName = item.findViewById(R.id.tv_problem_name);
            mParticipants = item.findViewById(R.id.lv_participants);
            mSolved = item.findViewById(R.id.b_solved_problem);
            mLeave = item.findViewById(R.id.b_leave_problem);
            mContext = item.getContext();

            mLeaveListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Problem p = (Problem) view.getTag();
                }
            };

            mSolvedListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Problem p = (Problem) view.getTag();
                }
            };

            mSolved.setOnClickListener(mSolvedListener);
            mLeave.setOnClickListener(mLeaveListener);
        }

        public void activate(final Problem problem) {
            mName.setText(problem.getName());
            names = problem.getParticipantNames();
            ids = problem.getParticipantsIDs();
            ArrayList<UserInfo> infos = new ArrayList<>();
            for (int i = 0, size = ids.size(); i < size; ++ i) {
                infos.add(new UserInfo(names.get(i), ids.get(i), problem));
            }
            ParticipantsAdapter adapter = new ParticipantsAdapter(mContext, infos);
            mParticipants.setAdapter(adapter);
            mSolved.setTag(problem);
            mLeave.setTag(problem);
        }


        class UserInfo {
            UserInfo(String name, String id, Problem p){
                mName = name;
                mId = id;
                mProblem = p;
            }
            public Problem mProblem;
            public String mName;
            public String mId;
        }
    }
}
