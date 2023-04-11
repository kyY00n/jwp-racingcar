package racingcar.domain.race;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import racingcar.domain.car.Car;

public class RacingCars {
    private final List<Car> cars = new ArrayList<>();

    public List<Car> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public void add(Car car) {
        List<String> carNames = cars.stream().map(Car::getName).collect(Collectors.toList());
        if (carNames.contains(car.getName())) {
            throw new IllegalArgumentException("자동차의 이름은 중복일 수 없습니다.");
        }
        cars.add(car);
    }

    public void moveCars() {
        for (Car car : cars) {
            car.moveDependingOn();
        }
    }
}
