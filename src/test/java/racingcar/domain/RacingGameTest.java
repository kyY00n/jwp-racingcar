package racingcar.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import racingcar.WinnerJudgeImpl;
import racingcar.domain.car.Car;
import racingcar.domain.race.RacingGame;
import racingcar.domain.race.WinnerJudge;

class RacingGameTest {
    @Nested
    @DisplayName("이름 중복 검증기능")
    class DuplicatedNameTest {
        @Test
        @DisplayName("이름이 중복으로 입력되었을 때 예외 발생")
        void throwExceptionWhenDuplicateNameExists() {
            Assertions.assertThatThrownBy(
                            () -> new RacingGame(List.of("rosie", "hong", "rosie"), new WinnerJudgeImpl(), 10))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("우승자를 판별 기능은")
    class GetWinnersTest {
        private RacingGame race;
        private WinnerJudge mockWinnerJudge;


        @Test
        @DisplayName("우승자를 반드시 포함해서 알려준다")
        void shouldContainWinners() {
            // given
            mockWinnerJudge = cars -> List.of(new Car("rosie"));
            race = new RacingGame(List.of("rosie", "hong"), mockWinnerJudge, 10);

            // when
            List<Car> winners = race.getWinners();

            //then
            assertThat(getNamesOf(winners)).contains("rosie");
        }

        private List<String> getNamesOf(List<Car> winners) {
            return winners.stream().map(Car::getName).collect(Collectors.toList());
        }

        @Test
        @DisplayName("우승자가 아닌 사람을 포함하지 않고 알려준다.")
        void shouldNotContainNonWinners() {
            // given
            mockWinnerJudge = cars -> List.of(new Car("rosie"));
            race = new RacingGame(List.of("rosie", "hong"), mockWinnerJudge, 10);

            // when
            List<Car> winners = race.getWinners();

            //then
            assertThat(getNamesOf(winners)).doesNotContain("hong");
        }
    }
}