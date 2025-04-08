package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/04/2025
 * Time:11:20
 */
public class TestFileLoader {

    public static String loadFileAsString(String fileName) throws IOException {
        Path path = Paths.get("src/test/resources", fileName);
        return Files.readString(path);
    }
}