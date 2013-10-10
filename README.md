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