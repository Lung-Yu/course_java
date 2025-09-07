# course_java


## Environment Setup

### Windows

1. Install [Java JDK](https://www.oracle.com/java/technologies/downloads/).
2. Set the `JAVA_HOME` environment variable:
    - Open **System Properties** > **Environment Variables**.
    - Add a new variable `JAVA_HOME` pointing to your JDK installation path.
    - Add `%JAVA_HOME%\bin` to the `Path` variable.
3. Verify installation:
    ```sh
    java -version
    ```

### macOS

1. Install Java JDK using [Homebrew](https://brew.sh/):
    ```sh
    brew install openjdk
    ```
2. Add Java to your PATH:
    ```sh
    echo 'export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"' >> ~/.zshrc
    source ~/.zshrc
    ```
3. Verify installation:
    ```sh
    java -version
    ```



### 編譯
find . -name "*.java" -exec javac {} \;