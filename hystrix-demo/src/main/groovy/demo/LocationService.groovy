package demo

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
class LocationService {

    private final HttpClient httpClient

    private final String url = "https://www.metaweather.com/api/location/search/?query="

    private final JsonSlurper jsonSlurper = new JsonSlurper()

    @Inject
    LocationService(HttpClient httpClient) {
        this.httpClient = httpClient
    }

    private static final HystrixObservableCommand.Setter setter = HystrixObservableCommand.Setter
            .withGroupKey( HystrixCommandGroupKey.Factory.asKey("location-api"))
            .andCommandKey(HystrixCommandKey.Factory.asKey('LocationService.getLocation'))
            .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter())


    Observable<Map> getLocation(String city) {
        return new HystrixObservableCommand<Map>(setter) {

            @Override
            protected Observable<Map> construct() {
                return _getLocation(city)
            }

            @Override
            protected Observable<Map> resumeWithFallback() {
                log.error("Could not call location api for city $city")
                return Observable.just([title: city, "lat_long": "undefined"])
            }
        }.toObservable()
    }


    Observable<Map> _getLocation(String city) {
        def uri = "$url$city".toURI()
        Promise<ReceivedResponse> request = httpClient.get(uri) {
            it.method("GET")
            .headers {
                it.set('Accept', 'application/json')
            }
        }
        observe(request).map { ReceivedResponse response ->
            if(response.statusCode != 200)
                throw new Exception("Could not retrieve location information")

            def json = jsonSlurper.parseText(response.body.text)
            if(!(json instanceof List) || json.size() == 0)
                throw new Exception("Could not retrieve location information")

            return json.first() as Map
        }
    }
}
