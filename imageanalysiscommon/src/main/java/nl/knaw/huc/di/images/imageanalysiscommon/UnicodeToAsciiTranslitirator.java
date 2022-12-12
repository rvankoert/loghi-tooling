package nl.knaw.huc.di.images.imageanalysiscommon;

import net.gcardone.junidecode.Junidecode;

// For more information see: https://github.com/gcardone/junidecode
public class UnicodeToAsciiTranslitirator {

    public String toAscii(String unicode){
        return Junidecode.unidecode(unicode);
    }
}
