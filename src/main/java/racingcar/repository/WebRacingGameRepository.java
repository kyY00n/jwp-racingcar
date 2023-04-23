package racingcar.repository;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import racingcar.dao.CarRecordWithGameHistoryDto;
import racingcar.dao.RacingCarRecordDao;
import racingcar.dao.RacingGameHistoryDao;
import racingcar.domain.cars.RacingCar;
import racingcar.domain.game.RacingGame;

@Repository
public class WebRacingGameRepository implements RacingGameRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RacingGameHistoryDao racingGameHistoryDao;
    private final RacingCarRecordDao racingCarRecordDao;

    public WebRacingGameRepository(JdbcTemplate jdbcTemplate, RacingGameHistoryDao racingGameHistoryDao,
                                   RacingCarRecordDao racingCarRecordDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.racingGameHistoryDao = racingGameHistoryDao;
        this.racingCarRecordDao = racingCarRecordDao;
    }

    @Override
    public RacingGame create(RacingGame racingGame) {
        Long historyId = racingGameHistoryDao.insert(racingGame.getTrialCount(), LocalDateTime.now());
        List<RacingCar> createdCars = getCreatedCars(racingGame, historyId);
        return new RacingGame(historyId, racingGame.getTrialCount(), createdCars, racingGame.getPlayTime());
    }

    private List<RacingCar> getCreatedCars(RacingGame racingGame, Long historyId) {
        return racingGame.getRacingCars().stream()
                .map(racingCar -> {
                    Long createdId = racingCarRecordDao.insert(historyId, racingCar, racingGame.isWinner(racingCar));
                    return new RacingCar(createdId, racingCar.getName(), racingCar.getPosition());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RacingGame> findAll() {
        List<CarRecordWithGameHistoryDto> carRecordWithGameHistory = jdbcTemplate.query(
                "SELECT r.id as game_id, r.play_time as play_time, r.trial_count as trial_count, c.id as id, c.name as name, c.position as `position` FROM racing_history r INNER JOIN car_record c ON (r.id=c.history_id)",
                (result, rowNum) -> new CarRecordWithGameHistoryDto(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getInt("position"),
                        result.getLong("game_id"),
                        result.getTimestamp("play_time").toLocalDateTime(),
                        result.getInt("trial_count")
                )
        );
        return carRecordWithGameHistory.stream()
                .collect(collectingAndThen(groupingBy(CarRecordWithGameHistoryDto::getGameId),
                                map -> map.entrySet().stream()
                                        .map(racingGameMapper())
                                        .collect(Collectors.toList())
                        )
                );
    }

    private Function<Entry<Long, List<CarRecordWithGameHistoryDto>>, RacingGame> racingGameMapper() {
        return historyIdToCarRecords -> {
            int trialCount = historyIdToCarRecords.getValue().get(0).getTrialCount();
            List<RacingCar> racingCars = historyIdToCarRecords.getValue().stream()
                    .map(c -> new RacingCar(c.getId(), c.getName(), c.getPosition()))
                    .collect(Collectors.toList());
            LocalDateTime playTime = historyIdToCarRecords.getValue().get(0).getPlayTime();
            return new RacingGame(historyIdToCarRecords.getKey(), trialCount, racingCars, playTime);
        };
    }

}
