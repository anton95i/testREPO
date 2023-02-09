package co.vasic.rest.resources;

import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import co.vasic.stats.StatsService;
import com.google.gson.Gson;

import java.util.HashMap;

public class ScoreboardServlet extends HttpServlet {

    StatsService statsService;
    Gson gson;

    public ScoreboardServlet() {
        gson = new Gson();
        this.statsService = StatsService.getInstance();
    }

    @Override
    public HttpResponseInterface handleIndex(HttpRequestInterface request) {
        // Only authorized users can view the scoreboard
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(gson.toJson(statsService.getScoreboard()))
                .build();
    }
}
