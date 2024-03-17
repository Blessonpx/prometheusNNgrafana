package com.example.prometheusNNgrafana.Controller;

import java.io.IOException;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
//import io.prometheus.client.Counter;
//import io.prometheus.metrics.exporter.servlet.jakarta.PrometheusMetricsServlet;
import io.prometheus.metrics.exporter.servlet.jakarta.PrometheusMetricsServlet;

@RestController
public class ApiController {
	
	/*
	 * Source for Opentelemetry and Testing
	 * https://github.com/adamquan/hello-observability/blob/main/hello-observability/src/main/java/org/grafana/HelloObservabilityBootApp.java#L8
	 * */
	private final MeterRegistry meterRegistry;
	
	public ApiController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
	}
//	private final OkHttpClient client = new OkHttpClient();
//	
//	private final Counter requestCounter = Counter.build().name("requests_total").help("Total number of requests.")
//			.labelNames("path").register();
//	
//	private final Histogram requestDurationHistogram = Histogram.build().name("http_request_duration_seconds").help("Request Duration in Seconds")
//			.labelNames("path").register();
//	
	@PostMapping("/api/postresource")
	public String getPostResource() throws IOException{
		
		String path = "/api/postresource";
		incrementRequestCounter(path);
		return "Post Response";
	}
	
	private void incrementRequestCounter(String path) {
        // Dynamically create or get an existing counter based on the path
        Counter requestCounter = Counter.builder("requests_total")
                .description("Total number of requests.")
                .tag("path", path)  // Unique tag for each path
                .register(meterRegistry);

        requestCounter.increment();
    }
	
	@Bean
    public ServletRegistrationBean<PrometheusMetricsServlet> createPrometheusMetricsEndpoint() {
        return new ServletRegistrationBean<>(new PrometheusMetricsServlet(), "/actuator/prometheus");
    }

}
