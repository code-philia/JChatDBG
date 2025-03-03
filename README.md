# ChatDBG

This project supports [ChatDBG](https://github.com/plasma-umass/ChatDBG)'s running on Java language, especially for cases in Defects4J dataset.

The project has not been completed yet, but it is under development.

## Branches

- `main`: The implementation of ChatDBG for Java language.

- `FullAutomated`: Base on ChatDBG, automate the whole process with no need to manually input some JDB debugging commands and natural language questions.

## Project Setup

### Environment

1. Install [Ant](https://ant.apache.org/) and add it to your system path.

2. Install [Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) and add it to your system path.

2. Choose Java17 as your project SDK. For example in IntelliJ IDEA, you can set it in `File -> Project Structure -> Project -> Project SDK`.

3. Install [Maven](https://maven.apache.org/download.cgi) and add it to your system path.

4. Install [Defects4J](https://github.com/rjust/defects4j) and put it at the project root path.

### Properties

You **must** modify the settings in `config.properties` to run cases in Defects4J dataset. The following properties must be identified:

- `repo`: The absolute path to the directory where the Defects4J dataset is located, for example `D:\\Defects4J`

- `name`: The name of the project in Defects4J dataset, for example `Chart`

- `id`: The id of the bug in the project, for example `1`

## How to use it

Run the `main` method in `src/main/java/ChatDBG/Main.java` to start the project.