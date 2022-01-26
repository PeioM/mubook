$(document).ready(function(){
    if($('body').is('.searchBody')){
        let title = document.getElementById("defaultSearchTitle");
        if(title == null){
            document.getElementById("filterButton").addEventListener("click", function() {
                updateItemModelPageWithFilters();
                updateItemModelsWithFilters(false);
            });
            document.getElementById("selectPage").addEventListener("change", function() {
                updateItemModelsWithFilters(true);
            });
            updateItemModelPageWithFilters();
            updateItemModelsWithFilters(false);
        }
        else{
            document.getElementById("selectPage").addEventListener("change", function() {
                updateItemModelsWithoutFilters(true);
            });
            updateItemModelPageWithoutFilters();
            updateItemModelsWithoutFilters(false);
        }

    }
});
//Ajax call for itemModels
function updateItemModelsWithFilters(checkSelectedPage){
    let options = document.getElementsByClassName("filterCheckBox");
    let dataMap = new Map();

    toggleFilters(document.getElementsByClassName("filterOption"));
    loadingSearch();

    //Get filters map
    for(let option of options){
        if(option.className.includes("selected")){
            let values = [];
            let div = option.closest("div.dropdown-menu");
            let select = $(div).siblings("select");
            let key = $(select).attr("specid");
            if(dataMap.has(key)){
                values = dataMap.get(key);
            }
            values.push(option.textContent);
            dataMap.set(key, values);
        }
    }

    let itemTypeHeader = document.getElementsByTagName("h4")[0].textContent;
    let itemType = itemTypeHeader.substring(itemTypeHeader.indexOf(':') + 2, itemTypeHeader.length);

    let actionUrl = "/ajax/filterItemModels/" + itemType + "/" + getSelectedPage(checkSelectedPage);
    let mapAsJson = Object.fromEntries(dataMap);

    ajaxCallGetItemModels(actionUrl, mapAsJson);
}
function updateItemModelsWithoutFilters(checkSelectedPage) {
    let actionUrl = "/ajax/filterItemModels/all/" + getSelectedPage(checkSelectedPage);

    toggleFilters(document.getElementsByClassName("filterOption"));
    loadingSearch();

    ajaxCallGetItemModels(actionUrl, null);
}
function ajaxCallGetItemModels(actionUrl,mapAsJson){
    $.ajax({
        method: 'GET',
        url: actionUrl,
        data: mapAsJson,

        success: function (result) {
            let itemModelsHTML;
            let itemModels = JSON.parse(result);
            itemModelsHTML="";

            for (let itemModel of itemModels){
                let itemSpecificationsHTML="";

                for(let spec of itemModel.specificationLists){
                    itemSpecificationsHTML +=
                        '<div class="p-1">' +
                        '    <p class="m-0">'+spec.specification.description+': '+spec.value+'</p>' +
                        '</div>';
                }

                let innerHTML =
                    '<div class="card col mb-4 shadow bg-light p-0" id="itemModel-'+itemModel.itemModelId+'">' +
                    '<a href="/itemModel/'+itemModel.itemModelId+'/view" ' +
                    '   class="itemModelCardLink text-decoration-none text-dark trackGrafana" grafanaId="17">' +
                    '   <img class="card-img-top img-fluid" src="'+itemModel.img+'" alt="Image '+itemModel.name+'">' +
                    '   <div class="card-body text-center">' +
                    '       <h5 class="card-title">'+itemModel.name+'</h5>' +
                    itemSpecificationsHTML +
                    '   </div>' +
                    '</a>' +
                    '</div>';
                itemModelsHTML += innerHTML;
            }
            $('#resultBlock').html(itemModelsHTML);
            toggleFilters(document.getElementsByClassName("filterOption"));

            let itemModelCards = document.getElementsByClassName("itemModelCardLink");
            for(let card of itemModelCards){
                card.addEventListener("click", function(){
                    ajaxCallRegisterButtonClick(card.getAttribute("grafanaId"));
                });
            }
        }
    });
}
//Ajax call for pages
function updateItemModelPageWithFilters() {
    let itemTypeHeader = document.getElementsByTagName("h4")[0].textContent;
    let itemType = itemTypeHeader.substring(itemTypeHeader.indexOf(':') + 2, itemTypeHeader.length);
    let actionUrl = "/ajax/filterItemModelsGetPages/" + itemType;

    ajaxCallGetPages(actionUrl, null)
}
function updateItemModelPageWithoutFilters() {
    let actionUrl = "/ajax/filterItemModelsGetPages/all";

    ajaxCallGetPages(actionUrl,null)
}


//General methods
function ajaxCallGetPages(actionUrl, data) {
    $.ajax({
        method: 'GET',
        url: actionUrl,
        data: data,

        success: function (result) {
            let pageOptionHTML="";
            if(result == 0){
                let innerHTML =  '<option>0</option>';
                pageOptionHTML += innerHTML;
            }
            else{
                for (let i = 0; i < result; i++) {
                    let innerHTML =  '<option>'+(i+1)+'</option>';
                    pageOptionHTML += innerHTML;
                }
            }
            $('#selectPage').html(pageOptionHTML);
            $("#selectPage").val($("#selectPage option:first").val());
        }
    });
}
function toggleFilters(elements) {
    for(let element of elements){
        element.disabled = !element.disabled;
    }
}
function getSelectedPage(checkSelectedPage){
    let selectedPage;
    if(checkSelectedPage){
        selectedPage = $('#selectPage option:selected').val();
    }
    else{
        selectedPage = 1;
    }
    return selectedPage;
}
function loadingSearch(){
    let loadingHTML = '<div class="align-self-center spinner-border" role="status">\n' +
        '  <span class="sr-only">Loading...</span>\n' +
        '</div>';
    $('#resultBlock').html(loadingHTML);
}