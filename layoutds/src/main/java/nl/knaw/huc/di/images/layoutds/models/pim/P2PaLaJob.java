package nl.knaw.huc.di.images.layoutds.models.pim;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import nl.knaw.huc.di.images.layoutds.models.DocumentImageSet;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


@Entity
@JsonIgnoreProperties(value = {"config"}, allowGetters = true)
public class P2PaLaJob implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;
    private UUID uuid;
    private Date started;
    private Date done;

    @ManyToOne
//    @JoinColumn(name="wrapper_id", nullable = false)
    @JsonIgnore
    private PimJob wrapper;

    @Type(type = "text")
    private String errors;

    public P2PaLaJob() {
        this.created = new Date();
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

    //--exp_name Bozen_zones_BL
    private String exp_name;
    //--work_dir ./work_zones_BL
    private String work_dir = "/work";
    //--log_level DEBUG
    private String log_level = "DEBUG";
    public static final Set<String> LOG_LEVEL_OPTS = Set.of("DEBUG","INFO","WARNING","ERROR","CRITICAL");
    //--num_workers 4
    private int num_workers = 4;
    //--img_size 1024 768
    // --img_size height width
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
    private String approx_alg = "optimal";
    public static final Set<String> APPROX_ALG_OPT = Set.of("optimal", "trace");
    //--num_segments 4
    private int num_segments = 4;
    //--batch_size 8
    private int batch_size = 8;
    //--input_channels 3
    private int input_channels = 3;
    //--out_mode LR
    private String out_mode = "LR";
    public static final Set<String> OUT_MODE_OPT = Set.of("L", "LR", "R");
    //--net_out_type C
    private String net_out_type = "C";
    //--cnn_ngf 64
    private int cnn_ngf = 64;
    //--gan_layers 3
    private int gan_layers = 3;
    //--loss_lambda 100
    private int loss_lambda = 100;
    //--g_loss L1
    private String g_loss = "L1";
    //--adam_lr 0.001
    private double adam_lr = 0.001;
    //--adam_beta1 0.5
    private double adam_beta1 = 0.5;
    //--adam_beta2 0.999
    private double adam_beta2 = 0.999;
    //--do_train
    private boolean do_train = false;
    //--tr_data data/train/
    private boolean cont_train = false;
    //--tr_data data/train/
    private String tr_data = "/data/train/";
    //--no-do_val
    private boolean do_val = false;
    //    --val_data data/val/
    private String val_data = "/data/val/";
    //--do_test
    private boolean do_test = false;
    //--te_data data/test
    private String te_data = "/data/test/";
    //--no-do_prod
    private boolean do_prod = true;
    private String prod_data = "/data/prod/";

    //--epochs 200
    private int epochs = 200;
    //--use_gan
    private boolean use_gan = true;
    //--max_vertex 30
    private int max_vertex = 30;
    //--e_stdv 6
    private int e_stdv = 6;
    private String line_alg = "external";
    public static final Set<String> LINE_ALG_OPT = Set.of("basic", "external");

    @OneToMany(cascade = CascadeType.REMOVE, targetEntity = PageImagePair.class, mappedBy = "p2palaparent", fetch = FetchType.EAGER, orphanRemoval = true)
    @ElementCollection(targetClass = PageImagePair.class)
//    @JsonIgnore
    private Set<PageImagePair> pageImagePairs;

    private String imageset_uuid;

    @ManyToOne(targetEntity = DocumentImageSet.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "documentImageSetId")
    @JsonIgnore
    private DocumentImageSet documentImageSet;


    private String p2palamodel;


    @JsonProperty("config")
    public String generateConfig() {
        StringBuilder configBuilder = new StringBuilder();

        configBuilder.append("--exp_name ").append(this.exp_name);
        configBuilder.append("\n");
        configBuilder.append("--work_dir ").append(this.work_dir);
        configBuilder.append("\n");
        configBuilder.append("--log_level ").append(this.log_level);
        configBuilder.append("\n");
        configBuilder.append("--num_workers ").append(this.num_workers);
        configBuilder.append("\n");
        configBuilder.append("--img_size ").append(this.img_size);
        configBuilder.append("\n");
        configBuilder.append("--line_color ").append(this.line_color);
        configBuilder.append("\n");
        configBuilder.append("--line_width ").append(this.line_width);
        configBuilder.append("\n");
        if (!Strings.isNullOrEmpty(this.regions)) {
            configBuilder.append("--regions ").append(this.regions);
            configBuilder.append("\n");
        }
        if (!Strings.isNullOrEmpty(this.regions) && !Strings.isNullOrEmpty(this.merge_regions)) {
            configBuilder.append("--merge_regions ").append(this.merge_regions);
            configBuilder.append("\n");
        }
        if (!Strings.isNullOrEmpty(this.regions)) {
            configBuilder.append("--approx_alg ").append(this.approx_alg);
            configBuilder.append("\n");
        }
        if (!Strings.isNullOrEmpty(this.regions)) {
            configBuilder.append("--num_segments ").append(this.num_segments);
            configBuilder.append("\n");
        }
        configBuilder.append("--batch_size ").append(this.batch_size);
        configBuilder.append("\n");
        configBuilder.append("--input_channels ").append(this.input_channels);
        configBuilder.append("\n");
        configBuilder.append("--out_mode ").append(this.out_mode);
        configBuilder.append("\n");
        configBuilder.append("--net_out_type ").append(this.net_out_type);
        configBuilder.append("\n");
        configBuilder.append("--cnn_ngf ").append(this.cnn_ngf);
        configBuilder.append("\n");
        configBuilder.append("--gan_layers ").append(this.gan_layers);
        configBuilder.append("\n");
        configBuilder.append("--loss_lambda ").append(this.loss_lambda);
        configBuilder.append("\n");
        configBuilder.append("--g_loss ").append(this.g_loss);
        configBuilder.append("\n");
        configBuilder.append("--adam_lr ").append(this.adam_lr);
        configBuilder.append("\n");
        configBuilder.append("--adam_beta1 ").append(this.adam_beta1);
        configBuilder.append("\n");
        configBuilder.append("--adam_beta2 ").append(this.adam_beta2);
        configBuilder.append("\n");
        if (this.do_train) {
            configBuilder.append("--do_train");
        } else {
            configBuilder.append("--no-do_train");
        }
        configBuilder.append("\n");
        if (this.cont_train) {
            configBuilder.append("--cont_train");
            configBuilder.append("\n");
        }
        configBuilder.append("--tr_data ").append(this.tr_data);
        configBuilder.append("\n");
        if (this.do_val) {
            configBuilder.append("--do_val");
        } else {
            configBuilder.append("--no-do_val");
        }
        configBuilder.append("\n");
        configBuilder.append("--val_data ").append(this.val_data);
        configBuilder.append("\n");


        if (this.do_test) {
            configBuilder.append("--do_test");
        } else {
            configBuilder.append("--no-do_test");
        }
        configBuilder.append("\n");
        configBuilder.append("--te_data ").append(this.te_data);
        configBuilder.append("\n");
        if (this.do_prod) {
            configBuilder.append("--do_prod");
        } else {
            configBuilder.append("--no-do_prod");
        }
        configBuilder.append("\n");
        configBuilder.append("--prod_data ").append(this.prod_data);
        configBuilder.append("\n");
        configBuilder.append("--epochs ").append(this.epochs);
        configBuilder.append("\n");
        if (use_gan) {
            configBuilder.append("--use_gan");
            configBuilder.append("\n");
        }
        configBuilder.append("--max_vertex ").append(max_vertex);
        configBuilder.append("\n");
        configBuilder.append("--e_stdv ").append(e_stdv);
        configBuilder.append("\n");
        configBuilder.append("--line_alg ").append(line_alg);

        if (!Strings.isNullOrEmpty(p2palamodel)) {
            configBuilder.append("\n");
            configBuilder.append("--prev_model ").append(p2palamodel);
        }

        return configBuilder.toString();
    }


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

    public String getExp_name() {
        return exp_name;
    }

    public void setExp_name(String exp_name) {
        this.exp_name = exp_name;
    }

    public String getWork_dir() {
        return work_dir;
    }

    public void setWork_dir(String work_dir) {
        this.work_dir = work_dir;
    }

    public String getLog_level() {
        return log_level;
    }

    public void setLog_level(String log_level) {
        this.log_level = log_level;
    }

    public int getNum_workers() {
        return num_workers;
    }

    public void setNum_workers(int num_workers) {
        this.num_workers = num_workers;
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

    public String getApprox_alg() {
        return approx_alg;
    }

    public void setApprox_alg(String approx_alg) {
        this.approx_alg = approx_alg;
    }

    public int getNum_segments() {
        return num_segments;
    }

    public void setNum_segments(int num_segments) {
        this.num_segments = num_segments;
    }

    public int getBatch_size() {
        return batch_size;
    }

    public void setBatch_size(int batch_size) {
        this.batch_size = batch_size;
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

    public int getCnn_ngf() {
        return cnn_ngf;
    }

    public void setCnn_ngf(int cnn_ngf) {
        this.cnn_ngf = cnn_ngf;
    }

    public int getGan_layers() {
        return gan_layers;
    }

    public void setGan_layers(int gan_layers) {
        this.gan_layers = gan_layers;
    }

    public int getLoss_lambda() {
        return loss_lambda;
    }

    public void setLoss_lambda(int loss_lambda) {
        this.loss_lambda = loss_lambda;
    }

    public String getG_loss() {
        return g_loss;
    }

    public void setG_loss(String g_loss) {
        this.g_loss = g_loss;
    }

    public double getAdam_lr() {
        return adam_lr;
    }

    public void setAdam_lr(double adam_lr) {
        this.adam_lr = adam_lr;
    }

    public double getAdam_beta1() {
        return adam_beta1;
    }

    public void setAdam_beta1(double adam_beta1) {
        this.adam_beta1 = adam_beta1;
    }

    public double getAdam_beta2() {
        return adam_beta2;
    }

    public void setAdam_beta2(double adam_beta2) {
        this.adam_beta2 = adam_beta2;
    }

    public boolean isDo_train() {
        return do_train;
    }

    public void setDo_train(boolean do_train) {
        this.do_train = do_train;
    }

    public boolean isCont_train() {
        return cont_train;
    }

    public void setCont_train(boolean cont_train) {
        this.cont_train = cont_train;
    }

    public String getTr_data() {
        return tr_data;
    }

    public void setTr_data(String tr_data) {
        this.tr_data = tr_data;
    }

    public boolean isDo_val() {
        return do_val;
    }

    public void setDo_val(boolean do_val) {
        this.do_val = do_val;
    }

    public String getVal_data() {
        return val_data;
    }

    public void setVal_data(String val_data) {
        this.val_data = val_data;
    }

    public boolean isDo_test() {
        return do_test;
    }

    public void setDo_test(boolean do_test) {
        this.do_test = do_test;
    }

    public String getTe_data() {
        return te_data;
    }

    public void setTe_data(String te_data) {
        this.te_data = te_data;
    }

    public boolean isDo_prod() {
        return do_prod;
    }

    public void setDo_prod(boolean do_prod) {
        this.do_prod = do_prod;
    }

    public String getProd_data() {
        return prod_data;
    }

    public void setProd_data(String prod_data) {
        this.prod_data = prod_data;
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public boolean isUse_gan() {
        return use_gan;
    }

    public void setUse_gan(boolean use_gan) {
        this.use_gan = use_gan;
    }

    public int getMax_vertex() {
        return max_vertex;
    }

    public void setMax_vertex(int max_vertex) {
        this.max_vertex = max_vertex;
    }

    public int getE_stdv() {
        return e_stdv;
    }

    public void setE_stdv(int e_stdv) {
        this.e_stdv = e_stdv;
    }


    public Set<PageImagePair> getPageImagePairs() {
        return pageImagePairs;
    }

    public void setPageImagePairs(Set<PageImagePair> pageImagePairs) {
        this.pageImagePairs = pageImagePairs;
    }

    public String getLine_alg() {
        return line_alg;
    }

    public void setLine_alg(String line_alg) {
        this.line_alg = line_alg;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted() {
        this.started = new Date();
    }

    public Date getDone() {
        return done;
    }

    public void setDone() {
        this.done = new Date();
    }

    public String getImageset_uuid() {
        return imageset_uuid;
    }

    public void setImageset_uuid(String imageset_uuid) {
        this.imageset_uuid = imageset_uuid;
    }

    @JsonIgnore
    public DocumentImageSet getDocumentImageSet() {
        return documentImageSet;
    }

    public void setDocumentImageSet(DocumentImageSet documentImageSet) {
        this.documentImageSet = documentImageSet;
        if (this.documentImageSet != null) {
            setImageset_uuid(documentImageSet.getUuid().toString());
        }
    }

    public String getP2palamodel() {
        return p2palamodel;
    }

    public void setP2palamodel(String p2palamodel) {
        this.p2palamodel = p2palamodel;
    }

    public void setWrapper(PimJob wrapper) {
        this.wrapper = wrapper;
    }

    public PimJob getWrapper() {
        return wrapper;
    }

    public void appendError(String appendix) {
        setErrors(getErrors() + "\n" + appendix);
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}
