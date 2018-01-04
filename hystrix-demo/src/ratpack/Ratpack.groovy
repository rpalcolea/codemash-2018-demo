import demo.LocationModule
import demo.LocationResource
import ratpack.hystrix.HystrixMetricsEventStreamHandler
import ratpack.hystrix.HystrixModule
import ratpack.rx.RxRatpack
import ratpack.service.Service
import ratpack.service.StartEvent

import static ratpack.groovy.Groovy.ratpack

ratpack {
  bindings {
    module new LocationModule()
    module new HystrixModule().sse()
    bindInstance Service, new Service() {
      @Override
      void onStart(StartEvent event) throws Exception {
        RxRatpack.initialize()
      }
    }

  }

  handlers {
    prefix('location') {
      all chain(registry.get(LocationResource))
    }
    get("hystrix.stream", new HystrixMetricsEventStreamHandler())
  }
}
