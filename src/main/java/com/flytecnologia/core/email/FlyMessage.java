package com.flytecnologia.core.email;

import org.springframework.core.io.InputStreamSource;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FlyMessage {
    private String from;
    private List<String> to;
    private String subject;
    private String text;

    private List<String> bcc;
    private List<String> cc;

    private Map<String, File> files;
    private Map<String, InputStreamSource> inputStreamSources;

    public FlyMessage() {
    }

    public FlyMessage(String from, List<String> to, String subject, String text) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

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

    public Map<String, File> getFiles() {
        return files;
    }

    public void setFiles(Map<String, File> files) {
        this.files = files;
    }

    public Map<String, InputStreamSource> getInputStreamSources() {
        return inputStreamSources;
    }

    public void setInputStreamSources(Map<String, InputStreamSource> inputStreamSources) {
        this.inputStreamSources = inputStreamSources;
    }
}
