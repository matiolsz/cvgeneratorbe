package pl.be.cvgeneratorbe.filters;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

public class EducationDescriptionFilter extends BaseFilter {

    private static final String BLOCK_START = "Education";
    private static final String BLOCK_END = "Skills";
    private static final String BLOCK_FONT_NAME = "EAAAAB+Arial-BoldMT";
    private static final String LINE_FONT_NAME = "EAAAAA+ArialUnicodeMS";
    private static final Float LINE_FONT_SIZE = 12.0f;

    public EducationDescriptionFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
                if (
                        setBlockByNamesAndFont(renderInfo, BLOCK_START, BLOCK_END, BLOCK_FONT_NAME) &&
                        isFontMatch(renderInfo, LINE_FONT_NAME, LINE_FONT_SIZE)
                ) {
                    super.elements.add(renderInfo.getText());
                return true;
            }
        }
        return false;
    }
}
