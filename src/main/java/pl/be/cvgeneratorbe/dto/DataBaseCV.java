package pl.be.cvgeneratorbe.dto;

import lombok.Getter;
import pl.be.cvgeneratorbe.entity.CvEntity;


import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DataBaseCV extends UserCV {
    private String id;

    private DataBaseCV(String fullName,
                       String role,
                       String experience,
                       String typeOfProjects,
                       String technologyStack,
                       String overallDescription,
                       List<Education> educationDtoList,
                       String languages,
                       List<Experience> detailedExperienceListDto,
                       String id) {
        super(fullName, role, experience, typeOfProjects, technologyStack, overallDescription , educationDtoList, languages, detailedExperienceListDto);
        this.id = id;
    }

    public static DataBaseCV of(CvEntity cv) {
        return new DataBaseCV(
                cv.getFullName(),
                cv.getRole(),
                cv.getExperience(),
                cv.getTypeOfProject(),
                cv.getTechnologyStack(),
                cv.getOverallDescription(),
                cv.getEducationEntityList().stream().map(Education::of).collect(Collectors.toList()),
                null,
                cv.getDetailedExperienceListDto().stream().map(Experience::of).collect(Collectors.toList()),
                cv.getId());
    }
}
