package io.github.thecarisma;

import java.io.IOException;

public class TestKonfiger {

    public static void main(String[] args) throws IOException, InvalidEntryException {
        Konfiger konfiger = new Konfiger("");
        konfiger.put("One", konfiger);
        konfiger.put("Two", "\"hello', \"world\"");
        konfiger.putString("Two", "hello world");
        konfiger.put("Three", 3);
        konfiger.putInt("Four", 4);
        konfiger.putBoolean("Five", true);
        konfiger.put("Six", false);
        konfiger.put("Seven", 121251656.1367367263726);
        konfiger.putFloat("Eight", 0.21f);


        System.out.println(konfiger.get("One"));
        System.out.println(konfiger.get("Two"));
        System.out.println(konfiger.get("Three"));
        System.out.println(konfiger.get("Four"));
        System.out.println(konfiger.get("Five"));
        System.out.println(konfiger.get("Six"));
        System.out.println(konfiger.get("Seven"));
        System.out.println(konfiger.get("Eight"));
    }

}
