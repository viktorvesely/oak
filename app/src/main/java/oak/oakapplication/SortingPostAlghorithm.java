package oak.oakapplication;

import android.location.Location;

import java.util.Comparator;

/**
 * Created by vikto on 8/10/2017.
 */

public class SortingPostAlghorithm implements Comparator<Post> {
    @Override
    public int compare(Post p1, Post p2) {
        int positivemeansfirstisgreater = 0;
        float distanceBetweenuUserAndPost = Float.MAX_VALUE;

        Location currentUserLocation = OakappMain.lastLocation;
        if (currentUserLocation != null) {

        }
        return positivemeansfirstisgreater;
    }

    SortingPostAlghorithm() {
        main = OakappMain.selfPointer;
    }

    private OakappMain main;

}
