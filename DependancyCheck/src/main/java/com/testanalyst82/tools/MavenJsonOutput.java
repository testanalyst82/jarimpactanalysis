package com.testanalyst82.tools;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class MavenJsonOutput {

    @SerializedName("Jars")
    @Expose
    private List<MavenJar> jars = null;

    public List<MavenJar> getJars() {
        return jars;
    }

    public void setJars(List<MavenJar> jars) {
        this.jars = jars;
    }

}
