/dev/root		/		ext4		defaults					1 1
proc			/proc		proc		defaults					0 0
sysfs			/sys		sysfs		rw,nosuid,nodev,noexec,relatime			0 0
devpts			/dev/pts	devpts		rw,nosuid,noexec,relatime,mode=0620,gid=5	0 0
tmpfs			/run		tmpfs		mode=0755,nodev,nosuid,strictatime		0 0
tmpfs			/var/volatile	tmpfs		defaults					0 0
tmpfs			/dev/shm	tmpfs		defaults					0 0

/dev/mmcblk2p3		/media/rfs/rw	ext4		defaults					0 1
/data/.var/log		/var/log	ext4		defaults,bind,x-systemd.after=data.mount	0 0
