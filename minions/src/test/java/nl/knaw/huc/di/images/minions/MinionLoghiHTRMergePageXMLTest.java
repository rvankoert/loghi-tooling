package nl.knaw.huc.di.images.minions;


import nl.knaw.huc.di.images.layoutds.models.Page.TextLineCustom;
import nl.knaw.huc.di.images.pagexmlutils.GroundTruthTextLineFormatter;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class MinionLoghiHTRMergePageXMLTest {

    @Test
    public void checkDetectSuperScriptCharacter() {
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom(GroundTruthTextLineFormatter.SUPERSCRIPTCHAR+"t");
        String custom = result.getTextStyles().get(0);
        final String textStyle = custom.substring(custom.indexOf("textStyle"));
        final String textStyleContents = textStyle.substring(textStyle.indexOf("{") + 1, textStyle.indexOf("}"));
        final String[] style = textStyleContents.split(";");

        for (String element : style) {
            final String[] nameValue = element.split(":");
            switch (nameValue[0].trim()) {
                case "superscript":
                    return;
            }
        }
        Assert.assertEquals(true, false);
    }

    @Test
    public void checkDetectSuperScriptCharacter2() {
        TextLineCustom result = MinionLoghiHTRMergePageXML.getTextLineCustom(GroundTruthTextLineFormatter.SUPERSCRIPTCHAR+"t"+GroundTruthTextLineFormatter.SUPERSCRIPTCHAR+"e");
        String custom = result.getTextStyles().get(0);
        final String textStyle = custom.substring(custom.indexOf("textStyle"));
        final String textStyleContents = textStyle.substring(textStyle.indexOf("{") + 1, textStyle.indexOf("}"));
        final String[] style = textStyleContents.split(";");

        for (String element : style) {
            final String[] nameValue = element.split(":");
            switch (nameValue[0].trim()) {
                case "superscript":
                    return;
            }
        }
        Assert.assertEquals(true, false);
    }

}