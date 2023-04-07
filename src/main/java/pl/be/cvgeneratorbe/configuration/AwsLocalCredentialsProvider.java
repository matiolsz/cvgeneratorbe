package pl.be.cvgeneratorbe.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

public class AwsLocalCredentialsProvider implements AWSCredentialsProvider {
    public AwsLocalCredentialsProvider() {
    }

    @Override
    public AWSCredentials getCredentials() {
        return new AwsCredentailsLocal();
    }

    @Override
    public void refresh() {

    }
}
