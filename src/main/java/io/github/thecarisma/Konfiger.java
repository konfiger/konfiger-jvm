package io.github.thecarisma;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
public class Konfiger {

    public static int MAX_CAPACITY = 10000000;
    public static String GLOBAL_SECTION_NAME = "__global__";
    public static String DEFAULT_TAB = "    ";
    final KonfigerStream stream;
    private final boolean lazyLoad;
    Map<String, Section> sections = new LinkedHashMap<>();
    Object[] prevCachedObject = {"", null};
    Object[] currentCachedObject = {"", null};
    private boolean loadingEnds = false;
    private boolean changesOccur = true;
    private String stringValue = "";
    private Object attachedResolveObj;
    List<EntryListener> entryListeners = new ArrayList<>();

    public Konfiger(Builder builder) {
        this(builder.build(), true);
    }

    public Konfiger(Builder builder, boolean lazyLoad) {
        this(builder.build(), lazyLoad);
    }

    public Konfiger(KonfigerStream konfigerStream) {
        this(konfigerStream, true);
    }

    public Konfiger(KonfigerStream konfigerStream, boolean lazyLoad) {
        this.stream = konfigerStream;
        this.lazyLoad = lazyLoad;

        if (!this.lazyLoad) {
            this.loadAllEntries();
        }
    }

    public List<EntryListener> getEntryListeners() {
        return entryListeners;
    }

    public void addEntryListeners(EntryListener entryListener) {
        this.entryListeners.add(entryListener);
    }

    public boolean removeEntryListeners(EntryListener entryListener) {
        return this.entryListeners.remove(entryListener);
    }

    public boolean contains(String key) {
        for (Section section : sections.values()) {
            if (section.contains(key)) {
                return true;
            }
        }
        if (!loadingEnds && this.lazyLoad) {
            try {
                while (stream.hasNext()) {
                    Entry obj =  stream.next();
                    put(obj.section, obj);
                    changesOccur = true;
                    if (obj.getKey().equals(key)) {
                        return true;
                    }
                }
                loadingEnds = true;
            } catch (InvalidFileException | InvalidEntryException ex) {
                if (!stream.builder.errTolerance) {
                    ex.printStackTrace();
                }
                return false;
            }
        }
        return false;
    }

    public void clear() {
        changesOccur = true;
        sections.clear();
        clearCache();
    }

    public void clearCache() {
        prevCachedObject[0] = "";
        prevCachedObject[1] = null;
        currentCachedObject[0] = "";
        currentCachedObject[1] = null;
    }

    public int size() {
        if (!loadingEnds && this.lazyLoad) {
            loadAllEntries();
        }
        return lazySize();
    }

    public int lazySize() {
        int size = 0;
        for (Section section : sections.values()) {
            size += section.size();
        }
        return size;
    }

    public int sectionSize() {
        return sections.size();
    }

    public int lazySectionSize() {
        if (!loadingEnds && this.lazyLoad) {
            loadAllEntries();
        }
        return sections.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        if (changesOccur) {
            if (lazyLoad && !loadingEnds) {
                try {
                    loadAllEntries();
                } catch (InvalidFileException | InvalidEntryException ex) {
                    if (!stream.builder.errTolerance) {
                        ex.printStackTrace();
                    }
                }
            }
            stringValue = "";
            for (Section section : sections.values()) {
                stringValue += section.toString(stream.builder);
            }
        }
        return stringValue;
    }

    public void save() {
        save(stream.builder.filePath);
    }

    public void save(String filePath) {
        stringValue = toString();
        File file = new File(filePath);
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Unable to create the file: " + file.getAbsolutePath());
            }
            try (PrintWriter out = new PrintWriter(file)) {
                out.write(stringValue);
            }
        } catch (IOException e) {
            throw new InvalidFileException(e);
        }
    }

    public void appendString(String rawString) {
        appendString(rawString, stream.builder);
    }

    public void appendString(String rawString, Builder builder) {
        KonfigerStream _stream = new KonfigerStream(builder);
        while (_stream.hasNext()) {
            Entry obj = _stream.next();
            put(obj.section, obj);
        }
        changesOccur = true;
    }

    public void appendFile(File file) {
        appendFile(file, stream.builder);
    }

    public void appendFile(File file, Builder builder) {
        KonfigerStream _stream = new KonfigerStream(builder);
        while (_stream.hasNext()) {
            Entry obj = _stream.next();
            put(obj.section, obj);
        }
        changesOccur = true;
    }

    private void loadAllEntries() throws InvalidFileException, InvalidEntryException {
        if (loadingEnds) {
            return;
        }
        while (stream.hasNext()) {
            Entry obj = stream.next();
            put(obj.section, obj);
        }
        this.loadingEnds = true;
    }

    private void shiftCache(String key, Section section) {
        prevCachedObject[0] = currentCachedObject[0];
        prevCachedObject[1] = currentCachedObject[1];
        currentCachedObject[0] = key;
        currentCachedObject[1] = section;
    }

    public void resolve(Object object) throws IllegalAccessException, InvocationTargetException {
        /*Method matchGetKey = null;
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
            if (f.isAnnotationPresent(EntryKey.class)) {
                isAnnotated = true;
                EntryKey annotation = f.getAnnotation(EntryKey.class);
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
        }*/
        this.attachedResolveObj = object;
    }

    public void dissolve(Object object) throws IllegalAccessException, InvocationTargetException {
        /*Method matchGetKey = null;
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
            if (f.isAnnotationPresent(EntryKey.class)) {
                isAnnotated = true;
                EntryKey annotation = f.getAnnotation(EntryKey.class);
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
        }*/
    }

    public void attach(Object object) {
        attachedResolveObj = object;
    }

    public Object detach() {
        Object tmpObj = attachedResolveObj;
        attachedResolveObj = null;
        return tmpObj;
    }

    public void put(String section, Entry entry) {
        if (section == null || section.isEmpty()) {
            section = GLOBAL_SECTION_NAME;
        }
        String[] nestedSections = section.split(Pattern.quote(this.stream.builder.subSectionDelimiter));
        for (int index = 0; index < nestedSections.length; ++index) {
            Section section1 = sections.get(section);
            if (section1 == null) {
                section1 = new Section(this);
                sections.put(section, section1);
            }
            section1.put(entry);
        }
    }

    public Section g() {
        Section global = getSection(GLOBAL_SECTION_NAME);
        if (global == null) {
            global = new Section(this);
            sections.put(GLOBAL_SECTION_NAME, global);
        }
        return global;
    }

    public Section getSection(String title) {
        if (lazyLoad && !loadingEnds && !sections.containsKey(title)) {
            loadAllEntries();
        }
        return sections.get(title);
    }


}
