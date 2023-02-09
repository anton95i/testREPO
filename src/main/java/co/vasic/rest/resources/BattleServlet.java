package co.vasic.rest.resources;

import co.vasic.battle.Battle;
import co.vasic.battle.BattleService;
import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import co.vasic.user.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.util.HashMap;

public class BattleServlet extends HttpServlet {

    BattleService battleService;
    Gson gson;

    public BattleServlet() {
        gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        this.battleService = BattleService.getInstance();
    }

    @SneakyThrows
    @Override
    public HttpResponseInterface handlePost(HttpRequestInterface request) {
        // Only authorized users can battle
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();

        Battle battle = (Battle) battleService.createOrAddUserToBattle(user);
        Battle battleResult = (Battle) battleService.waitForBattleToFinish(battle);

        if (battleResult != null) {
            return HttpResponse.builder()
                    .headers(new HashMap<>() {{
                        put("Content-Type", "application/json");
                    }})
                    .body(gson.toJson(battleResult))
                    .build();
        }

        return HttpResponse.internalServerError();
    }
}
