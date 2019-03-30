package com.flytecnologia.core.email;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.InputStreamSource;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FlyMailMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String from;
    private List<String> to;
    private List<String> bcc;
    private List<String> cc;
    private String subject;
    private String text;
    private Map<String, InputStreamSource> mapInputStream;

}
