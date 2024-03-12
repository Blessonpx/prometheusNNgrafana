package com.example.prometheusNNgrafana.Controller;

import java.io.IOException;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.prometheus.metrics.exporter.servlet.jakarta.PrometheusMetricsServlet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
public class ApiController {
	
	/*
	 * Source for Opentelemetry and Testing
	 * https://github.com/adamquan/hello-observability/blob/main/hello-observability/src/main/java/org/grafana/HelloObservabilityBootApp.java#L8
	 * */
	private final OkHttpClient client = new OkHttpClient();
	
	private final Counter requestCounter = Counter.build().name("requests_total").help("Total number of requests.")
			.labelNames("path").register();
	
	
	private final Gauge lastRequestTimestamp = Gauge.build().name("last_request_timestamp")
			.help("unix time of the last request").labelNames("path").register();
	
	private final Histogram requestDurationHistogram = Histogram.build().name("request_duration_histogram")
			.help("Request duration in seconds").labelNames("path")
			.buckets(0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009).register();
	
	private final Summary requestDurationSummary = Summary.build().name("request_duration_summary")
			.help("Request duration in seconds").labelNames("path").quantile(0.75, 0.01).quantile(0.85, 0.01)
			.register();
	
	@PostMapping("/api/postresource")
	public String getPostResource() throws IOException{
		
		String path = "/api/postresource";
		requestCounter.labels(path).inc();
		lastRequestTimestamp.labels(path).setToCurrentTime();
		Histogram.Timer histogramRequestTimer = requestDurationHistogram.labels(path).startTimer();
		Summary.Timer summaryRequestTimer = requestDurationSummary.labels(path).startTimer();
		
		try {
			// Generate some random errors
			randomError(path);

			// Randomly sleeps a bit
			try {
				Thread.sleep((long) (Math.random() * 1000));
			} catch (InterruptedException e) {
				System.out.println("throw new IOException(e);");
				throw new IOException(e);
			}

			Request request = new Request.Builder().url("http://localhost:8080/observability").build();
			try (Response response = client.newCall(request).execute()) {
				System.out.println("return \"Hello, \" + response.body().string() + \"!\\n\";");
				return "Hello, " + response.body().string() + "!\n";
			}
		} finally {
			histogramRequestTimer.observeDuration();
			summaryRequestTimer.observeDuration();
		}
		
		//return "Post Response";
	}
	
	private void randomError(String path) throws IOException {
		if (Math.random() > 0.9) {
			throw new IOException("Random error with " + path + "!");
		}
	}
	
	@Bean
    public ServletRegistrationBean<PrometheusMetricsServlet> createPrometheusMetricsEndpoint() {
        return new ServletRegistrationBean<>(new PrometheusMetricsServlet(), "/metrics/*");
    }

}
