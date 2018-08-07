JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	ChatServer.java \
	ChatClient.java \
	User.java \
	Display.java \
	userBuffer.java

default: classes

classes: $(CLASSES:.java=.class)

run: classes \
	$(JVM) $(MAIN)

clean:
	$(RM) *.class