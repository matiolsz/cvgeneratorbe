package pl.be.cvgeneratorbe.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.be.cvgeneratorbe.dto.UserCV;
import pl.be.cvgeneratorbe.service.PdfGenerator;
import pl.be.cvgeneratorbe.service.PdfParser;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PdfController {

    PdfParser pdfParser;

    PdfController(PdfParser pdfParser) {
        this.pdfParser = pdfParser;
    }
    PdfGenerator pdfGenerator;

    @PostMapping(value = "/generate",
            produces = "application/vnd.openxmlformats-"
                    + "officedocument.wordprocessingml.document")
    public ResponseEntity<InputStreamResource> word(@RequestBody UserCV userCV)
            throws IOException {

        ByteArrayInputStream bis = PdfGenerator.generateFromOldTemplate(userCV);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "inline; filename=mydoc.docx");
        return ResponseEntity.ok().headers(headers).
                             body(new InputStreamResource(bis));
    }

    @PostMapping(value = "/getpdf")
    public ResponseEntity<byte[]> getPDF(@RequestBody UserCV userCV) throws IOException {

        // generate the file
        ByteArrayInputStream bis = PdfGenerator.generateFromNewTemplate(userCV);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.ok().headers(headers).
                             body(bis.readAllBytes());
    }

    @PostMapping(value = "/parsecv")
    public ResponseEntity<UserCV> parseCv(@RequestParam("file") MultipartFile file) throws IOException {
        UserCV userCV = pdfParser.parseLinkedInCv(file);
        return ResponseEntity.status(HttpStatus.OK).body(userCV);
    }
}
