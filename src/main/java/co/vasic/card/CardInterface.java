package co.vasic.card;

public interface CardInterface {
    int getId();

    String getHashId();

    String getName();

    float getDamage();

    float calculateDamage(CardInterface card);

    CardType getCardType();

    ElementType getElementType();

    boolean isLocked();

    boolean winsAgainst(CardInterface card);
}
