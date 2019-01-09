package com.guilherme.ofertas.ajax.web.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.guilherme.ofertas.ajax.domain.Categoria;
import com.guilherme.ofertas.ajax.domain.Promocao;
import com.guilherme.ofertas.ajax.dto.PromocaoDto;
import com.guilherme.ofertas.ajax.repository.CategoriaRepository;
import com.guilherme.ofertas.ajax.repository.PromocaoRepository;
import com.guilherme.ofertas.ajax.service.PromocaoDataTableService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/promocao")
public class PromocaoController {

    private static Logger log = LoggerFactory.getLogger(PromocaoController.class);

    @Autowired
    private PromocaoRepository promocaoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/add")
    public String openRegister() {
        return "promo-add";
    }

    // Salvar promoção
    @PostMapping("/save")
    public ResponseEntity<?> savePromocao(@Valid Promocao promocao, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.unprocessableEntity().body(errors);
        }

        log.info("Promocao {}", promocao);
        promocao.setDtCadastro(LocalDateTime.now());
        promocaoRepository.save(promocao);
        return ResponseEntity.ok().build();
    }

    // Listar promoções ordenadas por data de cadastro
    @GetMapping("/list")
    public String listPromocoes(ModelMap model) {
        Sort sort = new Sort(Sort.Direction.DESC, "dtCadastro");
        PageRequest pageRequest = PageRequest.of(0, 8, sort);
        model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
        return "promo-list";
    }

    // 
    @GetMapping("/list/ajax")
    public String listCards(@RequestParam(name = "page", defaultValue = "1") int page, ModelMap model,
            @RequestParam(name = "site", defaultValue = "") String site) {
        Sort sort = new Sort(Sort.Direction.DESC, "dtCadastro");
        PageRequest pageRequest = PageRequest.of(page, 4, sort);
        List<String> sites = promocaoRepository.findAllSites();
        if (site.isEmpty() || !sites.contains(site)) {
            model.addAttribute("promocoes", promocaoRepository.findAll(pageRequest));
        } else {
            model.addAttribute("promocoes", promocaoRepository.findPromocaoBySite(site, pageRequest));
        }
        return "promo-card";
    }

    //Incrementa 1 like na promoção e retorna o numero total de likes presentes no BD
    @PostMapping("/like/{id}")
    public ResponseEntity<?> getLikes(@PathVariable("id") Long id) {
        promocaoRepository.updateNumLikes(id);
        int likes = promocaoRepository.findLikesById(id);
        return ResponseEntity.ok(likes);
    }

    //Autocomplete dos sites
    @GetMapping("/site")
    public ResponseEntity<?> getSiteByTerm(@RequestParam("termo") String termo) {
        List<String> sites = promocaoRepository.findSitesByTerm(termo);
        return ResponseEntity.ok(sites);
    }

    //Busca o site pesquisado após o autocomplete
    @GetMapping("/site/search")
    public String getPromocaoBySite(@RequestParam("site") String site, ModelMap model) {
        Sort sort = new Sort(Sort.Direction.DESC, "dtCadastro");
        PageRequest pageRequest = PageRequest.of(0, 4, sort);
        model.addAttribute("promocoes", promocaoRepository.findPromocaoBySite(site, pageRequest));
        return "promo-card";
    }

    //Abre a página da tabela de promoções
    @GetMapping("/table")
    public String showTable() {
        return "promo-datatables";
    }

    //Envia os dados para o lado cliente montar o datatable
    @GetMapping("/datatable/server")
    public ResponseEntity<?> listDataTable(HttpServletRequest request) {
        Map<String, Object> data = PromocaoDataTableService.execute(promocaoRepository, request);
        return ResponseEntity.ok(data);
    }

    //Retorna a promoção que será editada
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> preEditPromocao(@PathVariable("id") Long id) {
        Promocao promocao = promocaoRepository.findById(id).get();
        return ResponseEntity.ok(promocao);
    }

    //
    @PostMapping("/edit")
    public ResponseEntity<?> editPromocao(@Valid PromocaoDto dto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.unprocessableEntity().body(errors);
        }
        Promocao promocao = promocaoRepository.findById(dto.getId()).get();
        promocao.setCategoria(dto.getCategoria());
        promocao.setTitulo(dto.getTitulo());
        promocao.setPreco(dto.getPreco());
        promocao.setLinkImagem(dto.getLinkImagem());
        promocaoRepository.save(promocao);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deletePromocao(@PathVariable("id") Long id) {
        promocaoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    //Enviar a lista de categorias para o formulário
    @ModelAttribute("categorias")
    public List<Categoria> getCategorias() {

        return categoriaRepository.findAll();
    }
}
