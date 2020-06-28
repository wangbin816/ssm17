package com.xiaoshu.entity;

public class Sousuo {
    private String name;
    private String grade;
    private String begin;
    private String end;

    public Sousuo(String name, String grade, String begin, String end) {
        this.name = name;
        this.grade = grade;
        this.begin = begin;
        this.end = end;
    }

    public Sousuo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Sousuo{" +
                "name='" + name + '\'' +
                ", grade='" + grade + '\'' +
                ", begin='" + begin + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
