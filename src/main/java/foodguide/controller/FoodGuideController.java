package foodguide.controller;

import com.google.common.base.Throwables;
import foodguide.service.FoodGuide;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Produces;
import org.slieb.throwables.SuppressedException;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;

@Controller("/foodguide")
public class FoodGuideController {

    @Inject
    private FoodGuide foodGuide;

    @Get("/my-daily-menu/{idString}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getMyMenu(@NotBlank final String idString,
                                     @Header(value = "Accept-Language") final Optional<String> acceptLanguage) {
        final Identification identification;
        try {
            identification = Identification.parse(idString);
        } catch (BadIdentificationException e) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST)
                    .body(new Error(e.getMessage()));
        }

        final List<Locale.LanguageRange> languages = Locale.LanguageRange.parse(acceptLanguage.orElse("*"));
        try {
            return HttpResponse.ok(foodGuide.createDailyMenu(languages, identification));
        } catch (UnsupportedLocaleException e) {
            return HttpResponse.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new Error(e.getMessage()));
        }
    }

    @Get("/family-daily-menu/{idStrings*}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> getFamilyMenu(@NotEmpty final List<String> idStrings,
                                     @Header(value = "Accept-Language") final Optional<String> acceptLanguage) {
        final List<Identification> identifications;
        try {
            identifications = idStrings.stream().map(castFunctionWithThrowable(Identification::parse)).collect(Collectors.toList());
        } catch (final SuppressedException e) {
            if(e.getCause() instanceof BadIdentificationException) {
                BadIdentificationException cause = (BadIdentificationException) e.getCause();
                return HttpResponse.status(HttpStatus.BAD_REQUEST)
                        .body(new Error(cause.getMessage()));
            }
            throw e;
        }

        final List<Locale.LanguageRange> languages = Locale.LanguageRange.parse(acceptLanguage.orElse("*"));
        try {
            return HttpResponse.ok(foodGuide.createDailyMenu(languages, identifications));
        } catch (UnsupportedLocaleException e) {
            return HttpResponse.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new Error(e.getMessage()));
        }
    }
}