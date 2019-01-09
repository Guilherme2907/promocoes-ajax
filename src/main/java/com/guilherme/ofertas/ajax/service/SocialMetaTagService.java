/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guilherme.ofertas.ajax.service;

import com.guilherme.ofertas.ajax.domain.SocialMetaTag;

/**
 *
 * @author Guilherme
 */
public interface SocialMetaTagService {

    SocialMetaTag getSocialMetaTagByUrl(String url);

    SocialMetaTag getTwitterCardByUrl(String url);

    SocialMetaTag getOpenGraphByUrl(String url);

    boolean isEmpty(SocialMetaTag tag);
}
