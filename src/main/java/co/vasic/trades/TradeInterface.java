package co.vasic.trades;

import co.vasic.card.CardInterface;

public interface TradeInterface {

    int getId();

    CardInterface getCardA();

    CardInterface getCardB();

    int getCoins();

    boolean isAccepted();
}
