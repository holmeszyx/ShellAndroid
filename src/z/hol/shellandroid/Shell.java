package z.hol.shellandroid;

public interface Shell {


	/**
	 * execute shell command
	 * @param asRoot
	 * @param arrParam
	 */
	public boolean exec(boolean asRoot, String... arrParam);
	
	/**
	 * check root permission
	 */
	public void checkRoot();
	
	/**
	 * is rooted
	 * @return
	 */
	public boolean hasRoot();
	
	/**
	 * exit root user
	 * @return
	 */
	public boolean exitRoot();
	
	/**
	 * close the shell
	 * @return
	 */
	public boolean close();
}
