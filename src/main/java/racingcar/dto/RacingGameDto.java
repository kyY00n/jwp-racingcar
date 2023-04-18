package racingcar.dto;

import java.util.List;
import java.util.stream.Collectors;
import racingcar.domain.cars.RacingCar;
import racingcar.domain.game.RacingGame;

public class RacingGameDto {
    private final List<String> winnerNames;
    private final List<RacingCarDto> racingCars;

    private RacingGameDto(List<RacingCar> racingRacingCars, List<RacingCar> winnerNames) {
        this.winnerNames = convertToString(winnerNames);
        this.racingCars = convertToCarDto(racingRacingCars);
    }

    public static RacingGameDto from(RacingGame game) {
        List<RacingCar> racingCars = game.getRacingCars();
        List<RacingCar> winners = racingCars.stream()
                .filter(car -> game.isWinner(car))
                .collect(Collectors.toList());
        return new RacingGameDto(racingCars, winners);
    }

    private List<String> convertToString(List<RacingCar> winners) {
        return winners.stream()
                .map(RacingCar::getName)
                .collect(Collectors.toList());
    }

    private List<RacingCarDto> convertToCarDto(List<RacingCar> racingRacingCars) {
        return racingRacingCars.stream()
                .map(RacingCarDto::from)
                .collect(Collectors.toList());
    }

    public List<String> getWinnerNames() {
        return winnerNames;
    }

    public List<RacingCarDto> getRacingCars() {
        return racingCars;
    }

}
