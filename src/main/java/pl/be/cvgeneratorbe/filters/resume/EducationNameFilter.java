package pl.be.cvgeneratorbe.filters.resume;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import pl.be.cvgeneratorbe.filters.BaseFilter;

public class EducationNameFilter extends BaseFilter {

    private static final String BLOCK_START = "Education";
    private static final String BLOCK_END = "Skills";
    private static final String LINE_FONT_NAME = "EAAAAB+Arial-BoldMT";
    private static final float LINE_FONT_SIZE = 12.0f;
    public EducationNameFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            if (setBlockByNamesAndFont(renderInfo, BLOCK_START, BLOCK_END, LINE_FONT_NAME) && isFontMatch(renderInfo, LINE_FONT_NAME, LINE_FONT_SIZE)) {
                elements.add(renderInfo.getText());
                return true;
            }
        }
        return false;
    }
}
