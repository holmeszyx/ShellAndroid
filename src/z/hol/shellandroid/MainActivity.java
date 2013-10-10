package z.hol.shellandroid;

import java.io.File;
import java.io.IOException;

import z.hol.shellandroid.utils.AssetUtils;
import z.hol.shellandroid.utils.ShellUtils;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	
	private TextView txtResult;
	private EditText edtCmd;
	private ShellAndroid mShell;
	private File mFlagFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edtCmd = (EditText) findViewById(R.id.edit);
		txtResult = (TextView) findViewById(R.id.text);
		txtResult.setMovementMethod(new ScrollingMovementMethod());
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
		
		try {
			AssetUtils.extractAsset(this, "cflag", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ShellUtils.setChmod("/data/data/z.hol.shellandroid/files/cflag", "770");
		
		mShell = new ShellAndroid();
		mShell.printOutput();
		mShell.setFlagFile(mFlagFile.getAbsolutePath());
		
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String cmd = edtCmd.getText().toString();
		new ExecuteTask().execute(cmd);
		edtCmd.setText("");
		edtCmd.requestFocus();
	}
	
	private class ExecuteTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			mShell.exec(false, params);
			return mShell.getLastResult();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!TextUtils.isEmpty(result)){
				txtResult.setText(result);
			}else{
				txtResult.setText("Empty result");
			}
		}
	}
}
