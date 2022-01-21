
$(document).ready(function() {
    let buttons = document.getElementsByClassName("trackGrafana");

    for (let button of buttons) {
        button.addEventListener("click", function(){
            ajaxCallRegisterButtonClick(button.getAttribute("grafanaId"));
        });
    }
});

function ajaxCallRegisterButtonClick(grafanaId) {
    $.ajax({
        method: 'GET',
        url: "/ajax/registerGrafana/"+grafanaId,

        success: function (result){
            if(result != "")console.log("saved buttonClick");
            else console.log("buttonClick not saved");
        }
    });
}
