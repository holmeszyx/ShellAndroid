package z.hol.shellandroid;

import z.hol.shellandroid.utils.ShellUtils;

/**
 * Chmod implemented by Java Process
 * @author holmes
 *
 */
public final class DefaultChmod implements Chmod{

	@Override
	public boolean setChmod(String file, String mode) {
		// This is Auto-generated method stub
		ShellUtils.setChmod(file, mode);
		return true;
	}

}
