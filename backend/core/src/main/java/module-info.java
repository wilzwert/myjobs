module myjobs.core {
    requires com.fasterxml.jackson.jr.ob;
    requires org.jsoup;

    opens com.wilzwert.myjobs.core.domain.model.job.jsonld to com.fasterxml.jackson.jr.ob;
}