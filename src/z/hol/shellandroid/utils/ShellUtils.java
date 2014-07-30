package z.hol.shellandroid.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import z.hol.shellandroid.Shell;
import z.hol.shellandroid.ShellAndroid;
import android.util.Log;

public class ShellUtils {

	public static void setChmod(String file, String mode){
		try {
			Thread.sleep(10);
			if (ShellAndroid.DEBUG){
				Log.d("ShellAndroid", "chmod start " + file + " mode " + mode);
			}
			// String cmd = "chmod " + mode + " " + file;
			ProcessBuilder pb = new ProcessBuilder("chmod", mode, file).directory(new File("/"))
					.redirectErrorStream(true);
			Process process = pb.start();
			if (ShellAndroid.DEBUG){
				Log.d("ShellAndroid", "chmod run " + file + " mode " + mode);
			}

			emptyInputStream(process.getInputStream());
			//emptyInputStream(process.getErrorStream());
			process.getOutputStream().close();
			
			if (ShellAndroid.DEBUG){
				Log.d("ShellAndroid", "chmod over " + file + " mode " + mode);
			}
			
			process.waitFor();
			
			process.destroy();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (ShellAndroid.DEBUG){
				Log.e("ShellAndroid", "chmod exception1 " + file + " mode " + mode);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (ShellAndroid.DEBUG){
				Log.e("ShellAndroid", "chmod exception2 " + file + " mode " + mode);
			}
		}
	}
	
	/**
	 * 清空流
	 * @param in
	 * @throws IOException
	 */
	public static void emptyInputStream(final InputStream in){
		Thread thread = new Thread(){
			public void run() {
				if (in != null){
					try {
						while (in.read() != -1){

						}
						in.close();
					} catch (IOException e) {
						// This is Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Set file permission with a Shell
	 * @param shell
	 * @param file
	 * @param mode
	 */
	public static void setChmod(Shell shell, String file, String mode){
		if (shell != null){
			shell.exec(false, "chmod " + mode + " " + file);
		}
	}	
}
