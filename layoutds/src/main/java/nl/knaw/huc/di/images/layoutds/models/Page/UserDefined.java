package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class UserDefined {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "UserAttribute", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<UserAttribute> userAttributes;

    public List<UserAttribute> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(List<UserAttribute> userAttributes) {
        this.userAttributes = userAttributes;
    }
}
