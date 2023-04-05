package pl.be.cvgeneratorbe.dto;

import lombok.Data;

@Data
public class Education {
    String school;
    String description;
    String period;

    public Education(String school, String description, String period) {
        this.school = school;
        this.description = description;
        this.period = period;
    }

    public Education() {
    }
}
