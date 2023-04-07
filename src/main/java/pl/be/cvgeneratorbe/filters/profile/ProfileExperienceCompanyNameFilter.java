package pl.be.cvgeneratorbe.filters.profile;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import pl.be.cvgeneratorbe.filters.BaseFilter;

public class ProfileExperienceCompanyNameFilter extends BaseFilter {

    private static final String BLOCK_START = "Experience";
    private static final String BLOCK_END = "Education";
    private static final String BLOCK_FONT_NAME = "EAAAAA+ArialUnicodeMS";
    private static final String LINE_FONT_NAME = "EAAAAA+ArialUnicodeMS";
    private static final Float LINE_FONT_SIZE = 12.0f;

    public ProfileExperienceCompanyNameFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
                if (
                        setBlockByNamesAndFont(renderInfo, BLOCK_START, BLOCK_END, BLOCK_FONT_NAME) &&
                        isFontMatch(renderInfo, LINE_FONT_NAME, LINE_FONT_SIZE) && renderInfo.getText().length() > 1
                ) {
                    super.elements.add(renderInfo.getText());
                return true;
            }
        }
        return false;
    }
}
