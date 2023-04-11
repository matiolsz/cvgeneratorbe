package pl.be.cvgeneratorbe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.be.cvgeneratorbe.dto.Education;
import pl.be.cvgeneratorbe.dto.Experience;
import pl.be.cvgeneratorbe.dto.UserCV;

@Service
public class PdfParser {

    public UserCV parseLinkedInCv(MultipartFile multipartFile) throws IOException {

        File file = new File("src/main/resources/targetFile.tmp");
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }

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
                                              .filter(i -> array[i].equals("Experience"))
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
        int indexOfPage2Sign = IntStream.range(0, array.length)
                                        .filter(i -> array[i].contains("page 2"))
                                        .findFirst().orElse(-1);
        int indexOfPage3Sign = IntStream.range(0, array.length)
                                        .filter(i -> array[i].contains("page 3"))
                                        .findFirst().orElse(-1);
        int indexOfLastElement = 0;
        if(indexOfPage1Sign!=-1){
            indexOfLastElement = indexOfPage1Sign;
        }
        if(indexOfPage2Sign!=-1){
            indexOfLastElement = indexOfPage2Sign;
        }
        if(indexOfPage3Sign!=-1){
            indexOfLastElement = indexOfPage3Sign;
        }

        StringBuilder stringBuilder = new StringBuilder();

        if(startIndexOfSummary!=-1) {
            for (int j = startIndexOfSummary; j < startIndexOfExperience; j++) {
                stringBuilder.append(array[j + 1]);
            }
            userCV.setOverallDescription(stringBuilder.toString());
            if(stringBuilder.length()>0) {
                stringBuilder.delete(0, stringBuilder.length() - 1);
            }
        }

        if(startIndexOfExperience!=-1) {
            for (int j = startIndexOfExperience; j < startIndexOfEducation - 3; j = j + 3) {
                experience.add(new Experience(array[j + 1], array[j + 2], array[j + 3]));
            }
            userCV.setDetailedExperienceList(experience);
        }

        if(startIndexOfEducation!=-1) {
            for (int j = startIndexOfEducation; j < startIndexOfSkills - 1; j = j + 3) {
                if (array[j + 1].contains("Licenses & Certifications")) {
                    break;
                }
                if (j + 2 == startIndexOfSkills) {
                    educationList.add(new Education(array[j + 1], "", ""));
                    break;
                }
                if (j + 3 == startIndexOfSkills) {
                    educationList.add(new Education(array[j + 1], array[j + 2], ""));
                    break;
                }
                educationList.add(new Education(array[j + 1], array[j + 2], array[j + 3]));
            }
            userCV.setEducationList(educationList);

        }
        if(startIndexOfSkills!=-1) {
            for (int j = startIndexOfSkills; j < indexOfLastElement - 1; j++) {
                if (!array[j + 1].contains(" • ")) {
                    break;
                }
                Arrays.stream(array[j + 1].split(" • ")).forEach(i -> skills.add(i.trim()));
            }
            skills.removeIf(s -> s.equals(""));

            if (skills.size() > 1) {
                for (String skill : skills) {
                    stringBuilder.append(skill.replaceAll("[^A-Za-z0-9]", "") + ", ");
                }
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
                userCV.setTechnologyStack(stringBuilder.toString());
            }
        }
        document.close();
        return userCV;
    }

}
