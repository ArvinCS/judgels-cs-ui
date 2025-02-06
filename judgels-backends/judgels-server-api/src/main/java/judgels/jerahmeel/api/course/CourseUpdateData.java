package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourseUpdateData.class)
public interface CourseUpdateData {
    Optional<String> getSlug();
    Optional<String> getName();
    Optional<String> getDescription();
    Optional<List<String>> getGroups();

    class Builder extends ImmutableCourseUpdateData.Builder {}
}
