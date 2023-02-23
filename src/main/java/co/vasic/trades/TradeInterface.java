package co.vasic.trades;

import co.vasic.card.CardInterface;
import co.vasic.card.CardType;

public interface TradeInterface {

    int getId();

    String getTradeId();

    CardInterface getCardA();

    CardInterface getCardB();

    CardType getCardType();

    int getMinimumDamage();
}
