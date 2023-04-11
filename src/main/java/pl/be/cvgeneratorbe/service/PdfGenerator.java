package pl.be.cvgeneratorbe.service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import io.micrometer.common.util.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import pl.be.cvgeneratorbe.dto.Education;
import pl.be.cvgeneratorbe.dto.Experience;
import pl.be.cvgeneratorbe.dto.UserCV;

@Service
public class PdfGenerator {
    public static ByteArrayInputStream generateFromOldTemplate(UserCV userCV) throws IOException {
        try (
                XWPFDocument doc = new XWPFDocument()) {

            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun jobTitle = p1.createRun();
            jobTitle.setBold(true);
            jobTitle.setFontSize(18);
            jobTitle.setFontFamily("Calibri");
            jobTitle.setText(userCV.getRole() + "\n");

//experience
            createHeader(doc, "PROFESSIONAL EXPERIENCE");
            for (Experience experience : userCV.detailedExperienceList) {
                makeExperienceParagraph(doc, experience);
            }

//education
            createHeader(doc, "EDUCATION");
            for (Education education : userCV.getEducationList())
                makeEducationParagraph(doc, education);

//skills
            createHeader(doc, "SKILLS");
            XWPFParagraph skills = doc.createParagraph();
            XWPFRun skillsRun = skills.createRun();
            skillsRun.setFontSize(11);
            skillsRun.setFontFamily("Calibri");
            skillsRun.setText(userCV.getTechnologyStack() + "\t");

//languages
            createHeader(doc, "LANGUAGES");
            XWPFParagraph languages = doc.createParagraph();
            XWPFRun languagesRun = languages.createRun();
            languagesRun.setFontSize(11);
            languagesRun.setFontFamily("Calibri");
            languagesRun.setText(userCV.getLanguages() + "\t");

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            doc.write(b);
            return new ByteArrayInputStream(b.toByteArray());
        }
    }

    public static void createHeader(XWPFDocument doc, String paragraphName) {
        XWPFParagraph educationHeader = doc.createParagraph();
        educationHeader.setBorderTop(Borders.SINGLE);
        educationHeader.setBorderBottom(Borders.SINGLE);
        XWPFRun educationHeaderRun = educationHeader.createRun();
        educationHeaderRun.setBold(true);
        educationHeaderRun.setFontSize(11);
        educationHeaderRun.setFontFamily("Calibri");
        educationHeaderRun.setText(paragraphName);
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
        XWPFParagraph experienceComp = doc.createParagraph();
        XWPFRun experienceCompRun = experienceComp.createRun();
        experienceCompRun.setBold(true);
        experienceCompRun.setItalic(true);
        experienceCompRun.setFontSize(11);
        experienceCompRun.setFontFamily("Calibri");
        experienceCompRun.setText(experience.getCompany());
        if (!StringUtils.isEmpty(experience.getDescription())) {
            XWPFParagraph experienceDesc = doc.createParagraph();
            XWPFRun experienceDescRun = experienceDesc.createRun();
            experienceDescRun.setFontSize(11);
            experienceDescRun.setFontFamily("Calibri");
            experienceDescRun.setText("   •   " + experience.getDescription());
        }
        XWPFParagraph emptyLine = doc.createParagraph();
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

    public static ByteArrayInputStream generateFromNewTemplate(UserCV userCV) throws IOException {

        PDDocument document = new PDDocument();
        PDRectangle myPageSize = new PDRectangle(1400, 2000);
        PDPage myPage = new PDPage(myPageSize);
        PDPage mySecondPage = new PDPage(myPageSize);
        document.addPage(myPage);
        PDType0Font font = PDType0Font.load(document, new File("src/main/resources/arial.ttf"));
        PDPageContentStream contentStream = new PDPageContentStream(document, myPage);

//IMAGE
        PDImageXObject image = PDImageXObject.createFromFile("src/main/resources/newtemplateimage.jpg", document);
        contentStream.drawImage(image, 0, 1650);

//BLUE RECT
        contentStream.setNonStrokingColor(new Color(31, 78, 121));
        contentStream.addRect(0, 1350, 1400, 300);
        contentStream.fill();
//FOOTER
        contentStream.setNonStrokingColor(new Color(248, 180, 132));
        contentStream.addRect(0, 0, 1400, 100);
        contentStream.fill();

//HEADER text
        createCenteredTextInPDF(contentStream, myPage, userCV.fullName + " - " + userCV.getRole(), 400, font, 46, Color.WHITE);

//overall description
        createOverallDescription(contentStream, myPage, userCV.getOverallDescription(), font, 30);

        int height = 740;

        createTextInPDF(contentStream, myPage, "Role: " + userCV.getRole(), height, font, 28, Color.BLACK, 180);
        height = height + 70;

        createTextInPDF(contentStream, myPage, "Experience: " + userCV.getExperience(), height, font, 28, Color.BLACK, 180);
        height = height + 70;

        createTextInPDF(contentStream, myPage, "Type of projects: " + userCV.getTypeOfProjects(), height, font, 28, Color.BLACK, 180);
        height = height + 70;

        height = createWrappedText(contentStream, myPage, "Technology stack: " + userCV.getTechnologyStack(), font, 28, height, 180);
        height = height + 70;

        createTextInPDF(contentStream, myPage, "Education: ", height, font, 28, Color.BLACK, 180);
        height = height + 70;

        for (Education education : userCV.getEducationList()) {
            height = createWrappedText(contentStream, myPage, "•  " + education.getSchool() + " - " + education.getDescription() + " (" + education.getPeriod() + ")", font, 28, height, 250);
            height = height + 70;
        }

        createTextInPDF(contentStream, myPage, "Languages: " + userCV.getLanguages(), height, font, 28, Color.BLACK, 180);
        height = height + 70;

        createTextInPDF(contentStream, myPage, "Detailed experience: ", height, font, 28, Color.BLACK, 180);
        height = height + 70;

        boolean secondPageAdded = false;
        for (Experience experience : userCV.getDetailedExperienceList()) {
            if(height>1800){
                contentStream.close();
                document.addPage(mySecondPage);
                secondPageAdded = true;
                height = 150;
                contentStream = new PDPageContentStream(document, mySecondPage);

                //FOOTER
                contentStream.setNonStrokingColor(new Color(248, 180, 132));
                contentStream.addRect(0, 0, 1400, 100);
                contentStream.fill();
            }
            if(secondPageAdded) {
                height = createWrappedText(contentStream, mySecondPage, "•  " + experience.getJobRole() + " - " + experience.getCompany() + " (" + experience.getTimePeriod() + ")", font, 28, height, 250);
                height = height + 70;
            }
            else {
                height = createWrappedText(contentStream, myPage, "•  " + experience.getJobRole() + " - " + experience.getCompany() + " (" + experience.getTimePeriod() + ")", font, 28, height, 250);
                height = height + 70;
            }
        }

        contentStream.close();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        document.save(b);
        document.close();
        return new ByteArrayInputStream(b.toByteArray());
    }

    public static void createTextInPDF(PDPageContentStream contentStream, PDPage myPage, String text, int marginTop, PDFont font, int fontSize, Color color, int leftMargin) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(leftMargin, myPage.getMediaBox().getHeight() - marginTop);
        contentStream.setNonStrokingColor(color);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void createCenteredTextInPDF(PDPageContentStream contentStream, PDPage myPage, String text, int marginTop, PDFont font, int fontSize, Color color) throws IOException {
        float titleWidth = font.getStringWidth(text) / 1000 * fontSize;
        float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset((myPage.getMediaBox().getWidth() - titleWidth) / 2, myPage.getMediaBox().getHeight() - marginTop - titleHeight);
        contentStream.setNonStrokingColor(color);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void createOverallDescription(PDPageContentStream contentStream, PDPage myPage, String text, PDFont font, int fontSize) throws IOException {
        if (!StringUtils.isEmpty(text)) {
            int paragraphWidth = 1200;
            int start = 0;
            int end = 0;
            int height = 20;
            text = text.replace("\n", "").replace("\r", "");
            for (int i : possibleWrapPoints(text)) {
                float width = font.getStringWidth(text.substring(start, i)) / 1000 * fontSize;
                if (start < end && width > paragraphWidth) {
                    createCenteredTextInPDF(contentStream, myPage, text.substring(start, end), 460 + height, font, fontSize, Color.WHITE);
                    height += font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
                    start = end;
                }
                end = i;
            }
            createCenteredTextInPDF(contentStream, myPage, text.substring(start, end), 460 + height, font, fontSize, Color.WHITE);
        }
    }

    public static int createWrappedText(PDPageContentStream contentStream, PDPage myPage, String text, PDFont font, int fontSize, int height, int leftMargin) throws IOException {
        if (!StringUtils.isEmpty(text)) {
            int paragraphWidth = 1000;
            int start = 0;
            int end = 0;
            text = text.replace("\n", "").replace("\r", "");
            for (int i : possibleWrapPoints(text)) {
                float width = font.getStringWidth(text.substring(start, i)) / 1000 * fontSize;
                if (start < end && width > paragraphWidth) {
                    createTextInPDF(contentStream, myPage, text.substring(start, end), height, font, fontSize, Color.BLACK, leftMargin);
                    height += font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
                    start = end;
                }
                end = i;
            }
            createTextInPDF(contentStream, myPage, text.substring(start, end), height, font, fontSize, Color.BLACK, leftMargin);
            return height;
        }
        return 0;
    }

    static int[] possibleWrapPoints(String text) {
        String[] split = text.split("(?<=\\W)");
        int[] ret = new int[split.length];
        ret[0] = split[0].length();
        for (int i = 1; i < split.length; i++)
            ret[i] = ret[i - 1] + split[i].length();
        return ret;
    }
}
