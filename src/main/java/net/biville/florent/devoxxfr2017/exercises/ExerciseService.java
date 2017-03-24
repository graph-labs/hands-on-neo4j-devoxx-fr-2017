package net.biville.florent.devoxxfr2017.exercises;

import net.biville.florent.devoxxfr2017.logging.SimpleLogger;
import net.biville.florent.devoxxfr2017.user.UserSession;
import net.biville.florent.devoxxfr2017.user.UserSessionRepository;

import java.util.List;
import java.util.Map;

public class ExerciseService {

    private static final SimpleLogger LOGGER = new SimpleLogger();

    private final ExerciseValidator exerciseValidator;
    private final ExerciseRepository exerciseRepository;
    private final UserSessionRepository userSessionRepository;

    public ExerciseService(ExerciseValidator exerciseValidator,
                           ExerciseRepository exerciseRepository,
                           UserSessionRepository userSessionRepository) {

        this.exerciseValidator = exerciseValidator;
        this.exerciseRepository = exerciseRepository;
        this.userSessionRepository = userSessionRepository;
    }

    public void checkAnswer(UserSession session, Exercise currentExercise, String query) {
        List<Map<String, Object>> actualResult = exerciseRepository.computeResult(query);
        ExerciseValidation validation = exerciseValidator.validate(actualResult, currentExercise);
        if (!validation.isSuccessful()) {
            LOGGER.error(validation.getReport());
            return;
        }

        LOGGER.log(validation.getReport());
        userSessionRepository.saveProgression(session, currentExercise);
        exerciseRepository.getNextExercise(currentExercise)
                .ifPresent(next -> LOGGER.log(next.getStatement()));
    }
}
