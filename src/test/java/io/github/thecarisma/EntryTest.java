package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 28-Nov-20 01:50 PM
 */
public class EntryTest {

    @Test
    public void testEntry() {
        Entry entry = new Entry();
        entry.addComment("The ip of the service");
        entry.setInlineComment("must be in the range of 127.0.0.* to 127.0.1.*");
        entry.setKey("ip");
        entry.setValue("127.0.0.1");

        Assert.assertEquals(entry.toString(), "; The ip of the service\n" +
                "ip = 127.0.0.1 ; must be in the range of 127.0.0.* to 127.0.1.*");

        Assert.assertEquals(entry.toString('=', false), ";The ip of the service\n" +
                "ip=127.0.0.1;must be in the range of 127.0.0.* to 127.0.1.*");

        Assert.assertEquals(entry.toString(':', true), "; The ip of the service\n" +
                "ip : 127.0.0.1 ; must be in the range of 127.0.0.* to 127.0.1.*");
    }

    @Test
    public void testSectionEntry() {
        SectionEntry entry = new SectionEntry();
        entry.addComment("The ip of the service");
        entry.setInlineComment("must be in the range of 127.0.0.* to 127.0.1.*");
        entry.setKey("ip");
        entry.setValue("127.0.0.1");
        entry.setSection("service");
        entry.setSectionComment("The service configurations");

        Assert.assertEquals(entry.toString(), "; The service configurations\n" +
                "[service]\n" +
                "; The ip of the service\n" +
                "ip = 127.0.0.1 ; must be in the range of 127.0.0.* to 127.0.1.*");

        Assert.assertEquals(entry.toString('=', false), ";The service configurations\n" +
                "[service]\n" +
                ";The ip of the service\n" +
                "ip=127.0.0.1;must be in the range of 127.0.0.* to 127.0.1.*");

        Assert.assertEquals(entry.toString(':', true), "; The service configurations\n" +
                "[service]\n" +
                "; The ip of the service\n" +
                "ip : 127.0.0.1 ; must be in the range of 127.0.0.* to 127.0.1.*");
    }

    @Test
    public void testCommentOnlyEntry() {
        Entry entry = new Entry();
        entry.addComment("Empty comment only");

        Entry entry2 = new Entry();
        entry2.setKey("#a kind of comment");

        Entry entry3 = new Entry();
        entry3.setValue("; valued kind of comment");

        Assert.assertEquals(entry.toString(), "; Empty comment only");
        Assert.assertEquals(entry2.toString(), "#a kind of comment");
        Assert.assertEquals(entry3.toString(), "; valued kind of comment");
    }

    @Test
    public void testKeyOnlyEntry() {
        Entry entry1 = new Entry();
        entry1.setKey("key_without_value");

        Entry entry2 = new Entry();
        entry2.setKey("key with empty value");
        entry2.setValue("");

        Assert.assertEquals(entry1.toString(), "key_without_value");
        Assert.assertEquals(entry2.toString(), "key with empty value = ");
    }
}
