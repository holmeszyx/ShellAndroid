package com.example.shellandroid;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.shellandroid.TestForExec.TestShell;
import com.mgyun.shua.su.permis.utils.AssetUtils;

public class MainActivity extends Activity implements OnClickListener{
	
	private EditText edtCmd;
	private TestShell mShell;
	private File mFlagFile;
	private CmdTerminalObserver mTerminalObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edtCmd = (EditText) findViewById(R.id.edit);
		findViewById(R.id.execute).setOnClickListener(this);
		
		mFlagFile = getFileStreamPath("flag_file");
		if (!mFlagFile.exists()){
			try {
				mFlagFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		try {
//			AssetUtils.extractAsset(this, "cflag", false);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		mShell = new TestShell();
		mShell.printOutput();
		mShell.setFlagFile(mFlagFile.getAbsolutePath());
		
		mTerminalObserver = new CmdTerminalObserver();
		mTerminalObserver.startWatching();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mShell != null){
			mShell.close();
		}
		if (mTerminalObserver != null){
			mTerminalObserver.stopWatching();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String cmd = edtCmd.getText().toString();
		mShell.exec(false, cmd);
		edtCmd.setText("");
		edtCmd.requestFocus();
	}

	private class CmdTerminalObserver extends FileObserver{
		private final String mWatchedFile;

		public CmdTerminalObserver() {
			super(mFlagFile.getAbsolutePath(), OPEN);
			// TODO Auto-generated constructor stub
			mWatchedFile = mFlagFile.getAbsolutePath();
		}

		@Override
		public void onEvent(int event, String path) {
			// TODO Auto-generated method stub
			System.out.println(mWatchedFile + " opened");
		}
		
	}
}
