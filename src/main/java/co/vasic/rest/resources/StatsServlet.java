package co.vasic.rest.resources;

import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import co.vasic.stats.Stats;
import co.vasic.stats.StatsService;
import co.vasic.user.User;
import com.google.gson.Gson;

import java.util.HashMap;

public class StatsServlet extends HttpServlet {

    StatsService statsService;
    Gson gson;

    public StatsServlet() {
        gson = new Gson();
        this.statsService = StatsService.getInstance();
    }

    @Override
    public HttpResponseInterface handleIndex(HttpRequestInterface request) {
        // Only authorized users can view their stats
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();
        Stats stats = (Stats) statsService.getStatsForUser(user);

        return HttpResponse.builder()
                .headers(new HashMap<>() {{
                    put("Content-Type", "application/json");
                }})
                .body(gson.toJson(stats))
                .build();
    }
}
