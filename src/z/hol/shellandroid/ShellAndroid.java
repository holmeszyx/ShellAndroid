package z.hol.shellandroid;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import z.hol.shellandroid.utils.AssetUtils;
import z.hol.shellandroid.utils.ShellUtils;
import android.content.Context;
import android.os.FileObserver;
import android.text.TextUtils;
import android.util.Log;

/**
 * A Shell of android
 * 
 * @author holmes
 * 
 */
public class ShellAndroid implements Shell {
    public static boolean DEBUG = true;
    public static final String TAG = "ShellAndroid";

    public static final String CFLAG_TOOL_FILE_NAME = "cflag";
    public static final String FLAG_FILE_NAME = "flag_file";
    public static final int STILL_RUNNING = -1024;
    public static final int PROCESS_NEVER_CREATED = -18030;
    public static final int UNKNOWN_USER_ID = -1024;

    private Process mProcess;
    private InputStream mReadStream, mErrorStream;
    private OutputStream mWriteStream;
    private String mFlagFile;
    private String mFlagTrigger;
    private String mFlagCmd;

    private CmdTerminalObserver mTerminalObserver;

    private byte[] mLock = new byte[0];

    private StringBuilder mLastResultBuilder = new StringBuilder(512);
    private String mLastResult = null;

    private AtomicBoolean mHasRoot = new AtomicBoolean(false);

    private IdContext mIdContext;
    
    public ShellAndroid() {
        init();
    }

    /**
     * initialize command terminal flag tool
     * 
     * @param context
     * @return
     */
    public String initFlag(Context context) {

        File flagFile = context.getFileStreamPath(FLAG_FILE_NAME);
        if (!flagFile.exists()) {
            try {
                flagFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            AssetUtils.extractAsset(context, CFLAG_TOOL_FILE_NAME, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File cFlag = context.getFileStreamPath(CFLAG_TOOL_FILE_NAME);
        mFlagTrigger = cFlag.getAbsolutePath();
        ShellUtils.setChmod(mFlagTrigger, "770");
        return flagFile.getAbsolutePath();
    }

    /**
     * set command terminal flag file, which triggered by flag tool
     * 
     * @param file
     */
    public void setFlagFile(String file) {
        mFlagFile = file;
        mFlagCmd = mFlagTrigger + " " + mFlagFile;
        if (mTerminalObserver != null) {
            mTerminalObserver.stopWatching();
        }
        mTerminalObserver = new CmdTerminalObserver(mFlagFile);
        mTerminalObserver.startWatching();
    }

    private void init() {
        String initCommand = "/system/bin/sh";
        try {
            Process process = new ProcessBuilder(initCommand).redirectErrorStream(true).start();
            mProcess = process;
            mReadStream = process.getInputStream();
            mErrorStream = process.getErrorStream();
            mWriteStream = process.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean close() {
        if (mProcess != null) {
            try {
                mReadStream.close();
                mErrorStream.close();
                mWriteStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mProcess.destroy();
            Log.d(TAG, "**Shell destroyed**");
        }
        if (mTerminalObserver != null) {
            mTerminalObserver.stopWatching();
        }
        synchronized (mLock) {
            mLock.notifyAll();
        }
        return true;
    }

    @Override
    public boolean exec(boolean asRoot, String... arrParam) {
        // TODO Auto-generated method stub
        execute(arrParam);
        return true;
    }

    @Override
    public void checkRoot() {
        // TODO Auto-generated method stub
        if (!mHasRoot.get()) {
            int id = checkId();
            if (id == 0 && (mIdContext == null || mIdContext.isRootRole())) {
                mHasRoot.set(true);
                return;
            }
            execute("su");
            id = checkId();
            if (id == 0 && (mIdContext == null || mIdContext.isRootRole())) {
                mHasRoot.set(true);
            }
        }
    }

    @Override
    public boolean hasRoot() {
        // TODO Auto-generated method stub
        return mHasRoot.get();
    }

    @Override
    public boolean exitRoot() {
        // TODO Auto-generated method stub
        if (hasRoot()) {
            execute("exit");
            if (checkId() == 0) {
                // still root shell,
                // so exit it
                return exitRoot();
            }
            mHasRoot.set(false);
            return true;
        }
        return false;
    }

    /**
     * Check the id of current user in the shell
     * @return
     */
    public int checkId() {
        execute("id");
        final String idStr = getLastResult();
        if (!TextUtils.isEmpty(idStr) && idStr.startsWith("uid=")) {
            int endPos = idStr.indexOf('(');
            int id = Integer.valueOf(idStr.substring(4, endPos));
            
            // for SELinux
            // Text "context=u:r:init:s0" at last of idStr
            int contextPos = idStr.lastIndexOf("context=");
            if (contextPos > -1){
                // SELinux
                int contextEnd = idStr.indexOf(' ', contextPos);
                String contextStr;
                if (contextEnd == -1){
                    contextStr = idStr.substring(contextPos + 8);
                }else{
                    contextStr = idStr.substring(contextPos + 8,  contextEnd);
                }
                if (DEBUG) Log.d(TAG, "" + contextStr);
                if (mIdContext == null){
                    mIdContext = new IdContext(contextStr);
                }else{
                    mIdContext.update(contextStr);
                }
                
                //if (DEBUG) Log.d(TAG, String.format("u:%s, r:%s, role:%s, s:%s", 
                //        mIdContext.getU(), mIdContext.getR(), mIdContext.getRoll(), mIdContext.getS()));
            }
            return id;
        }
        return UNKNOWN_USER_ID;
    }
    
    /**
     * Get id context, may null if no in SELinux
     * @return
     */
    public IdContext getIdContext(){
        return mIdContext;
    }

    /**
     * internal execute
     * 
     * @param cmds
     */
    private void execute(String... cmds) {
        for (int i = 0; i < cmds.length; i++) {
            String cmd = cmds[i];
            if (DEBUG) Log.d(TAG, "cmd: " + cmd);
            byte[] rawCmd = cmd.getBytes();

            // clean the result for new command
            mLastResultBuilder.delete(0, mLastResultBuilder.length());
            mLastResult = null;

            try {
                mWriteStream.write(rawCmd);
                mWriteStream.write(10);
                mWriteStream.flush();

                mWriteStream.write(mFlagCmd.getBytes());
                mWriteStream.write(10);
                mWriteStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Start collect command out put result
     */
    public void printOutput() {
        Thread thread = new Thread(new OutputRunnable());
        thread.start();
    }

    /**
     * Command result out put runnable of thread
     * 
     * @author holmes
     * 
     */
    private class OutputRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            InputStream input = mReadStream;
            byte[] buff = new byte[4096];
            int readed = -1;
            try {
                while ((readed = input.read(buff)) > 0) {
                    printBuff(buff, readed);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.d(TAG, "**over**");
        }

        private void printBuff(byte[] buff, int length) {
            String buffStr = new String(buff, 0, length);
            if (DEBUG) Log.d(TAG, "~:" + buffStr);
            mLastResultBuilder.append(buffStr);
        }
    }

    public int getExitValue() {
        if (mProcess != null) {
            try {
                return mProcess.exitValue();
            } catch (IllegalThreadStateException e) {
                return STILL_RUNNING;
            }
        }
        return PROCESS_NEVER_CREATED;
    }

    /**
     * A observer for command finished
     * 
     * @author holmes
     * 
     */
    private class CmdTerminalObserver extends FileObserver {
        @SuppressWarnings("unused")
        protected final String mWatchedFile;

        public CmdTerminalObserver(String file) {
            super(file, OPEN);
            // TODO Auto-generated constructor stub
            mWatchedFile = file;
        }

        @Override
        public void onEvent(int event, String path) {
            // TODO Auto-generated method stub
            // Log.d(TAG, mWatchedFile + " opened");
            mLastResult = mLastResultBuilder.toString();
            synchronized (mLock) {
                mLock.notify();
            }
        }

    }

    /**
     * Get last command result
     * @return
     */
    public String getLastResult() {
        return mLastResult;
    }
}
