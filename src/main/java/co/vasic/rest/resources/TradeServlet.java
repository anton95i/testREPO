package co.vasic.rest.resources;

import co.vasic.card.Card;
import co.vasic.card.CardInterface;
import co.vasic.card.CardService;
import co.vasic.card.CardType;
import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import co.vasic.trades.Trade;
import co.vasic.trades.TradeInterface;
import co.vasic.trades.TradeService;
import co.vasic.user.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TradeServlet extends HttpServlet {

    TradeService tradeService;
    CardService cardService;
    Gson gson;
    Pattern pattern;

    public TradeServlet() {
        gson = new Gson();
        pattern = Pattern.compile("/trades/(\\d+)/?");
        this.tradeService = TradeService.getInstance();
        this.cardService = CardService.getInstance();
    }

    @Override
    public HttpResponseInterface handleIndex(HttpRequestInterface request) {
        // Only authorized users can view their trades
        if (request.getAuthUser() == null)
            return HttpResponse.unauthorized();

        List<TradeInterface> trades = tradeService.getTrades();

        return HttpResponse.builder()
                .headers(new HashMap<>() {
                    {
                        put("Content-Type", "application/json");
                    }
                })
                .body(gson.toJson(trades))
                .build();
    }

    public HttpResponseInterface handlePost(HttpRequestInterface request) {
        // Only authorized users can create trades
        if (request.getAuthUser() == null)
            return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();

        JsonObject jsonObject = JsonParser.parseString(request.getBody()).getAsJsonObject();

        System.out.println(jsonObject);

        if (jsonObject.has("CardToTrade")) {

            System.out.println("CardToTrade");
            System.out.println(jsonObject.get("CardToTrade").getAsString());

            String id = jsonObject.get("CardToTrade").getAsString();
            String tradeId = jsonObject.get("id").getAsString();
            CardType cardType = CardType.valueOf(jsonObject.get("Type").getAsString());
            int minimumDamage = jsonObject.get("MinimumDamage").getAsInt();
            List<CardInterface> userCards = cardService.getCardsForUser(user);
            List<CardInterface> filteredCards = userCards.stream().filter(card -> card.getHashId().equals(id))
                    .collect(Collectors.toList());
            if (filteredCards.size() > 0) {
                Trade trade = (Trade) tradeService.addTrade(filteredCards.get(0), tradeId, cardType, minimumDamage);

                if (trade != null) {
                    return HttpResponse.builder()
                            .headers(new HashMap<>() {
                                {
                                    put("Content-Type", "application/json");
                                }
                            })
                            .statusCode(201)
                            .body(gson.toJson(trade))
                            .build();
                }
            }
        }

        return HttpResponse.badRequest();
    }

    public HttpResponseInterface handlePostOffer(HttpRequestInterface request) {
        // Only authorized users can offer on trades
        if (request.getAuthUser() == null)
            return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();

        Matcher m = pattern.matcher(request.getPath());
        if (m.matches()) {
            String id = m.group(1);
            Trade trade = (Trade) tradeService.getTrade(id);

            if (trade != null) {

                JsonObject jsonObject = JsonParser.parseString(request.getBody()).getAsJsonObject();

                if (jsonObject.has("CardToTrade")) {

                    String cardId = jsonObject.get("CardToTrade").getAsString();
                    Card cardA = (Card) trade.getCardA();

                    List<CardInterface> userCards = cardService.getCardsForUser(user);
                    List<CardInterface> filteredCardsToCheck = userCards.stream()
                            .filter(card -> card.getId() == cardA.getId()).collect(Collectors.toList());
                    List<CardInterface> filteredCards = userCards.stream()
                            .filter(card -> card.getHashId().equals(cardId)).collect(Collectors.toList());

                    if (filteredCards.size() > 0 && filteredCardsToCheck.size() == 0) {

                        Card cardB = (Card) filteredCards.get(0);

                        CardType cardBType = cardB.getCardType();

                        System.out.println(cardBType);

                        if (cardB.getDamage() >= trade.getMinimumDamage()
                                && cardB.getCardType().equals(trade.getCardType())) {

                            trade = (Trade) tradeService.addOffer(trade, filteredCards.get(0));

                            if (trade != null) {
                                return HttpResponse.builder()
                                        .headers(new HashMap<>() {
                                            {
                                                put("Content-Type", "application/json");
                                            }
                                        })
                                        .body(gson.toJson(trade))
                                        .build();
                            }
                        }
                    }
                }
            }
        }

        return HttpResponse.badRequest();

    }

    public HttpResponseInterface handlePostAccept(HttpRequestInterface request) {
        // Only authorized users can accept trades
        if (request.getAuthUser() == null)
            return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();

        Matcher m = Pattern.compile("/trades/(\\d+)/accept/?").matcher(request.getPath());
        if (m.matches()) {
            String id = m.group(1);
            Trade trade = (Trade) tradeService.getTrade(id);

            if (trade != null && trade.getCardA() != null && (trade.getCardB() != null)) {
                List<CardInterface> userCards = cardService.getCardsForUser(user);

                Trade finalTrade = trade;
                List<CardInterface> filteredCards = userCards.stream()
                        .filter(card -> card.getId() == finalTrade.getCardA().getId()).collect(Collectors.toList());

                if (filteredCards.size() > 0) {
                    trade = (Trade) tradeService.acceptTrade(trade);

                    if (trade != null) {
                        return HttpResponse.builder()
                                .headers(new HashMap<>() {
                                    {
                                        put("Content-Type", "application/json");
                                    }
                                })
                                .body(gson.toJson(trade))
                                .build();
                    }
                }
            }
        }

        return HttpResponse.badRequest();
    }

    @Override
    public HttpResponseInterface handleDelete(HttpRequestInterface request) {
        // Only authorized users can delete trades
        if (request.getAuthUser() == null)
            return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();

        Matcher m = pattern.matcher(request.getPath());
        if (m.matches()) {
            String id = m.group(1);
            // String id = request.getParams().get("id");

            Trade trade = (Trade) tradeService.getTrade(id);

            if (trade != null) {
                List<CardInterface> userCards = cardService.getCardsForUser(user);
                List<CardInterface> filteredCards = userCards.stream()
                        .filter(card -> card.getId() == trade.getCardA().getId()).collect(Collectors.toList());
                if (filteredCards.size() > 0) {
                    if (tradeService.deleteTrade(trade.getTradeId())) {
                        return HttpResponse.ok();
                    }
                }
            }
        }

        return HttpResponse.badRequest();
    }
}
