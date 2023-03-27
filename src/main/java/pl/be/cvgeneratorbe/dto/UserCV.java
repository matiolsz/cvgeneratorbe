package pl.be.cvgeneratorbe.dto;

import java.util.Arrays;
import java.util.List;

import lombok.Data;


@Data
public class UserCV {
    public String fullName;
    public String role;
    public String experience;
    public String typeOfProjects;
    public String technologyStack;
    public List<Education> educationList;
    public String languages;
    public List<Experience> detailedExperienceList;

    public UserCV(String fullName, String role, String experience, String typeOfProjects, String technologyStack, List<Education> educationList, String languages, List<Experience> detailedExperienceList) {
        this.fullName = fullName;
        this.role = role;
        this.experience = experience;
        this.typeOfProjects = typeOfProjects;
        this.technologyStack = technologyStack;
        this.educationList = educationList;
        this.languages = languages;
        this.detailedExperienceList = detailedExperienceList;
    }

    public UserCV() {
    }

    @Override
    public String toString() {
        return "UserCV{" +
                "fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", experience='" + experience + '\'' +
                ", typeOfProjects='" + typeOfProjects + '\'' +
                ", technologyStack='" + technologyStack + '\'' +
                ", educationList=" + educationList +
                ", languages='" + languages + '\'' +
                ", detailedExperienceList=" + detailedExperienceList +
                '}';
    }
}
