package oak.oakapplication;

import android.support.design.widget.Snackbar;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.View;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by vikto on 8/21/2017.
 */

public class ReputationManager {
    ReputationManager () {

    }

    public static void Init(View v) {
        if (System.currentTimeMillis() - OakappMain.user.lastLogged >  1000 * 60 * 60 * 24 * 7) {
            OakappMain.user.AddReputation(-(int)(OakappMain.user.mReputation * 0.05 ));
            Snackbar.make(v,OakappMain.selfPointer.getString(R.string.you_wasnt_there),Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }

    }

    public static void  LikePost(int likeLevel, int postId, User user) {
        int repBoost = 0;
        double percentagePenalty = 0;
        switch (likeLevel) {
            case 1:
                percentagePenalty = 0.25;
                break;
            case 2:
                percentagePenalty = 0.5;
                break;
            case 3:
                percentagePenalty = 1;
                break;
        }

        repBoost = (int)(OakappMain.user.mJudgePower * percentagePenalty * 2);

        Post post = OakappMain.postsToShow.get(postId);
        post.mReputation += repBoost;
        user.AddReputation(repBoost);
        user.mJudgePower += CalculateJudgePower(user, OakappMain.user, percentagePenalty);
        // also set rank

        OakappMain.SavePostByKey(post);
        OakappMain.SaveUserByUid(user);

    }

    private static int CalculateJudgePower(User target, User giver, double level) {
        double percentagePenalty = 0.4;

        for (int i = 50; i < target.mJudgePower; i += 50) {
            percentagePenalty -= 0.1;
        }
        if (percentagePenalty == 0) percentagePenalty = 0.05;


        return (int)(giver.mJudgePower * percentagePenalty * level);
    }



}
