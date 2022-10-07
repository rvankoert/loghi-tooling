package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;


//alter table Swipe add column uuid uuid ;
//        update Swipe set uuid = uuid_generate_v4() where uuid is null;
//        alter table Swipe alter column uuid set not null;
//
//        alter table Person add column uuid uuid ;
//        update Person set uuid = uuid_generate_v4() where uuid is null;
//        alter table Person alter column uuid set not null;
//
//        alter table Job add column uuid uuid ;
//        update Job set uuid = uuid_generate_v4() where uuid is null;
//        alter table Job alter column uuid set not null;
//
//        alter table PimField add column uuid uuid ;
//        update PimField set uuid = uuid_generate_v4() where uuid is null;
//        alter table PimField alter column uuid set not null;
//
//        alter table PimFieldSet alter column uuid set not null;
//
//
//        alter table PimFieldValue add column uuid uuid ;
//        update PimFieldValue set uuid = uuid_generate_v4() where uuid is null;
//        alter table PimFieldValue alter column uuid set not null;
//
//        alter table PimRecord add column uuid uuid ;
//        update PimRecord set uuid = uuid_generate_v4() where uuid is null;
//        alter table PimRecord alter column uuid set not null;
//
//        alter table PimUserSession add column uuid uuid ;
//        update PimUserSession set uuid = uuid_generate_v4() where uuid is null;
//        alter table PimUserSession alter column uuid set not null;
//
//        alter table HCADescription add column uuid uuid ;
//        update HCADescription set uuid = uuid_generate_v4() where uuid is null;
//        alter table HCADescription alter column uuid set not null;
//
//        alter table Annotation add column uuid uuid ;
//        update Annotation set uuid = uuid_generate_v4() where uuid is null;
//        alter table Annotation alter column uuid set not null;
//
//        alter table DocumentGroundTruth add column uuid uuid ;
//        update DocumentGroundTruth set uuid = uuid_generate_v4() where uuid is null;
//        alter table DocumentGroundTruth alter column uuid set not null;
//
//        alter table DocumentManifest add column uuid uuid ;
//        update DocumentManifest set uuid = uuid_generate_v4() where uuid is null;
//        alter table DocumentManifest alter column uuid set not null;
//
//        alter table DocumentOCRResult add column uuid uuid ;
//        update DocumentOCRResult set uuid = uuid_generate_v4() where uuid is null;
//        alter table DocumentOCRResult alter column uuid set not null;
//
//        alter table DocumentTextLineSnippet add column uuid uuid ;
//        update DocumentTextLineSnippet set uuid = uuid_generate_v4() where uuid is null;
//        alter table DocumentTextLineSnippet alter column uuid set not null;
//
//
//
//        alter table Environment add column uuid uuid ;
//        update Environment set uuid = uuid_generate_v4() where uuid is null;
//        alter table Environment alter column uuid set not null;
//
//
//        alter table IIIFDocument add column uuid uuid ;
//        update IIIFDocument set uuid = uuid_generate_v4() where uuid is null;
//        alter table IIIFDocument alter column uuid set not null;
//
//        alter table Place add column uuid uuid ;
//        update Place set uuid = uuid_generate_v4() where uuid is null;
//        alter table Place alter column uuid set not null;
//
//
//        alter table SearchFacet add column uuid uuid ;
//        update SearchFacet set uuid = uuid_generate_v4() where uuid is null;
//        alter table SearchFacet alter column uuid set not null;
//
//
//
//        alter table XmlDocument add column uuid uuid ;
//        update XmlDocument set uuid = uuid_generate_v4() where uuid is null;
//        alter table XmlDocument alter column uuid set not null;
//
//        alter table Geonames add column uuid uuid ;
//        update Geonames set uuid = uuid_generate_v4() where uuid is null;
//        alter table Geonames alter column uuid set not null;



@Entity
@XmlRootElement
public class ScriptDescription implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uri;

    @Column(unique = true, nullable = false)
    private String name;


    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        uri = UUID.randomUUID().toString() + "_" + name;
    }
}