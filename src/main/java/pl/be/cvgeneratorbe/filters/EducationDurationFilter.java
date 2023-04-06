package pl.be.cvgeneratorbe.filters;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

public class EducationDurationFilter extends BaseFilter {

    private static final String START_BLOCK = "Education";
    private static final String END_BLOCK = "Skills";
    private static final String BLOCK_FONT_NAME = "EAAAAB+Arial-BoldMT";
    private static final String LINE_FONT_NAME = "EAAAAA+ArialUnicodeMS";
    private static final float LINE_FONT_SIZE = 10.0f;
    private static final String REGEX = "[0-9]{4} - [A-Za-z0-9]{4,8}";
    public EducationDurationFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
                if (
                        setBlockByNamesAndFont(renderInfo, START_BLOCK, END_BLOCK, BLOCK_FONT_NAME) &&
                        isFontMatch(renderInfo, LINE_FONT_NAME, LINE_FONT_SIZE) &&
                        isRegexMatch(renderInfo, REGEX)
                ) {
                    elements.add(renderInfo.getText());
                }
                return true;
            }
        return false;
    }
}
