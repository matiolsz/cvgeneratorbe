package pl.be.cvgeneratorbe.dto;

import lombok.Data;

@Data
public class Experience {
    String jobRole;
    String company;
    String timePeriod;

    @Override
    public String toString() {
        return "Experience{" +
                "jobRole='" + jobRole + '\'' +
                ", company='" + company + '\'' +
                ", timePeriod='" + timePeriod + '\'' +
                '}';
    }
}
