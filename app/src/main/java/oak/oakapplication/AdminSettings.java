package oak.oakapplication;

/**
 * Created by vikto on 8/31/2017.
 */

public class AdminSettings {
    public static boolean showInActivePosts = false;
    public static boolean showInActiveComments = false;
    

    public static void Activate(){
        wereActivated = true;
        showInActiveComments = true;
        showInActivePosts = true;
    }

    public static boolean wereActivated;
}
