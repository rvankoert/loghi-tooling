package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class UserDefined {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "UserAttribute", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<UserAttribute> userAttributes = new ArrayList<>();

    public List<UserAttribute> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(List<UserAttribute> userAttributes) {
        this.userAttributes = userAttributes;
    }

    public void addUserAttribute(UserAttribute userAttribute) {
        this.userAttributes.add(userAttribute);
    }
}
