package io.github.thecarisma;

import java.io.File;
import java.io.IOException;

public class TestKonfiger_9 {

    public static void main(String[] args) throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger(new File("src/test/resources/test.config.ini"), true);
        System.out.println(konfiger.toString());
        System.out.println();
        konfiger.appendString("\n" +
                "Language=English\n"+
                "");
        konfiger.appendFile(new File("src/test/resources/test.txt"));
        System.out.println(konfiger.toString());
    }

}
