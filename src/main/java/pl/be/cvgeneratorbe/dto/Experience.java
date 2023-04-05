package pl.be.cvgeneratorbe.dto;

import lombok.Data;

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
}

