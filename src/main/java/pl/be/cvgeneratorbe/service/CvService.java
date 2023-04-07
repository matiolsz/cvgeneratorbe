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
import org.springframework.web.multipart.MultipartFile;
import pl.be.cvgeneratorbe.dto.DataBaseCV;
import pl.be.cvgeneratorbe.dto.Education;
import pl.be.cvgeneratorbe.dto.Experience;
import pl.be.cvgeneratorbe.dto.UserCV;
import pl.be.cvgeneratorbe.entity.CvEntity;
import pl.be.cvgeneratorbe.filters.profile.NameSurnameFilter;
import pl.be.cvgeneratorbe.filters.profile.ProfileExperienceCompanyNameFilter;
import pl.be.cvgeneratorbe.filters.BaseFilter;
import pl.be.cvgeneratorbe.filters.profile.ProfileExperienceRoleFilter;
import pl.be.cvgeneratorbe.filters.resume.EducationDescriptionFilter;
import pl.be.cvgeneratorbe.filters.resume.EducationDurationFilter;
import pl.be.cvgeneratorbe.filters.resume.EducationNameFilter;
import pl.be.cvgeneratorbe.filters.resume.ExperienceCompanyNamesFilter;
import pl.be.cvgeneratorbe.filters.resume.ExperienceRoleFilter;
import pl.be.cvgeneratorbe.filters.resume.ExperienceTimeFilter;
import pl.be.cvgeneratorbe.filters.resume.SkillsFilter;
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

    public DataBaseCV saveCV(UserCV userCv) {
        var cvEntity = CvEntity.of(userCv);

        cvEntity.withCryptDecryptedName(cryptService.encrypt(cvEntity.getFullName()));

        var saved = cvRepository.save(cvEntity);

        saved.withCryptDecryptedName(cryptService.decrypt(cvEntity.getFullName()));

        return DataBaseCV.of(saved);
    }

    public UserCV parseFromResume(MultipartFile file) {
        PdfDocument pdfDoc = openPdf(file);
        Rectangle rect = new Rectangle(PageSize.A4);

        var schoolNamesFilter = new EducationNameFilter(rect);
        var names = getElementsByFilter(pdfDoc, schoolNamesFilter);

        EducationDescriptionFilter educationDescriptionFilter = new EducationDescriptionFilter(rect);
        var degree = getElementsByFilter(pdfDoc, educationDescriptionFilter);

        var eduDatesFilter = new EducationDurationFilter(rect);
        var years = getElementsByFilter(pdfDoc, eduDatesFilter);

        var nameAndSurname = getNameAndSurnameFromResume(pdfDoc);

        List<Education> eduList = new ArrayList<>();
        SkillsFilter skillsFilter = new SkillsFilter(rect);
        var skills = getElementsByFilter(pdfDoc, skillsFilter);
        var skillsApp = new ArrayList<>();
        while (!skills.isEmpty()) {
            if (skills.peek().contains(nameAndSurname)) {
                skills.poll();
            }

            skillsApp.add(skills.poll());
        }

        while (!years.isEmpty()) {
            eduList.add(new Education(names.poll(), degree.poll(), years.poll()));
        }

        List<Experience> jobs = extractExperience(pdfDoc, rect);

        String lastRole = extractLastRole(jobs);

        pdfDoc.close();

        return new UserCV(nameAndSurname, lastRole, getFullExperience(jobs), null, skillsApp.toString(), "", eduList, null, jobs);

    }

    public List<Experience> extractExperience(PdfDocument pdfDoc, Rectangle rect) {
        List<Experience> jobs = new ArrayList<Experience>();

        ExperienceRoleFilter roleFilter = new ExperienceRoleFilter(rect);

        Queue<String> roles = getElementsByFilter(pdfDoc, roleFilter);

        ExperienceCompanyNamesFilter companyNamesFilter = new ExperienceCompanyNamesFilter(rect);

        Queue<String> companyNames = getElementsByFilter(pdfDoc, companyNamesFilter);

        ExperienceTimeFilter dur = new ExperienceTimeFilter(rect);

        Queue<String> duration = getElementsByFilter(pdfDoc, dur);


        while (!companyNames.isEmpty()) {
            var exp = new Experience(roles.poll(), companyNames.poll(), duration.poll());
            jobs.add(exp);
        }

        return jobs;
    }

    public String getFullExperience(List<Experience> experienceDtoList) {
        String monthLabel = "month";
        String yearLabel = "year";

        int year = 0;
        int month = 0;

        for (Experience experience : experienceDtoList) {
            var element = experience.getTimePeriod().substring(experience.getTimePeriod().indexOf("(") + 1, experience.getTimePeriod().indexOf(")")).split(" ");
            if (element.length == 4) {
                year += Integer.parseInt(element[0]);
                month += Integer.parseInt(element[2]);
            }
            if (element.length == 2) {
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

    public String extractLastRole(List<Experience> expList) {
        if (expList.size() == 0) {
            return "";
        }
        return expList.get(0).getJobRole();
    }

    private PdfDocument openPdf(MultipartFile file) {
        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfReader(file.getInputStream()));
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
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            parser.processPageContent(pdfDocument.getPage(i));
        }
        return filter.getElements();
    }

    public String getNameAndSurnameFromResume(PdfDocument pdfDoc) {
        SimpleTextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
        PdfCanvasProcessor proc = new PdfCanvasProcessor(strategy);
        proc.processPageContent(pdfDoc.getFirstPage());
        return strategy.getResultantText().split(System.lineSeparator())[0];
    }

    public List<DataBaseCV> findByNameAndSurname(String nameAndSurname) {
        var cryptedNameAndSurname = cryptService.encrypt(nameAndSurname);

        List<CvEntity> matching = cvRepository.findByFullName(cryptedNameAndSurname);
        if (matching.size() == 0) {
            return Collections.emptyList();
        }
        List<DataBaseCV> DbCvs = new ArrayList<>();
        for (CvEntity cv : matching) {
            cv.setFullName(nameAndSurname);
            var cvDto = DataBaseCV.of(cv);
            DbCvs.add(cvDto);
        }

        return DbCvs;
    }

    public UserCV parseFromProfile(MultipartFile file) {
        PdfDocument pdfDoc = openPdf(file);
        Rectangle rect = new Rectangle(PageSize.A4);

        var nameAndSurname = getNameAndSurnameFromProfile(pdfDoc);
        ProfileExperienceCompanyNameFilter pecnf = new ProfileExperienceCompanyNameFilter(rect);
        var v1 = getElementsByFilter(pdfDoc, pecnf);

        System.out.println(v1);

        ProfileExperienceRoleFilter perf = new ProfileExperienceRoleFilter(rect);
        var role = getElementsByFilter(pdfDoc, perf);
        System.out.println(role);


        return new UserCV(nameAndSurname, null, null, null, null, null, null, null, null);
    }

    public String getNameAndSurnameFromProfile(PdfDocument document) {
        FilteredEventListener listener = new FilteredEventListener();
        LocationTextExtractionStrategy extractionStrategy = listener.attachEventListener(new LocationTextExtractionStrategy(), new NameSurnameFilter(new Rectangle(PageSize.A4)));
        PdfCanvasProcessor parser = new PdfCanvasProcessor(listener);
        parser.processPageContent(document.getFirstPage());
        return extractionStrategy.getResultantText();
    }
}
