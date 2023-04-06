package pl.be.cvgeneratorbe.service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import org.springframework.stereotype.Service;
import pl.be.cvgeneratorbe.dto.DataBaseCV;
import pl.be.cvgeneratorbe.dto.Education;
import pl.be.cvgeneratorbe.dto.Experience;
import pl.be.cvgeneratorbe.dto.UserCV;
import pl.be.cvgeneratorbe.entity.CvEntity;
import pl.be.cvgeneratorbe.filters.BaseFilter;
import pl.be.cvgeneratorbe.filters.EducationDescriptionFilter;
import pl.be.cvgeneratorbe.filters.EducationDurationFilter;
import pl.be.cvgeneratorbe.filters.EducationNameFilter;
import pl.be.cvgeneratorbe.filters.ExperienceCompanyNamesFilter;
import pl.be.cvgeneratorbe.filters.ExperienceRoleFilter;
import pl.be.cvgeneratorbe.filters.ExperienceTimeFilter;
import pl.be.cvgeneratorbe.filters.SkillsFilter;
import pl.be.cvgeneratorbe.repositories.CvRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;


@Service
public class CvService {

    CvRepository cvRepository;

    CryptingService cryptService;

    public CvService(CvRepository cvRepository,
                     CryptingService cryptingService) {
        this.cryptService = cryptingService;
        this.cvRepository = cvRepository;
    }

    public DataBaseCV saveCV(UserCV userCv){
        var cvEntity = CvEntity.of(userCv);

        cvEntity.withCryptDecryptedName(cryptService.encrypt(cvEntity.getFullName()));

        var saved = cvRepository.save(cvEntity);

        saved.withCryptDecryptedName(cryptService.decrypt(cvEntity.getFullName()));

        return DataBaseCV.of(saved);
    }

    public UserCV buildCV() {
        PdfDocument pdfDoc = openPdf();
        Rectangle rect = new Rectangle(PageSize.A4);

        var schoolNamesFilter = new EducationNameFilter(rect);
        var names = getElementsByFilter(pdfDoc, schoolNamesFilter);

        EducationDescriptionFilter educationDescriptionFilter = new EducationDescriptionFilter(rect);
        var degree = getElementsByFilter(pdfDoc, educationDescriptionFilter);

        var eduDatesFilter = new EducationDurationFilter(rect);
        var years = getElementsByFilter(pdfDoc, eduDatesFilter);

        var nameAndSurname = getNameAndSurname(pdfDoc);

        List<Education> eduList = new ArrayList<>();
        SkillsFilter skillsFilter = new SkillsFilter(rect);
        var skills  = getElementsByFilter(pdfDoc, skillsFilter);
        var skillsApp = new ArrayList<>();
        while (!skills.isEmpty()){
            if (skills.peek().contains(nameAndSurname)){
                skills.poll();
            }

            skillsApp.add(skills.poll());
        }

        while(!years.isEmpty()){
            eduList.add(new Education(names.poll(), degree.poll(), years.poll()));
        }

        List<Experience> jobs = extractExperience(pdfDoc, rect);

        String lastRole = extractLastRole(jobs);

        pdfDoc.close();

        return new UserCV(nameAndSurname, lastRole, getFullExperience(jobs), null, skillsApp.toString(), "", eduList, null, jobs);

    }

    public List<Experience> extractExperience(PdfDocument pdfDoc, Rectangle rect){
        List<Experience> jobs = new ArrayList<Experience>();

        ExperienceRoleFilter roleFilter = new ExperienceRoleFilter(rect);

        Queue<String> roles = getElementsByFilter(pdfDoc, roleFilter);

        ExperienceCompanyNamesFilter companyNamesFilter = new ExperienceCompanyNamesFilter(rect);

        Queue<String> companyNames = getElementsByFilter(pdfDoc, companyNamesFilter);

        ExperienceTimeFilter dur = new ExperienceTimeFilter(rect);

        Queue<String> duration = getElementsByFilter(pdfDoc, dur);


        while(!companyNames.isEmpty()){
            var exp = new Experience(roles.poll(), companyNames.poll(), duration.poll());
            jobs.add(exp);
        }

        return jobs;
    }

    public String getFullExperience(List<Experience> experienceDtoList){
        String monthLabel = "month";
        String yearLabel = "year";

        int year = 0;
        int month = 0;

        for (Experience experience : experienceDtoList){
            var element = experience.getTimePeriod().substring(experience.getTimePeriod().indexOf("(") + 1, experience.getTimePeriod().indexOf(")")).split(" ");
            if (element.length == 4){
                year += Integer.parseInt(element[0]);
                month += Integer.parseInt(element[2]);
            }
            if (element.length == 2){
                month += Integer.parseInt(element[0]);
            }
        }
        if (month != 0) {
            year += month / 12;
        }
        month %= 12;
        if (year >= 0 && !yearLabel.contains("s")) {
            yearLabel += "s";
        }
        if (month >= 0 && !monthLabel.contains("s")) {
            monthLabel += "s";
        }
        return String.format("%s %s %s %s", year, yearLabel, month, monthLabel);
    }

    public String extractLastRole(List<Experience> expList){
        if (expList.size() == 0){
            return "";
        }
        return expList.get(0).getJobRole();
    }

    private PdfDocument openPdf(){
        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfReader("cv4.pdf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pdfDoc;
    }

    public Queue<String> getElementsByFilter(PdfDocument pdfDocument, BaseFilter filter) {
        FilteredEventListener listener = new FilteredEventListener();
        LocationTextExtractionStrategy extractionStrategy = listener
                .attachEventListener(new LocationTextExtractionStrategy(), filter);
        PdfCanvasProcessor parser = new PdfCanvasProcessor(listener);
        String act = "";
        for (int i = 1; i <= pdfDocument.getNumberOfPages() ; i++) {
            parser.processPageContent(pdfDocument.getPage(i));
            act = extractionStrategy.getResultantText();
        }
        return filter.getElements();
    }

    public String getNameAndSurname(PdfDocument pdfDoc) {
        SimpleTextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
        PdfCanvasProcessor proc = new PdfCanvasProcessor(strategy);
        proc.processPageContent(pdfDoc.getFirstPage());
        System.out.println(strategy.getResultantText());
        return strategy.getResultantText().split(System.lineSeparator())[0];
    }

    public List<DataBaseCV> findByNameAndSurname(String nameAndSurname){
        var cryptedNameAndSurname = cryptService.encrypt(nameAndSurname);

        List<CvEntity> matching = cvRepository.findByFullName(cryptedNameAndSurname);
        if (matching.size() == 0){
            return Collections.emptyList();
        }
        List<DataBaseCV> DbCvs = new ArrayList<>();
        for (CvEntity cv : matching){
            cv.setFullName(nameAndSurname);
            var cvDto = DataBaseCV.of(cv);
            DbCvs.add(cvDto);
        }

        return DbCvs;
    }
}
