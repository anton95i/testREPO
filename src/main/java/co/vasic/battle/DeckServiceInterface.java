package co.vasic.battle;

import co.vasic.card.CardInterface;
import co.vasic.user.UserInterface;

import java.util.List;

public interface DeckServiceInterface {
    List<CardInterface> getDeck(UserInterface user);

    boolean addCardsWithIdsToDeck(int[] ids, UserInterface user);

    boolean addCardToDeck(CardInterface card, UserInterface user);

    boolean clearDeck(UserInterface user);
}
