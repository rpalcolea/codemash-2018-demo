import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.health.HealthCheckHandler
import ratpack.hystrix.HystrixMetricsEventStreamHandler
import ratpack.hystrix.HystrixModule
import ratpack.rx.RxRatpack
import ratpack.service.Service
import ratpack.service.StartEvent
import rpalcolea.twitter.TwitterModule
import rpalcolea.twitter.TwitterResource

import static ratpack.groovy.Groovy.ratpack

final Logger logger = LoggerFactory.getLogger(Ratpack.class)

ratpack {
  bindings {
    module new TwitterModule()
    module new HystrixModule().sse()
    bindInstance Service, new Service() {
      @Override
      void onStart(StartEvent event) throws Exception {
        logger.info "Initializing RX"
        RxRatpack.initialize()
      }
    }
  }

  handlers {
    prefix('tweets') {
      all chain(registry.get(TwitterResource))
    }

    get("health-check/:name?", new HealthCheckHandler())
    get("hystrix.stream", new HystrixMetricsEventStreamHandler())
  }
}
