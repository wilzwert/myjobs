module myjobs.core {
    requires tools.jackson.jr.ob;
    requires org.jsoup;

    opens com.wilzwert.myjobs.core.domain.model.job.jsonld to tools.jackson.jr.ob;
}