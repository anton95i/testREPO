package co.vasic.trades;

import co.vasic.card.CardInterface;
import co.vasic.card.CardService;
import co.vasic.card.CardType;
import co.vasic.database.DatabaseService;
import co.vasic.user.User;
import co.vasic.user.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TradeService implements TradeServiceInterface {
    private static TradeService instance;
    private final CardService cardService;
    private final UserService userService;

    private TradeService() {
        cardService = CardService.getInstance();
        userService = UserService.getInstance();
    }

    public static TradeService getInstance() {
        if (TradeService.instance == null) {
            TradeService.instance = new TradeService();
        }
        return TradeService.instance;
    }

    @Override
    public TradeInterface getTrade(String id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            // PreparedStatement ps = conn.prepareStatement("SELECT id, card_a, card_b,
            // coins, accepted FROM trades WHERE id=?;");
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, tradeId, card_a, card_b, card_type, minimum_damage FROM trades WHERE tradeId=?;");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                CardInterface cardA = cardService.getCard(rs.getInt(3));
                CardInterface cardB = cardService.getCard(rs.getInt(4));

                Trade trade = Trade.builder()
                        .id(rs.getInt(1))
                        .tradeId(rs.getString(2))
                        .cardA(cardA)
                        .cardB(cardB)
                        .cardType(CardType.valueOf(rs.getString(5)))
                        .minimumDamage(rs.getInt(6))
                        .build();

                rs.close();
                ps.close();
                conn.close();

                return trade;
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<TradeInterface> getTrades() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT tradeId FROM trades;");
            ResultSet rs = ps.executeQuery();

            List<TradeInterface> trades = new ArrayList<>();
            while (rs.next()) {
                trades.add(this.getTrade(rs.getString(1)));
            }

            rs.close();
            ps.close();
            conn.close();

            return trades;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TradeInterface addTrade(CardInterface card, String tradeId, CardType cardType, int minimumDamage) {
        if (!card.isLocked()) {
            try {
                Connection conn = DatabaseService.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO trades(tradeId, card_a, card_type, minimum_damage) VALUES(?, ?, ?, ?);",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, tradeId);
                ps.setInt(2, card.getId());
                ps.setString(3, cardType.name());
                ps.setInt(4, minimumDamage);

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    return null;
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String id = generatedKeys.getString(2);
                        ps.close();
                        conn.close();

                        cardService.lockCard(card, true);

                        return this.getTrade(id);
                    }
                }
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean deleteTrade(String id) {
        // Unlock cards
        Trade trade = (Trade) this.getTrade(id);
        if (trade != null) {
            CardInterface cardA = trade.getCardA();
            if (cardA != null) {
                cardService.lockCard(cardA, false);
            }
            CardInterface cardB = trade.getCardB();
            if (cardB != null) {
                cardService.lockCard(cardB, false);
            }
        }

        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM trades WHERE id = ?;");
            ps.setInt(1, trade.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public TradeInterface addOffer(TradeInterface trade, CardInterface card) {
        if (!card.isLocked()) {
            try {
                Connection conn = DatabaseService.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement("UPDATE trades SET card_b = ? WHERE id = ?;");
                ps.setInt(1, card.getId());
                ps.setInt(2, trade.getId());

                int affectedRows = ps.executeUpdate();

                ps.close();
                conn.close();

                if (affectedRows > 0) {
                    // return this.getTrade(trade.getTradeId());

                    try {
                        Connection conn2 = DatabaseService.getInstance().getConnection();
                        PreparedStatement ps2 = conn2.prepareStatement("SELECT id, tradeId, card_a, card_b, card_type, minimum_damage FROM trades WHERE id=?;");
                        ps2.setInt(1, trade.getId());
                        ResultSet rs2 = ps2.executeQuery();

                        if (rs2.next()) {
                            User userA = (User) userService.getUser(trade.getCardA().getUserId());
                            User userB = (User) userService.getUser(card.getUserId());

                            System.out.println("Users found");

                            cardService.addCardToUser(trade.getCardA(), userB);
                            cardService.addCardToUser(card, userA);
                            //cardService.lockCard(trade.getCardB(), false);
                            //cardService.lockCard(trade.getCardA(), false);

                            System.out.println("Cards added to users");

                            ps2 = conn2.prepareStatement("DELETE FROM trades WHERE id = ?;");
                            ps2.setInt(1, trade.getId());
                            int affectedRows2 = ps2.executeUpdate();
                            if (affectedRows2 != 0) {
                                System.out.println("Trade deleted");
                                return this.getTrade(trade.getTradeId());
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public TradeInterface acceptTrade(TradeInterface trade) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT a.user_id, b.user_id FROM trades JOIN cards a on trades.card_a = a.id JOIN cards b ON trades.card_b = b.id WHERE trades.id=?;");
            ps.setInt(1, trade.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User userA = (User) userService.getUser(rs.getInt(1));
                User userB = (User) userService.getUser(rs.getInt(2));

                cardService.addCardToUser(trade.getCardB(), userA);
                cardService.addCardToUser(trade.getCardA(), userB);
                cardService.lockCard(trade.getCardB(), false);
                cardService.lockCard(trade.getCardA(), false);

                ps = conn.prepareStatement("UPDATE trades SET accepted = TRUE WHERE id=?;");
                ps.setInt(1, trade.getId());
                int affectedRows = ps.executeUpdate();
                if (affectedRows != 0) {
                    return this.getTrade(trade.getTradeId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
