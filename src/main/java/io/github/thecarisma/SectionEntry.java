package io.github.thecarisma;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 29-Nov-20 08:15 AM
 */
public class SectionEntry extends Entry {
    String section;
    List<Comment> sectionComment = new ArrayList<>();

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<Comment> getSectionComment() {
        return sectionComment;
    }

    public void setSectionComment(List<Comment> sectionComment) {
        this.sectionComment = sectionComment;
    }

    public void addSectionComment(Comment comment) {
        this.sectionComment.add(comment);
    }

    public void addSectionComment(String commentValue) {
        Comment comment = new Comment();
        comment.setCommentKeyword(";");
        comment.setValue(commentValue);
        this.sectionComment.add(comment);
    }

    @Override
    public String toString() {
        return toString(true, '=');
    }

    public String toString(boolean addAssignmentSpacing, char delimiter, boolean addSpaceBeforeCommentKeyword) {
        return toString(addAssignmentSpacing, delimiter, addSpaceBeforeCommentKeyword, true);
    }

    public String toString(boolean addAssignmentSpacing, char delimiter, boolean addSpaceBeforeCommentKeyword,
                           boolean renderSection) {
        String entryValue = super.toString(addAssignmentSpacing, delimiter, addSpaceBeforeCommentKeyword);
        if (section == null || section.isEmpty() || section.equals("__global__") || !renderSection) {
            return entryValue;
        }
        String sectionComment_ = "";
        if (sectionComment != null && !sectionComment.isEmpty()) {
            for (int index = 0; index < sectionComment.size(); ++index) {
                sectionComment_ += sectionComment.get(index).toString();
                if (index <= sectionComment.size()-1) {
                    sectionComment_ += "\n";
                }
            }
        }
        return String.format("%s[%s]\n%s",
                sectionComment_,
                section,
                entryValue);
    }
}
