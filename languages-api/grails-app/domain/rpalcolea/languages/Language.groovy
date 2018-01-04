package rpalcolea.languages

import grails.rest.Resource

@Resource(uri='/languages')
class Language {

    String name
    String hashtag
    String website
    String stableRelease

    static constraints = {
        name unique: true
        hashtag blank: false
        website url: true
        stableRelease blank: false
    }
}
