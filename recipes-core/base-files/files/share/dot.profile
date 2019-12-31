# ~/.profile: executed by Bourne-compatible login shells.

if [ -f ~/.bashrc ]; then
  . ~/.bashrc
fi

PATH=/bin:/usr/bin:/usr/local/bin:/sbin:/usr/sbin:/usr/local/sbin:/opt/scripts
export PATH

# Might fail after "su - myuser" when /dev/tty* is not writable by "myuser".
mesg n 2>/dev/null
