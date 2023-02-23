package co.vasic.card;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MonsterCard extends Card {
    @Getter
    CardType cardType = CardType.MONSTER;

    @Builder
    public MonsterCard(int id, String hashId, String name, float damage, ElementType elementType, int userId, boolean locked) {
        super(id, hashId, name, damage, elementType, userId, locked);
        this.cardType = CardType.MONSTER;
    }
}
