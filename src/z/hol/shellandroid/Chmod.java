package z.hol.shellandroid;

/**
 * A interface to implement chmod cmd like "chmod 0777 file"
 * @author holmes
 *
 */
public interface Chmod {
	
	/**
	 * like "chmod mode file"
	 * @param file
	 * @param mode
	 * @return
	 */
	boolean setChmod(String file, String mode);
}
