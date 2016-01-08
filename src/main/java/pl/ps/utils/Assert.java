package pl.ps.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;

public class Assert {

    public static void isTrue(boolean condition, String message) throws AssertionError {
        if(!condition) {
            throw new AssertionError(message);
        }
    }

    public static void notNull(Object object, String message) throws AssertionError {
        isTrue(object != null, message);
    }

    public static void fileExists(File file) throws AssertionError {
        notNull(file, "Przekazany plik jest NULLem!");
        String dirPath = " [" + file.getAbsolutePath() + "]";
        isTrue(file.exists(), "Plik nie istnieje!" + dirPath);
        isTrue(file.canRead(), "Brak uprawnień odczytu do pliku!" + dirPath);
        isTrue(file.canWrite(), "Brak uprawnień zapisu do pliku!" + dirPath);
    }

    public static void directoryExists(File directory) throws AssertionError {
        fileExists(directory);
        String dirPath = " [" + directory.getAbsolutePath() + "]";
        isTrue(directory.isDirectory(), "Plik nie jest katalogiem!" + dirPath);
    }

    public static void isNumeric(String string) throws AssertionError {
        isTrue(NumberUtils.isNumber(string), "Napis nie zawiera liczby! [" + string + "]");
    }

    public static void stringNotEmpty(String string) throws AssertionError {
        isTrue(StringUtils.isNotBlank(string), "Napis jest pusty! [" + string + "]");
    }

}
