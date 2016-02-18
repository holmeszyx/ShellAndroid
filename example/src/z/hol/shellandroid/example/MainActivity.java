package z.hol.shellandroid.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import z.hol.shellandroid.ShellAndroid;

/**
 * The example activity, and also for test
 * @author holmes
 *
 */
public class MainActivity extends Activity implements OnClickListener{

	public static final String TAG = "ShellEg";
	
	private TextView txtResult, txtCheckRoot, txtExitRoot, txtBatCmd;
	private EditText edtCmd;
	private ShellAndroid mShell;
	private boolean mUseMinimum = true;

	private String[] mBatCmds = new String[]{
			"id",
			"cd",
			"pwd",
			"which su",
			"id",
			"date",
			"sync",
			"id",
			"ls -l",
			"echo 'this is last cmd for bat cmds'",
			"pwd",
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edtCmd = (EditText) findViewById(R.id.edit);
		txtResult = (TextView) findViewById(R.id.text);
		txtResult.setMovementMethod(new ScrollingMovementMethod());
		txtCheckRoot = (TextView) findViewById(R.id.check_root_result);
		txtExitRoot = (TextView) findViewById(R.id.exit_root_result);
		txtBatCmd = (TextView) findViewById(R.id.bat_exec_result);

		findViewById(R.id.execute).setOnClickListener(this);
		findViewById(R.id.check_root).setOnClickListener(this);
		findViewById(R.id.exit_root).setOnClickListener(this);
		findViewById(R.id.bat_exec).setOnClickListener(this);

		mBatCmds[1] = "cd /data/data/" + getPackageName();

		//---- shell initialization ----
		ShellAndroid.DEBUG = true;
		mShell = new ShellAndroid(null);
		String flagFile;
		if (!mUseMinimum){
			flagFile = mShell.initFlag(getApplicationContext());
		}else {
			flagFile = mShell.initFlagMinimum(getApplicationContext());
			Toast.makeText(this, "Use minimum init flag", Toast.LENGTH_SHORT).show();
		}
		mShell.printOutput();
		mShell.setFlagFile(flagFile);
		//---- finish shell initialization ----
		
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
		switch (v.getId()) {
		case R.id.execute:
			String cmd = edtCmd.getText().toString();
			new ExecuteTask().execute(cmd);
			edtCmd.setText("");
			edtCmd.requestFocus();
			break;
		case R.id.check_root:
			new RootCheckTask(1).execute();
			break;
		case R.id.exit_root:
			new RootCheckTask(2).execute();
			break;
		case R.id.bat_exec:
			new BatExecuteTask().execute(mBatCmds);
			break;
		}
	}
	
	private class ExecuteTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String cmd = params[0];
			if (cmd.startsWith("0x")){
				byte[] ascii = new byte[]{Integer.valueOf(cmd.substring(2), 16).byteValue()};
				mShell.exec(false, new String(ascii));
			}else{
				mShell.exec(false, params);
			}
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

	private class BatExecuteTask extends AsyncTask<String, String, String>{

		private ArrayList<String> mResults = new ArrayList<String>(16);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			findViewById(R.id.bat_exec).setEnabled(false);
		}

		@Override
		protected String doInBackground(String... params) {
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i ++) {
					mShell.exec(false, params[i]);
					publishProgress(String.valueOf(i+1), mShell.getLastResult());
				}
			}
			return mShell.getLastResult();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (values != null && values.length > 0) {
				txtBatCmd.setText(String.format("%s cmd execed", values[0]));
				txtResult.setText(values[1]);
				mResults.add(values[1]);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			findViewById(R.id.bat_exec).setEnabled(true);
			if (!TextUtils.isEmpty(result)){
				txtResult.setText(result);
			}else{
				txtResult.setText("Empty result");
			}
			Log.d(TAG, mResults.toString());
		}
	}

	public class RootCheckTask extends AsyncTask<Void, Void, Boolean>{
		
		private final int mTaskType;
		
		public RootCheckTask(int type) {
			// TODO Auto-generated constructor stub
			mTaskType = type;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (mTaskType == 1){
				mShell.checkRoot();
			}else if (mTaskType == 2){
				mShell.exitRoot();
			}
			return mShell.hasRoot();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mTaskType == 1){
				txtCheckRoot.setText(result.toString());
			}else if (mTaskType == 2){
				txtExitRoot.setText(result.toString());
			}
		}
	}
}
