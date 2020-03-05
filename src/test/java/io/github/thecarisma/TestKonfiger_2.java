package io.github.thecarisma;

import java.io.IOException;

public class TestKonfiger_2 {

    public static void main(String[] args) throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("src/test/resources/test.config.ini", true);
        konfiger.setSeperator('-');
        konfiger.setDelimeter('+');

        System.out.println(konfiger.getSeperator());
        System.out.println(konfiger.getDelimeter());
    }

}
