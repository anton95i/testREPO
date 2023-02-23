package co.vasic.trades;

import co.vasic.card.CardInterface;
import co.vasic.card.CardType;
import lombok.Builder;
import lombok.Getter;

@Builder
public class Trade implements TradeInterface {

    @Getter
    int id;

    @Getter
    String tradeId;

    @Getter
    CardInterface cardA;

    @Getter
    CardInterface cardB;

    @Getter
    CardType cardType;

    @Getter
    int minimumDamage;
}
