$(info Set the JAVA_HOME directory in your system, either by adding JAVA_HOME=... in /etc/environment and rebooting (permanently), \
or just issuing the JAVA_HOME=... command in your command line (only for the current session))
$(info )

SERVER_CLASS_PATH=Internet_Radio/bin/app
SERVER_JNI_DIR=Internet_Radio/jni
SERVER_SRC_DIR=Internet_Radio/src/app
JAVAC=$(JAVA_HOME)/bin/javac
LIB_DIR=-L./libpifacecad
LIB_DIR+=-L./libmcp23s17
LIBS=-lpifacecad
LIBS+=-lmcp23s17

SERVER_C_SRC=$(wildcard $(SERVER_JNI_DIR)/*.c)
SERVER_JAVA_SRC=$(wildcard $(SERVER_SRC_DIR)/*.java)

SERVER_OBJS=$(SERVER_C_SRC:.c=.o)


vpath %.class $(SERVER_CLASS_PATH)

all : header $(SERVER_JNI_DIR)/libradio.so

header : $(SERVER_JNI_DIR)/RadioControl.h


$(SERVER_JNI_DIR)/libradio.so : $(SERVER_OBJS)
	gcc -W -shared -o $@ $<

%.o: %.c
	gcc -fPIC -c -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" $(LIB_DIR) $(LIBS) -I"$(SERVER_JNI_DIR)" $< -o $@

$(SERVER_JNI_DIR)/RadioControl.h :
	$(JAVAC) -h $(SERVER_JNI_DIR) -d $(SERVER_CLASS_PATH) $(SERVER_JAVA_SRC)


clean :
	rm -Rf $(SERVER_JNI_DIR)/*.h $(SERVER_JNI_DIR)/*.o $(SERVER_JNI_DIR)/*.so $(SERVER_CLASS_PATH)/*