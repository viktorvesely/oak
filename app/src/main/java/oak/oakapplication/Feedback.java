package oak.oakapplication;

import java.util.List;

/**
 * Created by matis on 17.09.2017.
 */

public class Feedback {

    public Feedback() {}

    public int mN;
    public int mT_1;
    public int mT_2;
    public int mT_3;

    public Feedback(int a, int b, int c, int d){
        this.mN = a;
        this.mT_1 = b;
        this.mT_2 = c;
        this.mT_3 = d;

    }

    public int Avg () {
        if (mN>0) {
            return (mT_1 + mT_2 + mT_3) / (mN);
        } else {
            return 0;
        }
    }
}

