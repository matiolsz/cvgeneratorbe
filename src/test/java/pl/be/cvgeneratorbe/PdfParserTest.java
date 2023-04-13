package pl.be.cvgeneratorbe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pl.be.cvgeneratorbe.dto.Education;
import pl.be.cvgeneratorbe.dto.Experience;
import pl.be.cvgeneratorbe.dto.UserCV;
import pl.be.cvgeneratorbe.service.PdfParser;

public class PdfParserTest {

    @Test
    public void runMethod() throws IOException {
        PdfParser pdfParser = new PdfParser();
        File file = new File("src/main/resources/cvpm.pdf");
        InputStream stream =  new FileInputStream(file);
        MultipartFile multipartFileToSend = new MockMultipartFile("file", file.getName(), MediaType.TEXT_HTML_VALUE, stream);
        UserCV userCv = pdfParser.parseLinkedInCv(multipartFileToSend);

        System.out.println("000000000000FULLNAME");
        System.out.println(userCv.getFullName());
        System.out.println("000000000000ROLE");
        System.out.println(userCv.getRole());
        System.out.println("000000000000EXPERIENCE");
        if(userCv.getDetailedExperienceList()!=null) {
            for (Experience experience : userCv.getDetailedExperienceList()) {
                System.out.println(experience.getJobRole());
                System.out.println(experience.getTimePeriod());
                System.out.println(experience.getDescription());
                System.out.println(experience.getCompany());
                System.out.println("--------------");
            }
        }

        System.out.println("000000000000EDUCATION");
        if(userCv.getEducationList()!=null) {
            for (Education education : userCv.getEducationList()) {
                System.out.println(education.getSchool());
                System.out.println(education.getDescription());
                System.out.println(education.getPeriod());
                System.out.println("--------------");
            }
        }


        System.out.println(userCv.technologyStack);
    }
}
