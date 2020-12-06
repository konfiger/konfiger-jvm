package io.github.thecarisma;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 06-Dec-20 12:55 PM
 */
public class TestSection_Java {

    @Test
    public void testSectionWithNoTitle() {
        Section section = new Section();

        Assert.assertTrue(section.toString().isEmpty());
    }

    @Test
    public void testSectionWithTitle() {
        Section section = new Section();
        section.setTitle("Profile");

        Assert.assertEquals(section.toString(), "[Profile]\n");
    }

    @Test
    public void testSectionWithComment() {
        Section section = new Section();
        section.setTitle("Profile");
        Entry.Comment comment = new Entry.Comment();
        comment.setValue("This is the profile configuration");
        section.addComment(comment);

        Assert.assertEquals(section.toString(), ";This is the profile configuration\n" +
                                                      "[Profile]\n");
    }

    @Test
    public void testSectionWithMultilineComment() {
        Section section = new Section();
        section.setTitle("Profile");
        Entry.Comment comment = new Entry.Comment();
        comment.setValue("The profile must contain \n - name \n - email \n - picture \n - address");
        comment.setMultiline(true);
        section.addComment(comment);

        Assert.assertEquals(section.toString(), "\"\"\"The profile must contain \n" +
                " - name \n" +
                " - email \n" +
                " - picture \n" +
                " - address\"\"\"\n" +
                "[Profile]\n");
    }

    @Test
    public void testSectionWithComments() {
        Section section = new Section();
        section.setTitle("Profile");
        Entry.Comment comment1 = new Entry.Comment();
        comment1.setValue("This is the profile configuration");
        Entry.Comment comment2 = new Entry.Comment();
        comment2.setValue("The profile must contain \n - name \n - email \n - picture \n - address");
        comment2.setMultiline(true);
        section.addComment(comment2);
        section.addComment(comment1);

        Assert.assertEquals(section.toString(), "\"\"\"The profile must contain \n" +
                " - name \n" +
                " - email \n" +
                " - picture \n" +
                " - address\"\"\"\n" +
                ";This is the profile configuration\n" +
                "[Profile]\n");
    }

    @Test
    public void testSectionWithEntries() {
        Section section = new Section();
        section.setTitle("Profile");

        Entry entry1 = new Entry();
        entry1.setKey("name");
        entry1.addValue("thecarisma");

        Entry entry2 = new Entry();
        entry2.setKey("email");
        entry2.addValue("xxxxxxxxx@xxxxx.com");

        section.put(entry1);
        section.put(entry2);

        System.out.println(section);
        Assert.assertEquals(section.toString(), "[Profile]\n" +
                "name=thecarisma\n" +
                "email=xxxxxxxxx@xxxxx.com\n");
    }

    @Test
    public void testSectionWithSubSection() {
        Section section = new Section();
        section.setTitle("Profile");

        Section subSection1 = new Section();
        subSection1.setTitle("Location");

        Section nestedSubSection1 = new Section();
        nestedSubSection1.setTitle("GPS");
        subSection1.putSection(nestedSubSection1);

        Section subSection2 = new Section();
        subSection2.setTitle("Aliases");

        section.putSection(subSection1);
        section.putSection(subSection2);

        System.out.println(section.toString());
        System.out.println(section.toString(new Builder()
                .indentSubSection()));
        System.out.println(section.toString(new Builder()
                .indentSubSection()
                .writeSubSectionTitleAsNested()));
    }

    @Test
    public void testSectionWithSubSectionAndEntries() {
        Section section = new Section();
        section.setTitle("Profile");

        Section subSection1 = new Section();
        subSection1.setTitle("Location");

        Section nestedSubSection1 = new Section();
        nestedSubSection1.setTitle("GPS");
        subSection1.putSection(nestedSubSection1);

        Section subSection2 = new Section();
        subSection2.setTitle("Aliases");

        section.putSection(subSection1);
        section.putSection(subSection2);

        Entry entry1 = new Entry();
        entry1.setKey("name");
        entry1.addValue("thecarisma");

        Entry entry2 = new Entry();
        entry2.setKey("email");
        entry2.addValue("xxxxxxxxx@xxxxx.com");

        section.put(entry1);
        section.put(entry2);
        nestedSubSection1.put(entry1);
        nestedSubSection1.put(entry2);
        subSection1.put(entry1);
        subSection1.put(entry2);
        subSection2.put(entry1);
        subSection2.put(entry2);

        System.out.println(section.toString());
        System.out.println(section.toString(new Builder()
                .indentSubSection()));
        System.out.println(section.toString(new Builder()
                .indentSubSection()
                .addSeparatorBeforeSection()
                .writeSubSectionTitleAsNested()));
    }

    @Test
    public void testSectionWithSubSectionAndEntriesAndComments() {
        Section section = new Section();
        section.setTitle("Profile");
        Entry.Comment comment1 = new Entry.Comment();
        comment1.setValue("This is the profile configuration");
        Entry.Comment comment2 = new Entry.Comment();
        comment2.setValue("The profile must contain \n - name \n - email \n - picture \n - address");
        comment2.setMultiline(true);
        section.addComment(comment2);
        section.addComment(comment1);

        Entry entry1 = new Entry();
        entry1.setKey("name");
        entry1.addValue("thecarisma");
        Entry entry2 = new Entry();
        entry2.setKey("email");
        entry2.addValue("xxxxxxxxx@xxxxx.com");
        section.put(entry1);
        section.put(entry2);

        Section subSection1 = new Section();
        subSection1.setTitle("Location");
        subSection1.addComment("The location of the user including postal code");

        Section nestedSubSection1 = new Section();
        nestedSubSection1.setTitle("GPS");
        subSection1.putSection(nestedSubSection1);
        Entry.Comment comment3 = new Entry.Comment();
        comment3.setValue("GPS co-ordinates \nlatitude \nlongitude");
        comment3.setMultiline(true);
        nestedSubSection1.addComment(comment3);

        Entry entry3 = new Entry();
        entry3.setKey("longitude");
        entry3.addValue("1.23536356");
        Entry entry4 = new Entry();
        entry4.setKey("latitude");
        entry4.addValue("0.5635464");
        nestedSubSection1.put(entry3);
        nestedSubSection1.put(entry4);

        Section subSection2 = new Section();
        subSection2.setTitle("Aliases");
        subSection2.addComment("User nicknames");

        section.putSection(subSection1);
        section.putSection(subSection2);

        System.out.println(section.toString());
        System.out.println(section.toString(new Builder()
                .indentSubSection()));
        System.out.println(section.toString(new Builder()
                .indentSubSection()
                .addSeparatorBeforeSection()
                .withSpaceAfterCommentKeyword()
                .writeSubSectionTitleAsNested()));
    }

}
