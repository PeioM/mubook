$(document).ready(function(){
    document.getElementById("filterButton").addEventListener("click",updateItems);
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
    let actionUrl = "/ajax/filter/" + itemTypeHeader.textContent;
    let mapAsJson = Object.fromEntries(dataMap);

    $.ajax({
        method: 'GET',
        url: actionUrl,
        data: mapAsJson,

        success: function (result) {
            let itemModelIds = JSON.parse(result);
            //let itemModelIds = details.values();

            let allModels = document.getElementsByClassName("itemModel");
            for (let itemModel of allModels){
                let id = itemModel.id.slice(-1);
                //If includes hidden = false, else hidden = true
                itemModel.hidden = !itemModelIds.includes(parseInt(id));
            }
        }
    });
}