package com.testanalyst82.tools;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Class {

    @SerializedName("ClassNames")
    @Expose
    private String classNames;
    @SerializedName("ReferenceFiles")
    @Expose
    private List<ReferenceFile> referenceFiles = null;

    public String getClassNames() {
        return classNames;
    }

    public void setClassNames(String classNames) {
        this.classNames = classNames;
    }

    public List<ReferenceFile> getReferenceFiles() {
        return referenceFiles;
    }

    public void setReferenceFiles(List<ReferenceFile> referenceFiles) {
        this.referenceFiles = referenceFiles;
    }

}