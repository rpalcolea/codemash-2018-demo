package rpalcolea.language.aggregator

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import rpalcolea.language.aggregator.service.GithubApiService
import rpalcolea.language.aggregator.service.LanguageAggregatorService
import rpalcolea.language.aggregator.service.LanguagesApiService
import rpalcolea.language.aggregator.service.TwitterApiService

class LanguageAggregatorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LanguageAggregatorService).in(Scopes.SINGLETON)
        bind(LanguageAggregatorResource).in(Scopes.SINGLETON)
        bind(GithubApiService).in(Scopes.SINGLETON)
        bind(LanguagesApiService).in(Scopes.SINGLETON)
        bind(TwitterApiService).in(Scopes.SINGLETON)
    }
}