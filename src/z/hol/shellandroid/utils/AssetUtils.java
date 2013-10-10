package z.hol.shellandroid.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class AssetUtils {

	/**
	 * 释放Asset里面的文件
	 * @param context
	 * @param fileName
	 * @param checkFile 如果检测文件，则在文件存在的时候不进行释放
	 * @throws IOException
	 */
	public static void extractAsset(Context context, String fileName, boolean checkFile) throws IOException{
		if (!checkFile || !isFileExist(context, fileName)){
			AssetManager manager = context.getAssets();
			InputStream in = manager.open(fileName);
			OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			BufferedOutputStream bout = new BufferedOutputStream(out);
			int iByte = -1;
			while ((iByte = in.read()) != -1){
				bout.write(iByte);
			}
			bout.flush();
			bout.close();
			out.close();
			in.close();
		}
	}
	
	public static boolean isFileExist(Context context, String fileName){
		File fileDir = context.getFilesDir();
		File f = new File(fileDir, fileName);
		boolean exist = f.exists();
		f = null;
		fileDir = null;
		return exist;
	}
}
