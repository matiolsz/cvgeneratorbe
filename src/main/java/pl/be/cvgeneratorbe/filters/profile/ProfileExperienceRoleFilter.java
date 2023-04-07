package pl.be.cvgeneratorbe.filters.profile;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import pl.be.cvgeneratorbe.filters.BaseFilter;

public class ProfileExperienceRoleFilter extends BaseFilter {

    public ProfileExperienceRoleFilter(Rectangle filterRect) {
        super(filterRect);
    }

    public static String BLOCK_START = "Experience";

    public static String BLOCK_END = "Education";

    //The same as block font
    public static String FONT = "EAAAAA+ArialUnicodeMS";

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)){
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            if (
                    setBlockByNamesAndFont(renderInfo, BLOCK_START, BLOCK_END, FONT) &&
                    isFontMatch(renderInfo, FONT, 11.5f)){
                super.elements.add(renderInfo.getText());
                return true;
            }
        }
        return false;
    }
}
