package rpalcolea.github

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class GithubModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GithubService).in(Scopes.SINGLETON)
        bind(GithubResource).in(Scopes.SINGLETON)
    }
}