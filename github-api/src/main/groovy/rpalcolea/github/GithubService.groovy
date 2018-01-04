package rpalcolea.github

import com.google.inject.Inject
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixCommandProperties
import com.netflix.hystrix.HystrixObservableCommand
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import ratpack.exec.Promise
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse
import rx.Observable
import static ratpack.rx.RxRatpack.observe


@Slf4j
class GithubService {

    private final HttpClient httpClient
    private final GithubConfig config
    private final JsonSlurper jsonSlurper = new JsonSlurper()

    private static final HystrixObservableCommand.Setter hystrixSetter = HystrixObservableCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("github-api"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("GithubService.findRepositories"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter())

    @Inject
    GithubService(GithubConfig config, HttpClient httpClient) {
        this.config = config
        this.httpClient = httpClient
    }

    Observable<List> findRepositories(String language) {
        return new HystrixObservableCommand<List>(hystrixSetter) {
            @Override
            protected Observable<List> construct() {
                return _findRepositories(language)
            }

            @Override
            protected Observable<List> resumeWithFallback() {
                log.error("Could not retrieve repositories from Github | Language: $language | ${this?.commandKey?.name()}")
                return Observable.just([])
            }

        }.toObservable()
    }

    Observable _findRepositories(String language) {
        def uri = "${config.host}${config.path}?q=language:${language}&sort=stars&order=desc".toURI()
        Promise<ReceivedResponse> request = httpClient.get(uri) {
            it.method('GET')
            .headers {
                it.set('Accept', 'application/vnd.github.mercy-preview+json')
                it.set('User-Agent', config.userAgent)
                it.set('Authentication', "${config.token} TOKEN")
            }
        }
        observe(request).map { ReceivedResponse response ->
            if(response.statusCode != 200) {
                log.error("Could not retrieve repositories from Github ${response.statusCode} | ${response.body.text}")
                throw new RuntimeException("Could not retrieve repositories from Github")
            }
            return jsonSlurper.parseText(response.body.text)?.items
        }.map { Object repositories ->
            if(!(repositories instanceof List<Map>)) {
                return []
            }

            repositories.collect { repo ->
                new GithubRepository(
                        name: repo.name,
                        description: repo.description,
                        url: repo.html_url,
                        forks: repo.forks ?: 0,
                        stars: repo.stargazers_count ?: 0,
                        watchers: repo.watchers ?: 0
                )
            }
        }
    }
}
