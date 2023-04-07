package pl.be.cvgeneratorbe.filters.profile;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;

public class NameSurnameFilter extends TextRegionEventFilter {

    public NameSurnameFilter(Rectangle filterRect) {
        super(filterRect);
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            if (renderInfo.getFontSize() == 26.0f && renderInfo.getFont().getFontProgram().getFontNames().getFontName().equals("EAAAAA+ArialUnicodeMS")) {
                return true;
            }
        }
        return false;
    }
}
