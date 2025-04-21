# DSAIPG

## Instructions to Run

We use a fork of the existing DSAIPG repository so it’s still the same Maven project. Using an IDE like IntelliJ IDEA, or VS Code, you can also run the program after cloning. After Maven downloads all the dependencies, run `MCTS.java` for TicTacToe or `ReversiMCTS.java` for Reversi. Similarly, you can run MCTSTest.java, ReversiMCTSTest.java or others for test cases. All new files are in sub-directories of `Java/src/main/java/com/phasmidsoftware/dsaipg/projects/mcts` while the new test cases are in sub-directories of `Java/src/test/java/com/phasmidsoftware/dsaipg/projects/mcts`

## Installation
There are two major directories within this repository:
* Java
* Python

The repository is designed to be cloned from https://github.com/rchillyard/DSAIPG.git
Alternatively, if you will be submitting assignments based on the repository,
then you might want to fork it instead.

The Java repository contains a Maven project (see the `pom.xnl` file in the top level).
Ideally, you will use an IDE that is suited to Maven projects.
I recommend IntelliJ IDEA for Java work.

## Building and Testing (Java)
If you have cloned (or forked) the repository into IDEA, it should build the Java project
for you without much intervention on your part.
You will need at least Java 17 as your SDK.
Recommended: Oracle OpenJDK 18.0.2

To test the installation, run all the tests in `src/test/java`.
There are about a thousand active tests, of which two-thirds should run green.
Don't worry about the failing tests--they fail because there are stubs in the code
that you need to replace with functioning code in many places
(see above in Navigation).

There are also functional tests in the `src/it/java` directory.
However, these take significantly longer to run and are really not necessary. 