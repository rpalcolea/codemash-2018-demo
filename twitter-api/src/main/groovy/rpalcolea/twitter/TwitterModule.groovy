package rpalcolea.twitter

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import twitter4j.Twitter
import twitter4j.TwitterFactory

class TwitterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Twitter).toInstance(TwitterFactory.getSingleton())
        bind(TwitterService).in(Scopes.SINGLETON)
        bind(TwitterResource).in(Scopes.SINGLETON)
    }
}