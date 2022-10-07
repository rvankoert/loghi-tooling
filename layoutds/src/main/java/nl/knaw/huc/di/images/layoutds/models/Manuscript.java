package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement

public class Manuscript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}