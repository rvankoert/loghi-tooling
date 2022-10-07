package nl.knaw.huc.di.images.layoutds.models.pim;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;
import java.util.UUID;


@Entity
public class P2PaLAModel implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;
    private UUID uuid;
    private Date done;

    @JsonIgnore
    @Lob
//    @Column(name = "file", columnDefinition="BLOB")
    private Blob file;

    private String hash;
    private String path;

    @ManyToOne(targetEntity = P2PaLaJob.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "p2palaparentId")
    @JsonIgnore
    private P2PaLaJob p2palaparent;


    public P2PaLAModel() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    private String img_size = "1024 768";
    //--line_color 128
    private int line_color = 128;
    //--line_width 8
    private int line_width = 8;
    //--regions paragraph marginalia page-number
    private String regions = "paragraph marginalia page-number";
    //--merge_regions paragraph:heading
    private String merge_regions = "paragraph:heading";
    //--approx_alg optimal
    private int input_channels = 3;
    //--out_mode LR
    private String out_mode = "LR";
    //--net_out_type C
    private String net_out_type = "C";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String getImg_size() {
        return img_size;
    }

    public void setImg_size(String img_size) {
        this.img_size = img_size;
    }

    public int getLine_color() {
        return line_color;
    }

    public void setLine_color(int line_color) {
        this.line_color = line_color;
    }

    public int getLine_width() {
        return line_width;
    }

    public void setLine_width(int line_width) {
        this.line_width = line_width;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public String getMerge_regions() {
        return merge_regions;
    }

    public void setMerge_regions(String merge_regions) {
        this.merge_regions = merge_regions;
    }

    public int getInput_channels() {
        return input_channels;
    }

    public void setInput_channels(int input_channels) {
        this.input_channels = input_channels;
    }

    public String getOut_mode() {
        return out_mode;
    }

    public void setOut_mode(String out_mode) {
        this.out_mode = out_mode;
    }

    public String getNet_out_type() {
        return net_out_type;
    }

    public void setNet_out_type(String net_out_type) {
        this.net_out_type = net_out_type;
    }

    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public P2PaLaJob getP2palaparent() {
        return p2palaparent;
    }

    public void setP2palaparent(P2PaLaJob p2palaparent) {
        this.p2palaparent = p2palaparent;
    }

    public void setCnn_ngf(int cnn_ngf) {

    }
}
