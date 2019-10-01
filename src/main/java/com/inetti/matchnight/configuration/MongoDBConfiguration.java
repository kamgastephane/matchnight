package com.inetti.matchnight.configuration;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Objects;

@Configuration
@EnableMongoRepositories(basePackages = {"com.inetti.matchnight.data.repository"})
public class MongoDBConfiguration extends AbstractMongoConfiguration {


    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String database;

    public MongoDBConfiguration(@Value("${spring.data.mongodb.host}") String host,
                                @Value("${spring.data.mongodb.port}") String port,
                                @Value("${spring.data.mongodb.username}") String username,
                                @Value("${spring.data.mongodb.password}") String password,
                                @Value("${spring.data.mongodb.database}") String database) {

        this.host = Objects.requireNonNull(host);
        this.port = Objects.requireNonNull(port);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.database = Objects.requireNonNull(database);
    }
    @Bean
    @Override
    public MongoClient mongoClient() {
        ServerAddress serverAddress = new ServerAddress(host, Integer.parseInt(port));
//        MongoCredential credential = MongoCredential.createCredential(username);
        return new MongoClient(serverAddress);
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }


}
