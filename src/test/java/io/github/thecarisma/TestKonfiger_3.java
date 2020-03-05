package io.github.thecarisma;

import java.io.File;
import java.io.IOException;

public class TestKonfiger_3 {

    public static void main(String[] args) throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger(new File("src/test/resources/test.config.ini"), true);
        konfiger.putString("Greet", "\tHello \tWorld");
        konfiger.putLong("Long", 245134535524372L);
        konfiger.putString("Last", "Hello every one \rStart again");
        System.out.println(konfiger.getString("Name"));

        System.out.println(konfiger.toString());
        System.out.println();
        System.out.println(konfiger.hashCode());
        System.out.println(konfiger.getString("Last"));
    }

}
