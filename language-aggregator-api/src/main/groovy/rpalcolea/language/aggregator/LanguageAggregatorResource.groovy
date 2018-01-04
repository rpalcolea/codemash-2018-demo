package rpalcolea.language.aggregator

import com.google.inject.Inject
import ratpack.groovy.handling.GroovyChainAction
import ratpack.rx.RxRatpack
import rpalcolea.language.aggregator.model.Result
import rpalcolea.language.aggregator.service.LanguageAggregatorService

import static ratpack.jackson.Jackson.json

class LanguageAggregatorResource extends GroovyChainAction {

    @Inject
    LanguageAggregatorService languageAggregatorService

    @Override
    void execute() throws Exception {
        path('language/:language') {
            byMethod {
                get {
                    String language = context?.pathTokens?.language
                    if(!language) {
                        context.response.status(400)
                        context.render(json([message: 'language was not provided']))
                        return
                    }

                    RxRatpack.promiseSingle(languageAggregatorService.find(language)).then { Result result  ->
                        if(!result.isSuccessful()) {
                            context.response.status(result.statusCode)
                            context.render(json([message: result.error]))
                            return
                        }

                        return RxRatpack.promiseSingle(languageAggregatorService.getSocialActivity(result)).then {
                            context.response.contentType('application/json;charset=UTF-8')
                            context.render(json(result.language))                        }
                    }
                }
            }
        }
    }
}
