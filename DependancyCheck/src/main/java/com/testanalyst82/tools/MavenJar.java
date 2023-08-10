package com.testanalyst82.tools;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class MavenJar {

    @SerializedName("jarsName")
    @Expose
    private String jarsName;
    @SerializedName("DependentJars")
    @Expose
    private List<String> dependentJars = null;

    public String getJarsName() {
        return jarsName;
    }

    public void setJarsName(String jarsName) {
        this.jarsName = jarsName;
    }

    public List<String> getDependentJars() {
        return dependentJars;
    }

    public void setDependentJars(List<String> dependentJars) {
        this.dependentJars = dependentJars;
    }

}
