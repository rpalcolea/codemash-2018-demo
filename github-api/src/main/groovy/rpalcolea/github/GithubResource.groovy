package rpalcolea.github

import com.google.inject.Inject
import ratpack.groovy.handling.GroovyChainAction
import ratpack.rx.RxRatpack

import static ratpack.jackson.Jackson.json

class GithubResource extends GroovyChainAction {

    @Inject
    GithubService githubService

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

                    RxRatpack.promiseSingle(githubService.findRepositories(language)).then { repositories ->
                        context.response.contentType('application/json;charset=UTF-8')
                        context.render(json([language: language, repositories: repositories ?: []]))
                    }
                }
            }
        }
    }
}
