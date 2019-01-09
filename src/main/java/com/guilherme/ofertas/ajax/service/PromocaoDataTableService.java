/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guilherme.ofertas.ajax.service;

import com.guilherme.ofertas.ajax.domain.Promocao;
import com.guilherme.ofertas.ajax.repository.PromocaoRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *
 * @author Guilherme
 */
@Service
public class PromocaoDataTableService {

    private static String[] columns = {"id", "titulo", "site", "linkPromocao", "descricao", "linkPromocao",
        "preco", "likes", "dtCadastro", "categoria"};

    public static Map<String, Object> execute(PromocaoRepository repository, HttpServletRequest request) {
        int start = Integer.parseInt(request.getParameter("start"));
        int length = Integer.parseInt(request.getParameter("length"));
        int draw = Integer.parseInt(request.getParameter("draw"));

        int currentPage = findCurrentPage(start, length);

        String columnName = findColumnName(request);

        Sort.Direction direction = findDirection(request);

        Pageable pageable = PageRequest.of(currentPage, length, direction, columnName);

        String search = findWordSearched(request);

        Page<Promocao> promocoes = findPromocoesPage(search, repository, pageable);

        Map<String, Object> json = new LinkedHashMap();
        json.put("draw", draw);
        json.put("recordsTotal", promocoes.getTotalElements());
        json.put("recordsFiltered", promocoes.getTotalElements());
        json.put("data", promocoes.getContent());
        return json;
    }

    //Retorna a pagina atual da requisição
    private static int findCurrentPage(int start, int length) {
        return start / length;
    }

    //Retorna o nome da coluna do datatable
    private static String findColumnName(HttpServletRequest request) {
        int indexColumn = Integer.parseInt(request.getParameter("order[0][column]"));
        return columns[indexColumn];
    }

    //Retorna o tipo de ordenação que a página está solicitando
    private static Sort.Direction findDirection(HttpServletRequest request) {
        String dir = request.getParameter("order[0][dir]");
        if (dir.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

    //Encontra a busca adequada a ser feita no banco de dados e retorna
    private static Page<Promocao> findPromocoesPage(String search, PromocaoRepository repository, Pageable pageable) {
        if (search.isEmpty()) {
            return repository.findAll(pageable);
        } else if (search.matches("^[0-9]+([.,][0-9]{2})?$")) {
            search = search.replace(",", ".");
            return repository.findPromocaoByPreco(new BigDecimal(search), pageable);
        }

        return repository.findPromocaoByTituloOrSiteOrCategoria(search, pageable);
    }

    //Retorna a palavra digitada no campo de busca do datatable
    private static String findWordSearched(HttpServletRequest request) {
        return request.getParameter("search[value]");
    }
}
