package com.flytecnologia.core.email;


import org.springframework.core.io.InputStreamSource;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FlyMailMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String from;
    private List<String> to;
    private List<String> bcc;
    private List<String> cc;
    private String subject;
    private String text;
    private Map<String, InputStreamSource> mapInputStream;

    public FlyMailMessage() {
    }

    /*public FlyMailMessage(String from, List<String> to, String subject, String text) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
    }*/

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public Map<String, InputStreamSource> getMapInputStream() {
        return mapInputStream;
    }

    public void setMapInputStream(Map<String, InputStreamSource> mapInputStream) {
        this.mapInputStream = mapInputStream;
    }
}
