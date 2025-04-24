package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonTypeName("SCOREBOARD")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableScoreboardModuleConfig.class)
public interface ScoreboardModuleConfig extends ModuleConfig {
    ScoreboardModuleConfig DEFAULT = new Builder()
            .isIncognitoScoreboard(false)
            .topParticipantsCount(-1)
            .build();

    boolean getIsIncognitoScoreboard();
    @Value.Default
    default int getTopParticipantsCount() {
        return -1;
    }

    class Builder extends ImmutableScoreboardModuleConfig.Builder {}
}
