##### DA ricreare tutt e le variabili di android studio


D:\
cd "/home/ubuntu/AndroidDevelop/iMuve/app/src/main/jni"
### -mno-cygwin

set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_17

gcc -I"C:\Program Files\Java\jdk1.7.0_17\include" -I"C:\Program Files\Java\jdk1.7.0_17\include\win32" -I"/home/ubuntu/AndroidDevelop/iMuve/app/src/main/jni" -Wl,--add-stdcall-alias -shared -o imuvecpp.dll iMuveCPP.c

cd "/home/ubuntu/AndroidDevelop/iMuve"
