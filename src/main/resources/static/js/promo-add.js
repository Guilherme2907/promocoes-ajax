//submit do formulario para o controller
$("#form-add-promo").submit(function (evt) {
    //bloquear o comportamento padrão do submit
    evt.preventDefault();

    var promo = {};
    promo.linkPromocao = $("#linkPromocao").val();
    promo.descricao = $("#descricao").val();
    promo.preco = $("#preco").val();
    promo.titulo = $("#titulo").val();
    promo.categoria = $("#categoria").val();
    promo.linkImagem = $("#linkImagem").attr("src");
    promo.site = $("#site").text();


    $.ajax({
        method: "POST",
        url: "/promocao/save",
        data: promo,
        beforeSend: function () {
            $("#form-add-promo").hide();
            $("#loader-form").addClass("loader").show();
            $("span").closest(".error-span").remove();
            $(".is-invalid").removeClass("is-invalid");
        },
        success: function () {
            $("#form-add-promo").each(function () {
                this.reset();
            });
            $("#linkImagem").attr("src", "/images/promo-dark.png");
            $("#site").text("");
            $("#alert").removeClass("alert-danger");
            $("#alert").addClass("alert alert-success").text("Promoção cadastrada com sucesso.");
        },

        statusCode: {
            422: function (xhr) {
                var errors = $.parseJSON(xhr.responseText);
                $.each(errors, function (key, value) {
                    $("#" + key).addClass("is-invalid");
                    $("#error-" + key).addClass("invalid-feedback").append("<span class='error-span'>" + value + "</span>");
                });
            }
        },
        error: function (xhr) {
            console.log("> error: ", xhr.responseText);
            $("#alert").addClass("alert alert-danger").text("Não foi possível salvar esta promoção.");
        },
        complete: function () {
            $("#loader-form").fadeOut(800, function () {
                $("#form-add-promo").fadeIn(250);
                $("#loader-form").removeClass("loader");
            });
        }
    });
});




//funcao para capturar as metas tags
$("#linkPromocao").on('change', function () {

    var url = $(this).val();

    if (url.length > 7) {

        $.ajax({
            method: "POST",
            url: "/meta/info?url=" + url,
            cache: false,
            beforeSend: function () {
                $("#alert").removeClass("alert alert-danger alert-success").text('');
                $("#titulo").val("");
                $("#site").text("");
                $("#linkImagem").attr("src", "");
                $("#loader-img").addClass("loader");
            },
            success: function (data) {
                $("#titulo").val(data.title);
                $("#site").text(data.site.replace("@", ""));
                $("#linkImagem").attr("src", data.image);
            },
            statusCode: {
                404: function () {
                    $("#alert").addClass("alert alert-danger").text("Nenhuma informação pode ser recuperada dessa url.");
                    $("#linkImagem").attr("src", "/images/promo-dark.png");
                }
            },
            error: function () {
                $("#alert").addClass("alert alert-danger").text("Ops... algo deu errado, tente mais tarde.");
                $("#linkImagem").attr("src", "/images/promo-dark.png");
            },
            complete: function () {
                $("#loader-img").removeClass("loader");
            }
        });
    }
});