package demo

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class LocationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LocationResource).in(Scopes.SINGLETON)
        bind(LocationService).in(Scopes.SINGLETON)
    }
}
