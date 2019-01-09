//Esconde o botão no final da página e o loader
$(document).ready(function () {
    $("#loader-img").hide();
    $("#fim-btn").hide();
});

//criar efeito infinite scroll
var pageNumber = 0;
$(window).scroll(function () {
    var scrolltop = $(this).scrollTop();
    var content = $(document).height() - $(window).height();

//    console.log("scrolltop", scrolltop, " | ", "content", content);

    if (scrolltop >= content) {
        pageNumber++;
        setTimeout(function () {
            loadByScrollBar(pageNumber);
        }, 200);
    }
});

//Envia a requisição assim que a barra de rolagem chegar ao final da página
function loadByScrollBar(pageNumber) {
    var site = $("#autocomplete-input").val();

    $.ajax({
        method: "GET",
        url: "/promocao/list/ajax",
        data: {
            page: pageNumber,
            site: site
        },
        beforeSend: function () {
            $("#loader-img").show();
        },
        success: function (response) {
//            console.log("response > ", response);
            if (response.length > 150) {
                $(".row").fadeIn(function () {
                    $(this).append(response);
                });
            } else {
                $("#loader-img").removeClass("loader");
                $("#fim-btn").show();
            }
        },
        error: function (xhr) {
            alert("Ops,algo deu errado: " + xhr.status + "-" + xhr.statusText);
        }
    });
}

//Incrementar um like ao clicar no botão de curtir
$(document).on("click", "button[id*='likes-btn-']", function () {
    var id = $(this).attr("id").split("-")[2];
    console.log("id:" + id);

    $.ajax({
        method: "POST",
        url: "/promocao/like/" + id,
        success: function (response) {
            $("#likes-count-" + id).text(response);
        },
        error: function (xhr) {
            alert("Ops,ocorreu um ocorreu: " + xhr.status + " - " + xhr.statusText);
        }
    });
});

//Função de autocomplete para buscar os nomes dos sites
$("#autocomplete-input").autocomplete({
    source: function (request, response) {
        $.ajax({
            method: "GET",
            url: "/promocao/site",
            data: {
                termo: request.term
            },
            success: function (result) {
                response(result);
            }
        });
    }
});


//Buscar promoções pelo site digitado
$("#autocomplete-submit").click(function () {
    var site = $("#autocomplete-input").val();

    $.ajax({
        method: "GET",
        url: "/promocao/site/search",
        data: {
            site: site
        },
        beforeSend: function () {
            pageNumber = 0;
            $("#fim-btn").hide;
            $(".row").fadeOut(400, function () {
                $(this).empty();
            });
        },
        success: function (response) {
            $(".row").fadeIn(250, function () {
                $(this).append(response);
            });
        },
        error: function (xhr) {
            alert("Ops,ocorreu um erro: " + xhr.status + " - " + xhr.statusText);
        }
    });
});

