# Makefile for RdasRelay

JAVA_CLASSES = $(JAVA_SRCS:.java=.class)
JAVAC = javac

JAVA_SRCS = \
	ClientComms.java \
	DataBufQueue.java \
	RdasRelay.java \
	RelayComms.java \
	RelayServer.java

all: $(JAVA_CLASSES)

clean:
	/bin/rm -f $(JAVA_CLASSES) *.class

jar:
	jar cmf Manifest ../RdasRelay.jar *

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $(JAVA_FLAGS) $<

