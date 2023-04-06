package pl.be.cvgeneratorbe.filters;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;


public class ExperienceRoleFilter extends BaseFilter {

    private static final String START_BLOCK = "Experience";
    private static final String END_BLOCK = "Education";

    private static final String FONT_NAME = "EAAAAB+Arial-BoldMT";
    private static final float FONT_SIZE = 12.0f;

    Boolean isInEducationBlock = false;

    public ExperienceRoleFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            if (
                    setBlock(renderInfo, START_BLOCK, END_BLOCK) &&
                            isFontMatch(renderInfo, FONT_NAME, FONT_SIZE)
            ) {
                super.elements.add(renderInfo.getText());
            }
        }
        return false;
    }
}

