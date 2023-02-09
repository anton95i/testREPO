package co.vasic.rest.resources;

import co.vasic.card.CardService;
import co.vasic.card.Package;
import co.vasic.card.PackageService;
import co.vasic.rest.HttpRequestInterface;
import co.vasic.rest.HttpResponse;
import co.vasic.rest.HttpResponseInterface;
import co.vasic.rest.HttpServlet;
import co.vasic.user.User;
import com.google.gson.Gson;

public class TransactionServlet extends HttpServlet {
    PackageService packageService;
    CardService cardService;
    Gson gson;

    public TransactionServlet() {
        gson = new Gson();
        this.packageService = PackageService.getInstance();
        this.cardService = CardService.getInstance();
    }

    public HttpResponseInterface handleAcquirePackage(HttpRequestInterface request) {
        // Only authorized users can acquire packages
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = (User) request.getAuthUser();
        Package cardPackage = (Package) packageService.getRandomPackage();

        if (cardPackage != null && packageService.addPackageToUser(cardPackage, user)) {
            return HttpResponse.ok();
        }

        return HttpResponse.internalServerError();
    }
}
