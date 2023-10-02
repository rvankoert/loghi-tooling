package nl.knaw.huc.di.images.pagexmlutils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
        import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class AnnotationIntrospector extends JacksonXmlAnnotationIntrospector
{
    private static final long serialVersionUID = 1L;
    private final String namespace;

    public AnnotationIntrospector(String namespace) {
        super();
        this.namespace = namespace;
    }

    @Override
    public PropertyName findRootName(AnnotatedClass ac) {
        PropertyName pn = super.findRootName(ac);
        return pn.withNamespace(this.namespace);
    }

    final private String oldNameSpace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15";
    @Override
    public String findNamespace(MapperConfig<?> config, Annotated ann) {
        JacksonXmlProperty prop = (JacksonXmlProperty)this._findAnnotation(ann, JacksonXmlProperty.class);
        if (prop != null) {
            String test = prop.namespace();
            if (oldNameSpace.equals(test)) {
                return this.namespace;
            }else{
                return test;
            }
        } else {
            JsonProperty jprop = (JsonProperty)this._findAnnotation(ann, JsonProperty.class);
            String test = jprop != null ? jprop.namespace() : null;
            return test;
        }
    }

}
