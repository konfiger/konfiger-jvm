package io.github.thecarisma;

import java.io.IOException;
import java.util.Map;

public class TestKonfiger_7 {

    public static void main(String[] args) throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("Name===Adewale AzeezgOccupation=Software En\\gineergGreet=\tHello\nWorldgLocation=Ni\\geria", false, '=', 'g');
        konfiger.setSeperator('\n');
        konfiger.putString("Greet", "\tHello-      World");

        System.out.println(konfiger.getString("Greet"));
        System.out.println(konfiger.toString());
        konfiger.save("src/test/resources/test.txt");

        System.out.println();
        Map<String, String> en = konfiger.entries();
        for (String key : en.keySet()) {
            System.out.println(key + "=" + en.get(key));
        }
    }

}
