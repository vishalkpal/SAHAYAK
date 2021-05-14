package com.ashtech.sahayak_updated.Databases;

public class CircleJoin {
    public String grpname,code,circlehead;

    public CircleJoin(String grpname, String code, String circlehead) {
        this.grpname=grpname;
        this.code=code;
        this.circlehead = circlehead;

    }
    public CircleJoin()
    {}

    public String getGrpname() {
        return grpname;
    }

    public void setGrpname(String grpname) {
        this.grpname = grpname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCirclehead() {
        return circlehead;
    }

    public void setCirclehead(String circlehead) {
        this.circlehead = circlehead;
    }





}
