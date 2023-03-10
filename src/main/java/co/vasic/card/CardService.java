package co.vasic.card;

import co.vasic.database.DatabaseService;
import co.vasic.user.UserInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardService implements CardServiceInterface {
    private static CardService instance;

    private CardService() {

    }

    public static CardService getInstance() {
        if (CardService.instance == null) {
            CardService.instance = new CardService();
        }
        return CardService.instance;
    }

    @Override
    public CardInterface getCard(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, hashId, name, damage, card_type, element_type, is_locked FROM cards WHERE id=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                CardInterface card = Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // hashId
                        rs.getString(3), // name
                        rs.getFloat(4), // damage
                        // rs.getString(5), // card_type
                        // rs.getString(6), // element_type
                        rs.getBoolean(7)); // is_locked
                rs.close();
                ps.close();
                conn.close();

                return card;
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
    public List<CardInterface> getCards() {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            Statement sm = conn.createStatement();
            ResultSet rs = sm
                    .executeQuery("SELECT id, hashId, name, damage, card_type, element_type, is_locked FROM cards;");

            List<CardInterface> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // hashId
                        rs.getString(3), // name
                        rs.getFloat(4), // damage
                        // rs.getString(5), // card_type
                        // rs.getString(6), // element_type
                        rs.getBoolean(7))); // is_locked
            }

            rs.close();
            sm.close();
            conn.close();

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CardInterface> getCardsForUser(UserInterface user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, hashId, name, damage, card_type, element_type, is_locked FROM cards WHERE user_id = ?;");
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            List<CardInterface> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // hashId
                        rs.getString(3), // name
                        rs.getFloat(4), // damage
                        // rs.getString(5), // card_type
                        // rs.getString(6), // element_type
                        rs.getBoolean(7))); // is_locked
            }

            rs.close();
            ps.close();
            conn.close();

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CardInterface> getCardsForPackage(PackageInterface cardPackage) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, hashId, name, damage, card_type, element_type, is_locked FROM cards WHERE package_id = ?;");
            ps.setInt(1, cardPackage.getId());
            ResultSet rs = ps.executeQuery();

            List<CardInterface> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(Card.fromPrimitives(
                        rs.getInt(1), // id
                        rs.getString(2), // hashId
                        rs.getString(3), // name
                        rs.getFloat(4), // damage
                        // rs.getString(5), // card_type
                        // rs.getString(6), // element_type
                        rs.getBoolean(7))); // is_locked
            }

            rs.close();
            ps.close();
            conn.close();

            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CardInterface addCard(CardInterface card) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO cards(hashId, name, damage, element_type, card_type, package_id, user_id) VALUES(?,?,?,?,?,?,?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, card.getHashId());
            ps.setString(2, card.getName());
            ps.setFloat(3, card.getDamage());
            ps.setString(4, card.getElementType().toString());
            ps.setString(5, card.getCardType().toString());
            ps.setNull(6, java.sql.Types.NULL);
            ps.setNull(7, java.sql.Types.NULL);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return this.getCard(generatedKeys.getInt(1));
                }
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteCard(int id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM cards WHERE id = ?;");
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return false;
            }

            ps.close();
            conn.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CardInterface addCardToPackage(CardInterface card, PackageInterface cardPackage) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE cards SET package_id = ? WHERE id = ?;");
            ps.setInt(1, cardPackage.getId());
            ps.setInt(2, card.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }
            return this.getCard(card.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CardInterface addCardToUser(CardInterface card, UserInterface user) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn
                    .prepareStatement("UPDATE cards SET package_id = NULL, user_id = ? WHERE id = ?;");
            ps.setInt(1, user.getId());
            ps.setInt(2, card.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }
            return this.getCard(card.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CardInterface lockCard(CardInterface card, boolean isLocked) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE cards SET is_locked = ? WHERE id = ?;");
            ps.setBoolean(1, isLocked);
            ps.setInt(2, card.getId());

            int affectedRows = ps.executeUpdate();

            ps.close();
            conn.close();

            if (affectedRows == 0) {
                return null;
            }
            return this.getCard(card.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
