package co.vasic.card;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MonsterCard extends Card {
    @Getter
    CardType cardType = CardType.MONSTER;

    @Builder
    public MonsterCard(int id, String hashId, String name, float damage, ElementType elementType, boolean locked) {
        super(id, hashId, name, damage, elementType, locked);
        this.cardType = CardType.MONSTER;
    }
}
