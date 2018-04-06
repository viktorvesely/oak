package oak.oakapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.Console;
import java.util.ArrayList;

/**
 * Created by Viktor on 1/4/2018.
 */

public class ParticipantsAdapter extends ArrayAdapter<ProblemsAdapter.ProblemViewHolder.UserInfo> {

    private ArrayList<ProblemsAdapter.ProblemViewHolder.UserInfo> mInfos;
    private ParticipantsAdapter thisA;

    ParticipantsAdapter(Context c, ArrayList<ProblemsAdapter.ProblemViewHolder.UserInfo> names) {
        super(c, 0 ,names);
        thisA = this;
        mInfos = names;
    }


    public View getView(int position, View viewConverter, ViewGroup parent) {
        final ProblemsAdapter.ProblemViewHolder.UserInfo info = mInfos.get(position);
        final TextView display;
        if (info.mId.equals(OakappMain.user.mId)) {
          return LayoutInflater.from(getContext()).inflate(R.layout.empty, parent, false);
        }
        if (viewConverter == null) {
            viewConverter = LayoutInflater.from(getContext()).inflate(R.layout.text_view_item, parent, false);
            viewConverter.setTag(info);
        }

        display =  viewConverter.findViewById(R.id.tv_simple);
        display.setText(info.mName);
        display.setTag(info);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ProblemsAdapter.ProblemViewHolder.UserInfo info = (ProblemsAdapter.ProblemViewHolder.UserInfo) view.getTag();

                DialogInterface.OnClickListener onKick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case DialogInterface.BUTTON_POSITIVE:
                                if(info.mProblem.kickUser(info.mId)) {
                                    Snackbar.make(view.getRootView(), getContext().getString(R.string.kicked_out_msg, info.mName), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    thisA.remove(info);
                                    thisA.notifyDataSetChanged();
                                }

                                else {
                                    Log.e("ParticipantsAdapter", "Could not find user while trying to kick him");
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogInterface.dismiss();
                                break;
                        }
                    }

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getContext().getString(R.string.kick_out_confirm, info.mName)).setPositiveButton("Áno", onKick)
                        .setNegativeButton("Nie", onKick).show();
            }
        });
        return viewConverter;
    }

    @Override
    public int getCount(){
        return mInfos.size();
    }


}

