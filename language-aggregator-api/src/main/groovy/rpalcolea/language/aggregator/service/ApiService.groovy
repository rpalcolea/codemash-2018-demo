package rpalcolea.language.aggregator.service

import groovy.json.JsonSlurper
import rx.Observable

abstract class ApiService<T> {

    protected final JsonSlurper jsonSlurper = new JsonSlurper()

    abstract Observable<T> call(String language)

}
