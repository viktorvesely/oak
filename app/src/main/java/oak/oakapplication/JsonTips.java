package oak.oakapplication;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by viktor on 29.10.2017.
 */

public class JsonTips {
    @SerializedName("tips")
    public ArrayList<JsonTip> mTips;

    public class JsonTip{
        @SerializedName("text")
        public String mText;
    }
}
