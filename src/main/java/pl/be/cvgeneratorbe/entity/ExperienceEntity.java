package pl.be.cvgeneratorbe.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import pl.be.cvgeneratorbe.dto.Experience;


@DynamoDBDocument
public class ExperienceEntity {

    String jobRole;
    String company;
    String timePeriod;

    @DynamoDBAttribute(attributeName = "JobRole")
    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    @DynamoDBAttribute(attributeName = "Company")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @DynamoDBAttribute(attributeName = "TimePeriod")
    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    private ExperienceEntity(String jobRole, String company, String timePeriod) {
        this.jobRole = jobRole;
        this.company = company;
        this.timePeriod = timePeriod;
    }
    public ExperienceEntity() {
        // DON'T DELETE - required by AWS SDK
    }

    public static ExperienceEntity of(Experience experience){
        return new ExperienceEntity(experience.getJobRole(), experience.getCompany(), experience.getTimePeriod());
    }
}
