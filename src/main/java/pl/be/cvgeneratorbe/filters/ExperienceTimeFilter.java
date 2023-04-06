package pl.be.cvgeneratorbe.filters;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

public class ExperienceTimeFilter extends BaseFilter {

    private static final String START_BLOCK = "Experience";
    private static final String END_BLOCK = "Education";

    /*
        Regex matches:
        Feb 2022 - Feb 2022 (1 year 2 months)
        Feb 2022 - Feb 2022 (2 months)
        Feb 2022 - Present (1 year 2 months)
        Feb 2022 - Present (2 months)
    */
    private static final String REGEX = "^[A-Za-z]{3} [0-9]{4} - [A-Za-z]{3,7} [0-9]{0,4} ?\\([0-9]{1,2} (year|years|months|month) ?(\\)|[0-9]{1,2} (months|month))\\)?$";

    private static final String FONT_NAME = "EAAAAA+ArialUnicodeMS";
    private static final float FONT_SIZE = 10.0f;



    public ExperienceTimeFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            if (setBlock(renderInfo, START_BLOCK, END_BLOCK)) {
                if (isFontMatch(renderInfo, FONT_NAME, FONT_SIZE) && isRegexMatch(renderInfo, REGEX)) {
                    super.elements.add(renderInfo.getText());
                    return true;
                }
            }
        }
        return false;
    }
}
