package com.lzy.attnd.service;


import com.lzy.attnd.model.Attnd;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface AttndService {
    void AddAttnd(@Valid Attnd attnd);

}
