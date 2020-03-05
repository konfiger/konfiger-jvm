package io.github.thecarisma;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TestKonfiger_8 {

    public static void main(String[] args) throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("\n" +
                "Name=Adewale Azeez\n" +
                "Occupation=Software Engineer\n" +
                "Location=Nigeria\n", true);
        System.out.println(konfiger.toString());
        System.out.println();
        konfiger.appendString("\n" +
        "Language=English\n"+
        "");
        konfiger.appendFile(new File("src/test/resources/test.txt"));
        System.out.println(konfiger.toString());
    }

}
