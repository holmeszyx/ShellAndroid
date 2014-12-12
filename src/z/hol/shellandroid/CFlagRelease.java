package z.hol.shellandroid;

import android.content.Context;
import android.os.Build;

/**
 * To release cflag file
 * Created by holmes on 11/21/14.
 */
public class CFlagRelease {

    private CFlagRelease(){

    }

    /**
     * Get a releaser for cflag
     * @param context
     * @return
     */
    public static AbsReleaser getReleaser(Context context){
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 21){
            // Lollipop
            return new LollipopReleaser(context);
        }
        return new NormalReleaser(context);
    }
}
