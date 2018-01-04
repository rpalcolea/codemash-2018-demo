package rpalcolea.twitter

import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixCommandProperties
import com.netflix.hystrix.HystrixObservableCommand
import groovy.util.logging.Slf4j
import rx.Observable
import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterFactory

@Slf4j
class TwitterService {

    private static final HystrixObservableCommand.Setter hystrixSetter = HystrixObservableCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("twitter-api"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("TwitterService.getTweets"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter())

    private final Twitter twitter =  TwitterFactory.getSingleton()

    Observable<List> getTweets(String hashtag) {
        return new HystrixObservableCommand<List>(hystrixSetter) {
            @Override
            protected Observable<List> construct() {
                return _getTweets(hashtag)
            }

            @Override
            protected Observable<List> resumeWithFallback() {
                log.error("Error while executing ${this?.commandKey?.name()}")
                return Observable.just([])
            }
        }.toObservable()
    }

    Observable<List> _getTweets(String hashtag) {
        hashtag = hashtag.contains("#") ? hashtag : "#$hashtag"
        Query query = new Query(hashtag)
        QueryResult result = twitter.search(query)
        List<Status> statuses = result.tweets
        List<Tweet> tweets = statuses.take(10).collect {
            new Tweet(it.user.name, it.text)
        }
        return Observable.just(tweets)
    }
}
