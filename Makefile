CC=kotlinc
CFLAGS=-include-runtime

OUTPUT_NAME=KFlex.jar
OUTPUT_DIR=./bin/

all:
	$(CC) KFlex/src/Main.kt $(CFLAGS) -d $(OUTPUT_NAME)
	mkdir -p $(OUTPUT_DIR)
	mv $(OUTPUT_NAME) $(OUTPUT_DIR)$(OUTPUT_NAME)

clean:
	rm -r $(OUTPUT_DIR)