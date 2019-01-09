$(document).ready(function () {
    //Utiliza a data do Brasil
    moment.locale('pt-br');
    //Inicia o datatable
    var table = $("#table-server").DataTable({
        processing: true, //exibe uma barra indicando que os dados estão sendo processados
        serverSide: true,
        responsive: true,
        lengthMenu: [10, 15, 20, 25],
        ajax: {
            url: "/promocao/datatable/server"
        },
        columns: [
            {data: "id"},
            {data: "titulo"},
            {data: "site"},
            {data: "linkPromocao"},
            {data: "descricao"},
            {data: "linkImagem"},
            {data: "preco", render: $.fn.dataTable.render.number('.', ',', 2, 'R$')},
            {data: "likes"},
            {data: "dtCadastro", render:
                        function (dtCadastro) {
                            return moment(dtCadastro).format('LLL');
                        }
            },
            {data: "categoria.titulo"}
        ],
        dom: 'Bfrtip',
        buttons: [
            {
                text: "Editar",
                attr: {
                    id: "btn-editar",
                    type: "button"
                },
                enabled: false
            },
            {
                text: "Excluir",
                attr: {
                    id: "btn-excluir",
                    type: "button"
                },
                enabled: false
            }
        ]

    });

    //Desabilita os botões se o usuário ordenar a página
    $("#table-server thead").on("click", "tr", function () {
        table.buttons().disable();
    });

    //Adiciona ou remove a classe selected da linha do datatable
    $("#table-server tbody").on("click", "tr", function () {
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected");
            table.buttons().disable();
        } else {
            $("tr.selected").removeClass("selected");
            $(this).addClass("selected");
            table.buttons().enable();
        }
    });

    //Retorna o id da linha selecionada no datatable
    function getPromoId() {
        return table.row(table.$("tr.selected")).data().id;
    }

    //Verifica se a linha está selecionada
    function isRowEnabled() {
        return table.row(table.$("tr.selected")).data() !== undefined;
    }

    //Abrir o modal referente a edição da promoção e carregar os dados 
    $("#btn-editar").click(function () {
        if (isRowEnabled()) {
            var id = getPromoId();
            $.ajax({
                method: "GET",
                url: "/promocao/edit/" + id,
                beforeSend: function () {
                    //Remove os spans de erros de validações
                    $("span").closest(".error-span").remove();
                    //Remove as classes de invalidações dos inputs
                    $(".is-invalid").removeClass("is-invalid");
                    $("#modal-form").modal("show");
                },
                success: function (data) {
                    $("#edt_id").val(data.id);
                    $("#edt_titulo").val(data.titulo);
                    $("#edt_preco").val(data.preco.toLocaleString("pt-br", {
                        maximumFractionDigits: 2,
                        minimumFractionDigits: 2
                    }));
                    $("#edt_categoria").val(data.categoria.id);
                    $("#edt_descricao").val(data.descricao);
                    $("#edt_imagem").attr("src", data.linkImagem);
                    $("#edt_linkImagem").val(data.linkImagem);
                    $("#edt_site").text(data.site);
                },
                error: function () {
                    alert("Não foi possível editar essa promoção");
                }
            });
        }
    });

    $("#btn-edit-modal").click(function () {
        var promo = {};
        promo.id = $("#edt_id").val();
        promo.titulo = $("#edt_titulo").val();
        promo.preco = $("#edt_preco").val();
        promo.categoria = $("#edt_categoria").val();
        promo.descricao = $("#edt_descricao").val();
        promo.linkImagem = $("#edt_linkImagem").val();
        $.ajax({
            method: "POST",
            url: "/promocao/edit",
            data: promo,
            beforeSend: function () {
                //Remove os spans de erros de validações
                $("span").closest(".error-span").remove();
                //Remove as classes de invalidações dos inputs
                $(".is-invalid").removeClass("is-invalid");
            },
            success: function () {
                $("#modal-form").modal("hide");
                table.ajax.reload();
            },
            statusCode: {
                422: function (xhr) {
                    var errors = $.parseJSON(xhr.responseText);
                    $.each(errors, function (key, value) {
                        $("#edt_" + key).addClass("is-invalid");
                        $("#error-" + key).addClass("invalid-feedback").append("<span class='error-span'>" + value + "</span>");
                    });
                }
            }
        });
    });

    //Atualiza a imagem conforme o link passado no momento da edição
    $("#edt_linkImagem").on("change", function () {
        var imagem = $(this).val();
        $("#edt_imagem").attr("src", imagem);
    });

    //Abrir modal para exclusão da promoção
    $("#btn-excluir").click(function () {
        if (isRowEnabled()) {
            $("#modal-delete").modal("show");
        }
    });


    //Confirmar a exclusão da promoção
    $("#btn-del-modal").click(function () {
        var id = getPromoId();
        $.ajax({
            method: "GET",
            url: "/promocao/delete/" + id,
            success: function () {
                $("#modal-delete").modal("hide");
                table.ajax.reload();
            },
            error: function () {
                $("#modal-delete").modal("hide");
                alert("Não foi possível excluir essa promoção");
            }
        });
    });

});