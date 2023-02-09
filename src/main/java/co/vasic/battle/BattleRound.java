package co.vasic.battle;

import co.vasic.card.CardInterface;
import lombok.Builder;
import lombok.Getter;

@Builder
public class BattleRound implements BattleRoundInterface {
    @Getter
    int id;

    @Getter
    CardInterface cardA;

    @Getter
    CardInterface cardB;

    @Getter
    CardInterface winnerCard;
}
