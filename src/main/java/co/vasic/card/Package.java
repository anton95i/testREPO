package co.vasic.card;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public class Package implements PackageInterface {
    @Getter
    int id;

    @Getter
    transient List<CardInterface> cards;

    @Getter
    @Builder.Default
    int price = 5;

    @Getter
    String name;
}
