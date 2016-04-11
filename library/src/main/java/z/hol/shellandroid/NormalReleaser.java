package z.hol.shellandroid;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import z.hol.shellandroid.utils.AssetUtils;

/**
 * Normal releaser for most android os
 * Created by holmes on 11/21/14.
 */
public class NormalReleaser extends AbsReleaser {


    /**
     * use #getContext method to get context
     *
     * @param context
     */
    public NormalReleaser(Context context) {
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

        try {
            AssetUtils.extractAsset(context, cflagName, true);
        } catch (IOException e) {
            // e.printStackTrace();
            // extra cflag error, so don't block the sh
            throw e;
        }
        File cFlag = context.getFileStreamPath(cflagName);
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
        return cflagName;
    }
}
