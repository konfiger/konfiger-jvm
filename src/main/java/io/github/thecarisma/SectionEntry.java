package io.github.thecarisma;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 29-Nov-20 08:15 AM
 */
public class SectionEntry extends Entry {
    String section;
    String sectionComment;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSectionComment() {
        return sectionComment;
    }

    public void setSectionComment(String sectionComment) {
        this.sectionComment = sectionComment;
    }

    @Override
    public String toString() {
        return toString(true, '=');
    }

    public String toString(boolean addAssignmentSpacing, boolean commentsAsMultiline, char delimiter, String commentChar,
                           String multilineCommentKeyword, boolean addSpacePrePostCommentKeyword) {
        String entryValue = super.toString(addAssignmentSpacing, commentsAsMultiline, delimiter, commentChar, multilineCommentKeyword,
                addSpacePrePostCommentKeyword);
        if (section == null || section.isEmpty() || section.equals("__global__")) {
            return entryValue;
        }
        return String.format("%s[%s]\n%s",
                sectionComment == null || sectionComment.isEmpty() ? "" : commentChar +
                        (addSpacePrePostCommentKeyword ? " " : "") + sectionComment + "\n",
                section,
                entryValue);
    }
}
