.PHONY: build
build:
	./gradlew build

.PHONY: run
run: jar
	java -jar ./build/libs/trekwars-all.jar

.PHONY: jar
jar:
	./gradlew shadowJar

.PHONY: watch
watch:
	find ./src/ | grep -e 'java$$' | entr -c make build
