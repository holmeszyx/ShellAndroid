package z.hol.shellandroid.utils;

import java.io.IOException;

public class ShellUtils {

	public static void setChmod(String file, String mode){
		try {
			Process process = new ProcessBuilder().command("/system/bin/chmod", mode, file).start();
			process.waitFor();
			process.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
