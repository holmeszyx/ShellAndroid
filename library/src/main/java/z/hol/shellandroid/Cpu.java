package z.hol.shellandroid;

/**
 * CPU information
 */
final class Cpu {

	public static final int CPU_ARM = 0;
	public static final int CPU_MIPS = 1;
	public static final int CPU_INTEL = 2;

	/**
	 * get cpu type, {@link #CPU_ARM}, {@link #CPU_INTEL}, {@link #CPU_MIPS}
	 * @return
	 */
	public static int getCpuType(){
		int c = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO){
			int c1 = translateCputType(android.os.Build.CPU_ABI);
			int c2 = translateCputType(android.os.Build.CPU_ABI2);
			if (c1 != c2){
			    c = c1 != CPU_ARM ? c1 : c2;
			}else{
			    c = c1;
			}
		}else{
		    c = translateCputType(android.os.Build.CPU_ABI);
		}
		return c;
	}
	
	private static int translateCputType(String cpuAbi){
	    int c = 0;
	    if ("armeabi".equals(cpuAbi)){
	        c = CPU_ARM;
	    }else if ("x86".equals(cpuAbi)){
	        c = CPU_INTEL;
	    }else if ("mips".equals(cpuAbi)){
	        c = CPU_MIPS;
	    }
	    return c;
	}
	
}
