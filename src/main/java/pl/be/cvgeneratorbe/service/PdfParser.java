package pl.be.cvgeneratorbe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
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
        PDFStyledTextStripper stripper = new PDFStyledTextStripper();
        String text = stripper.getText(document);
        String[] array = deletePageInfoFromString(text.split("\n"));

//        System.out.println(text);

        UserCV userCV = new UserCV();
        ArrayList<Experience> experience = new ArrayList<>();
        ArrayList<Education> educationList = new ArrayList<>();
        ArrayList<String> skills = new ArrayList<>();

//        set full name
        userCV.setFullName(deleteBrackets(array[0]));

        int startIndexOfSummary = IntStream.range(0, array.length)
                                           .filter(i -> array[i].contains("Summary"))
                                           .findFirst().orElse(-1);
        int startIndexOfExperience = IntStream.range(0, array.length)
                                              .filter(i -> array[i].strip().equals("[Bold]Experience"))
                                              .findFirst().orElse(-1);
        int startIndexOfEducation = IntStream.range(0, array.length)
                                             .filter(i -> array[i].contains("Education"))
                                             .findFirst().orElse(-1);
        int startIndexOfSkills = IntStream.range(0, array.length)
                                          .filter(i -> array[i].contains("Skills"))
                                          .findFirst().orElse(-1);
        int startIndexOfCertificates = IntStream.range(0, array.length)
                                                .filter(i -> array[i].contains("Licenses & Certifications"))
                                                .findFirst().orElse(-1);

        StringBuilder stringBuilder = new StringBuilder();

//        set summary - overall description
        if (startIndexOfSummary != -1) {
            for (int j = startIndexOfSummary; j < startIndexOfExperience - 1; j++) {
                stringBuilder.append(formatString(array[j + 1]));
            }
            userCV.setOverallDescription(stringBuilder.toString());
            if (stringBuilder.length() > 0) {
                stringBuilder.delete(0, stringBuilder.length() - 1);
            }
        }

//        set experience
        if (startIndexOfExperience != -1) {
            int j = startIndexOfExperience;
            StringBuilder sbExpDesc = new StringBuilder();
            do
            {
                int i = 4;
                while (!array[j + i].contains("[Bold]")) {
                    sbExpDesc.append(array[j + i]);
                    i++;
                }

                if (sbExpDesc.length() > 0) {
                    experience.add(new Experience(
                            deleteBrackets(array[j + 1]),
                            deleteBrackets(array[j + 2]),
                            deleteBrackets(array[j + 3]),
                            sbExpDesc.toString()));

                    sbExpDesc.delete(0, sbExpDesc.length() - 1);

                    j = j + i - 1;
                } else {
                    experience.add(new Experience(
                            deleteBrackets(array[j + 1]),
                            deleteBrackets(array[j + 2]),
                            deleteBrackets(array[j + 3])));
                    j = j + i - 1;
                }
            } while (j < startIndexOfEducation - 1);
            userCV.setDetailedExperienceList(experience);
        }

//        set education
        if (startIndexOfEducation != -1) {
            for (int j = startIndexOfEducation; j < indexEndingEducation(startIndexOfSkills, startIndexOfCertificates) - 1; j = j + 3) {
                if (array[j + 1].contains("Licenses & Certifications")) {
                    break;
                }
                if (j + 2 == startIndexOfSkills) {
                    educationList.add(new Education(deleteBrackets(array[j + 1])
                            , "",
                            ""));
                    break;
                }
                if (j + 3 == startIndexOfSkills) {
                    educationList.add(new Education(
                            deleteBrackets(array[j + 1]),
                            deleteBrackets(array[j + 2]),
                            ""));
                    break;
                }
                educationList.add(new Education(
                        deleteBrackets(array[j + 1]),
                        deleteBrackets(array[j + 2]),
                        deleteBrackets(array[j + 3])));
            }
            userCV.setEducationList(educationList);
        }

//        set skills
        if (startIndexOfSkills != -1) {
            int j = startIndexOfSkills;
            do
            {
                skills.addAll(Arrays.asList(array[j + 1].split(" • ")));
                j++;
            } while (!array[j].contains(" • "));
        }
        skills.removeIf(s -> s.equals(""));

        if (skills.size() > 1) {
            for (String skill : skills) {
                stringBuilder.append(formatString(skill)).append(", ");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
            userCV.setTechnologyStack(stringBuilder.toString());
        }

//        set role
        if(!userCV.getDetailedExperienceList().isEmpty()){
            userCV.setRole(userCV.getDetailedExperienceList().get(0).getJobRole());
        }

        document.close();
        return userCV;
    }

    String formatString(String input) {
        return input.replaceAll("[^A-Za-z0-9 .,:]", "");
    }

    String deleteBrackets(String input) {
        return input.replaceAll("\\[[^]]*]", "");
    }

    String[] deletePageInfoFromString(String[] array) {
        String[] finalArray = array;
        int[] indexesOfPageNumber = IntStream.range(0, array.length)
                                             .filter(i -> finalArray[i].contains("- page ")).toArray();

        for (int indexToDelete : indexesOfPageNumber) {
            for (int i = indexToDelete; i < array.length - 1; i++) {
                array[i] = array[i + 1];
            }
            array = Arrays.copyOf(array, array.length - 1);
        }

        return array;
    }

    int indexEndingEducation(int skillsIndex, int certificatesIndex) {
        if (skillsIndex == -1) {
            return certificatesIndex;
        }
        if (certificatesIndex == -1) {
            return skillsIndex;
        }
        if (skillsIndex > certificatesIndex) {
            return certificatesIndex;
        } else {
            return -1;
        }
    }
}


