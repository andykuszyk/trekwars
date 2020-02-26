.PHONY: build
build:
	./gradlew clean build

.PHONY: run
run: jar
	java -jar ./build/libs/trekwars-all.jar

.PHONY: jar
jar:
	./gradlew clean shadowJar
