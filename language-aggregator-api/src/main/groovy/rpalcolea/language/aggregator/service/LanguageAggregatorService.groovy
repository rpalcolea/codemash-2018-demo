package rpalcolea.language.aggregator.service

import com.google.inject.Inject
import org.slf4j.MDC
import ratpack.rx.RxRatpack
import rpalcolea.language.aggregator.model.Api
import rpalcolea.language.aggregator.model.ApiResult
import rpalcolea.language.aggregator.model.Language
import rpalcolea.language.aggregator.model.Result
import rx.Observable
import rx.functions.Func1


class LanguageAggregatorService {

    @Inject
    GithubApiService githubApiService

    @Inject
    TwitterApiService twitterApiService

    @Inject
    LanguagesApiService languagesApiService

    Observable<Result> find(String language) {
        languagesApiService.call(language).map { ApiResult apiResult ->
            if (!apiResult.result) {
                return new Result(error: "language ${language} not found", statusCode: 404)
            }

            return new Result(language:
                    new Language(
                            name: language,
                            hashtag: apiResult.result.hashtag,
                            stableRelease: apiResult.result.stableRelease,
                            website: apiResult.result.website,
                    )
            )
        }
    }
    Observable<Result> getSocialActivity(Result result) {
        Map observables = [
                github   : githubApiService.call(result.language.name).replay(),
                languages: twitterApiService.call(result.language.hashtag).replay()
        ]

        doInParallel(observables.values() as List<Observable>)
                .reduce(result, this.&aggregate)
                .onErrorResumeNext(this.&mapError as Func1)

    }

    Observable doInParallel(List<Observable> forkItems) {
        def mdcContext = MDC.copyOfContextMap
        Observable.from(forkItems)
                .compose(RxRatpack.&forkEach)
                .flatMap {
            MDC.setContextMap(mdcContext)
            it.connect()
            return it
        }
    }

    static Result aggregate(Result aggregate, ApiResult current) {
        if (current.api == Api.GITHUB)
            aggregate.language.repositories = current.result
        else
            aggregate.language.tweets = current.result
        return aggregate
    }

    static Observable<Result> mapError(Exception ex) {
        return Observable.just(new Result(error: ex.message, statusCode: 500))
    }
}
