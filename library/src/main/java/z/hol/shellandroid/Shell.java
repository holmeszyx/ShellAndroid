package z.hol.shellandroid;

import z.hol.shellandroid.exception.ShellExecuteException;

/**
 * A shell
 * @author holmes
 *
 */
public interface Shell {
    
    /**
     * The context data in result of "id" command
     * e.g  uid=0(root) context=u:r:init:s0
     * @author holmes
     *
     */
    public static class IdContext{
        public static final String ROOT_ROLE = "init".intern();
        public static final String KERNEL_ROLE = "kernel".intern();
        public static final String TOOLBOX_ROLE = "toolbox".intern();
        
        String u;
        String r;
        String role;
        String s;
        
        IdContext(String contextStr) {
            update(contextStr);
        }
        
        /**
         * update informations
         * @param contextStr
         */
        void update(String contextStr){
            clean();
            String[] splited = contextStr.split(":");
            if (splited != null){
            	int len = splited.length;
            	if (len > 0){
            		u = splited[0];
            	}
            	if (len > 1){
            		r = splited[1];
            	}
            	if (len > 2){
            		role = splited[2].intern();
            	}
            	if (len > 3){
            		s = splited[3];
            	}
            }
        }
        
        void clean(){
        	u = null;
        	r = null;
        	role = null;
        	s = null;
        }

        public String getU() {
            return u;
        }

        public String getR() {
            return r;
        }

        public String getRole() {
            return role;
        }

        public String getS() {
            return s;
        }
        
        public boolean isRootRole(){
            return ROOT_ROLE.equals(role) || KERNEL_ROLE.equals(role)
                    || TOOLBOX_ROLE.equals(role);
        }
    }


	/**
	 * execute shell command
	 * @param asRoot
	 * @param arrParam
	 * @exception ShellExecuteException
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
	
	/**
	 * It's closed
	 * @return
	 */
	public boolean isClosed();
}
