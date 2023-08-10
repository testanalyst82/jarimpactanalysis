package com.testanalyst82.tools;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class JsonOutput {

    @SerializedName("Jars")
    @Expose
    private List<Jar> jars = null;

    public List<Jar> getJars() {
        return jars;
    }

    public void setJars(List<Jar> jars) {
        this.jars = jars;
    }

}