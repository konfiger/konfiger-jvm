package io.github.thecarisma

import org.junit.Assert
import org.junit.Test

class TestUtil_Scala {
    @Test def Test_Check_Escape_And_Unescape_Seperator(): Unit = {
        val actualStr = "\\,Hello¬W\n-\t-\torld"
        val t1 = KonfigerUtil.escapeString(actualStr, '¬')
        val t2 = KonfigerUtil.escapeString(actualStr)
        Assert.assertNotEquals(actualStr, t1)
        Assert.assertEquals(t1, "\\,Hello^¬W\n-\t-\torld")
        Assert.assertNotEquals(t1, KonfigerUtil.unEscapeString(t1, '¬'))
        Assert.assertNotEquals(actualStr, KonfigerUtil.unEscapeString(t1))
        Assert.assertEquals(KonfigerUtil.unEscapeString(t1, '¬'), actualStr)
        Assert.assertNotEquals(t1, t2)
        Assert.assertEquals(t2, "\\,Hello¬W\n-\t-\torld")
        Assert.assertNotEquals(t2, KonfigerUtil.unEscapeString(t1))
        Assert.assertEquals(actualStr, KonfigerUtil.unEscapeString(t2))
        Assert.assertEquals(KonfigerUtil.unEscapeString(t1, '¬'), actualStr)
    }
}