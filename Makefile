$(info Set the JAVA_HOME directory in your system, either by adding JAVA_HOME=... in /etc/environment and rebooting (permanently), \
or just issuing the JAVA_HOME=... command in your command line (only for the current session))
$(info )

CLASS_PATH=Internet_Radio/bin/app
JNI_DIR=Internet_Radio/jni
SRC_DIR=Internet_Radio/src/app
JAVAC=$(JAVA_HOME)/bin/javac


C_SRC=$(wildcard $(JNI_DIR)/*.c)
JAVA_SRC=$(wildcard $(SRC_DIR)/*.java)

OBJS=$(C_SRC:.c=.o)


vpath %.class $(CLASS_PATH)

all : header $(JNI_DIR)/libradio.so

header : $(JNI_DIR)/RadioControl.h


$(JNI_DIR)/libradio.so : $(OBJS)
	gcc -W -shared -o $@ $<

%.o: %.c
	gcc -fPIC -c -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -I"$(JNI_DIR)" $< -o $@

$(JNI_DIR)/RadioControl.h :
	$(JAVAC) -h $(JNI_DIR) -d $(CLASS_PATH) $(JAVA_SRC)


clean :
	rm -Rf $(JNI_DIR)/*.h $(JNI_DIR)/*.o $(JNI_DIR)/*.so $(CLASS_PATH)/*