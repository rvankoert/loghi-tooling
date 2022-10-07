package nl.knaw.huc.di.images.layoutds.models.Annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name= "AnnotationSelector")
public class Selector
{
    @JsonProperty("@id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("@type")
    private String type;

    @OneToOne(cascade = CascadeType.ALL, targetEntity=Default.class, fetch=FetchType.EAGER)
    private Default _default;

    @OneToOne(cascade = CascadeType.ALL, targetEntity=Item.class, fetch=FetchType.EAGER)
    private Item item;

    public Default getDefault ()
    {
        return _default;
    }

    public void setDefault (Default _default)
    {
        this._default = _default;
    }

    public Item getItem ()
    {
        return item;
    }

    public void setItem (Item item)
    {
        this.item = item;
    }

}
