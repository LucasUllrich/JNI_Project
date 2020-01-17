$(info Set the JAVA_HOME directory in your system, either by adding JAVA_HOME=... in /etc/environment and rebooting (permanently), \
or just issuing the JAVA_HOME=... command in your command line (only for the current session))
$(info )

SERVER_CLASS_PATH=Internet_Radio/bin/
SERVER_JNI_DIR=Internet_Radio/jni
SERVER_SRC_DIR=Internet_Radio/src/app
JAVAC=$(JAVA_HOME)/bin/javac

#LIB_PIFACECAD=./libpifacecad
#LIB_MCP23S17=./libmcp23s17

LIB_PIFACECAD_DIR=./libpifacecad
LIB_MCP23S17_DIR=./libmcp23s17
LIB_PIFACECAD=-lpifacecad
LIB_MCP23S17=-lmcp23s17

LIBS=-L$(LIB_PIFACECAD_DIR) $(LIB_PIFACECAD)
LIBS+=-L$(LIB_MCP23S17_DIR) $(LIB_MCP23S17)

INC_DIR=-I$(LIB_PIFACECAD_DIR)/src
INC_DIR+=-I$(LIB_MCP23S17_DIR)/src

SERVER_C_SRC=$(wildcard $(SERVER_JNI_DIR)/*.c)
SERVER_JAVA_SRC=$(wildcard $(SERVER_SRC_DIR)/*.java)

SERVER_OBJS=$(SERVER_C_SRC:.c=.o)

uname_m := $(shell uname -m)

vpath %.class $(SERVER_CLASS_PATH)

all : java $(SERVER_JNI_DIR)/libradio.so

java : $(SERVER_JNI_DIR)/RadioControl.h


$(SERVER_JNI_DIR)/libradio.so : $(SERVER_OBJS)
ifeq ($(uname_m),armv7l)
	gcc -W -shared -o $@ $^ $(LIBS)
else
	gcc -W -shared -o $@ $^
endif

%.o: %.c
ifeq ($(uname_m),armv7l)
	gcc -fPIC -c -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" $(INC_DIR) $(LIBS) -I"$(SERVER_JNI_DIR)" $< -o $@
else
	gcc -fPIC -c -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -DPC_BUILD=1 -I"$(SERVER_JNI_DIR)" $< -o $@
endif

$(SERVER_JNI_DIR)/RadioControl.h :
	$(JAVAC) -h $(SERVER_JNI_DIR) -cp $(SERVER_CLASS_PATH) -d $(SERVER_CLASS_PATH) $(SERVER_JAVA_SRC)

clean :
	rm -Rf $(SERVER_JNI_DIR)/*.h $(SERVER_JNI_DIR)/*.o $(SERVER_JNI_DIR)/*.so $(SERVER_CLASS_PATH)/*
