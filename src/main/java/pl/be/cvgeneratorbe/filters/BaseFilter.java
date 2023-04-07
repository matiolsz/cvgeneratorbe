package pl.be.cvgeneratorbe.filters;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class BaseFilter extends TextRegionEventFilter {

    private Boolean isInBlock = false;

    public Boolean setBlockByNamesAndFont(TextRenderInfo renderInfo,
                                          String startBlock,
                                          String endBlock,
                                          String fontName) {
        if (renderInfo.getText().equals(startBlock)
                && renderInfo.getFont().getFontProgram().getFontNames().getFontName().contains(fontName)) {
            this.isInBlock = true;
        }
        if (renderInfo.getText().contains(endBlock)) {
            this.isInBlock = false;
        }
        return isInBlock;
    }

    public Boolean setBlock(TextRenderInfo renderInfo,
                            String startBlock,
                            String endBlock) {
        if (renderInfo.getText().equals(startBlock)) {
            this.isInBlock = true;
        }
        if (renderInfo.getText().contains(endBlock)) {
            this.isInBlock = false;
        }
        return isInBlock;
    }

    protected Queue<String> elements = new LinkedList<>();

    public BaseFilter(Rectangle filterRect) {
        super(filterRect);
    }

    public Queue<String> getElements() {
        return this.elements;
    }

    public Boolean isFontMatch(TextRenderInfo renderInfo, String fontName, Float fontSize){
        PdfFont font = renderInfo.getFont();
        float size = renderInfo.getFontSize();
        return isFontNameMatch(font, fontName) && isFontSizeMatch(size, fontSize);
    }

    private boolean isFontNameMatch(PdfFont font, String fontName){
        return font.getFontProgram().getFontNames().getFontName().contains(fontName);
    }

    private boolean isFontSizeMatch(float expectedFontSize, float actualFontSize){
        return Objects.equals(expectedFontSize, actualFontSize);
    }

    public boolean isRegexMatch(TextRenderInfo renderInfo, String regex){
        return renderInfo.getText().matches(regex);
    }
}
