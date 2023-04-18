package racingcar.web;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import racingcar.domain.RacingGameService;
import racingcar.dto.RacingGameDto;

@RestController
public class RacingGameController {

    private final RacingGameService racingGameService;

    public RacingGameController(RacingGameService racingGameService) {
        this.racingGameService = racingGameService;
    }

    @PostMapping("/plays")
    public ResponseEntity<RacingGameResponse> play(@Validated @RequestBody RacingGameRequest request) {
        RacingGameDto racingGameResult = racingGameService.play(request.getCount(), request.readSplitNames());
        return ResponseEntity.ok(RacingGameResponse.from(racingGameResult));
    }

}
