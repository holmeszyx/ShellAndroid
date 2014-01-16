ShellAndroid
============

A shell for android. Can execute most linux shell command in it.
You can easy to get last command result.
At most time, it like a lite terminal, you can keep global shell in your application.

	sh --> su --> id
	the last result: uid=0(root) gid=0(root)
	
and then
	
	exit --> id
	the last result: uid=10103(u0_a103) gid=10103(u0_a103) groups=1028(sdcard_r)
	
A exact result of last command got from the ShellAndroid.

How to use
----------

* Put the cflag executable file to assets in your project.

* Initialize the ShellAndroid.(see mainActivity.java in example)

		//---- shell initialization ----
		mShell = new ShellAndroid();
		String flagFile = mShell.initFlag(getApplicationContext());
		mShell.printOutput();
		mShell.setFlagFile(flagFile);
		//---- finish shell initialization ----

* Close the ShellAndroid if you never need it.(like the example close the shell on the onDestory method in activity)

		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			if (mShell != null){
				mShell.close();
			}
		}

* **Execute shell command in a work thread.**
* Get command result by getLastResult() in ShellAndroid.(more details are in example)

        mShell.exec(false, "cd /sdcard");
        mShell.exec(false, "pwd");
        mShell.getLastResult(); // pwd result, return "/sdcard"
        
* If you want get root permission, the best way is use the shell methods.

		shell.checkRoot()	// try to get root permission
		shell.hasRoot()	// Is it the root user now
		shell.exitRoot()	// exit root user, even if you execute su command many times.


Bug
---

* Some commands makes the shell unable to work. The known commands below(They almost are interactive commands):
	
		if
		wc