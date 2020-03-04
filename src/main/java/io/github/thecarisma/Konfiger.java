package io.github.thecarisma;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Konfiger {

    public static int MAX_CAPACITY = 10000000;
    private KonfigerStream stream;
    private boolean lazyLoad = false;
    private String filePath = "";
    private boolean enableCache_ = true;
    private Map<String, String> konfigerObjects = new HashMap<>();
    private String[] prevCachedObject = {"", ""};
    private String[] currentCachedObject = {"", ""};
    private boolean loadingEnds = false;
    private boolean changesOccur = false;

    public Konfiger(String rawString, boolean lazyLoad, char delimeter, char separator, boolean errTolerance) throws IOException, InvalidEntryException {
        this(new KonfigerStream(rawString, delimeter, separator, errTolerance), lazyLoad);
    }

    public Konfiger(File file, char delimeter, boolean lazyLoad, char separator, boolean errTolerance) throws IOException, InvalidEntryException {
        this(new KonfigerStream(file, delimeter, separator, errTolerance), lazyLoad);
    }

    public Konfiger(KonfigerStream konfigerStream, boolean lazyLoad) throws IOException, InvalidEntryException {
        this.stream = konfigerStream;
        this.lazyLoad = lazyLoad;
        this.filePath = konfigerStream.filePath;

        if (!this.lazyLoad) {
            this.lazyLoader();
        }
    }

    public void put(String key, Object value) {
        if (value instanceof String) {
            putString(key, (String)value);
        } else if (value instanceof Boolean) {
            putBoolean(key, (boolean)value);
        } else if (value instanceof Long) {
            putLong(key, (long)value);
        } else if (value instanceof Integer) {
            putInt(key, (int)value);
        } else if (value instanceof Float || value instanceof Double) {
            putFloat(key, (float)value);
        } else {
            putString(key, value.toString());
        }
    }

    public void putString(String key, String value) {
        if (lazyLoad && !loadingEnds && !contains(key)) {
            String _value = getString(key);
            if (_value.equals(value)) {
                return;
            }
        }
        if (!contains(key)) {
            if (konfigerObjects.size() == MAX_CAPACITY) {
                try {
                    throw new MaxCapacityException();
                } catch (MaxCapacityException e) {
                    e.printStackTrace();
                }
            }
        }
        konfigerObjects.put(key, value);
        changesOccur = true;
        if (enableCache_) {
            shiftCache(key, value);
        }
    }

    public void putBoolean(String key, boolean value) {
        putString(key, Boolean.toString(value));
    }

    public void putLong(String key, long value) {
        putString(key, Long.toString(value));
    }

    public void putInt(String key, int value) {
        putString(key, Integer.toString(value));
    }

    public void putFloat(String key, float value) {
        putString(key, Float.toString(value));
    }

    public boolean contains(String key) {
        return false;
    }

    public Object get(String key) {
        return null;
    }

    public String getString(String key) {
        return "";
    }

    private void lazyLoader() throws IOException, InvalidEntryException {
        if (loadingEnds) {
            return;
        }
        while (stream.hasNext()) {
            String[] obj = stream.next();
            putString(obj[0], obj[1]);
        }
        this.loadingEnds = true;
    }

    private void shiftCache(String key, String value) {
        prevCachedObject = currentCachedObject;
        currentCachedObject[0] = key;
        currentCachedObject[1] = value;
        System.out.println(prevCachedObject[0]);
        System.out.println(currentCachedObject[0]);
    }

}
