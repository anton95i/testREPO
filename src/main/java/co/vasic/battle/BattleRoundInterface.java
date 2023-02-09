package co.vasic.battle;

import co.vasic.card.CardInterface;

public interface BattleRoundInterface {
    int getId();

    CardInterface getCardA();

    CardInterface getCardB();

    CardInterface getWinnerCard();
}
