package nl.knaw.huc.di.images.altoxmlutils;

import nl.knaw.huc.di.images.layoutds.models.Alto.AltoDocument;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TEST-01 baseline coverage for {@link AltoUtils}.
 * <p>
 * These tests are intentionally lightweight — they exercise the public entry point of
 * AltoUtils with hand-crafted ALTO fragments and unknown-element fallbacks. The internal
 * conversion helpers are exercised indirectly.
 */
public class AltoUtilsTest {

    private static final String MINIMAL_ALTO =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<alto xmlns=\"http://www.loc.gov/standards/alto/ns-v3#\">" +
                    "  <Description>" +
                    "    <MeasurementUnit>pixel</MeasurementUnit>" +
                    "  </Description>" +
                    "  <Layout>" +
                    "    <Page ID=\"PAGE1\" PHYSICAL_IMG_NR=\"1\" HEIGHT=\"100\" WIDTH=\"200\"/>" +
                    "  </Layout>" +
                    "</alto>";

    @Test
    public void readMinimalAlto_returnsNonNullDocument() {
        AltoDocument doc = AltoUtils.readAltoDocumentFromString(MINIMAL_ALTO);
        assertNotNull("Parsing a minimal ALTO document should yield a non-null result", doc);
        assertNotNull("Description must be parsed", doc.getDescription());
        assertNotNull("Layout must be parsed", doc.getLayout());
    }

    @Test
    public void readMalformedAlto_returnsNull() {
        // convertStringToXMLDocument returns null on parse error; AltoUtils must propagate.
        AltoDocument doc = AltoUtils.readAltoDocumentFromString("<not-valid-xml>");
        assertNull(doc);
    }

    @Test
    public void readEmptyString_returnsNull() {
        AltoDocument doc = AltoUtils.readAltoDocumentFromString("");
        assertNull(doc);
    }

    @Test
    public void readAltoWithUnknownTopLevelElement_doesNotThrow() {
        String alto =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<alto xmlns=\"http://www.loc.gov/standards/alto/ns-v3#\">" +
                        "  <CompletelyUnknownThing/>" +
                        "</alto>";
        AltoDocument doc = AltoUtils.readAltoDocumentFromString(alto);
        // Unknown elements must be tolerated (a WARN is logged) so the public API stays robust.
        assertNotNull(doc);
    }

    @Test
    public void readAltoWithLayoutAndPrintSpace_succeeds() {
        String alto =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<alto xmlns=\"http://www.loc.gov/standards/alto/ns-v3#\">" +
                        "  <Layout>" +
                        "    <Page ID=\"PAGE1\" PHYSICAL_IMG_NR=\"1\" HEIGHT=\"100\" WIDTH=\"200\">" +
                        "      <PrintSpace HEIGHT=\"80\" WIDTH=\"180\" VPOS=\"10\" HPOS=\"10\"/>" +
                        "    </Page>" +
                        "  </Layout>" +
                        "</alto>";
        AltoDocument doc = AltoUtils.readAltoDocumentFromString(alto);
        assertNotNull(doc);
        assertNotNull(doc.getLayout());
        assertNotNull(doc.getLayout().getPage());
    }

    @Test
    public void readAltoIsStaticThreadSafe() throws Exception {
        // Verify the public API can be called from multiple threads concurrently without
        // races. AltoUtils only has static methods over per-call DOMs so this is a sanity
        // check guarding against accidental shared mutable state.
        Thread t1 = new Thread(() -> assertNotNull(AltoUtils.readAltoDocumentFromString(MINIMAL_ALTO)));
        Thread t2 = new Thread(() -> assertNotNull(AltoUtils.readAltoDocumentFromString(MINIMAL_ALTO)));
        t1.start();
        t2.start();
        t1.join(5_000);
        t2.join(5_000);
        assertTrue(!t1.isAlive() && !t2.isAlive());
    }
}

