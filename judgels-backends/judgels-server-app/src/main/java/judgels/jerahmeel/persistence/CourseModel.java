package judgels.jerahmeel.persistence;

import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jerahmeel_course")
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "updatedAt")})
@JidPrefix("COUR")
public class CourseModel extends JudgelsModel {
    @Column(unique = true)
    public String slug;

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;

    @ElementCollection
    @CollectionTable(name = "jerahmeel_course_group", joinColumns = @JoinColumn(name = "courseId"))
    @Column(name = "groupItem")
    public List<String> groups;
}
