package pl.be.cvgeneratorbe.configuration;

import com.amazonaws.auth.AWSCredentials;

public class AwsCredentailsLocal implements AWSCredentials {

    public AwsCredentailsLocal() {
    }

    @Override
    public String getAWSAccessKeyId() {
        return "Dummy";
    }

    @Override
    public String getAWSSecretKey() {
        return "dummy1";
    }
}
