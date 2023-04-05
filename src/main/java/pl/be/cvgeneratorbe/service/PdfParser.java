package pl.be.cvgeneratorbe.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import pl.be.cvgeneratorbe.dto.Education;
import pl.be.cvgeneratorbe.dto.Experience;
import pl.be.cvgeneratorbe.dto.UserCV;

public class PdfParser {

    public void parseLinkedInCv() throws IOException {
        File file = new File("src/main/resources/cv.pdf");
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        String[] array = text.split("\n");

        UserCV userCV = new UserCV();
        ArrayList<Experience> experience = new ArrayList<>();
        ArrayList<Education> educationList = new ArrayList<>();
        ArrayList<String> skills = new ArrayList<>();

        userCV.setFullName(array[0]);

        int startIndexOfSummary = IntStream.range(0, array.length)
                                              .filter(i -> array[i].contains("Summary"))
                                              .findFirst().orElse(-1);
        int startIndexOfExperience = IntStream.range(0, array.length)
                                              .filter(i -> array[i].contains("Experience"))
                                              .findFirst().orElse(-1);
        int startIndexOfEducation = IntStream.range(0, array.length)
                                             .filter(i -> array[i].contains("Education"))
                                             .findFirst().orElse(-1);
        int startIndexOfSkills = IntStream.range(0, array.length)
                                          .filter(i -> array[i].contains("Skills"))
                                          .findFirst().orElse(-1);
        int indexOfPage1Sign = IntStream.range(0, array.length)
                                        .filter(i -> array[i].contains("page 1"))
                                        .findFirst().orElse(-1);

        StringBuilder stringBuilder = new StringBuilder();
        for (int j = startIndexOfSummary; j < startIndexOfExperience; j++) {
            stringBuilder.append(array[j+1]);
        }
        userCV.setOverallDescription(stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length()-1);

        for (int j = startIndexOfExperience; j < startIndexOfEducation - 3; j = j + 3) {
            experience.add(new Experience(array[j + 1], array[j + 2], array[j + 3]));
        }
        for (int j = startIndexOfEducation; j < startIndexOfSkills - 1; j = j + 3) {
            if(array[j+1].contains("Licenses & Certifications")){
                break;
            }
            if(j+2 == startIndexOfSkills) {
                educationList.add(new Education(array[j + 1], "", ""));
                break;
            }
            if(j+3 == startIndexOfSkills){
                educationList.add(new Education(array[j + 1], array[j + 2], ""));
                break;
            }
            educationList.add(new Education(array[j + 1], array[j + 2], array[j + 3]));
        }
        for (int j = startIndexOfSkills; j < indexOfPage1Sign - 1; j++) {
            if(!array[j+1].contains(" • ")){
                break;
            }
            Arrays.stream(array[j + 1].split(" • ")).forEach(i -> skills.add(i.trim()));
        }

        skills.removeIf(s -> s.equals(""));
        userCV.setDetailedExperienceList(experience);
        userCV.setEducationList(educationList);
        if (skills.size() > 1) {
            for (String skill : skills) {
                stringBuilder.append(skill.replaceAll("[^A-Za-z0-9]", "") + ", ");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
            userCV.setTechnologyStack(stringBuilder.toString());
        }

        document.close();
    }
}
