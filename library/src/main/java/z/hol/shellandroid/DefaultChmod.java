package z.hol.shellandroid;

import android.util.Log;

import z.hol.shellandroid.utils.ShellUtils;

/**
 * Chmod implemented by Java Process
 * @author holmes
 *
 */
public final class DefaultChmod implements Chmod{

    private ShellAndroid mShell;

    public DefaultChmod(){
        mShell = null;
    }

    /**
     * Internal method
     * @param shell
     */
    DefaultChmod(ShellAndroid shell){
        mShell = shell;
    }

	@Override
	public boolean setChmod(String file, String mode) {
		// This is Auto-generated method stub
        if (mShell == null){
            ShellUtils.setChmod(file, mode);
        }else{
            if (ShellAndroid.DEBUG){
                Log.d(ShellAndroid.TAG, "_chmod " + mode + " " + file);
            }
            try {
                mShell.chmodWithSh(file, mode);
            }catch (Exception e){
                return false;
            }
        }
		return true;
	}

}
