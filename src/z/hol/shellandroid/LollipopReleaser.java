package z.hol.shellandroid;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import z.hol.shellandroid.utils.AssetUtils;

/**
 * release cflag file for Android 5.0, that with pie attr
 * Created by holmes on 11/21/14.
 */
public class LollipopReleaser extends AbsReleaser{
    public static final String FLAG_TAG = "_21";
    public static final String ASSET_NAME = "cflag_21";

    /**
     * use #getContext method to get context
     *
     * @param context
     */
    public LollipopReleaser(Context context) {
        super(context);
    }

    @Override
    public File release() throws IOException {
        Context context = getContext();
        int cpuType = Cpu.getCpuType();
        final String cflagName;
        if (cpuType == Cpu.CPU_INTEL){
            cflagName = ShellAndroid.CFLAG_TOOL_X86_FILE_NAME;
        }else{
            cflagName = ShellAndroid.CFLAG_TOOL_FILE_NAME;
        }

        final String fixedFlagName = cflagName + FLAG_TAG;
        if (!AssetUtils.isFileExist(context, fixedFlagName)){
            // export
            AssetManager am = context.getAssets();
            InputStream input = am.open(ASSET_NAME);
            ZipInputStream zip = new ZipInputStream(input);
            try {
                ZipEntry ze;
                while ((ze = zip.getNextEntry()) != null) {
                    String filename = ze.getName();
                    if (!filename.equals(fixedFlagName)){
                        continue;
                    }

                    OutputStream out = context.openFileOutput(fixedFlagName, Context.MODE_PRIVATE);
                    BufferedOutputStream bout = new BufferedOutputStream(out);
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = zip.read(buffer)) != -1) {
                        bout.write(buffer, 0, count);
                    }
                    bout.flush();
                    bout.close();
                    out.close();

                    break;
                }
            } finally {
                zip.close();
            }

        }

        File cFlag = context.getFileStreamPath(fixedFlagName);
        return cFlag;
    }

    @Override
    public String getCFlagName() {
        int cpuType = Cpu.getCpuType();
        final String cflagName;
        if (cpuType == Cpu.CPU_INTEL){
            cflagName = ShellAndroid.CFLAG_TOOL_X86_FILE_NAME;
        }else{
            cflagName = ShellAndroid.CFLAG_TOOL_FILE_NAME;
        }

        final String fixedFlagName = cflagName + FLAG_TAG;
        return fixedFlagName;
    }
}
