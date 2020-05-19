package io.github.thecarisma.java;

import io.github.thecarisma.KonfigerFieldsExposer;
import org.junit.Assert;
import org.junit.Test;

public class TestUtil {

    @Test
    public void Test_Check_Escape_And_Unescape_Seperator() {
        String actualStr = "\\,Hello¬W\n-\t-\torld";
        String t1 = KonfigerFieldsExposer.getKonfigerUtil_escapeString(actualStr, '¬');
        String t2 = KonfigerFieldsExposer.getKonfigerUtil_escapeString(actualStr);

        Assert.assertNotEquals(actualStr, t1);
        Assert.assertEquals(t1, "\\,Hello/¬W\n-\t-\torld");
        Assert.assertNotEquals(t1, KonfigerFieldsExposer.getKonfigerUtil_unEscapeString(t1, '¬'));
        Assert.assertNotEquals(actualStr, KonfigerFieldsExposer.getKonfigerUtil_unEscapeString(t1));
        Assert.assertEquals(KonfigerFieldsExposer.getKonfigerUtil_unEscapeString(t1, '¬'), actualStr);

        Assert.assertNotEquals(t1, t2);
        Assert.assertEquals(t2, "\\,Hello¬W\n-\t-\torld");
        Assert.assertNotEquals(t2, KonfigerFieldsExposer.getKonfigerUtil_unEscapeString(t1));
        Assert.assertEquals(actualStr, KonfigerFieldsExposer.getKonfigerUtil_unEscapeString(t2));
        Assert.assertEquals(KonfigerFieldsExposer.getKonfigerUtil_unEscapeString(t1, '¬'), actualStr);
    }

}
