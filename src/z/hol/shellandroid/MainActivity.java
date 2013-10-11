package z.hol.shellandroid;

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

/**
 * The example activity, and also for test
 * @author holmes
 *
 */
public class MainActivity extends Activity implements OnClickListener{
	
	private TextView txtResult, txtCheckRoot, txtExitRoot;
	private EditText edtCmd;
	private ShellAndroid mShell;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edtCmd = (EditText) findViewById(R.id.edit);
		txtResult = (TextView) findViewById(R.id.text);
		txtResult.setMovementMethod(new ScrollingMovementMethod());
		txtCheckRoot = (TextView) findViewById(R.id.check_root_result);
		txtExitRoot = (TextView) findViewById(R.id.exit_root_result);

		findViewById(R.id.execute).setOnClickListener(this);
		findViewById(R.id.check_root).setOnClickListener(this);
		findViewById(R.id.exit_root).setOnClickListener(this);
		
		//---- shell initialization ----
		mShell = new ShellAndroid();
		String flagFile = mShell.initFlag(getApplicationContext());
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
