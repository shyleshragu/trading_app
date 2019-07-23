package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.logging.Logger;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    //private Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Value("${iex.host}")
    private String iex_host;

    /*@Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
    }

    @Bean
    public MarketDataConfig marketDataConfig() {
    }

    @Bean
    public DataSource dataSource() {
    }*/

    //http://bit.ly/2tWTmzQ connectionPool
    @Bean
    public HttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(50);
        return cm;
    }
}
