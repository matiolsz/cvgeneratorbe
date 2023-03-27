package pl.be.cvgeneratorbe.dto;

import lombok.Data;

@Data
public class Education {
    String school;
    String description;
    String period;

    @Override
    public String toString() {
        return "Education{" +
                "school='" + school + '\'' +
                ", description='" + description + '\'' +
                ", period='" + period + '\'' +
                '}';
    }
}
