package rpalcolea.language.aggregator.service

import com.google.inject.Inject
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixCommandProperties
import com.netflix.hystrix.HystrixObservableCommand
import groovy.util.logging.Slf4j
import ratpack.exec.Promise
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse
import rpalcolea.language.aggregator.config.GithubApiConfig
import rpalcolea.language.aggregator.model.Api
import rpalcolea.language.aggregator.model.ApiResult
import rx.Observable

import static ratpack.rx.RxRatpack.observe

@Slf4j
class GithubApiService extends ApiService {

    private final HttpClient httpClient
    private final GithubApiConfig config

    private static final HystrixObservableCommand.Setter hystrixSetter = HystrixObservableCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("github-service"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("GithubService.getRepositories"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter())

    @Inject
    GithubApiService(GithubApiConfig config, HttpClient httpClient) {
        this.config = config
        this.httpClient = httpClient
    }

    Observable<ApiResult> call(String language) {
        return new HystrixObservableCommand<ApiResult>(hystrixSetter) {
            @Override
            protected Observable<ApiResult> construct() {
                return getRepositories(language)
            }

            @Override
            protected Observable<ApiResult> resumeWithFallback() {
                log.error("Could not retrieve repositories from github-api | Language: $language | ${this?.commandKey?.name()}")
                return Observable.just(new ApiResult(api: Api.GITHUB, result: []))
            }

        }.toObservable()
    }

    Observable<ApiResult> getRepositories(String language) {
        def uri = "${config.host}${config.path}/${language}".toURI()
        Promise<ReceivedResponse> request = httpClient.get(uri) {
            it.method('GET')
                    .headers {
                it.set('Accept', 'application/json')
            }
        }
        observe(request).map { ReceivedResponse response ->
            if(response.statusCode != 200) {
                throw new RuntimeException("Could not retrieve repositories from github-api")
            }

            def json = jsonSlurper.parseText(response.body.text)
            if(!(json.repositories instanceof List<Map>)) {
                throw new RuntimeException("Could not retrieve repositories from github-api")
            }
            return new ApiResult(result: json?.repositories, api: Api.GITHUB)
        }
    }
}
