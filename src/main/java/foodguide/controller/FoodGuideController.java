package foodguide.controller;

import foodguide.service.FoodGuide;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

@Controller("/foodguide")
public class FoodGuideController {

    @Inject
    private FoodGuide foodGuide;

    @Get("/my-daily-menu/{idString}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getMyMenu(@NotBlank final String idString) {
        final Identification identification;
        try {
            identification = Identification.parse(idString);
        } catch (BadIdentificationException e) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST)
                    .body(new Error(e.getMessage()));
        }

        return HttpResponse.ok(foodGuide.createDailyMenu(identification));
    }
}