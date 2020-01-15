CLASS_PATH=Internet_Radio/bin/app
JNI_DIR=Internet_Radio/jni
SRC_DIR=Internet_Radio/src/app
#SERVER_DIR=./Internet_Radio/
#CLIENT_DIR=./Internet_Radio_Client/
#JNI_DIR=$(SERVER_DIR)$(JNI_DIR_NAME)
#SRC_DIR=$(SERVER_DIR)$(SRC_DIR_NAME)
#CLASS_PATH=$(SERVER_DIR)$(CLASS_PATH_NAME)

C_SRC=$(wildcard SRC_DIR/*.c)

OBJS=$(C_SRC:.c=.o);

$(info C_FILES is $(C_FILES))

vpath %.class $(CLASS_PATH)

all : $(JNI_DIR)/libradio.so

header : $(JNI_DIR)/RadioControl.h

$(JNI_DIR)/libradio.so : $(JNI_DIR)/RadioControl.o
	gcc -W -shared -o $@ $<

$(JNI_DIR)/RadioControl.o : $(C_FILES) $(C_H_FILES)
	gcc -fPIC -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -I"$(JNI_DIR)" -c $^ -o $@

%*.c : %*.o

$(JNI_DIR)/RadioControl.h :
	$(JAVAC) -h $(JNI_DIR) -d $(CLASS_PATH) $(JAVA_FILES)


clean :
	rm -Rf $(JNI_DIR)/*.h $(JNI_DIR)/*.o $(JNI_DIR)/*.so $(CLASS_PATH)/*