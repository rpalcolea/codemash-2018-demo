package rpalcolea.language.aggregator.model

import com.fasterxml.jackson.annotation.JsonIgnore

class Language {
    String name
    String website
    String stableRelease
    List tweets
    List repositories

    @JsonIgnore
    String hashtag
}
