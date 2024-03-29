package com.example.prometheusNNgrafana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.prometheus.client.hotspot.DefaultExports;

@SpringBootApplication
public class PrometheusNNgrafanaApplication {

	public static void main(String[] args) {
		DefaultExports.initialize();
		SpringApplication.run(PrometheusNNgrafanaApplication.class, args);
	}

}
