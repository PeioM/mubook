$(document).ready(function(){
    if($('body').is('.searchBody')){
        let title = document.getElementById("defaultSearchTitle");
        if(title == null){
            document.getElementById("filterButton").addEventListener("click", function() {
                updateItemModelsPage();
                updateItemModels(false);
            });
            document.getElementById("selectPage").addEventListener("change", function() {
                updateItemModels(true);
            });
        }
        updateItemModelsPage();
        updateItemModels(false);
    }
});
function updateItemModelsPage() {
    let itemTypeHeader = document.getElementsByTagName("h4")[0].textContent;
    let itemType = itemTypeHeader.substring(itemTypeHeader.indexOf(':') + 2, itemTypeHeader.length);
    let actionUrl = "/ajax/filterItemModelsGetPages/" + itemType;

    $.ajax({
        method: 'GET',
        url: actionUrl,

        success: function (result) {
            let pageOptionHTML="";

            for (let i = 0; i < result; i++) {
                let innerHTML =  '<option>'+(i+1)+'</option>';
                pageOptionHTML += innerHTML;
            }
            $('#selectPage').html(pageOptionHTML);
            $("#selectPage").val($("#selectPage option:first").val());
        }
    });
}

function updateItemModels(checkSelectedPage){
    let options = document.getElementsByClassName("filterCheckBox");
    let dataMap = new Map();

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
    let selectedPage;
    if(checkSelectedPage){
        selectedPage = $('#selectPage option:selected').val();
    }
    else{
        selectedPage = 1;
    }
    let actionUrl = "/ajax/filterItemModels/" + itemType + "/" + selectedPage;
    let mapAsJson = Object.fromEntries(dataMap);
    let itemModelsHTML;

    $.ajax({
        method: 'GET',
        url: actionUrl,
        data: mapAsJson,

        success: function (result) {
            let itemModels = JSON.parse(result);
            itemModelsHTML="";

            for (let itemModel of itemModels){
                let itemSpecificationsHTML="";

                for(let spec of itemModel.specificationLists){
                    itemSpecificationsHTML +=
                        '<div class="p-1">' +
                        '    <p class="m-0">'+spec.specification.description+':'+spec.value+'</p>' +
                        '</div>';
                }

                let innerHTML =
                    '<div class="itemModel card col mb-4 shadow-sm p-0" id="itemModel-'+itemModel.itemModelId+'">' +
                    '<a href="/itemModel/view/'+itemModel.itemModelId+'" class="text-decoration-none text-dark">' +
                    '   <img class="card-img-top img-fluid" src="'+itemModel.img+'" alt="Card image cap">' +
                    '   <div class="card-body text-center">' +
                    '       <h5 class="card-title">'+itemModel.name+'</h5>' +
                        itemSpecificationsHTML +
                    '   </div>' +
                    '</a>' +
                    '</div>';
                itemModelsHTML += innerHTML;
            }
            $('#resultBlock').html(itemModelsHTML);
        }
    });
}