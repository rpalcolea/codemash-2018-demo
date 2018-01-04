import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.error.ServerErrorHandler
import ratpack.health.HealthCheckHandler
import ratpack.hystrix.HystrixMetricsEventStreamHandler
import ratpack.hystrix.HystrixModule
import ratpack.rx.RxRatpack
import ratpack.service.Service
import ratpack.service.StartEvent
import rpalcolea.language.aggregator.ErrorHandler
import rpalcolea.language.aggregator.LanguageAggregatorModule
import rpalcolea.language.aggregator.LanguageAggregatorResource
import rpalcolea.language.aggregator.config.GithubApiConfig
import rpalcolea.language.aggregator.config.LanguagesApiConfig
import rpalcolea.language.aggregator.config.TwitterApiConfig

import static ratpack.groovy.Groovy.ratpack

final Logger logger = LoggerFactory.getLogger(Ratpack.class)

ratpack {
  serverConfig {
    props("application.properties")
    require("/github", GithubApiConfig)
    require("/languages", LanguagesApiConfig)
    require("/twitter", TwitterApiConfig)
  }
  bindings {
    module new LanguageAggregatorModule()
    module new HystrixModule().sse()
    bindInstance Service, new Service() {
      @Override
      void onStart(StartEvent event) throws Exception {
        RxRatpack.initialize()
        logger.info "Initialized RX"

      }
    }
    bind ServerErrorHandler, ErrorHandler
  }

  handlers {
    prefix('aggregate') {
      all chain(registry.get(LanguageAggregatorResource))
    }

    get("health-check/:name?", new HealthCheckHandler())
    get("hystrix.stream", new HystrixMetricsEventStreamHandler())
  }
}
