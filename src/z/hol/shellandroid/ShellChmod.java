package z.hol.shellandroid;

/**
 * Chmod impelemnted by shell.<br>
 * sh --> chmod mode file
 * @author holmes
 *
 */
public final class ShellChmod implements Chmod{
	
	private Shell mShell;
	
	public ShellChmod(Shell shell) {
		// This is Auto-generated constructor stub
		mShell = shell;
	}

	@Override
	public boolean setChmod(String file, String mode) {
		// This is Auto-generated method stub
		if (mShell != null){
			mShell.exec(false, "chmod " + mode + " " + file);
			return true;
		}
		return false;
	}

}
