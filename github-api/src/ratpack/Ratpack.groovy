import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.health.HealthCheckHandler
import ratpack.hystrix.HystrixMetricsEventStreamHandler
import ratpack.hystrix.HystrixModule
import ratpack.rx.RxRatpack
import ratpack.service.Service
import ratpack.service.StartEvent
import rpalcolea.github.GithubConfig
import rpalcolea.github.GithubModule
import rpalcolea.github.GithubResource

import static ratpack.groovy.Groovy.ratpack

final Logger logger = LoggerFactory.getLogger(Ratpack.class)

ratpack {
  serverConfig {
    props("application.properties")
    require("/github", GithubConfig)
  }
  bindings {
    module new GithubModule()
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
    prefix('repositories') {
      all chain(registry.get(GithubResource))
    }

    get("health-check/:name?", new HealthCheckHandler())
    get("hystrix.stream", new HystrixMetricsEventStreamHandler())
  }
}
