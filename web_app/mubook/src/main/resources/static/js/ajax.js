$(document).ready(function(){
    if($('body').is('.searchBody')){
        document.getElementById("filterButton").addEventListener("click", function() {
            updateItemModelsPage();
            updateItemModels(false);
        });
        document.getElementById("selectPage").addEventListener("change", function() {
            updateItemModels(true);
        });
        updateItemModelsPage();
        updateItemModels(false);
    }
});


function updateItemModelsPage() {
    let itemTypeHeader = document.getElementsByTagName("h3")[0];
    let actionUrl = "/ajax/filterItemModelsGetPages/" + itemTypeHeader.textContent;

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
        if(option.checked){
            let values = [];
            if(dataMap.has(option.name)){
                values = dataMap.get(option.name);
            }
            values.push(option.value);
            dataMap.set(option.name, values);
        }
    }
    let itemTypeHeader = document.getElementsByTagName("h3")[0];
    let selectedPage;
    if(checkSelectedPage){
        selectedPage = $('#selectPage option:selected').val();
    }
    else{
        selectedPage = 1;
    }
    let actionUrl = "/ajax/filterItemModels/" + itemTypeHeader.textContent + "/" + selectedPage;
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
                let innerHTML = '<div class="itemModel" id="itemModel-'+itemModel.itemModelId+'">' +
                                '<button style="font-size: 15px">'+itemModel.name+'</button>' +
                                '</div>';
                itemModelsHTML += innerHTML;
            }
            $('#resultBlock').html(itemModelsHTML);
        }
    });
}
