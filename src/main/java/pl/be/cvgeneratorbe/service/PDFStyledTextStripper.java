package pl.be.cvgeneratorbe.service;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;

public class PDFStyledTextStripper extends PDFTextStripper {
    public PDFStyledTextStripper() throws IOException {
        super();
        registerOperatorProcessor("re", new AppendRectangleToPath());
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        for (TextPosition textPosition : textPositions) {
            Set<String> style = determineStyle(textPosition);
            if (!style.equals(currentStyle)) {
                output.write(style.toString());
                currentStyle = style;
            }
            output.write(textPosition.getUnicode());
        }
    }

    Set<String> determineStyle(TextPosition textPosition) {
        Set<String> result = new HashSet<>();

        if (textPosition.getFont().getName().toLowerCase().contains("bold"))
            result.add("Bold");

        if (textPosition.getFont().getName().toLowerCase().contains("italic"))
            result.add("Italic");

        if (rectangles.stream().anyMatch(r -> r.underlines(textPosition)))
            result.add("Underline");

        if (rectangles.stream().anyMatch(r -> r.strikesThrough(textPosition)))
            result.add("StrikeThrough");

        return result;
    }

    class AppendRectangleToPath extends OperatorProcessor
    {
        Point2D.Double transformedPoint(double x, double y)
        {
            double[] position = {x,y};
            getGraphicsState().getCurrentTransformationMatrix().createAffineTransform().transform(
                    position, 0, position, 0, 1);
            return new Point2D.Double(position[0],position[1]);
        }

        @Override
        public void process(Operator operator, List<COSBase> arguments) throws IOException {
            COSNumber x = (COSNumber) arguments.get(0);
            COSNumber y = (COSNumber) arguments.get(1);
            COSNumber w = (COSNumber) arguments.get(2);
            COSNumber h = (COSNumber) arguments.get(3);

            double x1 = x.doubleValue();
            double y1 = y.doubleValue();

            // create a pair of coordinates for the transformation
            double x2 = w.doubleValue() + x1;
            double y2 = h.doubleValue() + y1;

            Point2D p0 = transformedPoint(x1, y1);
            Point2D p1 = transformedPoint(x2, y1);
            Point2D p2 = transformedPoint(x2, y2);
            Point2D p3 = transformedPoint(x1, y2);

            rectangles.add(new TransformedRectangle(p0, p1, p2, p3));
        }

        @Override
        public String getName() {
            return "re";
        }
    }

    static class TransformedRectangle
    {
        public TransformedRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3)
        {
            this.p0 = p0;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

        boolean strikesThrough(TextPosition textPosition)
        {
            Matrix matrix = textPosition.getTextMatrix();
            // TODO: This is a very simplistic implementation only working for horizontal text without page rotation
            // and horizontal rectangular strikeThroughs with p0 at the left bottom and p2 at the right top

            // Check if rectangle horizontally matches (at least) the text
            if (p0.getX() > matrix.getXPosition() || p2.getX() < matrix.getXPosition() + textPosition.getWidth() - textPosition.getFontSizeInPt() / 10.0)
                return false;
            // Check whether rectangle vertically is at the right height to underline
            double vertDiff = p0.getY() - matrix.getYPosition();
            if (vertDiff < 0 || vertDiff > textPosition.getFont().getFontDescriptor().getAscent() * textPosition.getFontSizeInPt() / 1000.0)
                return false;
            // Check whether rectangle is small enough to be a line
            return Math.abs(p2.getY() - p0.getY()) < 2;
        }

        boolean underlines(TextPosition textPosition)
        {
            Matrix matrix = textPosition.getTextMatrix();
            // TODO: This is a very simplistic implementation only working for horizontal text without page rotation
            // and horizontal rectangular underlines with p0 at the left bottom and p2 at the right top

            // Check if rectangle horizontally matches (at least) the text
            if (p0.getX() > matrix.getXPosition() || p2.getX() < matrix.getXPosition() + textPosition.getWidth() - textPosition.getFontSizeInPt() / 10.0)
                return false;
            // Check whether rectangle vertically is at the right height to underline
            double vertDiff = p0.getY() - matrix.getYPosition();
            if (vertDiff > 0 || vertDiff < textPosition.getFont().getFontDescriptor().getDescent() * textPosition.getFontSizeInPt() / 500.0)
                return false;
            // Check whether rectangle is small enough to be a line
            return Math.abs(p2.getY() - p0.getY()) < 2;
        }

        final Point2D p0, p1, p2, p3;
    }

    final List<TransformedRectangle> rectangles = new ArrayList<>();
    Set<String> currentStyle = Collections.singleton("Undefined");
}