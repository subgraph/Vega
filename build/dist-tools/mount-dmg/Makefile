TARGET=mount-dmg
all: $(TARGET)
OBJS=mount-dmg.o
LD=gcc
CC=gcc
CFLAGS=-Wall
.c.o: 
	$(CC) $(CFLAGS) -c $<
$(TARGET): $(OBJS)
	$(LD) -o $@ $(OBJS)
clean:
	rm -f *.o $(TARGET)
install:
	cp $(TARGET) /usr/local/bin/$(TARGET)
	chmod 4755 /usr/local/bin/$(TARGET)
