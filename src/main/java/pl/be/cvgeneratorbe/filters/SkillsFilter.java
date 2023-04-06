package pl.be.cvgeneratorbe.filters;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

public class SkillsFilter extends BaseFilter {

    private boolean isInSkillBlock = false;

    public SkillsFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            PdfFont font = renderInfo.getFont();
            if(renderInfo.getText().contains("Skills")){
                this.isInSkillBlock = true;
                return false;
            }
            if (null != font && isInSkillBlock) {
                super.elements.add(((TextRenderInfo) data).getText().replace("•", "").replace("     ", "").trim());
            }
        }
        return false;
    }
}
