package co.vasic.rest.resources;

import co.vasic.card.CardInterface;
import co.vasic.card.CardService;
import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import co.vasic.user.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class CardServlet extends HttpServlet {

    CardService cardService;
    Gson g;

    public CardServlet() {
        g = new Gson();
        this.cardService = CardService.getInstance();
    }

    @Override
    public HttpResponseInterface handleIndex(HttpRequestInterface request) {
        // Only authorized users can view their cards
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();
        List<CardInterface> cards = cardService.getCardsForUser(user);

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(g.toJson(cards))
                .build();
    }
}
