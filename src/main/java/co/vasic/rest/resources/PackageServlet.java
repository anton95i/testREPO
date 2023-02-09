package co.vasic.rest.resources;

import co.vasic.card.Package;
import co.vasic.card.*;
import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PackageServlet extends HttpServlet {

    PackageService packageService;
    CardService cardService;
    Gson gson;

    public PackageServlet() {
        gson = new Gson();
        this.packageService = PackageService.getInstance();
        this.cardService = CardService.getInstance();
    }

    public HttpResponseInterface handlePost(HttpRequestInterface request) {

        // Only admins can create card packages.
        System.out.println(request.getHeaders());
        System.out.println(request.getBody());
        if (request.getAuthUser() == null || !"admin".equalsIgnoreCase(request.getAuthUser().getUsername())) {
            return HttpResponse.unauthorized();
        }

        Package cardPackage = (Package) packageService.addPackage(gson.fromJson(request.getBody(), Package.class));
        
        if (cardPackage != null) {

            JsonObject jsonObject = JsonParser.parseString(request.getBody()).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("cards");
            List<CardInterface> cards = new ArrayList<>();

            for (JsonElement cardJsonElement : jsonArray) {
                JsonObject cardJson = cardJsonElement.getAsJsonObject();

                CardInterface card = cardService.addCard(Card.fromPrimitives(
                        0,
                        cardJson.get("id").getAsString(),
                        cardJson.get("name").getAsString(),
                        cardJson.get("damage").getAsFloat(),
                        //cardJson.get("cardType").getAsString(),
                        //cardJson.get("elementType").getAsString(),
                        false
                ));
                card = cardService.addCardToPackage(card, cardPackage);

                cards.add(card);
            }

            JsonObject returnJsonObject = (JsonObject) gson.toJsonTree(cardPackage);
            returnJsonObject.add("cards", gson.toJsonTree(cards));

            return HttpResponse.builder()
                    .headers(new HashMap<>() {{
                        put("Content-Type", "application/json");
                    }})
                    .statusCode(201)
                    .body(gson.toJson(returnJsonObject))
                    .build();
        }
        return HttpResponse.internalServerError();
    }
}
