package rpalcolea.twitter

import com.google.inject.Inject
import ratpack.groovy.handling.GroovyChainAction
import ratpack.rx.RxRatpack

import static ratpack.jackson.Jackson.json

class TwitterResource extends GroovyChainAction {

    @Inject
    TwitterService twitterService

    @Override
    void execute() throws Exception {
        path('hashtag/:hashtag') {
            byMethod {
                get {
                    String hashtag = context?.pathTokens?.hashtag
                    if(!hashtag) {
                        context.response.status(400)
                        context.render(json([message: 'hashtag was not provided']))
                        return
                    }

                    RxRatpack.promiseSingle(twitterService.getTweets(hashtag)).then { tweets ->
                        context.response.contentType('application/json;charset=UTF-8')
                        context.render(json([hashtag: hashtag, tweets: tweets ?: []]))
                    }
                }
            }
        }
    }
}
