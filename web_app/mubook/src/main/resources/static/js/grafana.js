
$(document).ready(function() {
    let grafanaElement = document.getElementsByClassName("trackGrafana");

    for (let element of grafanaElement) {
        let tag = element.tagName.toLowerCase();

        if(tag === "button" || tag === "a" || (tag === "input" && element.getAttribute("type") === "submit" )){
            element.addEventListener("click", function(){
                ajaxCallRegisterButtonClick(element.getAttribute("grafanaId"));
            });
        }
        else {
            //Checkbox active in reservation filters
            element.addEventListener("change", function(){
                ajaxCallRegisterButtonClick(element.getAttribute("grafanaId"));
            });
        }
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
