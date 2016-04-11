package z.hol.shellandroid;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * cflag releaser
 * Created by holmes on 11/21/14.
 */
public abstract class AbsReleaser {

    private Context mContext;

    /**
     * use #getContext method to get context
     * @param context
     */
    public AbsReleaser(Context context){
        mContext = context;
    }

    public Context getContext(){
        return mContext;
    }

    /**
     * release cflag
     * @return The released cflag file
     * @throws IOException
     */
    public abstract File release() throws IOException;

    /**
     * Get extracted cflag file name
     * @return
     */
    public abstract String getCFlagName();
}
