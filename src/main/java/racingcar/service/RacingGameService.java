package racingcar.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import racingcar.domain.game.NumberGenerator;
import racingcar.domain.game.RacingGame;
import racingcar.dto.RacingGameDto;
import racingcar.repository.RacingGameRepository;

@Service
public class RacingGameService {

    private final RacingGameRepository racingGameRepository;
    private final NumberGenerator numberGenerator;

    public RacingGameService(RacingGameRepository racingGameRepository, NumberGenerator numberGenerator) {
        this.racingGameRepository = racingGameRepository;
        this.numberGenerator = numberGenerator;
    }

    public RacingGameDto play(int trialCount, List<String> names) {
        RacingGame game = RacingGame.of(trialCount, names);
        game.play(numberGenerator);
        RacingGame createdGame = racingGameRepository.create(game);
        return RacingGameDto.from(createdGame);
    }

    public List<RacingGameDto> readGameHistory() {
        return racingGameRepository.findAll().stream()
                .sorted((game, other) -> other.getPlayTime().compareTo(game.getPlayTime()))
                .map(RacingGameDto::from)
                .collect(Collectors.toList());
    }
}
