package languages.api

import rpalcolea.languages.Language

class SearchController {
	static responseFormats = ['json', 'xml']
	
    def index(String language) {
        respond(Language.findByName(language))
    }
}
