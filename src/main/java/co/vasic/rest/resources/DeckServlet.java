package co.vasic.rest.resources;

import co.vasic.battle.DeckService;
import co.vasic.card.CardInterface;
import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import co.vasic.user.User;
import com.google.gson.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeckServlet extends HttpServlet {

    DeckService deckService;
    Gson gson;

    public DeckServlet() {
        gson = new Gson();
        deckService = DeckService.getInstance();
    }

    @Override
    public HttpResponseInterface handleIndex(HttpRequestInterface request) {
        // Only authorized users can view their decks
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();
        List<CardInterface> cards = deckService.getDeck(user);

        String returnBody;
        String returnContentType;

        if ("text/plain".equals(request.getHeaders().get("Accept"))) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Deck: ");
            for (CardInterface card : cards) {
                stringBuilder
                        .append(card.getName())
                        .append("(")
                        .append(card.getElementType())
                        .append(", ")
                        .append(card.getCardType())
                        .append(", ")
                        .append(card.getDamage())
                        .append("), ");
            }

            stringBuilder.setLength(stringBuilder.length() - 2);

            returnBody = stringBuilder.toString();
            returnContentType = "text/plain";
        } else {
            returnBody = gson.toJson(cards);
            returnContentType = "application/json";
        }

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", returnContentType);
                }})
                .body(returnBody)
                .build();
    }

    @Override
    public HttpResponseInterface handlePut(HttpRequestInterface request) {

        System.out.println("handlePut");
        // Only authorized users can update their decks
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();

        System.out.println(request.getBody());

        //int[] ids = gson.fromJson(request.getBody(), int[].class);
        //String[] ids = gson.fromJson(request.getBody(), String[].class);

        //String[] ids = gson.fromJson(request.getBody(), String[].class);

        /**
         * resolve this json
         * [
         * {"id": "asgag"},
         * {"id": "asdfagah"},
         * {"id": "jadfagag"}
         *    ]
         */
        JsonArray jsonArray = gson.fromJson(request.getBody(), JsonArray.class);
        System.out.println("jsonArray:");
        System.out.println(jsonArray);
        String[] ids = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            System.out.println(jsonArray.get(i));
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            System.out.println(jsonObject.get("id").getAsString());
            ids[i] = jsonObject.get("id").getAsString();
        }

        System.out.println(ids);

        boolean result = deckService.addCardsWithIdsToDeck(ids, user);

        int statusCode = result ? 200 : 400;
        String reasonPhrase = result ? "OK" : "Bad Request";
        List<CardInterface> cards = deckService.getDeck(user);

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .statusCode(statusCode)
                .reasonPhrase(reasonPhrase)
                .body(gson.toJson(cards))
                .build();
    }
}