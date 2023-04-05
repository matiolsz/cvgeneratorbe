package pl.be.cvgeneratorbe;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import pl.be.cvgeneratorbe.service.PdfParser;

public class PdfParserTest {

    @Test
    public void runMethod() throws IOException {
        PdfParser pdfParser = new PdfParser();
        pdfParser.parseLinkedInCv();
    }
}
