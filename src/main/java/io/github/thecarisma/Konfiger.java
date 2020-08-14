package io.github.thecarisma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class Konfiger {

    public static int MAX_CAPACITY = 10000000;
    private final KonfigerStream stream;
    private boolean lazyLoad = false;
    private String filePath = "";
    private boolean enableCache_ = true;
    private boolean caseSensitive = true;
    private final Map<String, String> konfigerObjects = new LinkedHashMap<>();
    String[] prevCachedObject = {"", ""};
    String[] currentCachedObject = {"", ""};
    private boolean loadingEnds = false;
    private boolean changesOccur = true;
    private char delimeter = '=';
    private char seperator = '\n';
    private String stringValue = "";
    private Object attachedResolveObj;

    public Konfiger(String rawString, boolean lazyLoad, char delimeter, char seperator, boolean errTolerance) throws IOException, InvalidEntryException {
        this(new KonfigerStream(rawString, delimeter, seperator, errTolerance), lazyLoad);
    }

    public Konfiger(String rawString, boolean lazyLoad, char delimeter, char seperator) throws IOException, InvalidEntryException {
        this(new KonfigerStream(rawString, delimeter, seperator, false), lazyLoad);
    }

    public Konfiger(String rawString) throws IOException, InvalidEntryException {
        this(new KonfigerStream(rawString, '=', '\n', false), false);
    }

    public Konfiger(String rawString, boolean lazyLoad) throws IOException, InvalidEntryException {
        this(new KonfigerStream(rawString, '=', '\n', false), lazyLoad);
    }

    public Konfiger(File file, boolean lazyLoad, char delimeter, char seperator, boolean errTolerance) throws IOException, InvalidEntryException {
        this(new KonfigerStream(file, delimeter, seperator, errTolerance), lazyLoad);
    }

    public Konfiger(File file, boolean lazyLoad, char delimeter, char seperator) throws IOException, InvalidEntryException {
        this(new KonfigerStream(file, delimeter, seperator, false), lazyLoad);
    }

    public Konfiger(File file) throws IOException, InvalidEntryException {
        this(new KonfigerStream(file, '=', '\n', false), false);
    }

    public Konfiger(File file, boolean lazyLoad) throws IOException, InvalidEntryException {
        this(new KonfigerStream(file, '=', '\n', false), lazyLoad);
    }

    public Konfiger(KonfigerStream konfigerStream) throws IOException, InvalidEntryException {
        this(konfigerStream, true);
    }

    public Konfiger(KonfigerStream konfigerStream, boolean lazyLoad) throws IOException, InvalidEntryException {
        this.stream = konfigerStream;
        this.lazyLoad = lazyLoad;
        this.filePath = konfigerStream.filePath;
        this.seperator = konfigerStream.seperator;
        this.delimeter = konfigerStream.delimeter;

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
        if (lazyLoad && !loadingEnds && konfigerObjects.containsKey(key)) {
            String _value = getString(key);
            if (_value.equals(value)) {
                return;
            }
        }
        if (!konfigerObjects.containsKey(key)) {
            if (konfigerObjects.size() == MAX_CAPACITY) {
                try {
                    throw new MaxCapacityException();
                } catch (MaxCapacityException e) {
                    e.printStackTrace();
                }
            }
        }
        konfigerObjects.put(key, value);
        if (attachedResolveObj != null) {
            Method matchPutKey = null;
            Method[] methods = attachedResolveObj.getClass().getDeclaredMethods();
            Field[] fields = attachedResolveObj.getClass().getDeclaredFields();
            for(Method method : methods){
                if (method.getName().equals("matchPutKey")) {
                    matchPutKey = method;
                    matchPutKey.setAccessible(true);
                }
            }
            String findKey = "";
            boolean isAnnotated = false;
            for(Field f : fields) {
                if (f.isAnnotationPresent(KonfigerValue.class)) {
                    KonfigerValue annotation = f.getAnnotation(KonfigerValue.class);
                    if (annotation.value().equals(key)) {
                        isAnnotated = true;
                        findKey = f.getName();
                        break;
                    }
                }
            }
            try {
                if (!isAnnotated && (matchPutKey == null ||
                        ((findKey = (String) matchPutKey.invoke(attachedResolveObj, key)) == null ||
                                findKey.equals("")))) {

                    findKey = key;
                }
                Field f = attachedResolveObj.getClass().getDeclaredField(findKey);
                f.setAccessible(true);
                if (f.getType() == String.class) {
                    f.set(attachedResolveObj, value);

                } else if (f.getType() == Boolean.class || f.getType() == boolean.class) {
                    f.setBoolean(attachedResolveObj, Boolean.parseBoolean(value));

                } else if (f.getType() == Long.class || f.getType() == long.class) {
                    f.setLong(attachedResolveObj, Long.parseLong(value));

                } else if (f.getType() == Integer.class || f.getType() == int.class) {
                    f.setInt(attachedResolveObj, Integer.parseInt(value));

                } else if (f.getType() == Float.class || f.getType() == float.class) {
                    f.setFloat(attachedResolveObj, Float.parseFloat(value));

                } else if (f.getType() == Double.class || f.getType() == double.class) {
                    f.setDouble(attachedResolveObj, Double.parseDouble(value));

                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                if (!stream.errTolerance) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException ignored) { }
        }
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

    public void putComment(String theComment) {
        putString(this.stream.getCommentPrefix(), theComment);
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
        if (!caseSensitive) {
            for (String entryKey : keys()) {
                if (entryKey.toLowerCase().equals(key.toLowerCase())) {
                    key = entryKey;
                    break;
                }
            }
        }
        if (!contains(key)) {
            return null;
        }
        String value = konfigerObjects.get(key);
        if (enableCache_) {
            shiftCache(key, value);
        }
        return value;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public float getFloat(String key) {
        return getFloat(key, 0F);
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public Object get(String key, Object fallbackValue) {
        Object _ret = null;
        if (lazyLoad && !loadingEnds) {
            _ret = get(key);
        }
        return ( _ret == null ? fallbackValue : _ret );
    }

    public String getString(String key, String fallbackValue) {
        Object _ret = get(key);
        return (_ret != null ? _ret.toString() : fallbackValue);
    }

    public boolean getBoolean(String key, boolean fallbackValue) {
        boolean ret = fallbackValue;
        try {
            if (!contains(key)) {
                return ret;
            }
            ret = Boolean.parseBoolean((getString(key)));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public long getLong(String key, long fallbackValue) {
        long ret = fallbackValue;
        try {
            if (!contains(key)) {
                return ret;
            }
            ret = Long.parseLong(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public int getInt(String key, int fallbackValue) {
        int ret = fallbackValue;
        try {
            if (!contains(key)) {
                return ret;
            }
            ret = Integer.parseInt(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public float getFloat(String key, float fallbackValue) {
        float ret = fallbackValue;
        try {
            if (!contains(key)) {
                return ret;
            }
            ret = Float.parseFloat(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public double getDouble(String key, double fallbackValue) {
        double ret = fallbackValue;
        try {
            if (!contains(key)) {
                return ret;
            }
            ret = Double.parseDouble(getString(key));
        } catch (Exception ex) {
            if (!stream.errTolerance) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public Set<String> keys() {
        stringValue = toString();
        return konfigerObjects.keySet();
    }

    public Collection<String> values() {
        stringValue = toString();
        return konfigerObjects.values();
    }

    public Set<Map.Entry<String, String>> entries() {
        return konfigerObjects.entrySet();
    }

    public void enableCache(boolean enableCache_) {
        this.enableCache_ = enableCache_;
        prevCachedObject[0] = "";
        prevCachedObject[1] = "";
        currentCachedObject[0] = "";
        currentCachedObject[1] = "";
    }

    public boolean contains(String key) {
        if (konfigerObjects.containsKey(key)) {
            return true;
        }
        if (!loadingEnds && this.lazyLoad) {
            try {
                while (stream.hasNext()) {
                    String[] obj = stream.next();
                    putString(obj[0], obj[1]);
                    changesOccur = true;
                    if (obj[0].equals(key)) {
                        if (enableCache_) {
                            shiftCache(obj[0], obj[1]);
                        }
                        return true;
                    }
                }
                loadingEnds = true;
            } catch (IOException | InvalidEntryException ex) {
                if (!stream.errTolerance) {
                    ex.printStackTrace();
                }
                return false;
            }
        }
        return false;
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

    public String remove(int index) {
        int i = -1;
        for (String key : konfigerObjects.keySet()) {
            ++i;
            if (i==index) {
                return remove(key);
            }
        }
        return null;
    }

    public void updateAt(int index, String value) {
        if (index < size()) {
            int i = -1;
            for (String key : konfigerObjects.keySet()) {
                ++i;
                if (i==index) {
                    this.changesOccur = true;
                    enableCache(enableCache_);
                    konfigerObjects.put(key, value);
                }
            }
        }
    }

    public int size() {
        if (!loadingEnds && this.lazyLoad) {
            String tmp = toString();
        }
        return konfigerObjects.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public char getDelimeter() {
        return delimeter;
    }

    public void setDelimeter(char delimeter) {
        if (this.delimeter != delimeter) {
            this.delimeter = delimeter;
            changesOccur = true;
        }
    }

    public char getSeperator() {
        return seperator;
    }

    public void setSeperator(char seperator) {
        if (this.seperator != seperator) {
            changesOccur = true;
            char oldSeperator = this.seperator;
            this.seperator = seperator;
            for ( String key : konfigerObjects.keySet()) {
                konfigerObjects.put(key, KonfigerUtil.unEscapeString(konfigerObjects.get(key), oldSeperator));
            }
        }
    }

    public void setCaseSensitivity(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    @Override
    public String toString() {
        if (changesOccur) {
            if (lazyLoad) {
                try {
                    lazyLoader();
                } catch (IOException | InvalidEntryException e) {
                    e.printStackTrace();
                }
            }
            stringValue = "";
            int index = 0;
            for (Map.Entry<String, String> entry : entries()) {
                stringValue += entry.getKey() + delimeter + KonfigerUtil.escapeString(entry.getValue(), seperator);
                ++index;
                if (index < size()) stringValue += seperator;
            }
            changesOccur = false;
        }
        return stringValue;
    }

    public void save() throws IOException {
        save(filePath);
    }

    public void save(String filePath) throws IOException {
        stringValue = toString();
        File file = new File(filePath);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Unable to create the file: " + file.getAbsolutePath());
        }
        try (PrintWriter out = new PrintWriter(file)) {
            out.write(stringValue);
        }
    }

    public void appendString(String rawString) throws IOException, InvalidEntryException {
        appendString(rawString, this.delimeter, this.seperator);
    }

    public void appendString(String rawString, char delimeter, char seperator) throws IOException, InvalidEntryException {
        KonfigerStream _stream = new KonfigerStream(rawString, delimeter, seperator);
        while (_stream.hasNext()) {
            String[] obj = _stream.next();
            putString(obj[0], obj[1]);
        }
        changesOccur = true;
    }

    public void appendFile(File file) throws IOException, InvalidEntryException {
        appendFile(file, this.delimeter, this.seperator);
    }

    public void appendFile(File file, char delimeter, char seperator) throws IOException, InvalidEntryException {
        KonfigerStream _stream = new KonfigerStream(file, delimeter, seperator);
        while (_stream.hasNext()) {
            String[] obj = _stream.next();
            putString(obj[0], obj[1]);
        }
        changesOccur = true;
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

    public void resolve(Object object) throws IllegalAccessException, InvocationTargetException {
        Method matchGetKey = null;
        Method[] methods = object.getClass().getDeclaredMethods();
        Field[] fields = object.getClass().getDeclaredFields();
        for(Method method : methods){
            if (method.getName().equals("matchGetKey")) {
                matchGetKey = method;
                matchGetKey.setAccessible(true);
            }
        }
        for(Field f : fields) {
            String findKey = "";
            boolean isAnnotated = false;
            if (f.isAnnotationPresent(KonfigerValue.class)) {
                isAnnotated = true;
                KonfigerValue annotation = f.getAnnotation(KonfigerValue.class);
                if (!annotation.value().isEmpty()) {
                    findKey = annotation.value();
                }
            }
            if (!isAnnotated && (matchGetKey == null ||
                    ((findKey = (String) matchGetKey.invoke(object, f.getName())) == null ||
                            findKey.equals("")))) {

                findKey = f.getName();
            }
            if (contains(findKey)) {
                f.setAccessible(true);
                if (f.getType() == String.class) {
                    f.set(object, get(findKey));

                } else if (f.getType() == Boolean.class || f.getType() == boolean.class) {
                    f.set(object, getBoolean(findKey));

                } else if (f.getType() == Long.class || f.getType() == long.class) {
                    f.set(object, getLong(findKey));

                } else if (f.getType() == Integer.class || f.getType() == int.class) {
                    f.set(object, getInt(findKey));

                } else if (f.getType() == Float.class || f.getType() == float.class) {
                    f.set(object, getFloat(findKey));

                } else if (f.getType() == Double.class || f.getType() == double.class) {
                    f.set(object, getDouble(findKey));

                }
            }
        }
        this.attachedResolveObj = object;
    }

    public void dissolve(Object object) throws IllegalAccessException, InvocationTargetException {
        Method matchGetKey = null;
        Method[] methods = object.getClass().getDeclaredMethods();
        Field[] fields = object.getClass().getDeclaredFields();
        for(Method method : methods){
            if (method.getName().equals("matchGetKey")) {
                matchGetKey = method;
                matchGetKey.setAccessible(true);
            }
        }
        for(Field f : fields){
            String findKey = "";
            boolean isAnnotated = false;
            if (f.isAnnotationPresent(KonfigerValue.class)) {
                isAnnotated = true;
                KonfigerValue annotation = f.getAnnotation(KonfigerValue.class);
                if (!annotation.value().isEmpty()) {
                    findKey = annotation.value();
                }
            }
            if (!isAnnotated && (matchGetKey == null ||
                    ((findKey = (String) matchGetKey.invoke(object, f.getName())) == null ||
                            findKey.equals("")))) {

                findKey = f.getName();
            }
            f.setAccessible(true); // kotlin is a knuckle head here
            Object v = f.get(object);
            this.konfigerObjects.put(findKey, v.toString());
        }
    }

    public Object detach() {
        Object tmpObj = attachedResolveObj;
        attachedResolveObj = null;
        return tmpObj;
    }

}
