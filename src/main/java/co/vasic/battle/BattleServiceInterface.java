package co.vasic.battle;

import co.vasic.card.CardInterface;
import co.vasic.user.UserInterface;

public interface BattleServiceInterface {

    BattleInterface createOrAddUserToBattle(UserInterface user);

    BattleInterface getBattle(int id);

    BattleInterface addBattle(BattleInterface battle);

    boolean addUserToBattle(UserInterface user, BattleInterface battle);

    boolean addBattleRound(BattleInterface battle, CardInterface card1, CardInterface card2, CardInterface winnerCard);

    boolean setWinnerForBattle(UserInterface winner, BattleInterface battle);

    BattleInterface waitForBattleToFinish(BattleInterface battle);

    boolean battle(BattleInterface battle);
}
