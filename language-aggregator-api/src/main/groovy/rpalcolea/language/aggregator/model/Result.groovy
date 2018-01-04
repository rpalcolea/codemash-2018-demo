package rpalcolea.language.aggregator.model

class Result {
    Language language
    Integer statusCode = 200
    String error

    boolean isSuccessful() {
        return statusCode == 200
    }
}
