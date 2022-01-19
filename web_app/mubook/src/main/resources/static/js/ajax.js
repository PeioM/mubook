$(document).ready(function(){
    if($('body').is('.searchBody')){
        document.getElementById("filterButton").addEventListener("click",updateItems);
        document.getElementById("selectPage").addEventListener("change", updateItems);
    }
});

function updateItems(){
    let options = document.getElementsByClassName("filterCheckBox");
    let dataMap = new Map();
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
    let actionUrl = "/ajax/filter/" + itemTypeHeader.textContent + "/" + $('#selectPage option:selected').val();
    let mapAsJson = Object.fromEntries(dataMap);

    $.ajax({
        method: 'GET',
        url: actionUrl,
        data: mapAsJson,

        success: function (result) {
            let itemModelIds = JSON.parse(result);

            let allModels = document.getElementsByClassName("itemModel");
            for (let itemModel of allModels){
                let id = itemModel.id.split('-')[1];
                if(!itemModelIds.includes(parseInt(id))){
                    itemModel.style.display = "none";
                }
                else{
                    itemModel.style.display = "initial";
                }
            }
        }
    });
}