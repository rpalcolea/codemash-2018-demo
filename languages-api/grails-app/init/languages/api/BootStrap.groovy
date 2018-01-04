package languages.api

import rpalcolea.languages.Language

class BootStrap {

    def init = { servletContext ->

        createLanguage("groovy", "groovylang", "http://www.groovy-lang.org", "2.4.11")
        createLanguage("kotlin", "kotlin", "https://kotlinlang.org/", "1.1.2")
        createLanguage("scala", "scala", "https://scala-lang.org/", "2.12.2")
        createLanguage("ceylon", "ceylonlang", "https://ceylon-lang.org/", "1.3.2")
        createLanguage("clojure", "clojure", "https://clojure.org/", "1.8")


    }
    def destroy = {
    }

    private createLanguage(String name, String hashtag, String website, String stableRelease) {
        new Language(
                name: name,
                hashtag: hashtag,
                website: website,
                stableRelease: stableRelease
        ).save(failOnError: true)
    }
}
