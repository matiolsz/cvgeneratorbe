package pl.be.cvgeneratorbe.dto;

import lombok.Data;
import pl.be.cvgeneratorbe.entity.ExperienceEntity;

@Data
public class Experience {
    String jobRole;
    String company;
    String timePeriod;
    String description;

    public Experience() {
    }

    public Experience(String jobRole, String company, String timePeriod) {
        this.jobRole = jobRole;
        this.company = company;
        this.timePeriod = timePeriod;
    }


    public static Experience of(ExperienceEntity experience) {
        return new Experience(experience.getJobRole(), experience.getCompany(), experience.getTimePeriod());
    }
}

