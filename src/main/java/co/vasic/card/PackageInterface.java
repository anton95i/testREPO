package co.vasic.card;

import java.util.List;

public interface PackageInterface {
    int getId();

    List<CardInterface> getCards();

    int getPrice();

    String getName();
}
