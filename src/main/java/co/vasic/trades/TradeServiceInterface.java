package co.vasic.trades;

import co.vasic.card.CardInterface;
import co.vasic.card.CardType;

import java.util.List;

public interface TradeServiceInterface {
    TradeInterface getTrade(String id);

    List<TradeInterface> getTrades();

    TradeInterface addTrade(CardInterface card, String tradeId, CardType cardType, int minimumDamage);

    boolean deleteTrade(String id);

    TradeInterface addOffer(TradeInterface trade, CardInterface card);

    TradeInterface acceptTrade(TradeInterface trade);
}
