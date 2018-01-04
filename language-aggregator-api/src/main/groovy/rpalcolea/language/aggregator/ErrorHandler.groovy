package rpalcolea.language.aggregator

import groovy.util.logging.Slf4j
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context

@Slf4j
class ErrorHandler implements ServerErrorHandler {

    @Override
    void error(Context context, Throwable throwable) {
        log.warn "Error", throwable
        context.getResponse().status(500).send("text/html", "Error has occurred | ${throwable.message}")
    }

}
