package co.vasic.card;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpellCard extends Card {
    @Getter
    CardType cardType = CardType.SPELL;

    @Builder
    public SpellCard(int id, String hashId, String name, float damage, ElementType elementType, CardType cardType, boolean locked) {
        super(id, hashId, name, damage, elementType, locked);
        this.cardType = CardType.SPELL;
    }
}
