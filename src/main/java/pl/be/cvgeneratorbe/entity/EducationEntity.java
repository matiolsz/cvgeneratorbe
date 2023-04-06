package pl.be.cvgeneratorbe.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import pl.be.cvgeneratorbe.dto.Education;

@DynamoDBDocument
public class EducationEntity {

    private  String school;

    private String description;

    private String period;

    private EducationEntity(String school, String description, String period) {
        this.school = school;
        this.description = description;
        this.period = period;
    }

    public EducationEntity() {
        // DON'T DELETE - required by AWS SDK
    }

    @DynamoDBAttribute(attributeName = "School")
    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "Period")
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public static EducationEntity of (Education education){
        return new EducationEntity(education.getSchool(), education.getDescription(), education.getPeriod());
    }


}
