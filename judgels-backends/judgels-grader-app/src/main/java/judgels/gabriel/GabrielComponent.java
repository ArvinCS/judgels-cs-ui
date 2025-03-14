package judgels.gabriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.gabriel.cache.CacheModule;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.grading.GradingRequestPoller;
import judgels.gabriel.isolate.IsolateModule;
import judgels.gabriel.postgrelate.PostgrelateModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.service.JudgelsModule;

@Component(modules = {
        // Judgels service
        JudgelsModule.class,
        JudgelsGraderModule.class,

        // 3rd parties
        RabbitMQModule.class,
        IsolateModule.class,
        PostgrelateModule.class,

        // Features
        GradingModule.class,
        CacheModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();
}
