package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface CourseDao extends JudgelsDao<CourseModel> {
    Optional<CourseModel> selectBySlug(String courseSlug);
    List<CourseModel> selectByGroup(String group);
}
