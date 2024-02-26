package nl.knaw.huc.di.images.pagexmlutils;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.imageanalysiscommon.UnicodeToAsciiTranslitirator;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.layoutds.models.Page.Label;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.primaresearch.dla.page.io.xml.XmlPageReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.*;

import static nl.knaw.huc.di.images.stringtools.StringTools.convertStringToXMLDocument;

public class PageUtils {

    public static final UnicodeToAsciiTranslitirator UNICODE_TO_ASCII_TRANSLITIRATOR = new UnicodeToAsciiTranslitirator();
    public static final String NAMESPACE2013 = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15";
    public static final String NAMESPACE2019 = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15";
    private static final int BLACK = 0;

    public static HashMap<String, Integer> extractRegionTypes(boolean ignoreCase, String baseInput) throws IOException {
        Path inputPath = Paths.get(baseInput);
        HashMap<String, Integer> types = new HashMap<>();

        try (DirectoryStream<Path> files = Files.newDirectoryStream(inputPath)) {
            for (Path file : files) {
                if (file.getFileName().toString().endsWith(".xml")) {
                    System.out.println(file.toAbsolutePath().toString());
                    PcGts page = PageUtils.readPageFromFile(file.toAbsolutePath());

                    if (page.getMetadata().getTranskribusMetadata() != null &&
                            (!"GT".equals(page.getMetadata().getTranskribusMetadata().getStatus()) &&
                                    !"FINAL".equals(page.getMetadata().getTranskribusMetadata().getStatus())
                                    &&
                                    !"DONE".equals(page.getMetadata().getTranskribusMetadata().getStatus())

                            )
                    ) {
                        continue;
                    }
                    for (TextRegion textRegion : page.getPage().getTextRegions()) {

                        String type = null;
                        if (textRegion.getCustom() != null) {
                            String[] splitted = textRegion.getCustom().split(" ");
                            for (int i = 0; i < splitted.length; i++) {
                                if (splitted[i].equals("structure")) {
                                    type = splitted[i + 1]
                                            .replace("{", "")
                                            .replace("}", "")
                                            .replace(";", "")
                                            .split(":")[1];
                                    if (ignoreCase) {
                                        type = type.toLowerCase();
                                    }
                                }
                            }
                        }

                        if (type != null) {
                            if (!types.containsKey(type)) {
                                types.put(type, 0);
                            } else {
                                types.put(type, types.get(type) + 1);
                            }
                        }

                        if (textRegion.getRegionType() != null) {
                            if (ignoreCase) {
                                type = textRegion.getRegionType().toLowerCase();
                            }
                            if (!types.containsKey(type)) {
                                types.put(type, 0);
                            } else {
                                types.put(type, types.get(type) + 1);
                            }
                        }

                    }

                }
            }
        }

        for (String type : types.keySet()) {
            System.out.println(type + " : " + types.get(type));
        }
        return types;
    }

    public static String getTextRegionType(TextRegion textRegion) {
        String type = textRegion.getRegionType();
        if (textRegion.getCustom() != null) {
            String[] splitted = textRegion.getCustom().split(" ");
            for (int i = 0; i < splitted.length; i++) {
                if (splitted[i].equals("structure")) {
                    String tmpType = splitted[i + 1]
                            .replace("{", "")
                            .replace("}", "")
                            .replace(";", "")
                            .split(":")[1];
                    if (!Strings.isNullOrEmpty(tmpType) && !tmpType.equalsIgnoreCase("None")) {
                        type = tmpType;
                    }
                }
            }
        }
        return type;
    }

    public static void findErrors(String baseInput) throws IOException {
        Path inputPath = Paths.get(baseInput);
        try (DirectoryStream<Path> files = Files.newDirectoryStream(inputPath)) {
            for (Path file : files) {
                Mat colorized = null;
                if (file.getFileName().toString().endsWith(".xml")) {
                    System.out.println(file.toAbsolutePath().toString());
                    String pageXml = StringTools.readFile(file.toAbsolutePath().toString());
                    PcGts page = PageUtils.readPageFromString(pageXml);
                    if (page == null) {
                        System.out.println("Can get PageXML: " + file.getFileName().toString());
                        continue;
                    }
                    if (page.getMetadata().getTranskribusMetadata() != null &&
                            (!"GT".equals(page.getMetadata().getTranskribusMetadata().getStatus()) &&
                                    !"FINAL".equals(page.getMetadata().getTranskribusMetadata().getStatus())
                                    &&
                                    !"DONE".equals(page.getMetadata().getTranskribusMetadata().getStatus())

                            )
                    ) {
                        continue;
                    }
                    for (TextRegion textRegion : page.getPage().getTextRegions()) {
                        String type = getTextRegionType(textRegion);
                        if (type == null) {
                            System.out.println("Error type missing for TextRegion: " + file.getFileName().toString());
                            if (colorized == null) {
                                colorized = Imgcodecs.imread(FilenameUtils.removeExtension(file.toAbsolutePath().toString()) + ".jpg");
                            }
                            PageColorizer.colorizeTextRegion(colorized, textRegion, false);
                        }

                        for (TextLine textline : textRegion.getTextLines()) {
                            if (textline.getBaseline() == null || Strings.isNullOrEmpty(textline.getBaseline().getPoints())) {
                                System.out.println("Error baseline missing: " + file.getFileName().toString());
                                if (colorized == null) {
                                    colorized = Imgcodecs.imread(FilenameUtils.removeExtension(file.toAbsolutePath().toString()) + ".jpg");
                                }
                                PageColorizer.colorizeTextLine(colorized, textline);
                            }
                        }
                        for (TextLine textline : textRegion.getTextLines()) {
                            if ("DONE".equals(page.getMetadata().getTranskribusMetadata().getStatus())) {
                                break;
                            }
                            TextEquiv textEquiv = textline.getTextEquiv();
                            if (textEquiv == null || Strings.isNullOrEmpty(textEquiv.getUnicode())) {
                                System.out.println("Empty textline: " + file.getFileName().toString());
                                if (colorized == null) {
                                    colorized = Imgcodecs.imread(FilenameUtils.removeExtension(file.toAbsolutePath().toString()) + ".jpg");
                                }
                                PageColorizer.colorizeTextLine(colorized, textline);
                            }
                        }
                    }
                    if (colorized != null) {
                        String outputFile = FilenameUtils.removeExtension(file.getFileName().toString()) + ".jpg";
                        Imgcodecs.imwrite("/tmp/errors/" + outputFile, colorized);
                        colorized.release();
                    }

                }
            }
        }
    }

    private static void findTypes(String baseInput, String searchType) throws IOException {
        Path inputPath = Paths.get(baseInput);
        try (DirectoryStream<Path> files = Files.newDirectoryStream(inputPath)) {
            for (Path file : files) {
                if (file.getFileName().toString().endsWith(".xml")) {
                    String pageXml = StringTools.readFile(file.toAbsolutePath().toString());
                    PcGts page = readPageFromString(pageXml);
                    if (page == null) {
                        System.out.println("Can get PageXML: " + file.getFileName().toString());
                        continue;
                    }

                    if (page.getMetadata().getTranskribusMetadata() != null &&
                            (!"GT".equals(page.getMetadata().getTranskribusMetadata().getStatus()) &&
                                    !"FINAL".equals(page.getMetadata().getTranskribusMetadata().getStatus())
                                    &&
                                    !"DONE".equals(page.getMetadata().getTranskribusMetadata().getStatus())

                            )
                    ) {
                        continue;
                    }
                    for (TextRegion textRegion : page.getPage().getTextRegions()) {
                        String type = getTextRegionType(textRegion);
                        if (searchType.equalsIgnoreCase(type)) {
                            System.out.println(searchType + ": " + file.getFileName());
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String baseInput = "/home/rutger/src/republic/ARU-Net/train_images";
        findErrors(baseInput);
//        HashMap<String, Integer> types = extractRegionTypes(true, baseInput);
//
//        for (String type:types.keySet()) {
////            String type = "signature-mark";
//            findTypes(baseInput, type);
//        }
////        findErrors("/media/rutger/bf31fede-7650-4556-884c-2b0ed365db77/ijsberg/voc/");
//        findErrors("/media/rutger/bf31fede-7650-4556-884c-2b0ed365db77/ijsberg/notarieel/");

    }

    public static String convertPcGtsToString(PcGts page, String namespace) throws JsonProcessingException, TransformerException {
        XmlFactory factory = new XmlFactory(new WstxInputFactory(), new WstxOutputFactory());
        XmlMapper xmlMapper = new XmlMapper(factory);
//        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        xmlMapper.setAnnotationIntrospector(new AnnotationIntrospector(namespace));

        // Transform page to 2013 when namespace is 2013
        if (PageUtils.NAMESPACE2013.equals(namespace)) {
            final StreamSource xsltSource = new StreamSource(PageUtils.class.getResourceAsStream("/transformpage.xslt"));
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer(xsltSource);
            // pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            String pageString = xmlMapper.writeValueAsString(page);
            final StreamSource pageSource = new StreamSource(new StringReader(pageString));
            final StringWriter stringWriter = new StringWriter();
            final StreamResult streamResult = new StreamResult(stringWriter);
            transformer.transform(pageSource, streamResult);

            return stringWriter.toString();

        }
        return xmlMapper.writeValueAsString(page);
    }

    public static String convertAndValidate(PcGts page, String namespace) throws JsonProcessingException, TransformerException {
        final String pageString = convertPcGtsToString(page, namespace);
        try {
            XmlPageReader reader = PageValidator.validate(pageString);
            if (reader.getErrors().size() > 0) {
                System.err.println("Errors "+ page.getPage().getImageFilename()+": " + reader.getErrors().size());
                for ( org.primaresearch.io.xml.IOError error : reader.getErrors()) {
                    System.err.println(error.getMessage());
                }
            }
        }catch(Exception ex){
            System.err.println("Exception: " + ex.getMessage());
            System.err.println(pageString);
            throw ex;
        }
        return pageString;
    }
    public static void writePageToFileAtomic(PcGts page, String namespace, Path outputFile) throws IOException, TransformerException {
        final String pageString = convertAndValidate(page, namespace);
        StringTools.writeFileAtomic(outputFile.toFile().getAbsolutePath(), pageString, false);
    }

    public static void writePageToFile(PcGts page, String namespace, Path outputFile) throws IOException, TransformerException {
        final String pageString = convertAndValidate(page, namespace);
        StringTools.writeFile(outputFile.toFile().getAbsolutePath(), pageString, false);
    }

    public static PcGts readPageFromString(String pageXmlString) {
        Document document = convertStringToXMLDocument(pageXmlString);
        if (document == null) {
            return null;
        }
        Node documentElement = document.getFirstChild();

        PcGts pcGts = new PcGts();

        for (int i = 0; i < documentElement.getChildNodes().getLength(); i++) {
            Node node = documentElement.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Metadata")) {
                pcGts.setMetadata(getMetaData(node));
            } else if (node.getNodeName().equals("Page")) {
                pcGts.setPage(getPage(node));
            } else {
                System.out.println(documentElement.getNodeName() + " - " + node.getNodeName());
            }
        }
        return pcGts;
    }

    public static PcGts readPageFromFile(String path) throws IOException {
        String pageXmlString = StringTools.readFile(path);
        return readPageFromString(pageXmlString);
    }

    public static PcGts readPageFromFile(Path path) throws IOException {
        String pageXmlString = StringTools.readFile(path);
        return readPageFromString(pageXmlString);
    }

    private static Date getDate(Node node) {
        Date date = null;
        if (Strings.isNullOrEmpty(node.getTextContent())) {
            return null;
        }
        try {
            // Should never happen, PAGE XML should only contain UTC times
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(node.getTextContent());
            long epochMilli = offsetDateTime.toInstant().toEpochMilli();
            date = new Date(epochMilli);
        } catch (Exception ex) {
            try {
                DateTime dateTime = new DateTime(node.getTextContent(), DateTimeZone.UTC);
                date = dateTime.toDate();
            } catch (Exception subEx) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(node.getTextContent());
                    date = localDateTime.toDate(TimeZone.getDefault());
                } catch (Exception subSubEx) {
                }
            }
        }

        return date;
    }

    private static Metadata getMetaData(Node parent) {
        Metadata metadata = new Metadata();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (node.getNodeName()) {
                case "LastChange":
                    metadata.setLastChange(getDate(node));
                    break;
                case "Creator":
                    metadata.setCreator(node.getTextContent());
                    break;
                case "Created":
                    metadata.setCreated(getDate(node));
                    break;
                case "TranskribusMetadata":
                    metadata.setTranskribusMetadata(getTranskribusMetadata(node));
                    break;
                case "Comments":
                    metadata.setComments(node.getTextContent());
                    break;
                case "MetadataItem":
                    if (metadata.getMetadataItems()==null){
                        metadata.setMetadataItems(new ArrayList<>());
                    }
                    metadata.getMetadataItems().add(getMetadataItem(node));
                    break;


                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName() + " - " + node.getNodeValue());
                    break;
            }
        }
        return metadata;
    }

    private static MetadataItem getMetadataItem(Node parent) {
        MetadataItem metadataItem = new MetadataItem();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            switch (node.getNodeName()) {
                case "Labels":
                    metadataItem.setLabels(getLabels(node));
                    break;
                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName() + " - " + node.getNodeValue());
                    break;
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "type":
                    metadataItem.setType(attribute.getNodeValue());
                    break;
                case "name":
                    metadataItem.setName(attribute.getNodeValue());
                    break;
                case "value":
                    metadataItem.setValue(attribute.getNodeValue());
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }

        }
        return metadataItem;
    }

    private static Labels getLabels(Node parent) {
        Labels labels = new Labels();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            switch (node.getNodeName()) {
                case "Label":
                    if (labels.getLabel()==null){
                        labels.setLabel(new ArrayList<>());
                    }
                    labels.getLabel().add(getLabel(node));
                    break;
                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName() + " - " + node.getNodeValue());
                    break;
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }

        }
        return labels;
    }

    private static Label getLabel(Node parent) {
        Label label = new Label();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            switch (node.getNodeName()) {
                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName() + " - " + node.getNodeValue());
                    break;
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "value":
                    label.setValue(attribute.getNodeValue());
                    break;
                case "type":
                    label.setType(attribute.getNodeValue());
                    break;
                case "comments":
                    label.setComments(attribute.getNodeValue());
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return label;


    }

    private static TranskribusMetadata getTranskribusMetadata(Node parent) {
        TranskribusMetadata transkribusMetadata = new TranskribusMetadata();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            System.out.println(parent.getNodeName() + " - " + node.getNodeName());
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "docId":
                    transkribusMetadata.setDocId(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "imageId":
                    transkribusMetadata.setImageId(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "imgUrl":
                    transkribusMetadata.setImgUrl(attribute.getNodeValue());
                    break;
                case "pageId":
                    transkribusMetadata.setPageId(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "pageNr":
                    transkribusMetadata.setPageNr(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "status":
                    transkribusMetadata.setStatus(attribute.getNodeValue());
                    break;
                case "tsid":
                    transkribusMetadata.setTsid(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "userId":
                    transkribusMetadata.setUserId(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "xmlUrl":
                    transkribusMetadata.setXmlUrl(attribute.getNodeValue());
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }

        }
        return transkribusMetadata;
    }

    private static TextRegion getTextRegion(Node parent) {
        TextRegion textRegion = new TextRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (node.getNodeName()) {
                case "Coords":
                    textRegion.setCoords(getCoords(node));
                    break;
                case "TextLine":
                    textRegion.getTextLines().add(getTextLine(node));

                    break;
                case "TextEquiv":
                    textRegion.setTextEquiv(getTextEquiv(node));
                    break;
                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName());
                    break;
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    textRegion.setId(attribute.getNodeValue());
                    break;
                case "custom":
                    textRegion.setCustom(attribute.getNodeValue());
                    break;
                case "type":
                    textRegion.setRegionType(attribute.getNodeValue());
                    break;
                case "orientation":
                    textRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                case "readingDirection":
                    textRegion.setReadingDirection(attribute.getNodeValue());
                    break;
                case "textColour":
                    textRegion.setTextColour(attribute.getNodeValue());
                    break;
                case "bgColour":
                    textRegion.setBgColour(attribute.getNodeValue());
                    break;
                case "reverseVideo":
                    textRegion.setReverseVideo(Boolean.parseBoolean(attribute.getNodeValue()));
                    break;
                case "readingOrientation":
                    textRegion.setReadingOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                case "indented":
                    textRegion.setIndented(Boolean.parseBoolean(attribute.getNodeValue()));
                    break;
                case "primaryLanguage":
                    switch (attribute.getNodeValue()) {
                        case "nl":
                            textRegion.setPrimaryLanguage("Dutch");
                            break;
                        case "en":
                            textRegion.setPrimaryLanguage("English");
                            break;
                        case "fr":
                            textRegion.setPrimaryLanguage("French");
                            break;
                        case "it":
                            textRegion.setPrimaryLanguage("Italian");
                            break;
                        case "de":
                            textRegion.setPrimaryLanguage("German");
                            break;
                        case "la":
                            textRegion.setPrimaryLanguage("Latin");
                            break;
                        default:
                            textRegion.setPrimaryLanguage(attribute.getNodeValue());
                            break;
                    }
                    break;
                case "primaryScript":
                    switch (attribute.getNodeValue()) {
                        case "Dutch":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "English":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "French":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "Italian":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "German":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "nl":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "en":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "fr":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "it":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "de":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        case "la":
                            textRegion.setPrimaryScript("Latin");
                            break;
                        default:
                            textRegion.setPrimaryScript(attribute.getNodeValue());
                            break;
                    }
                    break;
                default:
                    System.out.println("TextRegion attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return textRegion;
    }

    private static TextLine getTextLine(Node parent) {
        TextLine textLine = new TextLine();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (node.getNodeName()) {
                case "Coords":
                    textLine.setCoords(getCoords(node));
                    break;
                case "Baseline":
                    textLine.setBaseline(getBaseline(node));
                    break;
                case "TextEquiv":
                    textLine.setTextEquiv(getTextEquiv(node));
                    break;
                case "Word":
                    if (textLine.getWords() == null) {
                        textLine.setWords(new ArrayList<>());
                    }
                    textLine.getWords().add(getWord(node));
                    break;
                case "TextStyle":
                    textLine.setTextStyle(getTextStyle(node));
                    break;
                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName());
                    break;
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    textLine.setId(attribute.getNodeValue());
                    break;
                case "custom":
                    textLine.setCustom(attribute.getNodeValue());
                    break;
                case "primaryLanguage":
                    switch (attribute.getNodeValue()) {
                        case "nl":
                            textLine.setPrimaryLanguage("Dutch");
                            break;
                        case "en":
                            textLine.setPrimaryLanguage("English");
                            break;
                        case "fr":
                            textLine.setPrimaryLanguage("French");
                            break;
                        case "it":
                            textLine.setPrimaryLanguage("Italian");
                            break;
                        case "de":
                            textLine.setPrimaryLanguage("German");
                            break;
                        case "la":
                            textLine.setPrimaryLanguage("Latin");
                            break;
                        default:
                            textLine.setPrimaryLanguage(attribute.getNodeValue());
                            break;
                    }
                    break;
                default:
                    System.out.println("TextLine attrib: " + attribute.getNodeName());
                    break;
            }
        }

        return textLine;
    }

    private static TextStyle getTextStyle(Node parent) {
        TextStyle textStyle = new TextStyle();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            System.out.println(parent.getNodeName() + " - " + node.getNodeName());
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);

            switch (attribute.getNodeName()) {
                case "xHeight":
                    textStyle.setxHeight(Integer.parseInt(attribute.getNodeValue()));
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }

        }
        return textStyle;
    }

    private static Word getWord(Node parent) {
        Word word = new Word();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (node.getNodeName()) {
                case "Coords":
                    word.setCoords(getCoords(node));
                    break;
                case "TextEquiv":
                    word.setTextEquiv(getTextEquiv(node));
                    break;
                case "TextStyle":
                    word.setTextStyle(getTextStyle(node));
                    break;
                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName());
                    break;
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("id")) {
                word.setId(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return word;
    }

    private static TextEquiv getTextEquiv(Node parent) {
        TextEquiv textEquiv = new TextEquiv();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Unicode")) {
                textEquiv.setUnicode(node.getTextContent());
            } else if (node.getNodeName().equals("PlainText")) {
                textEquiv.setPlainText(UNICODE_TO_ASCII_TRANSLITIRATOR.toAscii(node.getTextContent()));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("conf")) {
                textEquiv.setConf(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }

        if (textEquiv.getPlainText() == null && textEquiv.getUnicode() == null) {
            return null;
        }
        return textEquiv;
    }

    private static Baseline getBaseline(Node parent) {
        Baseline baseline = new Baseline();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Points")) {
                baseline.setPoints(node.getTextContent());
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("points")) {
                baseline.setPoints(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }

        return baseline;

    }

    private static Coords getCoords(Node parent) {
        Coords coords = new Coords();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Point")) {
                coords.setPoints((coords.getPoints() + " " + getPoint(node)).trim());
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("points")) {
                // yes duplicated to make sure it contains valid points
                ArrayList<Point> points = StringConverter.stringToPoint(attribute.getNodeValue());
                String pointsString = StringConverter.pointToString(points);
                if (!Strings.isNullOrEmpty(pointsString)) {
                    coords.setPoints(pointsString);
                }

            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }

        return coords;
    }

    private static String getPoint(Node point) {
        Integer x = null;
        Integer y = null;
        for (int i = 0; i < point.getAttributes().getLength(); i++) {
            Node attribute = point.getAttributes().item(i);
            if (attribute.getNodeName().equals("x")) {
                x = Integer.parseInt(attribute.getNodeValue());
            } else if (attribute.getNodeName().equals("y")) {
                y = Integer.parseInt(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        if (x != null && y != null) {
            return String.format("%s,%s", x, y);
        }
        return "";
    }

    private static void addRegionToReadingOrder(OrderedGroup orderedGroup, int readingOrderCount, String regionRef) {
        RegionRefIndexed regionRefIndexed = new RegionRefIndexed();
        regionRefIndexed.setIndex(readingOrderCount);
        readingOrderCount++;
        regionRefIndexed.setRegionRef(regionRef);
        orderedGroup.getRegionRefIndexedList().add(regionRefIndexed);

    }

    private static Page getPage(Node parent) {
        Page page = new Page();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (node.getNodeName()) {
                case "TextRegion":
                    TextRegion textRegion = getTextRegion(node);
                    page.getTextRegions().add(textRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, textRegion.getId());
                    break;
                case "ImageRegion":
                    ImageRegion imageRegion = getImageRegion(node);
                    page.getImageRegions().add(imageRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, imageRegion.getId());
                    break;
                case "SeparatorRegion":
                    SeparatorRegion separatorRegion = getSeparatorRegion(node);
                    page.getSeparatorRegions().add(separatorRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, separatorRegion.getId());
                    break;
                case "Border":
                    page.setBorder(getBorder(node));
                    break;
                case "GraphicRegion":
                    GraphicRegion graphicRegion = getGraphicRegion(node);
                    page.getGraphicRegions().add(graphicRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, graphicRegion.getId());
                    break;
                case "LineDrawingRegion":
                    LineDrawingRegion lineDrawingRegion = getLineDrawingRegion(node);
                    page.getLineDrawingRegions().add(lineDrawingRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, lineDrawingRegion.getId());
                    break;
                case "ChartRegion":
                    ChartRegion chartRegion = getChartRegion(node);
                    page.getChartRegions().add(chartRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, chartRegion.getId());
                    break;
                case "NoiseRegion":
                    NoiseRegion noiseRegion = getNoiseRegion(node);
                    page.getNoiseRegions().add(noiseRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, noiseRegion.getId());
                    break;
                case "MathsRegion":
                    MathsRegion mathsRegion = getMathsRegion(node);
                    page.getMathsRegions().add(mathsRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, mathsRegion.getId());
                    break;
                case "TableRegion":
                    TableRegion tableRegion = getTableRegion(node);
                    page.getTableRegions().add(tableRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, tableRegion.getId());
                    break;
                case "FrameRegion":
                    FrameRegion frameRegion = getFrameRegion(node);
                    page.getFrameRegions().add(frameRegion);
//                addRegionToReadingOrder(readingOrder.getOrderedGroup(), readingOrderCount++, frameRegion.getId());
                    break;
                case "ReadingOrder":
                    page.setReadingOrder(getReadingOrder(node));
                    break;
                case "Unknown":
                    page.getUnknowns().add(getUnknown(node));
                    break;
                case "PrintSpace":
                    page.setPrintSpace(getPrintSpace(node));
                    break;
                default:
                    System.out.println(parent.getNodeName() + " - " + node.getNodeName());
                    break;
            }
        }
//        if (page.getReadingOrder() == null) {
//            page.setReadingOrder(readingOrder);
//        }
//        imageFilename="cPAS-0409.jpg" imageWidth="2512" imageHeight="4096">
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "imageFilename":
                    page.setImageFilename(attribute.getNodeValue());
                    break;
                case "imageWidth":
                    page.setImageWidth(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "imageHeight":
                    page.setImageHeight(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "type":
                    page.setPageType(attribute.getNodeValue());
                    break;
                case "primaryLanguage":
                    switch (attribute.getNodeValue()) {
                        case "nl":
                            page.setPrimaryLanguage("Dutch");
                            break;
                        case "en":
                            page.setPrimaryLanguage("English");
                            break;
                        case "fr":
                            page.setPrimaryLanguage("French");
                            break;
                        case "it":
                            page.setPrimaryLanguage("Italian");
                            break;
                        case "de":
                            page.setPrimaryLanguage("German");
                            break;
                        case "la":
                            page.setPrimaryLanguage("Latin");
                            break;
                        default:
                            page.setPrimaryLanguage(attribute.getNodeValue());
                            break;
                    }
                    break;
                default:
                    System.out.println("Page attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return page;
    }

    private static PrintSpace getPrintSpace(Node parent) {
        PrintSpace printSpace = new PrintSpace();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                printSpace.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("id")) {
                printSpace.setId(attribute.getNodeValue());
            } else if (attribute.getNodeName().equals("custom")) {
                printSpace.setCustom(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return printSpace;

    }

    private static Unknown getUnknown(Node parent) {
        Unknown unknown = new Unknown();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                unknown.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("id")) {
                unknown.setId(attribute.getNodeValue());
            } else if (attribute.getNodeName().equals("custom")) {
                unknown.setCustom(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return unknown;
    }

    private static GraphicRegion getGraphicRegion(Node parent) {
        GraphicRegion graphicRegion = new GraphicRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                graphicRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    graphicRegion.setId(attribute.getNodeValue());
                    break;
                case "custom":
                    graphicRegion.setCustom(attribute.getNodeValue());
                    break;
                case "type":
                    graphicRegion.setRegionType(attribute.getNodeValue());
                    break;
                case "embText":
                    graphicRegion.setEmbText(Boolean.parseBoolean(attribute.getNodeValue()));
                    break;
                case "orientation":
                    graphicRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                case "numColours":
                    graphicRegion.setNumColours(Integer.parseInt(attribute.getNodeValue()));
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return graphicRegion;
    }

    private static LineDrawingRegion getLineDrawingRegion(Node parent) {
        LineDrawingRegion lineDrawingRegion = new LineDrawingRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                lineDrawingRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    lineDrawingRegion.setId(attribute.getNodeValue());
                    break;
                case "embText":
                    lineDrawingRegion.setEmbText(Boolean.parseBoolean(attribute.getNodeValue()));
                    break;
                case "penColour":
                    lineDrawingRegion.setPenColour(attribute.getNodeValue());
                    break;
                case "bgColour":
                    lineDrawingRegion.setBgColour(attribute.getNodeValue());
                    break;
                case "orientation":
                    lineDrawingRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return lineDrawingRegion;
    }


    private static ChartRegion getChartRegion(Node parent) {
        ChartRegion chartRegion = new ChartRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                chartRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    chartRegion.setId(attribute.getNodeValue());
                    break;
                case "type":
                    chartRegion.setRegionType(attribute.getNodeValue());
                    break;
                case "embText":
                    chartRegion.setEmbText(Boolean.parseBoolean(attribute.getNodeValue()));
                    break;
                case "orientation":
                    chartRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                case "numColours":
                    chartRegion.setNumColours(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "bgColour":
                    chartRegion.setBgColour(attribute.getNodeValue());
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return chartRegion;
    }

    private static NoiseRegion getNoiseRegion(Node parent) {
        NoiseRegion noiseRegion = new NoiseRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                noiseRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("id")) {
                noiseRegion.setId(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return noiseRegion;
    }

    private static MathsRegion getMathsRegion(Node parent) {
        MathsRegion mathsRegion = new MathsRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                mathsRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    mathsRegion.setId(attribute.getNodeValue());
                    break;
                case "bgColour":
                    mathsRegion.setBgColour(attribute.getNodeValue());
                    break;
                case "orientation":
                    mathsRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return mathsRegion;
    }

    private static FrameRegion getFrameRegion(Node parent) {
        FrameRegion frameRegion = new FrameRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                frameRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("id")) {
                frameRegion.setId(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return frameRegion;
    }

    private static TableRegion getTableRegion(Node parent) {
        TableRegion tableRegion = new TableRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                tableRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }

        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    tableRegion.setId(attribute.getNodeValue());
                    break;
                case "orientation":
                    tableRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                case "rows":
                    tableRegion.setRows(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "columns":
                    tableRegion.setColumns(Integer.parseInt(attribute.getNodeValue()));
                    break;
                case "lineColour":
                    tableRegion.setLineColour(attribute.getNodeValue());
                    break;
                case "lineSeparators":
                    tableRegion.setLineSeparators(attribute.getNodeValue());
                    break;
                case "embText":
                    tableRegion.setEmbText(Boolean.parseBoolean(attribute.getNodeValue()));
                    break;
                case "bgColour":
                    tableRegion.setBgColour(attribute.getNodeValue());
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return tableRegion;
    }

    private static ReadingOrder getReadingOrder(Node parent) {
        ReadingOrder readingOrder = new ReadingOrder();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("OrderedGroup")) {
                readingOrder.setOrderedGroup(getOrderedGroup(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            System.out.println("attrib: " + attribute.getNodeName());
        }
        return readingOrder;
    }

    private static OrderedGroup getOrderedGroup(Node parent) {
        OrderedGroup orderedGroup = new OrderedGroup();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("RegionRefIndexed")) {
                orderedGroup.getRegionRefIndexedList().add(getRegionRefIndexed(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("caption")) {
                orderedGroup.setCaption(attribute.getNodeValue());
            } else if (attribute.getNodeName().equals("id")) {
                orderedGroup.setId(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return orderedGroup;
    }

    private static RegionRefIndexed getRegionRefIndexed(Node parent) {
        RegionRefIndexed regionRefIndexed = new RegionRefIndexed();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            System.out.println(parent.getNodeName() + " - " + node.getNodeName());
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("index")) {
                regionRefIndexed.setIndex(Integer.parseInt(attribute.getNodeValue()));
            } else if (attribute.getNodeName().equals("regionRef")) {
                regionRefIndexed.setRegionRef(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return regionRefIndexed;
    }

    private static Border getBorder(Node parent) {
        Border border = new Border();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                border.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            if (attribute.getNodeName().equals("id")) {
                border.setId(attribute.getNodeValue());
            } else if (attribute.getNodeName().equals("custom")) {
                border.setCustom(attribute.getNodeValue());
            } else {
                System.out.println("attrib: " + attribute.getNodeName());
            }
        }
        return border;
    }

    private static ImageRegion getImageRegion(Node parent) {
        ImageRegion imageRegion = new ImageRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                imageRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    imageRegion.setId(attribute.getNodeValue());
                    break;
                case "orientation":
                    imageRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                case "colourDepth":
                    imageRegion.setColourDepth(attribute.getNodeValue());
                    break;
                case "bgColour":
                    imageRegion.setBgColour(attribute.getNodeValue());
                    break;
                case "embText":
                    imageRegion.setEmbText(Boolean.parseBoolean(attribute.getNodeValue()));
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }
        return imageRegion;
    }

    private static SeparatorRegion getSeparatorRegion(Node parent) {
        SeparatorRegion separatorRegion = new SeparatorRegion();
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node node = parent.getChildNodes().item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (node.getNodeName().equals("Coords")) {
                separatorRegion.setCoords(getCoords(node));
            } else {
                System.out.println(parent.getNodeName() + " - " + node.getNodeName());
            }
        }
        for (int i = 0; i < parent.getAttributes().getLength(); i++) {
            Node attribute = parent.getAttributes().item(i);
            switch (attribute.getNodeName()) {
                case "id":
                    separatorRegion.setId(attribute.getNodeValue());
                    break;
                case "orientation":
                    separatorRegion.setOrientation(Double.parseDouble(attribute.getNodeValue()));
                    break;
                case "colour":
                    separatorRegion.setColour(attribute.getNodeValue());
                    break;
                default:
                    System.out.println("attrib: " + attribute.getNodeName());
                    break;
            }
        }

        return separatorRegion;
    }

    public static void shrinkTextLines(Path imageFile, Path pageFile, String namespace) throws IOException, TransformerException {
        String filename = imageFile.toAbsolutePath().toString();
        Mat image = Imgcodecs.imread(filename);
        if (image.height() == 0) {
            System.err.println("image is empty: " + filename);
            image.release();
            return;
        }
        Mat grayImage = new Mat();
        Mat binaryImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
//        image.release();
//        Imgproc.threshold(grayImage, binaryImage, 0, 255, Imgproc.THRESH_OTSU);
        int blockSize = grayImage.width() / 50; // default should be something like width / 50
        if (blockSize % 2 == 0) {
            blockSize++;
        }
        if (blockSize <= 1) {
            blockSize = 3;
        }

        Imgproc.adaptiveThreshold(grayImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, blockSize, 15);

        grayImage.release();
        PcGts page = PageUtils.readPageFromFile(pageFile);
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            List<TextLine> textLinesToRemove = new ArrayList<>();
            for (TextLine textLine : textRegion.getTextLines()) {
                // remove white space before the baseline
                String baselinePoints = textLine.getBaseline().getPoints();
                if (Strings.isNullOrEmpty(baselinePoints)) {
                    textLinesToRemove.add(textLine);
                    continue;
                }
                List<org.opencv.core.Point> baseline = StringConverter.stringToPoint(baselinePoints);
                if (baseline.size() < 2) {
                    textLinesToRemove.add(textLine);
                    continue;
                }
                List<org.opencv.core.Point> expanded = StringConverter.expandPointList(baseline);
                if (baseline.size() == 0) {
                    textLinesToRemove.add(textLine);
                    continue;
                }
                int bestX = (int) baseline.get(0).x;
                boolean found = false;
                int counter = 0;
                Integer above = 20;
                TextStyle textStyle = textLine.getTextStyle();
                Integer xHeight = null;
                if (textStyle != null) {
                    xHeight = textStyle.getxHeight();
                }
                if (xHeight != null && xHeight > 10) {
                    above = 2 * xHeight;
                }
                Integer below = xHeight;
                if (below == null || below == 0) {
                    below = 10;
                }
                for (org.opencv.core.Point point : expanded) {
                    counter++;
                    int startY = (int) point.y + above;
                    if (startY > binaryImage.height()) {
                        startY = binaryImage.height() - 1;
                    }
                    for (int i = startY; i > 0 && i > point.y - below; i--) {
                        byte[] data = new byte[1];
                        binaryImage.get(i, (int) point.x, data);
                        if (data[0] != BLACK) {
                            found = true;
                            break;
                        }
                    }
                    if (found || counter > 50) {
                        bestX = (int) point.x;
                        break;
                    }
                }
                List<org.opencv.core.Point> pointsToRemove = new ArrayList<>();
                for (org.opencv.core.Point point : baseline) {
                    if (point.x <= bestX) {
                        pointsToRemove.add(point);
                    }
                }
                baseline.removeAll(pointsToRemove);

                if (pointsToRemove.size() > 0) {
                    pointsToRemove.get(pointsToRemove.size() - 1).x = bestX;
                    baseline.add(0, pointsToRemove.get(pointsToRemove.size() - 1));
                } else {
                    baseline.get(0).x = bestX;
                }
                // remove white space after the baseline
                baseline = Lists.reverse(baseline);
                expanded = Lists.reverse(expanded);
                bestX = (int) baseline.get(0).x;
                found = false;
                counter = 0;
                for (org.opencv.core.Point point : expanded) {
                    counter++;
                    int startY = (int) point.y + below;
                    if (startY > binaryImage.height()) {
                        startY = binaryImage.height() - 1;
                    }
                    for (int i = startY; i > 0 && i > point.y - above; i--) {
                        byte[] data = new byte[1];
                        binaryImage.get(i, (int) point.x, data);
                        if (data[0] != BLACK) {
                            found = true;
                            break;
                        }
                    }
                    if (found || counter > 50) {
                        bestX = (int) point.x;
                        break;
                    }
                }
                pointsToRemove = new ArrayList<>();
                for (Point point : baseline) {
                    if (point.x >= bestX) {
                        pointsToRemove.add(point);
                    }
                }
                baseline.removeAll(pointsToRemove);
                if (pointsToRemove.size() > 0) {
                    Point lastRemovedPoint = pointsToRemove.get(pointsToRemove.size() - 1);
                    lastRemovedPoint.x = bestX;
                    baseline.add(0, lastRemovedPoint);
                } else {
                    baseline.get(0).x = bestX;
                }
                baseline = Lists.reverse(baseline);
                baseline = StringConverter.simplifyPolygon(baseline, 1);
                textLine.setBaseline(new Baseline());
                textLine.getBaseline().setPoints(StringConverter.pointToString(baseline));
                if (baseline.size() < 2) {
                    textLinesToRemove.add(textLine);
                }
            }
            textRegion.getTextLines().removeAll(textLinesToRemove);
        }
        binaryImage.release();
        writePageToFile(page, namespace, pageFile);
    }

    private static Point findTopLeft(List<Point> points) {
        Point found = null;
        for (Point point : points) {
            if (found == null) {
                found = point;
                continue;
            }
            if (point.y * point.x < found.y * found.x) {
                found = point;
            }
        }
        return found;
    }

    private static Point findLeftBelowPoint(Point source, List<Point> points) {
        Point found = null;
        for (Point point : points) {
            if (point.y >= source.y) {
                if (found == null) {
                    found = point;
                    continue;
                }
                if (point.x <= found.x) {
                    found = point;
                }
            }
        }
        return found;
    }

    private static Point findBelowRightPoint(Point source, List<Point> points) {
        Point found = null;
        for (Point point : points) {
            if (point.x > source.x) {
                if (found == null) {
                    found = point;
                    continue;
                }
                if (point.y >= found.y) {
                    found = point;
                }
            }
        }
        return found;
    }

    private static Point findRightAbovePoint(Point source, List<Point> points) {
        Point found = null;
        for (Point point : points) {
            if (point.y < source.y) {
                if (found == null) {
                    found = point;
                    continue;
                }
                if (point.x > found.x) {
                    found = point;
                }
            }
        }
        return found;
    }

    public static double getDistance(Point first, Point second) {
        return Math.sqrt(Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2));
    }

    private static Point findClosest(Point source, List<Point> points) {
        Point found = null;
        for (Point point : points) {
            if (found == null) {
                found = point;
                continue;
            }
            double distanceNew = getDistance(source, point);
            double distanceExisting = getDistance(source, found);

            if (distanceNew < distanceExisting) {
                found = point;
            }
        }
        return found;
    }

    // Image file might be used in the futer
    public static void shrinkRegions(Path imageFile, Path pageFile, String namespace) throws IOException, TransformerException {
        PcGts page = PageUtils.readPageFromFile(pageFile);
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            ArrayList<Point> points = new ArrayList<>();
            for (TextLine textLine : textRegion.getTextLines()) {
                List<Point> baseline = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
                if (baseline.size() > 0) {
                    points.addAll(StringConverter.stringToPoint(textLine.getCoords().getPoints()));
                }
            }
            if (points.size() == 0) {
                continue;
            }
            List<Point> regionCoords = new ArrayList<>();
            Point topLeft = findTopLeft(points);
            regionCoords.add(topLeft);
            points.remove(regionCoords.get(regionCoords.size() - 1));
            while (true) {
                Point next = findLeftBelowPoint(regionCoords.get(regionCoords.size() - 1), points);
                if (next != null) {
                    regionCoords.add(next);
                    points.remove(next);
                } else {
                    break;
                }
            }
            while (true) {
                Point next = findBelowRightPoint(regionCoords.get(regionCoords.size() - 1), points);
                if (next != null) {
                    regionCoords.add(next);
                    points.remove(next);
                } else {
                    break;
                }
            }
            while (true) {
                Point next = findRightAbovePoint(regionCoords.get(regionCoords.size() - 1), points);
                if (next != null) {
                    regionCoords.add(next);
                    points.remove(next);
                } else {
                    break;
                }
            }

            while (points.size() > 0) {
                Point leftMost = findLeftMost(points);
                Point after = null;
                Point before = null;
                for (Point point : regionCoords) {
                    if (leftMost.y > point.y) {
                        after = point;
                    }
                    if (leftMost.y < point.y) {
                        before = point;
                        break;
                    }
                }
                if (after != null && before != null) {
                    int afterIndex = regionCoords.indexOf(after);
                    int beforeIndex = regionCoords.indexOf(before);
                    if (afterIndex == beforeIndex - 1) {
                        Point closest = findClosest(leftMost, regionCoords);
                        if (closest == before || closest == after) {
                            regionCoords.add(beforeIndex, leftMost);
                        }
                    }
                }
                points.remove(leftMost);

            }
            textRegion.getCoords().setPoints(StringConverter.pointToString(regionCoords));
        }

        PageUtils.writePageToFile(page, namespace, pageFile.toAbsolutePath());
    }

    private static Point findLeftMost(List<Point> points) {
        Point found = null;
        for (Point point : points) {
            if (found == null) {
                found = point;
                continue;
            }
            if (point.x < found.x) {
                found = point;
            }
        }
        return found;
    }

    public static PcGts createFromImage(Mat image, String imageFilename) {
        PcGts page = new PcGts();
        page.getMetadata().setCreated(new Date());
        page.getMetadata().setCreator("PIM");
        page.getPage().setImageFilename(imageFilename);
        page.getPage().setImageHeight(image.height());
        page.getPage().setImageWidth(image.width());
        return page;
    }

    public static void reOrderTextLines(TextRegion textRegion) {
        List<TextLine> textLines = textRegion.getTextLines();
        textRegion.setTextLines(new ArrayList<>());
        while (textLines.size() > 0) {
            TextLine bestTextLine = null;
            TextLine removeline = null;
            int bestY = Integer.MAX_VALUE;
            for (TextLine textLine : textLines) {
                List<Point> points = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
                if (points.size() == 1) {
                    removeline = textLine;
                }
                if (points.size() > 1 && points.get((points.size() / 2) - 1).y <= bestY) {
                    bestTextLine = textLine;
                    bestY = (int) points.get((points.size() / 2) - 1).y;
                }
            }
            textLines.remove(bestTextLine);
            textLines.remove(removeline);
            textRegion.getTextLines().add(bestTextLine);
        }
    }

    private static Polygon getPolygon(TextRegion textRegion) {
        Polygon polygon = new Polygon();
        for (Point point : StringConverter.stringToPoint(textRegion.getCoords().getPoints())) {
            polygon.addPoint((int) point.x, (int) point.y);
        }
        return polygon;
    }

    private static boolean pointInRegion(Point point, Polygon polygon, int margin) {
        return polygon.contains(point.x, point.y) ||
                polygon.contains(point.x, point.y + margin) ||
                polygon.contains(point.x + margin, point.y) ||
                polygon.contains(point.x, point.y - margin) ||
                polygon.contains(point.x - margin, point.y) ||
                polygon.contains(point.x + margin, point.y + margin) ||
                polygon.contains(point.x - margin, point.y - margin);
    }

    private static boolean textLineMostlyInRegion(TextLine textLine, TextRegion textRegion, float minimumPercentage, int margin) {
        int pointsInRegion = 0;
        int pointsOutsideRegion = 0;
        Polygon textRegionPolygon = getPolygon(textRegion);
        List<Point> expandedBaseline = StringConverter.expandPointList(StringConverter.stringToPoint(textLine.getBaseline().getPoints()));
        int totalPoints = expandedBaseline.size();
        for (Point point : expandedBaseline) {
            if (pointInRegion(point, textRegionPolygon, margin)) {
                pointsInRegion++;
                if (pointsInRegion > (totalPoints * minimumPercentage)) {
                    return true;
                }
            } else {
                pointsOutsideRegion++;
                if (pointsOutsideRegion > (totalPoints * (1.0 - minimumPercentage))) {
                    return false;
                }
            }
        }
        return ((float) pointsInRegion / (float) (pointsInRegion + pointsOutsideRegion)) > minimumPercentage;
    }

    public static List<TextLine> attachTextLines(TextRegion textRegion, List<TextLine> textLines, float minimumPercentage, int margin) {
        List<TextLine> remainingLines = new ArrayList<>();
        for (TextLine textLine : textLines) {
            if (textLineMostlyInRegion(textLine, textRegion, minimumPercentage, margin)) {
                textRegion.getTextLines().add(textLine);
            } else {
                remainingLines.add(textLine);
            }
        }
        return remainingLines;
    }


    public static PcGts reAttachAndOrderTextlines(PcGts page, int margin) {
        List<TextLine> textLines = new ArrayList<>();
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            textLines.addAll(textRegion.getTextLines());
            textRegion.setTextLines(new ArrayList<>());
        }

        // reattach everything to correct region
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            textLines = attachTextLines(textRegion, textLines, 0.66f, margin);
        }

        // These are without region
        for (TextLine textLine : textLines) {
            System.out.println("error: " + page.getPage().getImageFilename() + " " + textLine.getId() + " " + textLine.getCustom());
            System.err.println("line can't be attached to region.");
        }

        // reorder
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            reOrderTextLines(textRegion);
        }
        return page;
    }

    private static String getText(TextLine textLine){
        String text = null;
        if (textLine.getTextEquiv()!=null){
            if (textLine.getTextEquiv().getPlainText()!=null){
                text = textLine.getTextEquiv().getPlainText();
            }
            if (textLine.getTextEquiv().getUnicode()!=null){
                text = textLine.getTextEquiv().getUnicode();
            }
        }
        return text;
    }

    public static String convertToTxt(PcGts page){
        String output = "";
        for (TextRegion textRegion: page.getPage().getTextRegions()){
            for (TextLine textLine : textRegion.getTextLines()){
                String text = getText(textLine);
                output += text +"\n";
            }
        }
        return output;
    }
}
