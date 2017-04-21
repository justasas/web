package yt.movies.web;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class)
@ComponentScan()
@EnableAutoConfiguration
public class App extends SpringBootServletInitializer {
	public static void main(String[] args) {

		new SpringApplicationBuilder().bannerMode(Banner.Mode.OFF).sources(App.class).run(args);
	}
}
