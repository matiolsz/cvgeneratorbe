package pl.be.cvgeneratorbe.configuration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "pl.be.cvgeneratorbe.repositories")
public class DynamoDbConfig {

    private static final String isDevKey = "IsDev";
    private static final String CV_TABLE_NAME = "UserCv";

    @Value("${amazon.dynamodb.endpoint}")
    private String dbEndpoint;

    @Value("${amazon.dynamodb.region}")
    private String amazonDynamoDbRegion;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        var devEnv = isDev();

        var client = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dbEndpoint, amazonDynamoDbRegion))
                .build();

        createTableIfNotExists(client, CV_TABLE_NAME);

        return client;
    }

    //default is true.
    private boolean isDev() {
        var dev = System.getenv(isDevKey);
        if (dev != null) {
            return Boolean.parseBoolean(dev);
        }
        return true;
    }

    private void createTableIfNotExists(AmazonDynamoDB dynamodb, String tableName){
        try{
            dynamodb.describeTable(tableName);
        } catch (ResourceNotFoundException exc){
            System.out.println("change for logger");
            System.out.println("table not exists");
            System.out.println("creating table");

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("UserCv")
                    .withAttributeDefinitions(new AttributeDefinition("Id", ScalarAttributeType.S))
                    .withKeySchema(new KeySchemaElement("Id", KeyType.HASH))
                    .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

            dynamodb.createTable(request);
        }
    }
}
