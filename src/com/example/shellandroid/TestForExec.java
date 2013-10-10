package com.example.shellandroid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class TestForExec {
	
	public static void main(String[] args) {
		TestShell shell = new TestShell();
		shell.printOutput();
		shell.exec(false, "id", "which bash", "echo $$");
//		try {
//			Thread.sleep(1500);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		//shell.exec(false, "bash");
		//shell.exec(false, "echo $$");
		InputStream stdIn = System.in;
		byte[] buff = new byte[512];
		int len = -1;
		try {
			while ((len = stdIn.read(buff)) > 0){
				String cmd = new String(buff, 0, len - 1).intern();
				shell.exec(false, cmd);
				//sleep(500);
				//System.out.println("exitValue: " + shell.getExitValue());
				if (cmd == "exit"){
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shell.close();
	}
	
	public static void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public interface Shell {

		/**
		 * 执行shell命令
		 * @param asRoot
		 * @param arrParam
		 */
		public boolean exec(boolean asRoot, String... arrParam);
		
		/**
		 * 开始检测root授权
		 */
		public void checkRoot();
		
		/**
		 * 是否已是root
		 * @return
		 */
		public boolean hasRoot();
		
		/**
		 * 退出root
		 * @return
		 */
		public boolean exitRoot();
	}
	
	public static class TestShell implements Shell {
		
		private Process mProcess; 
		private InputStream mReadStream, mErrorStream;
		private OutputStream mWriteStream;
		private String mFlagFile;
		private String mFlagCmd;
		
		public TestShell(){
			init();
		}
		
		public void setFlagFile(String file){
			mFlagFile = file;
			mFlagCmd = "/data/data/com.example.shellandroid/files/cflag " + mFlagFile;
		}

		private void init(){
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
		
		public void close(){
			if (mProcess != null){
				try {
					mReadStream.close();
					mErrorStream.close();
					mWriteStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mProcess.destroy();
			}
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
			
		}

		@Override
		public boolean hasRoot() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean exitRoot() {
			// TODO Auto-generated method stub
			return false;
		}
		
		private void execute(String... cmds){
			for (int i = 0; i < cmds.length; i ++){
				String cmd = cmds[i];
				System.out.println("cmd: " + cmd);
				byte[] rawCmd = cmd.getBytes();
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
			}
		}
		
		public void printOutput(){
			Thread thread = new Thread(new OutputRunnable());
			thread.start();
		}
		
		private class OutputRunnable implements Runnable {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputStream input = mReadStream;
				byte[] buff = new byte[4096];
				int readed = -1;
				try {
					while ((readed = input.read(buff)) > 0){
						printBuff(buff, readed);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("**over**");
			}
			
			private void printBuff(byte[] buff, int length){
				String buffStr = new String(buff, 0, length);
				System.out.print("~:");
				System.out.print(buffStr);
			}
		}
		
		public int getExitValue(){
			if (mProcess != null){
				try{
					return mProcess.exitValue();
				} catch (IllegalThreadStateException e){
					return -1024;
				}
			}
			return -18030;
		}
	}
}
