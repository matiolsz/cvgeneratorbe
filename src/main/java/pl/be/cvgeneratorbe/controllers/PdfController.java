package pl.be.cvgeneratorbe.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.be.cvgeneratorbe.dto.UserCV;
import pl.be.cvgeneratorbe.service.PdfGenerator;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PdfController {

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

    @RequestMapping(value="/getpdf", method= RequestMethod.POST)
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
                             body(new byte[bis.available()]);
    }

}
