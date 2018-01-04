package demo

import com.google.inject.Inject
import ratpack.groovy.handling.GroovyChainAction
import ratpack.rx.RxRatpack

import static ratpack.jackson.Jackson.json


class LocationResource extends GroovyChainAction {

    @Inject
    LocationService locationService

    @Override
    void execute() throws Exception {
        path(":city") {
            byMethod {
                get {
                    String city = context?.pathTokens?.city
                    RxRatpack.promiseSingle(locationService.getLocation(city)).then { Map result ->
                        context.response.contentType("application/json")
                        context.render(json(result))
                    }
                }
            }
        }
    }
}
