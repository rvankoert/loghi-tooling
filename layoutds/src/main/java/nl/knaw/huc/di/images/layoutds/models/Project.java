package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.UUID;

@Entity
@XmlRootElement

public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Type(type = "text")
    private String name;

    private List<PimUser> pimUsers;



}