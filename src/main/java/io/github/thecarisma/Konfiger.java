package io.github.thecarisma;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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

    public Konfiger(String rawString) throws IOException, InvalidEntryException {
        this(new KonfigerStream(rawString, '=', '\n', false), false);
    }

    public Konfiger(File file, char delimeter, boolean lazyLoad, char separator, boolean errTolerance) throws IOException, InvalidEntryException {
        this(new KonfigerStream(file, delimeter, separator, errTolerance), lazyLoad);
    }

    public Konfiger(File file) throws IOException, InvalidEntryException {
        this(new KonfigerStream(file, '=', '\n', false), false);
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
        } else if (value instanceof Float) {
            putFloat(key, (float)value);
        } else if (value instanceof Double) {
            putDouble(key, (double)value);
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

    public void putDouble(String key, double value) {
        putString(key, Double.toString(value));
    }

    public Object get(String key) {
        if (enableCache_) {
            if (currentCachedObject[0].equals(key)) {
                return currentCachedObject[1];
            }
            if (prevCachedObject[0].equals(key)) {
                return prevCachedObject[1];
            }
        }
        if (!contains(key) && lazyLoad) {
            if (!loadingEnds) {

            }
        }
        return konfigerObjects.get(key);
    }

    public String getString(String key) {
        return get(key).toString();
    }

    public boolean getBoolean(String key) {
        boolean ret = false;
        try {
            ret = Boolean.parseBoolean(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public long getLong(String key) {
        long ret = 0;
        try {
            ret = Long.parseLong(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public long getInt(String key) {
        int ret = 0;
        try {
            ret = Integer.parseInt(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public float getFloat(String key) {
        float ret = 0;
        try {
            ret = Float.parseFloat(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public double getDouble(String key) {
        double ret = 0;
        try {
            ret = Double.parseDouble(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public Set<String> keys() {
        toString();
        return konfigerObjects.keySet();
    }

    public Collection<String> values() {
        toString();
        return konfigerObjects.values();
    }

    public Map<String, String> entries() {
        toString();
        return konfigerObjects;
    }

    public boolean contains(String key) {
        return konfigerObjects.containsKey(key);
    }

    public void clear() {
        changesOccur = true;
        enableCache(enableCache_);
        konfigerObjects.clear();
    }

    public String remove(String key) {
        changesOccur = true;
        enableCache(enableCache_);
        return konfigerObjects.remove(key);
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
        prevCachedObject[0] = currentCachedObject[0];
        prevCachedObject[1] = currentCachedObject[1];
        currentCachedObject[0] = key;
        currentCachedObject[1] = value;
    }

}
