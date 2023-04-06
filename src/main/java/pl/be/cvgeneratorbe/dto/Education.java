package pl.be.cvgeneratorbe.dto;

import lombok.Data;
import pl.be.cvgeneratorbe.entity.EducationEntity;

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

    public static Education of(EducationEntity education){
        return new Education(education.getSchool(), education.getDescription(), education.getPeriod());

    }
}
