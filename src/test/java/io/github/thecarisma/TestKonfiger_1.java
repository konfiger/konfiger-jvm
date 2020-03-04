package io.github.thecarisma;

import io.github.thecarisma.Konfiger;

import java.io.IOException;
import java.util.Map;

public class TestKonfiger_1 {

    public static void main(String[] args) throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("");
        konfiger.put("One", konfiger);
        konfiger.putLong("Two", 123456789);
        konfiger.putBoolean("Bool", true);
        konfiger.putFloat("Float", 123.56f);
        konfiger.putString("Dummy", "Noooooo 1");
        konfiger.putString("Dummy2", "Noooooo 1");


        System.out.println("=====================================");
        System.out.println(konfiger.get("Two"));
        System.out.println("IsString: " + (konfiger.get("Two") instanceof String));
        System.out.println("IsNumber: " + (konfiger.getLong("Two")));
        System.out.println("IsString: " + (konfiger.get("Two") instanceof Long));
        System.out.println("=====================================");
        System.out.println(konfiger.get("Bool"));
        System.out.println("IsString: " + (konfiger.get("Bool") instanceof Boolean));
        System.out.println("IsBoolean: " + (konfiger.getBoolean("Bool")));
        System.out.println("IsString: " + (konfiger.get("Bool") instanceof Boolean));
        System.out.println("=====================================");
        System.out.println(konfiger.get("Float"));
        System.out.println("IsString: " + (konfiger.get("Float") instanceof String));
        System.out.println("IsFloat: " + (konfiger.getFloat("Float")));
        System.out.println("IsString: " + (konfiger.get("Float") instanceof String));
        System.out.println("=====================================");
        System.out.println(konfiger.get("Three", "Default Value"));

        System.out.println();
        System.out.println("Keys");
        for (String en : konfiger.keys()) {
            System.out.println(en);
        }
        System.out.println();


        System.out.println("Values");
        for (String en : konfiger.values()) {
            System.out.println(en);
        }
        System.out.println();


        System.out.println("Entries");
        Map<String, String> en = konfiger.entries();
        for (String key : en.keySet()) {
            System.out.println(key + "=" + en.get(key));
        }
        System.out.println();

        System.out.println(konfiger.size());
        konfiger.remove("Dummy2");
        System.out.println(konfiger.size());
        konfiger.remove(konfiger.size() - 1);
        System.out.println(konfiger.size());
        System.out.println();
    }

}
