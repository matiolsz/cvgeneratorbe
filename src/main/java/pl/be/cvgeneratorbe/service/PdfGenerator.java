package pl.be.cvgeneratorbe.service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import pl.be.cvgeneratorbe.dto.Education;
import pl.be.cvgeneratorbe.dto.Experience;
import pl.be.cvgeneratorbe.dto.UserCV;

public class PdfGenerator {
    public static ByteArrayInputStream generateFromOldTemplate(UserCV userCV) throws IOException {
        try(
                XWPFDocument doc = new XWPFDocument())
        {
            // create a paragraph
            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);

            // set font
            XWPFRun jobTitle = p1.createRun();
            jobTitle.setBold(true);
            jobTitle.setFontSize(18);
            jobTitle.setFontFamily("Calibri");
            jobTitle.setText(userCV.getRole() + "\n");

            XWPFParagraph afterJobTitle = doc.createParagraph();

//            experience
            XWPFParagraph experienceHeader = doc.createParagraph();
            experienceHeader.setBorderTop(Borders.SINGLE);
            experienceHeader.setBorderBottom(Borders.SINGLE);
            XWPFRun experienceHeaderRun = experienceHeader.createRun();
            experienceHeaderRun.setBold(true);
            experienceHeaderRun.setFontSize(11);
            experienceHeaderRun.setFontFamily("Calibri");
            experienceHeaderRun.setText("PROFESSIONAL EXPERIENCE");

            for(Experience experience : userCV.detailedExperienceList){
                makeExperienceParagraph(doc, experience);
            }

//education
            XWPFParagraph educationHeader = doc.createParagraph();
            educationHeader.setBorderTop(Borders.SINGLE);
            educationHeader.setBorderBottom(Borders.SINGLE);
            XWPFRun educationHeaderRun = educationHeader.createRun();
            educationHeaderRun.setBold(true);
            educationHeaderRun.setFontSize(11);
            educationHeaderRun.setFontFamily("Calibri");
            educationHeaderRun.setText("EDUCATION");

            for(Education education : userCV.getEducationList())
            makeEducationParagraph(doc, education);

//skills
            XWPFParagraph skillsHeader = doc.createParagraph();
            skillsHeader.setBorderTop(Borders.SINGLE);
            skillsHeader.setBorderBottom(Borders.SINGLE);
            XWPFRun skillsHeaderRun = skillsHeader.createRun();
            skillsHeaderRun.setBold(true);
            skillsHeaderRun.setFontSize(11);
            skillsHeaderRun.setFontFamily("Calibri");
            skillsHeaderRun.setText("SKILLS");

            XWPFParagraph skills = doc.createParagraph();
            XWPFRun skillsRun = skills.createRun();
            skillsRun.setFontSize(11);
            skillsRun.setFontFamily("Calibri");
            skillsRun.setText(userCV.getTechnologyStack() + "\t");

//certificates
//            XWPFParagraph certificatesHeader = doc.createParagraph();
//            certificatesHeader.setBorderTop(Borders.SINGLE);
//            certificatesHeader.setBorderBottom(Borders.SINGLE);
//            XWPFRun certificatesHeaderRun = certificatesHeader.createRun();
//            certificatesHeaderRun.setBold(true);
//            certificatesHeaderRun.setFontSize(11);
//            certificatesHeaderRun.setFontFamily("Calibri");
//            certificatesHeaderRun.setText("CERTIFICATES AND TRAININGS");
//
//            XWPFParagraph certificates = doc.createParagraph();

//languages
            XWPFParagraph languagesHeader = doc.createParagraph();
            languagesHeader.setBorderTop(Borders.SINGLE);
            languagesHeader.setBorderBottom(Borders.SINGLE);
            XWPFRun languagesHeaderRun = languagesHeader.createRun();
            languagesHeaderRun.setBold(true);
            languagesHeaderRun.setFontSize(11);
            languagesHeaderRun.setFontFamily("Calibri");
            languagesHeaderRun.setText("LANGUAGES");

            XWPFParagraph languages = doc.createParagraph();
            XWPFRun languagesRun = languages.createRun();
            languagesRun.setFontSize(11);
            languagesRun.setFontFamily("Calibri");
            languagesRun.setText(userCV.getLanguages() + "\t");
            // save it to .docx file
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            doc.write(b);
            return new ByteArrayInputStream(b.toByteArray());
        }
    }

    public static ByteArrayInputStream generateFromNewTemplate(UserCV userCV) throws IOException {

        PDDocument document = new PDDocument();
        PDRectangle myPageSize = new PDRectangle(1400, 2000);
        PDPage myPage = new PDPage(myPageSize);
        document.addPage(myPage);

        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contentStream = new PDPageContentStream(document, myPage);
        PDImageXObject image = PDImageXObject.createFromFile("D:\\projects\\cvgeneratorbe\\src\\main\\resources\\newtemplateimage.jpg", document);
        contentStream.drawImage(image, 0, 1650);

//
        contentStream.setNonStrokingColor(new Color(31, 78, 121));
        contentStream.addRect(0, 1300, 1400, 350);
        contentStream.fill();

        contentStream.setNonStrokingColor(new Color(31, 78, 121));
        contentStream.addRect(0, 0, 1400, 100);
        contentStream.fill();

//      add text
        contentStream.beginText();
        //Setting the font to the Content stream
        contentStream.setFont(font, 20);
        //Setting the position for the line
        contentStream.newLineAtOffset(1000, 500);
        String text = "Name";
        //Adding text in the form of string
        contentStream.showText(text);
        //Ending the content stream
        contentStream.endText();

        contentStream.close();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        document.save("src\\main\\resources\\pedeef.pdf");
        document.save(b);
        document.close();
        return new ByteArrayInputStream(b.toByteArray());
    }

    public static void makeExperienceParagraph(XWPFDocument doc, Experience experience) {
        XWPFParagraph experienceParagraph = doc.createParagraph();
        XWPFRun experienceRun = experienceParagraph.createRun();
        experienceRun.setBold(true);
        experienceRun.setItalic(true);
        experienceRun.setFontSize(11);
        experienceRun.setFontFamily("Calibri");
        experienceRun.setText(experience.getTimePeriod());
        XWPFParagraph experienceTitle = doc.createParagraph();
        XWPFRun experienceRoleRun = experienceTitle.createRun();
        experienceTitle.setAlignment(ParagraphAlignment.CENTER);
        experienceRoleRun.setBold(true);
        experienceRoleRun.setItalic(true);
        experienceRoleRun.setFontSize(11);
        experienceRoleRun.setFontFamily("Calibri");
        experienceRoleRun.setText(experience.getJobRole());
        XWPFParagraph experienceDesc = doc.createParagraph();
        XWPFRun experienceDescRun = experienceDesc.createRun();
        experienceDescRun.setFontSize(11);
        experienceDescRun.setFontFamily("Calibri");
        experienceDescRun.setText(experience.getCompany());
    }

    public static void makeEducationParagraph(XWPFDocument doc, Education education) {
        XWPFParagraph educationParagraph = doc.createParagraph();
        XWPFRun educationRun = educationParagraph.createRun();
        educationRun.setBold(true);
        educationRun.setFontSize(11);
        educationRun.setFontFamily("Calibri");
        educationRun.setText(education.getPeriod() + "\t");
        XWPFRun educationDescRun = educationParagraph.createRun();
        educationDescRun.setFontSize(11);
        educationDescRun.setFontFamily("Calibri");
        educationDescRun.setText(education.getSchool() + " " + education.getDescription());
    }
}
